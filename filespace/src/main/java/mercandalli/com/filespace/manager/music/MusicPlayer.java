package mercandalli.com.filespace.manager.music;

import android.app.Application;
import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import mercandalli.com.filespace.model.file.FileMusicModel;

public class MusicPlayer implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener, AudioManager.OnAudioFocusChangeListener {

    int STATUS_PAUSED = 0;
    int STATUS_PLAYING = 1;
    int STATUS_PREPARING = 3;

    private int mCurrentStatus;

    private FileMusicModel mCurrentMusic;
    private FileMusicModel mPreparingMusic;
    private final List<FileMusicModel> mFileMusicModelList = new ArrayList<>();
    private int mCurrentMusicIndex;

    private MediaPlayer mMediaPlayer;
    private final Context mAppContext;
    private final AudioManager mAudioManager;


    public boolean isPlaying() {
        return mCurrentStatus == STATUS_PLAYING;
    }

    private final Handler mHandler = new Handler();

    private void updatePosition() {
        mHandler.removeCallbacks(updatePositionRunnable);
        if (isPlaying()) {
            synchronized (mOnPlayerStatusChangeListeners) {
                for (int i = 0, size = mOnPlayerStatusChangeListeners.size(); i < size; i++) {
                    mOnPlayerStatusChangeListeners.get(i).onPlayerProgressChanged(getCurrentProgress(), getPreviewDuration(), mCurrentMusicIndex, mCurrentMusic);
                }
            }
        }
        mHandler.postDelayed(updatePositionRunnable, 1000);
    }

    private class UpdaterPosition implements Runnable {
        @Override
        public void run() {
            updatePosition();
        }
    }

    private UpdaterPosition updatePositionRunnable = new UpdaterPosition();


    public interface OnPlayerStatusChangeListener {
        void onPlayerStatusChanged(int status);

        void onPlayerProgressChanged(int progress, int duration, int musicPosition, FileMusicModel music);
    }


    private final List<OnPlayerStatusChangeListener> mOnPlayerStatusChangeListeners;

    public MusicPlayer(Application application) {
        mAppContext = application.getApplicationContext();
        mOnPlayerStatusChangeListeners = new ArrayList<>();
        mMediaPlayer = new MediaPlayer();
        mMediaPlayer.setOnPreparedListener(this);
        mMediaPlayer.setOnCompletionListener(this);
        mCurrentStatus = STATUS_PAUSED;
        mAudioManager = (AudioManager) mAppContext.getSystemService(Context.AUDIO_SERVICE);
        updatePosition();
    }

    public void startMusic(final int currentMusicIndex, List<FileMusicModel> musics) {
        mCurrentMusicIndex = currentMusicIndex;
        mFileMusicModelList.clear();
        mFileMusicModelList.addAll(musics);
        final FileMusicModel currentMusic = mFileMusicModelList.get(mCurrentMusicIndex);

        if (mCurrentMusic == null || !currentMusic.getPath().equals(mCurrentMusic.getPath())) {
            prepare(currentMusic);
        } else if (mCurrentStatus == STATUS_PAUSED) {
            play();
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

    public FileMusicModel getCurrentPreview() {
        return mCurrentMusic;
    }

    public int getPreviewDuration() {
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

    public void next() {
        mCurrentMusicIndex++;
        if (mCurrentMusicIndex >= mFileMusicModelList.size()) {
            mCurrentMusicIndex = 0;
        }

        final FileMusicModel currentMusic = mFileMusicModelList.get(mCurrentMusicIndex);
        if (mCurrentMusic == null || !currentMusic.getPath().equals(mCurrentMusic.getPath())) {
            prepare(currentMusic);
        }
    }

    public void previous() {
        mCurrentMusicIndex--;
        if (mCurrentMusicIndex < 0) {
            mCurrentMusicIndex = mFileMusicModelList.size() - 1;
        }

        final FileMusicModel currentMusic = mFileMusicModelList.get(mCurrentMusicIndex);
        if (mCurrentMusic == null || !currentMusic.getPath().equals(mCurrentMusic.getPath())) {
            prepare(currentMusic);
        }
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
        if (mCurrentStatus == STATUS_PLAYING) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS
                    || focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                pause();
            }
        }
    }


    private void prepare(@NonNull FileMusicModel preview) {
        if (STATUS_PREPARING == mCurrentStatus) {
            return;
        }

        mPreparingMusic = preview;
        setCurrentStatus(STATUS_PREPARING);

        if (mMediaPlayer.isPlaying()) {
            mMediaPlayer.stop();
        }
        mMediaPlayer.reset();

        try {
            mMediaPlayer.setDataSource(preview.getPath());
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            mMediaPlayer.reset();
            setCurrentStatus(STATUS_PAUSED);
        }
    }

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

    public void pause() {
        if (STATUS_PLAYING == mCurrentStatus) {
            mMediaPlayer.pause();
            setCurrentStatus(STATUS_PAUSED);
        }
        mAudioManager.abandonAudioFocus(this);
    }

    private void setCurrentStatus(int currentStatus) {
        mCurrentStatus = currentStatus;
        synchronized (mOnPlayerStatusChangeListeners) {
            for (int i = 0, size = mOnPlayerStatusChangeListeners.size(); i < size; i++) {
                mOnPlayerStatusChangeListeners.get(i).onPlayerStatusChanged(mCurrentStatus);
            }
        }
    }

}

