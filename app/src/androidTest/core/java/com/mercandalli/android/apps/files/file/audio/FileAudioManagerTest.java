package com.mercandalli.android.apps.files.file.audio;

import android.content.Context;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.contrib.CountingIdlingResource;
import android.text.Spanned;
import android.util.Log;

import com.mercandalli.android.apps.files.file.FileModel;
import com.mercandalli.android.apps.files.file.local.provider.FileLocalProviderManager;

import java.io.File;
import java.util.List;

public class FileAudioManagerTest implements FileAudioManager {

    private static final String TAG = "FileAudioManagerTest";

    private FileAudioManager mFileAudioManager;
    private final CountingIdlingResource mLocalMusicFoldersCountingIdlingResource;
    private final CountingIdlingResource mAllLocalCountingIdlingResource;

    public FileAudioManagerTest(final Context contextApp, final FileLocalProviderManager fileLocalProviderManager) {
        mFileAudioManager = new FileAudioManagerImpl(contextApp, fileLocalProviderManager);

        mLocalMusicFoldersCountingIdlingResource =
                new CountingIdlingResource(TAG + "#getLocalMusicFolders", true);
        Espresso.registerIdlingResources(mLocalMusicFoldersCountingIdlingResource);
        mFileAudioManager.registerLocalMusicFoldersListener(new GetLocalMusicFoldersListener() {
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
        registerAllLocalMusicListener(new GetAllLocalMusicListener() {
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
    public void getLocalMusic(FileModel fileModelDirectParent) {
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
    public boolean setFileAudioMetaData(File fileAudio, String newTitle, String newArtist, String newAlbum) {
        return mFileAudioManager.setFileAudioMetaData(fileAudio, newTitle, newArtist, newAlbum);
    }

    @Override
    public boolean setFileAudioMetaData(FileAudioModel fileAudio, String newTitle, String newArtist, String newAlbum) {
        return mFileAudioManager.setFileAudioMetaData(fileAudio, newTitle, newArtist, newAlbum);
    }

    @Override
    public Spanned toSpanned(Context context, FileAudioModel fileAudioModel) {
        return mFileAudioManager.toSpanned(context, fileAudioModel);
    }

    @Override
    public void clearCache() {
        mFileAudioManager.clearCache();
    }

    @Override
    public boolean registerAllLocalMusicListener(GetAllLocalMusicListener getAllLocalMusicListener) {
        return mFileAudioManager.registerAllLocalMusicListener(getAllLocalMusicListener);
    }

    @Override
    public boolean unregisterAllLocalMusicListener(GetAllLocalMusicListener getAllLocalMusicListener) {
        return mFileAudioManager.unregisterAllLocalMusicListener(getAllLocalMusicListener);
    }

    @Override
    public boolean registerLocalMusicFoldersListener(GetLocalMusicFoldersListener getLocalImageFoldersListener) {
        return mFileAudioManager.registerLocalMusicFoldersListener(getLocalImageFoldersListener);
    }

    @Override
    public boolean unregisterLocalMusicFoldersListener(GetLocalMusicFoldersListener getLocalImageFoldersListener) {
        return mFileAudioManager.unregisterLocalMusicFoldersListener(getLocalImageFoldersListener);
    }

    @Override
    public boolean registerLocalMusicListener(GetLocalMusicListener getLocalImageListener) {
        return mFileAudioManager.registerLocalMusicListener(getLocalImageListener);
    }

    @Override
    public boolean unregisterLocalMusicListener(GetLocalMusicListener getLocalImageListener) {
        return mFileAudioManager.unregisterLocalMusicListener(getLocalImageListener);
    }

    @Override
    public boolean registerOnMusicUpdateListener(MusicsChangeListener musicsChangeListener) {
        return mFileAudioManager.registerOnMusicUpdateListener(musicsChangeListener);
    }

    @Override
    public boolean unregisterOnMusicUpdateListener(MusicsChangeListener musicsChangeListener) {
        return mFileAudioManager.unregisterOnMusicUpdateListener(musicsChangeListener);
    }

    @Override
    public boolean registerAllLocalMusicArtistsListener(GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener) {
        return mFileAudioManager.registerAllLocalMusicArtistsListener(getAllLocalMusicArtistsListener);
    }

    @Override
    public boolean unregisterAllLocalMusicArtistsListener(GetAllLocalMusicArtistsListener getAllLocalMusicArtistsListener) {
        return mFileAudioManager.unregisterAllLocalMusicArtistsListener(getAllLocalMusicArtistsListener);
    }

    @Override
    public boolean registerAllLocalMusicAlbumsListener(GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener) {
        return mFileAudioManager.registerAllLocalMusicAlbumsListener(getAllLocalMusicAlbumsListener);
    }

    @Override
    public boolean unregisterAllLocalMusicAlbumsListener(GetAllLocalMusicAlbumsListener getAllLocalMusicAlbumsListener) {
        return mFileAudioManager.unregisterAllLocalMusicAlbumsListener(getAllLocalMusicAlbumsListener);
    }
}
