package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;
import android.text.Spanned;

import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.audio.album.Album;
import com.mercandalli.android.apps.files.file.audio.artist.Artist;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * The {@link FileModel} Manager abstract class.
 */
public abstract class FileAudioManager {

    /* Listeners */
    protected final List<GetAllLocalMusicListener> mGetAllLocalMusicListeners = new ArrayList<>();
    protected final List<GetAllLocalMusicArtistsListener> mGetAllLocalMusicArtistsListeners = new ArrayList<>();
    protected final List<GetAllLocalMusicAlbumsListener> mGetAllLocalMusicAlbumsListeners = new ArrayList<>();
    protected final List<GetLocalMusicFoldersListener> mGetLocalMusicFoldersListeners = new ArrayList<>();
    protected final List<GetLocalMusicListener> mGetLocalMusicListeners = new ArrayList<>();
    protected final List<MusicsChangeListener> mMusicsChangeListeners = new ArrayList<>();

    /**
     * Get all the {@link FileAudioModel} in the device.
     */
    public abstract void getAllLocalMusic();

    /**
     * Get all the {@link FileAudioModel} in a folder.
     */
    public abstract void getLocalMusic(
            final FileModel fileModelDirectParent);

    /**
     * Get all local folders that contain music.
     */
    public abstract void getLocalMusicFolders();

    public abstract void getAllLocalMusicAlbums();

    public abstract void getAllLocalMusicArtists();

    /**
     * Edit the metadata.
     */
    public abstract boolean setFileAudioMetaData(
            final File fileAudio,
            final String newTitle,
            final String newArtist,
            final String newAlbum);

    /**
     * Edit the metadata.
     */
    @SuppressWarnings("unused")
    public abstract boolean setFileAudioMetaData(
            final FileAudioModel fileAudio,
            final String newTitle,
            final String newArtist,
            final String newAlbum);

    /**
     * Get the {@link FileAudioModel} overview.
     */
    public abstract Spanned toSpanned(
            final Context context,
            final FileAudioModel fileAudioModel);


    //region Register/Unregister.

    public boolean registerAllLocalMusicListener(GetAllLocalMusicListener getAllLocalMusicListener) {
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

    public boolean unregisterAllLocalMusicListener(GetAllLocalMusicListener getAllLocalMusicListener) {
        synchronized (mGetAllLocalMusicListeners) {
            return mGetAllLocalMusicListeners.remove(getAllLocalMusicListener);
        }
    }

    public boolean registerLocalMusicFoldersListener(GetLocalMusicFoldersListener getLocalImageFoldersListener) {
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

    public boolean unregisterLocalMusicFoldersListener(GetLocalMusicFoldersListener getLocalImageFoldersListener) {
        synchronized (mGetLocalMusicFoldersListeners) {
            return mGetLocalMusicFoldersListeners.remove(getLocalImageFoldersListener);
        }
    }

    public boolean registerLocalMusicListener(GetLocalMusicListener getLocalImageListener) {
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

    public boolean unregisterLocalMusicListener(GetLocalMusicListener getLocalImageListener) {
        synchronized (mGetLocalMusicListeners) {
            return mGetLocalMusicListeners.remove(getLocalImageListener);
        }
    }

    public boolean registerOnMusicUpdateListener(MusicsChangeListener musicsChangeListener) {
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

    public boolean unregisterOnMusicUpdateListener(MusicsChangeListener musicsChangeListener) {
        synchronized (mMusicsChangeListeners) {
            return mMusicsChangeListeners.remove(musicsChangeListener);
        }
    }

    public boolean registerAllLocalMusicArtistsListener(GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener) {
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

    public boolean unregisterAllLocalMusicArtistsListener(GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener) {
        synchronized (mGetAllLocalMusicArtistsListeners) {
            return mGetAllLocalMusicArtistsListeners.remove(getAllLocalMusicArtistsListener);
        }
    }

    public boolean registerAllLocalMusicAlbumsListener(GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener) {
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

    public boolean unregisterAllLocalMusicAlbumsListener(GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener) {
        synchronized (mGetAllLocalMusicAlbumsListeners) {
            return mGetAllLocalMusicAlbumsListeners.remove(getAllLocalMusicAlbumsListener);
        }
    }

    //endregion Register/Unregister.

    /**
     * Class used to count.
     * See {@link #getLocalMusicFolders()}.
     * http://stackoverflow.com/questions/81346/most-efficient-way-to-increment-a-map-value-in-java
     * Used to count with a map.
     */
    protected class MutableInt {
        int value = 1; // note that we start at 1 since we're counting

        public void increment() {
            ++value;
        }
    }

    //region Interface Listener.

    interface GetAllLocalMusicListener {

        /**
         * Called when the call of {@link #getAllLocalMusic()} succeeded.
         *
         * @param fileModels the {@link List} of result.
         */
        void onAllLocalMusicSucceeded(List<FileAudioModel> fileModels);

        void onAllLocalMusicFailed();
    }

    interface GetLocalMusicFoldersListener {

        /**
         * Called when the call of {@link #getLocalMusicFolders()} succeeded.
         *
         * @param fileModels the {@link List} of result.
         */
        void onLocalMusicFoldersSucceeded(List<FileModel> fileModels);

        void onLocalMusicFoldersFailed();
    }

    interface GetLocalMusicListener {

        /**
         * Called when the call of {@link #getLocalMusic(FileModel)} succeeded.
         *
         * @param fileModels the {@link List} of result.
         */
        void onLocalMusicSucceeded(List<FileAudioModel> fileModels);

        void onLocalMusicFailed();
    }

    interface GetAllLocalMusicArtistsListener {

        /**
         * Called when the call of {@link #getAllLocalMusicArtists()} succeeded.
         *
         * @param artists the {@link List} of result.
         */
        void onAllLocalMusicArtistsSucceeded(List<Artist> artists);

        void onAllLocalMusicArtistsFailed();
    }

    interface GetAllLocalMusicAlbumsListener {

        /**
         * Called when the call of {@link #getAllLocalMusicAlbums()} succeeded.
         *
         * @param albums the {@link List} of result.
         */
        void onAllLocalMusicAlbumsSucceeded(List<Album> albums);

        void onAllLocalMusicAlbumsFailed();
    }

    interface MusicsChangeListener {

        /**
         * At least one music on the device change.
         */
        void onMusicsContentChange();
    }

    //endregion Interface Listener.
}
