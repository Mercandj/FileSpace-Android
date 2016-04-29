package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.idling.CountingIdlingResource;
import android.text.Spanned;
import android.util.Log;

import com.mercandalli.android.apps.files.file.FileManager;
import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.local.provider.FileLocalProviderManager;

import java.io.File;
import java.util.List;

/* package */ class FileAudioManagerTest implements FileAudioManager {

    private static final String TAG = "FileAudioManagerTest";

    private final FileAudioManager mFileAudioManager;
    private final CountingIdlingResource mLocalMusicFoldersCountingIdlingResource;
    private final CountingIdlingResource mAllLocalCountingIdlingResource;

    public FileAudioManagerTest(
            final Context contextApp,
            final FileLocalProviderManager fileLocalProviderManager,
            final FileManager fileManager) {

        mFileAudioManager = new FileAudioManagerImpl(contextApp, fileLocalProviderManager, fileManager);

        mLocalMusicFoldersCountingIdlingResource =
                new CountingIdlingResource(TAG + "#getLocalMusicFolders", true);
        Espresso.registerIdlingResources(mLocalMusicFoldersCountingIdlingResource);
        mFileAudioManager.addGetLocalMusicFoldersListener(new GetLocalMusicFoldersListener() {
            @Override
            public void onLocalMusicFoldersSucceeded(List<FileModel> fileModels) {
                mLocalMusicFoldersCountingIdlingResource.decrement();
                Log.d(TAG, "decrement succeed");
            }

            @Override
            public void onLocalMusicFoldersFailed() {
                mLocalMusicFoldersCountingIdlingResource.decrement();
                Log.d(TAG, "decrement failed");
            }
        });

        mAllLocalCountingIdlingResource =
                new CountingIdlingResource("FileAudioManagerTest#getAllLocalMusic", true);
        Espresso.registerIdlingResources(mAllLocalCountingIdlingResource);
        addGetAllLocalMusicListener(new GetAllLocalMusicListener() {
            @Override
            public void onAllLocalMusicSucceeded(List<FileAudioModel> fileModels) {
                if (!mAllLocalCountingIdlingResource.isIdleNow()) {
                    mAllLocalCountingIdlingResource.decrement();
                }
            }

            @Override
            public void onAllLocalMusicFailed() {
                if (!mAllLocalCountingIdlingResource.isIdleNow()) {
                    mAllLocalCountingIdlingResource.decrement();
                }
            }
        });
    }

    @Override
    public void getAllLocalMusic() {
        if (mAllLocalCountingIdlingResource.isIdleNow()) {
            mAllLocalCountingIdlingResource.increment();
            Log.d(TAG, "increment allLocalMusic");
        }
        mFileAudioManager.getAllLocalMusic();
    }

    @Override
    public void getLocalMusic(final FileModel fileModelDirectParent) {
        mFileAudioManager.getLocalMusic(fileModelDirectParent);
    }

    @Override
    public void getLocalMusicFolders() {
        if (mLocalMusicFoldersCountingIdlingResource.isIdleNow()) {
            mLocalMusicFoldersCountingIdlingResource.increment();
            Log.d(TAG, "increment localMusicFolders");
        }
        mFileAudioManager.getLocalMusicFolders();
    }

    @Override
    public void getAllLocalMusicAlbums() {
        mFileAudioManager.getAllLocalMusicAlbums();
    }

    @Override
    public void getAllLocalMusicArtists() {
        mFileAudioManager.getAllLocalMusicArtists();
    }

    @Override
    public boolean setFileAudioMetaData(
            final File fileAudio,
            final String newTitle,
            final String newArtist,
            final String newAlbum) {
        return mFileAudioManager.setFileAudioMetaData(fileAudio, newTitle, newArtist, newAlbum);
    }

    @Override
    public boolean setFileAudioMetaData(
            final FileAudioModel fileAudio,
            final String newTitle,
            final String newArtist,
            final String newAlbum) {
        return mFileAudioManager.setFileAudioMetaData(fileAudio, newTitle, newArtist, newAlbum);
    }

    @Override
    public Spanned toSpanned(final Context context, final FileAudioModel fileAudioModel) {
        return mFileAudioManager.toSpanned(context, fileAudioModel);
    }

    @Override
    public void clearCache() {
        mFileAudioManager.clearCache();
    }

    @Override
    public boolean addGetAllLocalMusicListener(final GetAllLocalMusicListener getAllLocalMusicListener) {
        return mFileAudioManager.addGetAllLocalMusicListener(getAllLocalMusicListener);
    }

    @Override
    public boolean removeGetAllLocalMusicListener(final GetAllLocalMusicListener getAllLocalMusicListener) {
        return mFileAudioManager.removeGetAllLocalMusicListener(getAllLocalMusicListener);
    }

    @Override
    public boolean addGetLocalMusicFoldersListener(final GetLocalMusicFoldersListener getLocalImageFoldersListener) {
        return mFileAudioManager.addGetLocalMusicFoldersListener(getLocalImageFoldersListener);
    }

    @Override
    public boolean removeGetLocalMusicFoldersListener(final GetLocalMusicFoldersListener getLocalImageFoldersListener) {
        return mFileAudioManager.removeGetLocalMusicFoldersListener(getLocalImageFoldersListener);
    }

    @Override
    public boolean addGetLocalMusicListener(final GetLocalMusicListener getLocalImageListener) {
        return mFileAudioManager.addGetLocalMusicListener(getLocalImageListener);
    }

    @Override
    public boolean removeGetLocalMusicListener(final GetLocalMusicListener getLocalImageListener) {
        return mFileAudioManager.removeGetLocalMusicListener(getLocalImageListener);
    }

    @Override
    public boolean addMusicChangeListener(final MusicsChangeListener musicsChangeListener) {
        return mFileAudioManager.addMusicChangeListener(musicsChangeListener);
    }

    @Override
    public boolean removeMusicChangeListener(final MusicsChangeListener musicsChangeListener) {
        return mFileAudioManager.removeMusicChangeListener(musicsChangeListener);
    }

    @Override
    public boolean addGetAllLocalMusicArtistsListener(final GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener) {
        return mFileAudioManager.addGetAllLocalMusicArtistsListener(getAllLocalMusicArtistsListener);
    }

    @Override
    public boolean removeGetAllLocalMusicArtistsListener(final GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener) {
        return mFileAudioManager.removeGetAllLocalMusicArtistsListener(getAllLocalMusicArtistsListener);
    }

    @Override
    public boolean addGetAllLocalMusicAlbumsListener(final GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener) {
        return mFileAudioManager.addGetAllLocalMusicAlbumsListener(getAllLocalMusicAlbumsListener);
    }

    @Override
    public boolean removeGetAllLocalMusicAlbumsListener(final GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener) {
        return mFileAudioManager.removeGetAllLocalMusicAlbumsListener(getAllLocalMusicAlbumsListener);
    }
}
