package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Juraj on 2/20/2018.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movies.db";
    public static final int DATABASE_VERSION = 1;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME
                + " (" + MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + MovieContract.MovieEntry.COLUMN_VIDEO_ID + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_VIDEO_TITLE + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_VIDEO_OVERVIEW + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_VIDEO_POSTER + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_VIDEO_RATING + " TEXT NOT NULL, "
                + MovieContract.MovieEntry.COLUMN_VIDEO_RELASE_DATE + " TEXT NOT NULL"
                + ");";

        sqLiteDatabase.execSQL(SQL_CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // sqLiteDatabase.execSQL("ALTER TABLE " +  MovieContract.MovieEntry.TABLE_NAME + " IF EXISTS " );
        // onCreate(sqLiteDatabase);
    }
}
