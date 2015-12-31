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
import android.widget.RemoteViews;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.Preconditions;
import com.mercandalli.android.apps.files.shared.SharedAudioData;
import com.mercandalli.android.apps.files.shared.SharedAudioPlayerUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The {@link FileAudioModel} player.
 */
public class FileAudioPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    @SharedAudioPlayerUtils.Status
    private int mCurrentStatus = SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_UNKNOWN;

    private FileAudioModel mCurrentMusic;
    private FileAudioModel mPreparingMusic;
    private final List<FileAudioModel> mFileAudioModelList = new ArrayList<>();
    private int mCurrentMusicIndex;

    private MediaPlayer mMediaPlayer;
    private final Context mAppContext;
    private final AudioManager mAudioManager;

    private UpdaterPosition mUpdatePositionRunnable = new UpdaterPosition();
    private final List<OnPlayerStatusChangeListener> mOnPlayerStatusChangeListeners;

    private final Handler mHandler = new Handler();
    private String mWatchNodeId;

    public FileAudioPlayer(Application application) {
        mAppContext = application.getApplicationContext();
        mOnPlayerStatusChangeListeners = new ArrayList<>();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mCurrentStatus = SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED;
        mAudioManager = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        updatePosition();
        retrieveDeviceNode(mAppContext);

        // Register the local broadcast receiver, defined in step 3.
        IntentFilter messageFilter = new IntentFilter(Intent.ACTION_SEND);
        MessageReceiver messageReceiver = new MessageReceiver();
        LocalBroadcastManager.getInstance(mAppContext).registerReceiver(messageReceiver, messageFilter);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setCurrentStatus(SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED);
        next();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mCurrentMusic = mPreparingMusic;
        mPreparingMusic = null;
        setCurrentStatus(SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED);
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
        if (SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING == mCurrentStatus) {
            mMediaPlayer.pause();
            setCurrentStatus(SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED);
        }
        mAudioManager.abandonAudioFocus(this);
    }

    public void next() {
        mCurrentMusicIndex++;
        if (mCurrentMusicIndex >= mFileAudioModelList.size()) {
            mCurrentMusicIndex = 0;
        }

        final FileAudioModel currentMusic = mFileAudioModelList.get(mCurrentMusicIndex);
        if (mCurrentMusic == null || !currentMusic.getPath().equals(mCurrentMusic.getPath())) {
            prepare(currentMusic);
        }
    }

    public void previous() {
        mCurrentMusicIndex--;
        if (mCurrentMusicIndex < 0) {
            mCurrentMusicIndex = mFileAudioModelList.size() - 1;
        }

        final FileAudioModel currentMusic = mFileAudioModelList.get(mCurrentMusicIndex);
        if (mCurrentMusic == null || !currentMusic.getPath().equals(mCurrentMusic.getPath())) {
            prepare(currentMusic);
        }
    }

    public boolean isPlaying() {
        return mCurrentStatus == SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING;
    }

    public void startMusic(final int currentMusicIndex, List<FileAudioModel> musics) {
        mCurrentMusicIndex = currentMusicIndex;
        mFileAudioModelList.clear();
        mFileAudioModelList.addAll(musics);
        final FileAudioModel currentMusic = mFileAudioModelList.get(mCurrentMusicIndex);

        if (mCurrentMusic == null || !currentMusic.getPath().equals(mCurrentMusic.getPath())) {
            prepare(currentMusic);
        } else if (mCurrentStatus == SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED) {
            play();
        }
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

    public void stopPreview() {
        if (SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PREPARING == mCurrentStatus) {
            mMediaPlayer.reset();
            mCurrentMusic = null;
            setCurrentStatus(SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PAUSED);
        } else {
            pause();
        }
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

    public int getCurrentStatus() {
        return mCurrentStatus;
    }

    /**
     * Get the current audio file.
     */
    public FileAudioModel getCurrentPreview() {
        return mCurrentMusic;
    }


    /* PRIVATE */

    private void updatePosition() {
        mHandler.removeCallbacks(mUpdatePositionRunnable);
        if (isPlaying()) {
            synchronized (mOnPlayerStatusChangeListeners) {
                for (int i = 0, size = mOnPlayerStatusChangeListeners.size(); i < size; i++) {
                    mOnPlayerStatusChangeListeners.get(i).onPlayerProgressChanged(getCurrentProgress(), getDuration(), mCurrentMusicIndex, mCurrentMusic);
                }
            }
        }
        mHandler.postDelayed(mUpdatePositionRunnable, 1000);
    }

    private void prepare(@NonNull FileAudioModel fileAudioModel) {
        Preconditions.checkNotNull(fileAudioModel);
        if (SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PREPARING == mCurrentStatus) {
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

    private void setCurrentStatus(int currentStatus) {
        mCurrentStatus = currentStatus;
        setNotification(currentStatus == SharedAudioPlayerUtils.AUDIO_PLAYER_STATUS_PLAYING);

        sendWearMessage(mAppContext, currentStatus, mFileAudioModelList.get(mCurrentMusicIndex));

        synchronized (mOnPlayerStatusChangeListeners) {
            for (int i = 0, size = mOnPlayerStatusChangeListeners.size(); i < size; i++) {
                mOnPlayerStatusChangeListeners.get(i).onPlayerStatusChanged(mCurrentStatus);
            }
        }
    }

    /**
     * Display or hide the notification.
     */
    private void setNotification(boolean activated) {
        if (activated) {

            Intent intent = new Intent(mAppContext, FileAudioActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

            RemoteViews remoteViews = new RemoteViews(mAppContext.getPackageName(), R.layout.notification_musique);
            remoteViews.setTextViewText(R.id.titre_notif, mCurrentMusic.getName());
            remoteViews.setOnClickPendingIntent(R.id.close, NotificationAudioPlayerReceiver.getNotificationIntentClose(mAppContext));
            remoteViews.setOnClickPendingIntent(R.id.play, NotificationAudioPlayerReceiver.getNotificationIntentPlayPause(mAppContext));
            remoteViews.setOnClickPendingIntent(R.id.next, NotificationAudioPlayerReceiver.getNotificationIntentNext(mAppContext));
            remoteViews.setOnClickPendingIntent(R.id.prev, NotificationAudioPlayerReceiver.getNotificationIntentPrevious(mAppContext));

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
            foregroundNote.bigContentView = remoteViews;

            if (mMediaPlayer.isPlaying()) {
                NotificationManager notificationManager = (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
                notificationManager.notify(0, foregroundNote);
            }
        } else {
            NotificationManager notificationManager = (NotificationManager) mAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.cancel(0);
        }
    }

    private GoogleApiClient getGoogleApiClient(Context context) {
        Preconditions.checkNotNull(context);
        return new GoogleApiClient.Builder(context)
                .addApi(Wearable.API)
                .build();
    }

    private void retrieveDeviceNode(Context context) {
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

    private void sendWearMessage(Context context, final @SharedAudioPlayerUtils.Status int currentStatus, final FileAudioModel fileAudioModel) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(fileAudioModel);
        FileAudioWearUtils.sendWearMessage(getGoogleApiClient(context), mWatchNodeId, currentStatus, fileAudioModel);
    }


    /* INNER */

    private class UpdaterPosition implements Runnable {
        @Override
        public void run() {
            updatePosition();
        }
    }

    public interface OnPlayerStatusChangeListener {
        void onPlayerStatusChanged(@SharedAudioPlayerUtils.Status int status);

        void onPlayerProgressChanged(int progress, int duration, int musicPosition, FileAudioModel music);
    }

    public class MessageReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String message = intent.getStringExtra("message");
            final SharedAudioData sharedAudioData = new SharedAudioData(message);
            switch (sharedAudioData.getOrder()) {
                case SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_PAUSE:
                    pause();
                    break;
                case SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_PLAY:
                    play();
                    break;
                case SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_NEXT:
                    next();
                    break;
                case SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_PREVIOUS:
                    previous();
                    break;
                case SharedAudioPlayerUtils.AUDIO_PLAYER_ORDER_UNKNOWN:
                    break;
            }
        }
    }
}

