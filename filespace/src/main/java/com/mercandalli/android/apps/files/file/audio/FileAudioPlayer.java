package com.mercandalli.android.apps.files.file.audio;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.widget.RemoteViews;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.NodeApi;
import com.google.android.gms.wearable.Wearable;
import com.mercandalli.android.apps.files.R;
import com.mercandalli.android.apps.files.common.Preconditions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * The {@link FileAudioModel} player.
 */
public class FileAudioPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({STATUS_PAUSED, STATUS_PLAYING, STATUS_PREPARING})
    public @interface Status {
    }

    private static final int STATUS_PAUSED = 0;
    private static final int STATUS_PLAYING = 1;
    private static final int STATUS_PREPARING = 3;

    private static final long CONNECTION_TIME_OUT_MS = 5000;

    @Status
    private int mCurrentStatus;

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
        mCurrentStatus = STATUS_PAUSED;
        mAudioManager = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        updatePosition();
        retrieveDeviceNode(mAppContext);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        setCurrentStatus(STATUS_PAUSED);
        next();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mCurrentMusic = mPreparingMusic;
        mPreparingMusic = null;
        setCurrentStatus(STATUS_PAUSED);
        play();
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if ((mCurrentStatus == STATUS_PLAYING) &&
                (focusChange == AudioManager.AUDIOFOCUS_LOSS ||
                        focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT)) {
            pause();
        }
    }

    /**
     * Play the element #{@link #mCurrentMusicIndex} in {@link #mFileAudioModelList}.
     */
    public void play() {
        if (STATUS_PAUSED == mCurrentStatus) {
            final int request = mAudioManager.requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);
            if (request == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                mMediaPlayer.start();
                setCurrentStatus(STATUS_PLAYING);
            } else {
                setCurrentStatus(STATUS_PAUSED);
            }
        }
    }

    /**
     * Pause the element #{@link #mCurrentMusicIndex} in {@link #mFileAudioModelList}.
     */
    public void pause() {
        if (STATUS_PLAYING == mCurrentStatus) {
            mMediaPlayer.pause();
            setCurrentStatus(STATUS_PAUSED);
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
        return mCurrentStatus == STATUS_PLAYING;
    }

    public void startMusic(final int currentMusicIndex, List<FileAudioModel> musics) {
        mCurrentMusicIndex = currentMusicIndex;
        mFileAudioModelList.clear();
        mFileAudioModelList.addAll(musics);
        final FileAudioModel currentMusic = mFileAudioModelList.get(mCurrentMusicIndex);

        if (mCurrentMusic == null || !currentMusic.getPath().equals(mCurrentMusic.getPath())) {
            prepare(currentMusic);
        } else if (mCurrentStatus == STATUS_PAUSED) {
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
        if (STATUS_PREPARING == mCurrentStatus) {
            mMediaPlayer.reset();
            mCurrentMusic = null;
            setCurrentStatus(STATUS_PAUSED);
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
        if (STATUS_PREPARING == mCurrentStatus) {
            return;
        }

        mPreparingMusic = fileAudioModel;
        setCurrentStatus(STATUS_PREPARING);

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();

        try {
            mMediaPlayer.setDataSource(fileAudioModel.getPath());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            mMediaPlayer.reset();
            setCurrentStatus(STATUS_PAUSED);
        }
    }

    private void setCurrentStatus(int currentStatus) {
        mCurrentStatus = currentStatus;
        setNotification(currentStatus == STATUS_PLAYING);

        sendToast(mAppContext, currentStatus, mFileAudioModelList.get(mCurrentMusicIndex));

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
                client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
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

    private void sendToast(Context context, int currentStatus, final FileAudioModel fileAudioModel) {
        Preconditions.checkNotNull(context);
        Preconditions.checkNotNull(fileAudioModel);
        final JSONObject jsonObject = new JSONObject();
        try {
            final JSONObject file = new JSONObject();
            file.put("id", fileAudioModel.getId());
            file.put("album", fileAudioModel.getAlbum());
            file.put("artist", fileAudioModel.getArtist());
            jsonObject.put("file", file);
            jsonObject.put("audio_status", currentStatus);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        final GoogleApiClient client = getGoogleApiClient(context);
        if (mWatchNodeId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    client.blockingConnect(CONNECTION_TIME_OUT_MS, TimeUnit.MILLISECONDS);
                    Wearable.MessageApi.sendMessage(client, mWatchNodeId, jsonObject.toString(), null);
                    client.disconnect();
                }
            }).start();
        }
    }


    /* INNER */

    private class UpdaterPosition implements Runnable {
        @Override
        public void run() {
            updatePosition();
        }
    }

    public interface OnPlayerStatusChangeListener {
        void onPlayerStatusChanged(@Status int status);

        void onPlayerProgressChanged(int progress, int duration, int musicPosition, FileAudioModel music);
    }
}

