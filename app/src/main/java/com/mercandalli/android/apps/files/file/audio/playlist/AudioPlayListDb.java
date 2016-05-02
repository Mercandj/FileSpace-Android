package com.mercandalli.android.apps.files.file.audio.playlist;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

public class AudioPlayListDb {

    public static final String TABLE_NAME = "playlist";

    public static final int COLUMN_ID_PLAYLIST_ID = 0;
    public static final int COLUMN_ID_PLAYLIST_NAME = 1;

    private AudioPlayListDb() {
    }

    public static void createAudioPlayList(
            @NonNull final SQLiteDatabase database,
            @NonNull final AudioPlayList audioPlayList) {
        ContentValues values = new ContentValues();
        values.put(AudioPlayListDbHelper.COLUMN_PLAYLIST_NAME, audioPlayList.getName());
        database.insert(TABLE_NAME, null, values);
    }

    public static List<AudioPlayList> getAudioPlayLists(@NonNull final SQLiteDatabase database) {
        List<AudioPlayList> list = new ArrayList<>();
        Cursor cursor = database.query(TABLE_NAME, null, null, null, null, null, null);
        while (cursor.moveToNext()) {
            int id = cursor.getInt(COLUMN_ID_PLAYLIST_ID);
            String name = cursor.getString(COLUMN_ID_PLAYLIST_NAME);
            AudioPlayList event = new AudioPlayList(id, name);
            list.add(event);
        }
        cursor.close();
        return list;
    }

    public static void removeAudioPlayList(
            @NonNull final SQLiteDatabase database,
            @NonNull final AudioPlayList audioPlayList) {
        database.delete(TABLE_NAME, AudioPlayListDbHelper.COLUMN_PLAYLIST_ID + " = ?",
                new String[]{String.valueOf(audioPlayList.getId())});
    }
}
