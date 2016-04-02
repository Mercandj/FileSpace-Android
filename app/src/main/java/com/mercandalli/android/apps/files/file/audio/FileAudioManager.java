package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;
import android.text.Spanned;

import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.audio.album.Album;
import com.mercandalli.android.apps.files.file.audio.artist.Artist;

import java.io.File;
import java.util.List;

/**
 * The {@link FileModel} Manager manage {@link FileModel}.
 */
public interface FileAudioManager {

    /**
     * Get all the {@link FileAudioModel} in the device.
     */
    void getAllLocalMusic();

    /**
     * Get all the {@link FileAudioModel} in a folder.
     */
    void getLocalMusic(final FileModel fileModelDirectParent);

    /**
     * Get all local folders that contain music.
     */
    void getLocalMusicFolders();

    void getAllLocalMusicAlbums();

    void getAllLocalMusicArtists();

    /**
     * Edit the metadata.
     */
    boolean setFileAudioMetaData(
            final File fileAudio,
            final String newTitle,
            final String newArtist,
            final String newAlbum);

    /**
     * Edit the metadata.
     */
    @SuppressWarnings("unused")
    boolean setFileAudioMetaData(
            final FileAudioModel fileAudio,
            final String newTitle,
            final String newArtist,
            final String newAlbum);

    /**
     * Get the {@link FileAudioModel} overview.
     */
    Spanned toSpanned(
            final Context context,
            final FileAudioModel fileAudioModel);

    /**
     * Clear all the cache.
     */
    void clearCache();

    //region Register/Unregister.

    boolean registerAllLocalMusicListener(GetAllLocalMusicListener getAllLocalMusicListener);

    boolean unregisterAllLocalMusicListener(GetAllLocalMusicListener getAllLocalMusicListener);

    boolean registerLocalMusicFoldersListener(GetLocalMusicFoldersListener getLocalImageFoldersListener);

    boolean unregisterLocalMusicFoldersListener(GetLocalMusicFoldersListener getLocalImageFoldersListener);

    boolean registerLocalMusicListener(GetLocalMusicListener getLocalImageListener);

    boolean unregisterLocalMusicListener(GetLocalMusicListener getLocalImageListener);

    boolean registerOnMusicUpdateListener(MusicsChangeListener musicsChangeListener);

    boolean unregisterOnMusicUpdateListener(MusicsChangeListener musicsChangeListener);

    boolean registerAllLocalMusicArtistsListener(GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener);

    boolean unregisterAllLocalMusicArtistsListener(GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener);

    boolean registerAllLocalMusicAlbumsListener(GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener);

    boolean unregisterAllLocalMusicAlbumsListener(GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener);

    //endregion Register/Unregister.

    /**
     * Class used to count.
     * See {@link #getLocalMusicFolders()}.
     * http://stackoverflow.com/questions/81346/most-efficient-way-to-increment-a-map-value-in-java
     * Used to count with a map.
     */
    class MutableInt {
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
