package com.mercandalli.android.apps.files.file.audio;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;
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
import com.mercandalli.android.library.baselibrary.java.StringUtils;
import com.mercandalli.android.library.baselibrary.precondition.Preconditions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The {@link FileAudioModel} player.
 */
public class FileAudioPlayer implements
        MediaPlayer.OnPreparedListener,
        MediaPlayer.OnCompletionListener,
        AudioManager.OnAudioFocusChangeListener {

    private static final String TAG = "FileAudioPlayer";

    @SharedAudioPlayerUtils.Status
    private int mCurrentStatus = SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_UNKNOWN;

    private FileAudioModel mCurrentMusic;
    private FileAudioModel mPreparingMusic;
    private final List<FileAudioModel> mFileAudioModelList = new ArrayList<>();
    private int mCurrentMusicIndex;

    private MediaPlayer mMediaPlayer;
    private final Context mAppContext;
    private final AudioManager mAudioManager;

    private final UpdaterPosition mUpdatePositionRunnable = new UpdaterPosition();
    private final List<OnPlayerStatusChangeListener> mOnPlayerStatusChangeListeners = new ArrayList<>();

    private final Handler mHandler = new Handler();
    private String mWatchNodeId;

    public FileAudioPlayer(final Application application) {
        mAppContext = application.getApplicationContext();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mCurrentStatus = SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED;
        mAudioManager = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        notifyPositionChanged();
        retrieveDeviceNode(mAppContext);

        // Register the local broadcast receiver, defined in step 3.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(mAppContext).registerReceiver(messageReceiver, messageFilter);
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
    public void onAudioFocusChange(int focusChange) {
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
            final int request = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
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
        notifyAudioChanged();
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
        notifyAudioChanged();
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
        notifyAudioChanged();
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

    public void registerOnPlayerStatusChangeListener(OnPlayerStatusChangeListener listener) {
        synchronized (mOnPlayerStatusChangeListeners) {
            if (!mOnPlayerStatusChangeListeners.contains(listener)) {
                mOnPlayerStatusChangeListeners.add(listener);
            }
        }
    }

    public void unregisterOnPreviewPlayerStatusChangeListener(OnPlayerStatusChangeListener listener) {
        synchronized (mOnPlayerStatusChangeListeners) {
            mOnPlayerStatusChangeListeners.remove(listener);
        }
    }

    public int getCurrentMusicIndex() {
        return mCurrentMusicIndex;
    }

    public List<FileAudioModel> getFileAudioModelList() {
        return mFileAudioModelList;
    }

    /* PRIVATE */

    private void notifyPositionChanged() {
        mHandler.removeCallbacks(mUpdatePositionRunnable);
        if (isPlaying()) {
            synchronized (mOnPlayerStatusChangeListeners) {
                for (int i = 0, size = mOnPlayerStatusChangeListeners.size(); i < size; i++) {
                    mOnPlayerStatusChangeListeners.get(i).onPlayerProgressChanged(getCurrentProgress(), getDuration());
                }
            }
        }
        mHandler.postDelayed(mUpdatePositionRunnable, 1000);
    }

    private void notifyAudioChanged() {
        synchronized (mOnPlayerStatusChangeListeners) {
            for (int i = 0, size = mOnPlayerStatusChangeListeners.size(); i < size; i++) {
                mOnPlayerStatusChangeListeners.get(i).onAudioChanged(mCurrentMusicIndex, mFileAudioModelList);
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

        sendWearMessage(mAppContext, currentStatus, mFileAudioModelList.get(mCurrentMusicIndex));

        if (notifyListeners) {
            synchronized (mOnPlayerStatusChangeListeners) {
                for (int i = 0, size = mOnPlayerStatusChangeListeners.size(); i < size; i++) {
                    mOnPlayerStatusChangeListeners.get(i).onPlayerStatusChanged(mCurrentStatus);
                }
            }
        }
    }

    /**
     * Display or hide the notification.
     */
    /* package */ void setNotification(final boolean activated) {
        if (activated && mCurrentMusic != null) {

            final Intent intent = new Intent(mAppContext, FileAudioActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            RemoteViews remoteViews = new RemoteViews(mAppContext.getPackageName(), R.layout.notification_musique);
            remoteViews.setTextViewText(R.id.titre_notif, mCurrentMusic.getName());
            remoteViews.setOnClickPendingIntent(R.id.titre_notif, NotificationAudioPlayerReceiver.getNotificationIntentActivity(mAppContext));
            remoteViews.setOnClickPendingIntent(R.id.close, NotificationAudioPlayerReceiver.getNotificationIntentClose(mAppContext));
            remoteViews.setOnClickPendingIntent(R.id.activity_file_audio_play, NotificationAudioPlayerReceiver.getNotificationIntentPlayPause(mAppContext));
            remoteViews.setOnClickPendingIntent(R.id.activity_file_audio_next, NotificationAudioPlayerReceiver.getNotificationIntentNext(mAppContext));
            remoteViews.setOnClickPendingIntent(R.id.prev, NotificationAudioPlayerReceiver.getNotificationIntentPrevious(mAppContext));

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                Notification.Builder mNotifyBuilder = new Notification.Builder(mAppContext);
                Notification foregroundNote = mNotifyBuilder.setSmallIcon(R.drawable.audio)
                    /*
                    .setContentTitle("Music")
                    .setContentText( "Text" )*/
                        //.setContentIntent(pIntent)
                        .setAutoCancel(false)
                        .setOngoing(true)
                        .setContent(remoteViews)
                        .build();
                foregroundNote.contentView = remoteViews;

                if (mMediaPlayer.isPlaying()) {
                    NotificationManager notificationManager = (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(0, foregroundNote);
                }
            }

        } else {
            NotificationManager notificationManager = (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
        }
    }

    private GoogleApiClient getGoogleApiClient(final Context context) {
        Preconditions.checkNotNull(context);
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    private void retrieveDeviceNode(final Context context) {
        Preconditions.checkNotNull(context);
        final GoogleApiClient client = getGoogleApiClient(context);
        new Thread(new Runnable() {
            @Override
            public void run() {
                client.blockingConnect(FileAudioWearUtils.CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                NodeApi.GetConnectedNodesResult result =
                        Wearable.NodeApi.getConnectedNodes(client).await();
                List<Node> nodes = result.getNodes();
                if (nodes.size() > 0) {
                    mWatchNodeId = nodes.get(0).getId();
                }
                client.disconnect();
            }
        }).start();
    }

    private void sendWearMessage(
            final Context context,
            final @SharedAudioPlayerUtils.Status int currentStatus,
            final FileAudioModel fileAudioModel) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(fileAudioModel);
        FileAudioWearUtils.sendWearMessage(getGoogleApiClient(context), mWatchNodeId, currentStatus, fileAudioModel);
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

    public interface OnPlayerStatusChangeListener {

        /**
         * The player status change.
         *
         * @param status The new status.
         */
        void onPlayerStatusChanged(@SharedAudioPlayerUtils.Status int status);

        /**
         * The player progress change.
         */
        void onPlayerProgressChanged(int progress, int duration);

        void onAudioChanged(int musicPosition, List<FileAudioModel> musics);
    }

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
                sendWearMessage(mAppContext, mCurrentStatus, mFileAudioModelList.get(mCurrentMusicIndex));
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
}
