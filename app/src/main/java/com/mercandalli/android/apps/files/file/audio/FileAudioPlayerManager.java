package com.mercandalli.android.apps.files.file.audio;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.RemoteViews;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.shared.SharedAudioData;
import com.mercandalli.android.apps.files.shared.SharedAudioPlayerUtils;
import com.mercandalli.android.library.base.graphics.BitmapUtils;
import com.mercandalli.android.library.base.java.StringUtils;
import com.mercandalli.android.library.base.precondition.Preconditions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.TimeUnit;

import static com.mercandalli.android.apps.files.file.audio.NotificationAudioPlayerReceiver.getNotificationIntentActivity;
import static com.mercandalli.android.apps.files.file.audio.NotificationAudioPlayerReceiver.getNotificationIntentClose;
import static com.mercandalli.android.apps.files.file.audio.NotificationAudioPlayerReceiver.getNotificationIntentNext;
import static com.mercandalli.android.apps.files.file.audio.NotificationAudioPlayerReceiver.getNotificationIntentPause;
import static com.mercandalli.android.apps.files.file.audio.NotificationAudioPlayerReceiver.getNotificationIntentPlayPause;
import static com.mercandalli.android.apps.files.file.audio.NotificationAudioPlayerReceiver.getNotificationIntentPrevious;

/**
 * The {@link FileAudioModel} player.
 */
