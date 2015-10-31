package mercandalli.com.filespace.service;

import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.model.file.FileMusicModel;

/**
 * See http://code.tutsplus.com/tutorials/create-a-music-player-on-android-song-playback--mobile-22778
 */
public class AudioService extends Service implements
        MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener,
        MediaPlayer.OnCompletionListener {

    private MediaPlayer mMediaPlayer;
    private final List<FileMusicModel> mFileMusicModelList = new ArrayList<>();
    private int mPlayingIndex;

    private final Handler mHandler = new Handler();

    private final IBinder mMusicBinder = new MusicBinder();

    private boolean mIsPrepared = false;

    @Override
    public IBinder onBind(Intent arg0) {
        return mMusicBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        updatePositionRunnable.kill();
        mMediaPlayer.stop();
        mMediaPlayer.release();
        return false;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mPlayingIndex = 0;
        mMediaPlayer = new MediaPlayer();

        initMusicPlayer();
    }

    private void initMusicPlayer() {
        mMediaPlayer.setWakeMode(getApplicationContext(),
                PowerManager.PARTIAL_WAKE_LOCK);
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mMediaPlayer.setOnErrorListener(this);
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        next();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        return false;
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        mIsPrepared = true;
        updateView();
    }

    public void setList(List<FileMusicModel> fileMusicModelList) {
        mFileMusicModelList.clear();
        mFileMusicModelList.addAll(fileMusicModelList);
    }

    public class MusicBinder extends Binder {
        public AudioService getService() {
            return AudioService.this;
        }
    }

    public void setPlayingIndex(int songIndex) {
        mPlayingIndex = songIndex;
    }


    /**
     * Pauses playback. Call start() to resume.
     */
    public void pause() {
        mMediaPlayer.pause();
    }

    public void start() {
        mMediaPlayer.start();
    }

    public void play(final int position) {
        if (position < mFileMusicModelList.size()) {
            mPlayingIndex = position;
            play();
        }
    }

    public void play() {
        mMediaPlayer.reset();

        if (mPlayingIndex >= mFileMusicModelList.size()) {
            mPlayingIndex = 0;
        }

        final FileMusicModel fileMusicModel = mFileMusicModelList.get(mPlayingIndex);
        final Uri trackUri = Uri.parse((fileMusicModel.isOnline()) ? fileMusicModel.getOnlineUrl() : fileMusicModel.getUrl());

        try {
            mMediaPlayer.setDataSource(getApplicationContext(), trackUri);
        } catch (Exception e) {
            Log.e(AudioService.class.getName(), "Error setting data source", e);
        }

        mMediaPlayer.prepareAsync();
    }

    public void next() {
        mPlayingIndex++;
        if (mPlayingIndex >= mFileMusicModelList.size()) {
            mPlayingIndex = 0;
        }
        play();
    }

    public void previous() {
        mPlayingIndex--;
        if (mPlayingIndex < 0) {
            mPlayingIndex = mFileMusicModelList.size() - 1;
        }
        play();
    }


    private void updateView() {
        if (mIsPrepared && mViewUpdater != null) {
            mViewUpdater.updateViewAudioService(mPlayingIndex);
        }
    }

    private ViewUpdater mViewUpdater;

    public void setViewUpdater(ViewUpdater viewUpdater) {
        mViewUpdater = viewUpdater;
        updatePosition();
    }

    public interface ViewUpdater {
        void updateViewAudioService(int playingIndex);
    }

    private void updatePosition() {
        mHandler.removeCallbacks(updatePositionRunnable);
        updateView();
        this.mHandler.postDelayed(updatePositionRunnable, 1000);
    }

    private UpdaterPosition updatePositionRunnable = new UpdaterPosition();

    private class UpdaterPosition implements Runnable {
        boolean kill = false;

        @Override
        public void run() {
            if (!kill) {
                updatePosition();
            }
        }

        public void kill() {
            kill = true;
        }
    }


    /*
    Media Player data
     */

    /**
     * Seeks to specified time position.
     *
     * @param msec the offset in milliseconds from the start to seek to
     * @throws IllegalStateException if the internal player engine has not been
     *                               initialized
     */
    public void seekTo(int msec) {
        mMediaPlayer.seekTo(msec);
    }

    /**
     * Checks whether the MediaPlayer is playing.
     *
     * @return true if currently playing, false otherwise
     * @throws IllegalStateException if the internal player engine has not been
     *                               initialized or has been released.
     */
    public boolean isPlaying() {
        return mMediaPlayer.isPlaying();
    }

    /**
     * Gets the current playback position.
     *
     * @return the current position in milliseconds
     */
    public int getCurrentPosition() {
        return mMediaPlayer.getCurrentPosition();
    }

    /**
     * Gets the duration of the file.
     *
     * @return the duration in milliseconds, if no duration is available
     * (for example, if streaming live content), -1 is returned.
     */
    public int getDuration() {
        return mMediaPlayer.getDuration();
    }
}