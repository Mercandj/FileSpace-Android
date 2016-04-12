package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;
import android.text.Spanned;

import com.mercandalli.android.apps.files.file.FileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link FileModel} Manager manage {@link FileModel}.
 * This class manage the listeners.
 */
/* package */
abstract class FileAudioManagerNotifier implements FileAudioManager {

    /* Listeners */
    protected final List<GetAllLocalMusicListener> mGetAllLocalMusicListeners = new ArrayList<>();
    protected final List<GetAllLocalMusicArtistsListener> mGetAllLocalMusicArtistsListeners = new ArrayList<>();
    protected final List<GetAllLocalMusicAlbumsListener> mGetAllLocalMusicAlbumsListeners = new ArrayList<>();
    protected final List<GetLocalMusicFoldersListener> mGetLocalMusicFoldersListeners = new ArrayList<>();
    protected final List<GetLocalMusicListener> mGetLocalMusicListeners = new ArrayList<>();
    protected final List<MusicsChangeListener> mMusicsChangeListeners = new ArrayList<>();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void getAllLocalMusic();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void getLocalMusic(final FileModel fileModelDirectParent);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void getLocalMusicFolders();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void getAllLocalMusicAlbums();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void getAllLocalMusicArtists();

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract boolean setFileAudioMetaData(
            final File fileAudio,
            final String newTitle,
            final String newArtist,
            final String newAlbum);

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unused")
    public abstract boolean setFileAudioMetaData(
            final FileAudioModel fileAudio,
            final String newTitle,
            final String newArtist,
            final String newAlbum);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract Spanned toSpanned(
            final Context context,
            final FileAudioModel fileAudioModel);

    /**
     * {@inheritDoc}
     */
    @Override
    public abstract void clearCache();

    //region Register/Unregister.

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addGetAllLocalMusicListener(final GetAllLocalMusicListener getAllLocalMusicListener) {
        synchronized (mGetAllLocalMusicListeners) {
            //noinspection SimplifiableIfStatement
            if (getAllLocalMusicListener == null || mGetAllLocalMusicListeners.contains(getAllLocalMusicListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetAllLocalMusicListeners.add(getAllLocalMusicListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeGetAllLocalMusicListener(final GetAllLocalMusicListener getAllLocalMusicListener) {
        synchronized (mGetAllLocalMusicListeners) {
            return mGetAllLocalMusicListeners.remove(getAllLocalMusicListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addGetLocalMusicFoldersListener(final GetLocalMusicFoldersListener getLocalImageFoldersListener) {
        synchronized (mGetLocalMusicFoldersListeners) {
            //noinspection SimplifiableIfStatement
            if (getLocalImageFoldersListener == null || mGetLocalMusicFoldersListeners.contains(getLocalImageFoldersListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetLocalMusicFoldersListeners.add(getLocalImageFoldersListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeGetLocalMusicFoldersListener(final GetLocalMusicFoldersListener getLocalImageFoldersListener) {
        synchronized (mGetLocalMusicFoldersListeners) {
            return mGetLocalMusicFoldersListeners.remove(getLocalImageFoldersListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addGetLocalMusicListener(final GetLocalMusicListener getLocalImageListener) {
        synchronized (mGetLocalMusicListeners) {
            //noinspection SimplifiableIfStatement
            if (getLocalImageListener == null || mGetLocalMusicListeners.contains(getLocalImageListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetLocalMusicListeners.add(getLocalImageListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeGetLocalMusicListener(final GetLocalMusicListener getLocalImageListener) {
        synchronized (mGetLocalMusicListeners) {
            return mGetLocalMusicListeners.remove(getLocalImageListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addMusicChangeListener(final MusicsChangeListener musicsChangeListener) {
        synchronized (mMusicsChangeListeners) {
            //noinspection SimplifiableIfStatement
            if (musicsChangeListener == null || mMusicsChangeListeners.contains(musicsChangeListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mMusicsChangeListeners.add(musicsChangeListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeMusicChangeListener(final MusicsChangeListener musicsChangeListener) {
        synchronized (mMusicsChangeListeners) {
            return mMusicsChangeListeners.remove(musicsChangeListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addGetAllLocalMusicArtistsListener(final GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener) {
        synchronized (mGetAllLocalMusicArtistsListeners) {
            //noinspection SimplifiableIfStatement
            if (getAllLocalMusicArtistsListener == null || mGetAllLocalMusicArtistsListeners.contains(getAllLocalMusicArtistsListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetAllLocalMusicArtistsListeners.add(getAllLocalMusicArtistsListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeGetAllLocalMusicArtistsListener(final GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener) {
        synchronized (mGetAllLocalMusicArtistsListeners) {
            return mGetAllLocalMusicArtistsListeners.remove(getAllLocalMusicArtistsListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean addGetAllLocalMusicAlbumsListener(final GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener) {
        synchronized (mGetAllLocalMusicAlbumsListeners) {
            //noinspection SimplifiableIfStatement
            if (getAllLocalMusicAlbumsListener == null || mGetAllLocalMusicAlbumsListeners.contains(getAllLocalMusicAlbumsListener)) {
                // We don't allow to register null listener
                // And a listener can only be added once.
                return false;
            }
            return mGetAllLocalMusicAlbumsListeners.add(getAllLocalMusicAlbumsListener);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean removeGetAllLocalMusicAlbumsListener(final GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener) {
        synchronized (mGetAllLocalMusicAlbumsListeners) {
            return mGetAllLocalMusicAlbumsListeners.remove(getAllLocalMusicAlbumsListener);
        }
    }

    //endregion Register/Unregister.
}
