package com.mercandalli.android.apps.files.file.audio.playlist;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Jonathan on 02/05/2016.
 */
public class AudioPlayListDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "audioplaylist.db";
    private static final int DATABASE_VERSION = 1;

    public static final String COLUMN_PLAYLIST_ID = "id";
    public static final String COLUMN_PLAYLIST_NAME = "name";

    private static final String TABLE_PLAYLIST_CREATE = "create table " + AudioPlayListDb.TABLE_NAME + "("
            + COLUMN_PLAYLIST_ID + " integer primary key autoincrement not null,"
            + COLUMN_PLAYLIST_NAME + " text"
            + ");";

    public AudioPlayListDbHelper(final Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(final SQLiteDatabase db) {
        db.execSQL(TABLE_PLAYLIST_CREATE);
    }

    @Override
    public void onUpgrade(final SQLiteDatabase db, final int oldVersion, final int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + AudioPlayListDb.TABLE_NAME);
        onCreate(db);
    }
}
