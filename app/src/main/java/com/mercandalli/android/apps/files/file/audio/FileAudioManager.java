package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Spanned;

import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.audio.album.Album;
import com.mercandalli.android.apps.files.file.audio.artist.Artist;

import java.io.File;
import java.util.List;

/**
 * The {@link FileModel} Manager manage {@link FileModel}.
 */
public abstract class FileAudioManager {

    @Nullable
    private static FileAudioManager sInstance;

    @NonNull
    public static FileAudioManager getInstance(@NonNull final Context context) {
        if (sInstance == null) {
            sInstance = new FileAudioManagerImpl(context);
        }
        return sInstance;
    }

    /**
     * Get all the {@link FileAudioModel} in the device.
     */
    public abstract void getAllLocalMusic();

    /**
     * Get all the {@link FileAudioModel} in a folder.
     */
    public abstract void getLocalMusic(final FileModel fileModelDirectParent);

    /**
     * Get all local folders that contain music.
     */
    public abstract void getLocalMusicFolders();

    /**
     * Get all the {@link Album}s.
     * <p/>
     * Call {@link #addGetAllLocalMusicAlbumsListener(GetAllLocalMusicAlbumsListener)} and
     * {@link #removeGetAllLocalMusicAlbumsListener(GetAllLocalMusicAlbumsListener)} to get the result.
     */
    public abstract void getAllLocalMusicAlbums();

    /**
     * Get all the {@link Artist}s.
     * <p/>
     * Call {@link #addGetAllLocalMusicArtistsListener(GetAllLocalMusicArtistsListener)} and
     * {@link #removeGetAllLocalMusicArtistsListener(GetAllLocalMusicArtistsListener)} to get the result.
     */
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

    /**
     * Clear all the cache.
     */
    public abstract void clearCache();

    //region Register/Unregister.

    public abstract boolean addGetAllLocalMusicListener(GetAllLocalMusicListener getAllLocalMusicListener);

    public abstract boolean removeGetAllLocalMusicListener(GetAllLocalMusicListener getAllLocalMusicListener);

    public abstract boolean addGetLocalMusicFoldersListener(GetLocalMusicFoldersListener getLocalImageFoldersListener);

    public abstract boolean removeGetLocalMusicFoldersListener(GetLocalMusicFoldersListener getLocalImageFoldersListener);

    public abstract boolean addGetLocalMusicListener(GetLocalMusicListener getLocalImageListener);

    public abstract boolean removeGetLocalMusicListener(GetLocalMusicListener getLocalImageListener);

    public abstract boolean addMusicChangeListener(MusicsChangeListener musicsChangeListener);

    public abstract boolean removeMusicChangeListener(MusicsChangeListener musicsChangeListener);

    public abstract boolean addGetAllLocalMusicArtistsListener(GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener);

    public abstract boolean removeGetAllLocalMusicArtistsListener(GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener);

    public abstract boolean addGetAllLocalMusicAlbumsListener(GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener);

    public abstract boolean removeGetAllLocalMusicAlbumsListener(GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener);

    //endregion Register/Unregister.

    /**
     * Class used to count.
     * See {@link #getLocalMusicFolders()}.
     * http://stackoverflow.com/questions/81346/most-efficient-way-to-increment-a-map-value-in-java
     * Used to count with a map.
     */
    public class MutableInt {
        int value = 1; // note that we start at 1 since we're counting

        public void increment() {
            ++value;
        }
    }

    //region Interface Listener.

    public interface GetAllLocalMusicListener {

        /**
         * Called when the call of {@link #getAllLocalMusic()} succeeded.
         *
         * @param fileModels the {@link List} of result.
         */
        void onAllLocalMusicSucceeded(List<FileAudioModel> fileModels);

        void onAllLocalMusicFailed();
    }

    public interface GetLocalMusicFoldersListener {

        /**
         * Called when the call of {@link #getLocalMusicFolders()} succeeded.
         *
         * @param fileModels the {@link List} of result.
         */
        void onLocalMusicFoldersSucceeded(List<FileModel> fileModels);

        void onLocalMusicFoldersFailed();
    }

    public interface GetLocalMusicListener {

        /**
         * Called when the call of {@link #getLocalMusic(FileModel)} succeeded.
         *
         * @param fileModels the {@link List} of result.
         */
        void onLocalMusicSucceeded(List<FileAudioModel> fileModels);

        void onLocalMusicFailed();
    }

    public interface GetAllLocalMusicArtistsListener {

        /**
         * Called when the call of {@link #getAllLocalMusicArtists()} succeeded.
         *
         * @param artists the {@link List} of result.
         */
        void onAllLocalMusicArtistsSucceeded(List<Artist> artists);

        void onAllLocalMusicArtistsFailed();
    }

    public interface GetAllLocalMusicAlbumsListener {

        /**
         * Called when the call of {@link #getAllLocalMusicAlbums()} succeeded.
         *
         * @param albums the {@link List} of result.
         */
        void onAllLocalMusicAlbumsSucceeded(List<Album> albums);

        void onAllLocalMusicAlbumsFailed();
    }

    public interface MusicsChangeListener {

        /**
         * At least one music on the device change.
         */
        void onMusicsContentChange();
    }

    //endregion Interface Listener.
}
