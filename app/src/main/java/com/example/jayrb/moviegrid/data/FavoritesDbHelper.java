package com.example.jayrb.moviegrid.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.jayrb.moviegrid.data.FavoritesContract.FavoritesEntry;

/**
 * SQLiteDatabase favoritesDb.db constructor and methods.
 * Stores movie favorites locally for online and offline access.
 */

public class FavoritesDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "favoritesDb.db";

    // If database schema is changed, incrememt database version
    private static final int VERSION = 1;

    // Constructor
    FavoritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    /*
    * Called when the favorites database is created for first time (or upgraded).
    * On upgrade, previously saved favorites are lost.
    */
    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create favorites table
        final String CREATE_TABLE = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                FavoritesEntry._ID                  + " INTEGER PRIMARY KEY, " +
                FavoritesEntry.COLUMN_IDENTIFIER    + " INTEGER UNIQUE NOT NULL, " +
                FavoritesEntry.COLUMN_TITLE         + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_OVERVIEW      + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_YEAR          + " INTEGER NOT NULL, " +
                FavoritesEntry.COLUMN_VOTE          + " DECIMAL(2,1) NOT NULL, " +
                FavoritesEntry.COLUMN_THUMB + " BLOB NOT NULL);";
        db.execSQL(CREATE_TABLE);

    }

    /**
     * This method discards the old table of data and calls onCreate to recreate a new one.
     * This only occurs when the version number for this database (DATABASE_VERSION) is incremented.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