public class FileAudioPlayerManager implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "FileAudioPlayerManager";

    private static final int NOTIFICATION_ID = 0;

    @Nullable
    private static FileAudioPlayerManager sInstance;

    @NonNull
    public static FileAudioPlayerManager getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new FileAudioPlayerManager(context);
        }
        return sInstance;
    }

    @SharedAudioPlayerUtils.Status
    private int mCurrentStatus = SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_UNKNOWN;

    @Nullable
    private FileAudioModel mCurrentMusic;

    @Nullable
    private FileAudioModel mPreparingMusic;

    private int mCurrentMusicIndex;

    @Nullable
    private String mWatchNodeId;

    @NonNull
    private final List<FileAudioModel> mFileAudioModelList = new ArrayList<>();

    @NonNull
    private final MediaPlayer mMediaPlayer;

    @NonNull
    private final Context mContext;

    @NonNull
    private final AudioManager mAudioManager;

    @NonNull
    private final UpdaterPosition mUpdatePositionRunnable = new UpdaterPosition();

    @NonNull
    private final List<OnPlayerStatusChangeListener> mOnPlayerStatusChangeListeners = new ArrayList<>();

    @NonNull
    private final Handler mHandler = new Handler();

    private FileAudioPlayerManager(@NonNull final Context context) {
        mContext = context.getApplicationContext();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mCurrentStatus = SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        notifyPositionChanged();
        retrieveDeviceNode(mContext);

        // Register the local broadcast receiver, defined in step 3.
        final IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        final MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(mContext).registerReceiver(messageReceiver, messageFilter);
    }

    @Override
    public void onCompletion(final MediaPlayer mp) {
        next();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mCurrentMusic = mPreparingMusic;
        mPreparingMusic = null;
        setCurrentStatus(SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED, false);
        play();
    }

    @Override
    public void onAudioFocusChange(final int focusChange) {
        if ((mCurrentStatus == SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING) &&
                (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                        focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)) {
            pause();
        }
    }

    /**
     * Play the element #{@link #mCurrentMusicIndex} in {@link #mFileAudioModelList}.
     */
    public void play() {
        if (isPlayerKO()) {
            return;
        }
        if (SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED == mCurrentStatus) {
            final int request = mAudioManager.requestAudioFocus(
                    this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (request == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mMediaPlayer.start();
                setCurrentStatus(SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING);
            } else {
                setCurrentStatus(SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED);
            }
        }
    }

    /**
     * Pause the element #{@link #mCurrentMusicIndex} in {@link #mFileAudioModelList}.
     */
    public void pause() {
        if (isPlayerKO()) {
            return;
        }
        if (SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING == mCurrentStatus) {
            mMediaPlayer.pause();
            setCurrentStatus(SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED);
        }
        mAudioManager.abandonAudioFocus(this);
    }

    /**
     * Play the next {@link FileAudioModel} in {@link #mFileAudioModelList}.
     */
    public void next() {
        if (isPlayerKO()) {
            return;
        }
        mCurrentMusicIndex++;
        if (mCurrentMusicIndex >= mFileAudioModelList.size()) {
            mCurrentMusicIndex = 0;
        }
        final FileAudioModel currentMusic = mFileAudioModelList.get(mCurrentMusicIndex);
        if (mCurrentMusic == null || !StringUtils.isEquals(currentMusic.getPath(), mCurrentMusic.getPath())) {
            prepare(currentMusic);
        }
        notifyAudioChanged(SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_NEXT);
    }

    /**
     * Play the previous {@link FileAudioModel} in {@link #mFileAudioModelList}.
     */
    public void previous() {
        if (isPlayerKO()) {
            return;
        }
        mCurrentMusicIndex--;
        if (mCurrentMusicIndex < 0) {
            mCurrentMusicIndex = mFileAudioModelList.size() - 1;
        }
        final FileAudioModel currentMusic = mFileAudioModelList.get(mCurrentMusicIndex);
        if (mCurrentMusic == null || !StringUtils.isEquals(currentMusic.getPath(), mCurrentMusic.getPath())) {
            prepare(currentMusic);
        }
        notifyAudioChanged(SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PREVIOUS);
    }

    /**
     * Is the song playing.
     *
     * @return True if the {@code mCurrentStatus == SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING}.
     */
    public boolean isPlaying() {
        return mCurrentStatus == SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING;
    }

    public void startMusic(
            int currentMusicIndex,
            final List<FileAudioModel> musics) {

        Preconditions.checkNotNull(musics);
        if (musics.isEmpty()) {
            Log.e(TAG, "startMusic with empty List");
            return;
        }
        int musicsSize = musics.size();
        if (musicsSize <= currentMusicIndex) {
            Log.e(TAG, "startMusic invalid index : " + currentMusicIndex + " with musics.size() = " + musicsSize);
            currentMusicIndex = 0;
        }

        mFileAudioModelList.clear();
        mFileAudioModelList.addAll(musics);
        FileAudioModel newCurrentMusic = mFileAudioModelList.get(currentMusicIndex);
        if (newCurrentMusic.getPath() == null) {
            // Error with the newCurrentMusic: we play the first track.
            mCurrentMusicIndex = 0;
            mFileAudioModelList.remove(currentMusicIndex);
            newCurrentMusic = mFileAudioModelList.get(mCurrentMusicIndex);
            if (newCurrentMusic.getPath() == null) {
                // Error.
                return;
            }
        } else {
            mCurrentMusicIndex = currentMusicIndex;
        }

        if (mCurrentMusic == null || !newCurrentMusic.getPath().equals(mCurrentMusic.getPath())) {
            if (SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PREPARING == mCurrentStatus) {
                mMediaPlayer.reset();
                setCurrentStatus(SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED, false);
            }
            prepare(newCurrentMusic);
        } else if (mCurrentStatus == SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED) {
            play();
        }
        notifyAudioChanged(SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PLAY);
    }

    public int getDuration() {
        if (mPreparingMusic == null && mCurrentMusic != null) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public int getCurrentProgress() {
        if (mPreparingMusic == null && mCurrentMusic != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public void seekTo(int milliseconds) {
        if (mPreparingMusic == null && mCurrentMusic != null) {
            mMediaPlayer.seekTo(milliseconds);
        }
    }

    public boolean isEmpty() {
        return mFileAudioModelList.isEmpty();
    }

    public void addOnPlayerStatusChangeListener(OnPlayerStatusChangeListener listener) {
        synchronized (mOnPlayerStatusChangeListeners) {
            if (!mOnPlayerStatusChangeListeners.contains(listener)) {
                mOnPlayerStatusChangeListeners.add(listener);
            }
        }
    }

    public void removeOnPreviewPlayerStatusChangeListener(OnPlayerStatusChangeListener listener) {
        synchronized (mOnPlayerStatusChangeListeners) {
            mOnPlayerStatusChangeListeners.remove(listener);
        }
    }

    public int getCurrentMusicIndex() {
        return mCurrentMusicIndex;
    }

    @NonNull
    public List<FileAudioModel> getFileAudioModelList() {
        return mFileAudioModelList;
    }

    /* PRIVATE */

    private void notifyPositionChanged() {
        mHandler.removeCallbacks(mUpdatePositionRunnable);
        if (isPlaying()) {
            synchronized (mOnPlayerStatusChangeListeners) {
                final ListIterator<OnPlayerStatusChangeListener> listIterator =
                        mOnPlayerStatusChangeListeners.listIterator();
                while (listIterator.hasNext()) {
                    final OnPlayerStatusChangeListener next = listIterator.next();
                    if (next.onPlayerProgressChanged(getCurrentProgress(), getDuration())) {
                        listIterator.remove();
                    }
                }
            }
        }
        mHandler.postDelayed(mUpdatePositionRunnable, 1_000);
    }

    private void notifyAudioChanged(@SharedAudioPlayerUtils.Action final int action) {
        synchronized (mOnPlayerStatusChangeListeners) {
            final ListIterator<OnPlayerStatusChangeListener> listIterator =
                    mOnPlayerStatusChangeListeners.listIterator();
            while (listIterator.hasNext()) {
                final OnPlayerStatusChangeListener next = listIterator.next();
                if (next.onAudioChanged(
                        mCurrentMusicIndex,
                        mFileAudioModelList,
                        action)) {
                    listIterator.remove();
                }
            }
        }
    }

    private void prepare(@NonNull final FileAudioModel fileAudioModel) {
        Preconditions.checkNotNull(fileAudioModel);
        if (SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PREPARING == mCurrentStatus || isPlayerKO()) {
            return;
        }

        mPreparingMusic = fileAudioModel;
        setCurrentStatus(SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PREPARING);

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();

        try {
            mMediaPlayer.setDataSource(fileAudioModel.getPath());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            mMediaPlayer.reset();
            setCurrentStatus(SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED);
        }
    }

    private void setCurrentStatus(final int currentStatus) {
        setCurrentStatus(currentStatus, true);
    }

    private void setCurrentStatus(final int currentStatus, final boolean notifyListeners) {
        mCurrentStatus = currentStatus;
        setNotification(currentStatus == SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING);
        sendWearMessage(mContext, currentStatus, mFileAudioModelList.get(mCurrentMusicIndex));

        if (notifyListeners) {
            synchronized (mOnPlayerStatusChangeListeners) {
                final ListIterator<OnPlayerStatusChangeListener> listIterator =
                        mOnPlayerStatusChangeListeners.listIterator();
                while (listIterator.hasNext()) {
                    final OnPlayerStatusChangeListener next = listIterator.next();
                    if (next.onPlayerStatusChanged(mCurrentStatus)) {
                        listIterator.remove();
                    }
                }
            }
        }
    }

    /**
     * Display or hide the notification.
     */
    /* package */
    void setNotification(final boolean activated) {
        if (activated && mCurrentMusic != null) {
            if (mMediaPlayer.isPlaying()) {
                final RemoteViews remoteViews = new RemoteViews(mContext.getPackageName(),
                        R.layout.notification_musique);
                remoteViews.setTextViewText(R.id.titre_notif,
                        mCurrentMusic.getName());
                remoteViews.setOnClickPendingIntent(R.id.titre_notif,
                        getNotificationIntentActivity(mContext));
                remoteViews.setOnClickPendingIntent(R.id.close,
                        getNotificationIntentClose(mContext));
                remoteViews.setOnClickPendingIntent(R.id.activity_file_audio_play,
                        getNotificationIntentPlayPause(mContext));
                remoteViews.setOnClickPendingIntent(R.id.activity_file_audio_next,
                        getNotificationIntentNext(mContext));
                remoteViews.setOnClickPendingIntent(R.id.prev,
                        getNotificationIntentPrevious(mContext));
                NotificationManagerCompat.from(mContext).notify(NOTIFICATION_ID,
                        new NotificationCompat.Builder(mContext)
                                .setSmallIcon(R.drawable.ic_music_note_white_24dp)
                                .setAutoCancel(false)
                                //.setOngoing(true)
                                .setContent(remoteViews)
                                .addAction(R.mipmap.ic_launcher,
                                        "Next",
                                        getNotificationIntentNext(mContext))
                                .addAction(R.mipmap.ic_launcher,
                                        "Play/Pause",
                                        getNotificationIntentPlayPause(mContext))
                                .addAction(R.mipmap.ic_launcher,
                                        "Previous",
                                        getNotificationIntentPrevious(mContext))
                                .extend(new NotificationCompat.WearableExtender()
                                        .setBackground(BitmapUtils.drawableToBitmap(mContext.getResources()
                                                .getDrawable(R.drawable.ic_music_note_white_24dp))))
                                .setDeleteIntent(getNotificationIntentPause(mContext))
                                .build());
            }
        } else {
            NotificationManagerCompat.from(mContext).cancel(NOTIFICATION_ID);
        }
    }

    @NonNull
    private GoogleApiClient getGoogleApiClient(@NonNull final Context context) {
        Preconditions.checkNotNull(context);
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    private void retrieveDeviceNode(@NonNull final Context context) {
        Preconditions.checkNotNull(context);
        final GoogleApiClient client = getGoogleApiClient(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(FileAudioWearUtils.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                final List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    mWatchNodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    private void sendWearMessage(
            @NonNull final Context context,
            @SharedAudioPlayerUtils.Status final int currentStatus,
            @NonNull final FileAudioModel fileAudioModel) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(fileAudioModel);
        FileAudioWearUtils.sendWearMessage(
                getGoogleApiClient(context), mWatchNodeId, currentStatus, fileAudioModel);
    }

    /**
     * Is this player KO. Are the {@link #mFileAudioModelList}, {@link #mCurrentMusicIndex} KO.
     *
     * @return If this class is KO.
     */
    private boolean isPlayerKO() {
        boolean playerKO = false;
        if (mFileAudioModelList.isEmpty()) {
            Log.e(TAG, "mFileAudioModelList is empty");
            playerKO = true;
        } else if (mCurrentMusicIndex >= mFileAudioModelList.size()) {
            Log.e(TAG, "mCurrentMusicIndex >= mFileAudioModelList.size()");
            playerKO = true;
        }
        return playerKO;
    }

    /* INNER */

    private class UpdaterPosition implements Runnable {
        @Override
        public void run() {
            notifyPositionChanged();
        }
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String message = intent.getStringExtra("message");

            if (message.isEmpty() || message.replaceAll(" ", "").isEmpty()) {
                sendWearMessage(mContext, mCurrentStatus, mFileAudioModelList.get(mCurrentMusicIndex));
                return;
            }

            if (mFileAudioModelList.isEmpty()) {
                setNotification(false);
                return;
            }

            final SharedAudioData sharedAudioData = new SharedAudioData(message);
            switch (sharedAudioData.getAction()) {
                case SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PAUSE:
                    pause();
                    break;
                case SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PLAY:
                    play();
                    break;
                case SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_NEXT:
                    next();
                    break;
                case SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_PREVIOUS:
                    previous();
                    break;
                case SharedAudioPlayerUtils.AUDIO_PLAYER_ACTION_UNKNOWN:
                    break;
            }
        }
    }

    public interface OnPlayerStatusChangeListener {

        /**
         * The player status change.
         *
         * @param status The new status.
         * @return <code>true</code> if the caller has to remove the listener, otherwise keep this
         * listener in the {@link List} of listeners.
         */
        boolean onPlayerStatusChanged(@SharedAudioPlayerUtils.Status final int status);

        /**
         * The player progress change.
         *
         * @return <code>true</code> if the caller has to remove the listener, otherwise keep this
         * listener in the {@link List} of listeners.
         */
        boolean onPlayerProgressChanged(final int progress, final int duration);

        /**
         * @return <code>true</code> if the caller has to remove the listener, otherwise keep this
         * listener in the {@link List} of listeners.
         */
        boolean onAudioChanged(
                final int musicPosition,
                final List<FileAudioModel> musics,
                @SharedAudioPlayerUtils.Action final int action);
    }
}
