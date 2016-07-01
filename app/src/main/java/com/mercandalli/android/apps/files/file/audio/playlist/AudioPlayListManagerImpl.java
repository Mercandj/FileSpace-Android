package com.mercandalli.android.apps.files.file.audio.playlist;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 */
/* package */
class AudioPlayListManagerImpl extends AudioPlayListManager {

    @NonNull
    private final AudioPlayListDbHelper mAudioPlayListDbHelper;
    private SQLiteDatabase mDatabase;

    public AudioPlayListManagerImpl(@NonNull final Context context) {
        mAudioPlayListDbHelper = new AudioPlayListDbHelper(context.getApplicationContext());
    }

    @Override
    public void add(@NonNull final AudioPlayList audioPlayList) {
        open();
        AudioPlayListDb.createAudioPlayList(mDatabase, audioPlayList);
        close();
    }

    @NonNull
    @Override
    public List<AudioPlayList> get() {
        open();
        final List<AudioPlayList> list = new ArrayList<>();
        Cursor cursor = mDatabase.query(AudioPlayListDb.TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            final String name = cursor.getString(AudioPlayListDb.COLUMN_ID_PLAYLIST_NAME);
            list.add(new AudioPlayList(name));
        }
        cursor.close();
        close();
        return list;
    }

    @Override
    public void getPlayLists() {

    }

    @Override
    public boolean addGetPlayListsListener(final GetPlayListsListener getPlayListsListener) {
        return false;
    }

    @Override
    public boolean removeGetPlayListsListener(final GetPlayListsListener getPlayListsListener) {
        return false;
    }

    /**
     * Open the database in order to perform some operation on it.
     */
    private void open() {
        mDatabase = mAudioPlayListDbHelper.getWritableDatabase();
    }

    /**
     * Close the database.
     */
    private void close() {
        mAudioPlayListDbHelper.close();
    }

}
