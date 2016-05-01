package com.mercandalli.android.apps.files.file.audio.playlist;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 */
/* package */
class AudioPlayListManagerImpl implements AudioPlayListManager {

    @NonNull
    private final List<AudioPlayList> mAudioPlayLists = new ArrayList<>();

    @NonNull
    protected final List<GetPlayListsListener> mGetPlayListsListeners = new ArrayList<>();

    @NonNull
    private final Handler mUiHandler;
    @NonNull
    private final Thread mUiThread;

    public AudioPlayListManagerImpl(@NonNull final Application application) {
        final Looper mainLooper = Looper.getMainLooper();
        mUiHandler = new Handler(mainLooper);
        mUiThread = mainLooper.getThread();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getPlayLists() {
        // TODO
        notifyGetPlayListsListenerSucceeded();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addGetPlayListsListener(@Nullable final GetPlayListsListener getPlayListsListener) {
        synchronized (mGetPlayListsListeners) {
            //noinspection SimplifiableIfStatement
            if (getPlayListsListener == null ||
                    mGetPlayListsListeners.contains(getPlayListsListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetPlayListsListeners.add(getPlayListsListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeGetPlayListsListener(final GetPlayListsListener getPlayListsListener) {
        synchronized (mGetPlayListsListeners) {
            return mGetPlayListsListeners.remove(getPlayListsListener);
        }
    }

    private void notifyGetPlayListsListenerSucceeded() {
        if (mUiThread != Thread.currentThread()) {
            mUiHandler.post(new Runnable() {
                @Override
                public void run() {
                    notifyGetPlayListsListenerSucceeded();
                }
            });
            return;
        }
        synchronized (mGetPlayListsListeners) {
            final List<AudioPlayList> audioPlayLists = new ArrayList<>(mAudioPlayLists);
            for (int i = 0, size = mGetPlayListsListeners.size(); i < size; i++) {
                mGetPlayListsListeners.get(i).onGetPlayListsSucceeded(audioPlayLists);
            }
        }
    }
}
