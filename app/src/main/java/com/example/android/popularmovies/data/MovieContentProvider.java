package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by Juraj on 2/21/2018.
 */

public class MovieContentProvider extends ContentProvider {

    public static final int MOVIES = 100;
    public static final int MOVIE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_VIDEOS, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_VIDEOS + "/#", MOVIE_WITH_ID);
        return uriMatcher;
    }

    private MovieDbHelper mMovieDbHelper;

    @Override
    public boolean onCreate() {
        mMovieDbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase sqLiteDatabase = mMovieDbHelper.getReadableDatabase();
        Cursor returnCursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES: {
                returnCursor = sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME, projection,selection, selectionArgs, null, null, sortOrder );
                break;
            }
            case MOVIE_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                String mSelection = "_id";
                String [] mSelectionArgs = { id } ;
                returnCursor = sqLiteDatabase.query(MovieContract.MovieEntry.TABLE_NAME, projection,mSelection, mSelectionArgs, null, null, sortOrder );
            }
            default: {
                throw new UnsupportedOperationException("Unknown URI: " + uri);
            }
        }
        returnCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase sqLiteDatabase = mMovieDbHelper.getWritableDatabase();
        Uri returnUri;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES: {
                long id = sqLiteDatabase.insert(MovieContract.MovieEntry.TABLE_NAME, null, contentValues);
                if( id != -1) {
                    returnUri = ContentUris.withAppendedId(MovieContract.MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            }
            default: {
                throw new UnsupportedOperationException("Unknown URI: " + uri);
            }
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase sqLiteDatabase = mMovieDbHelper.getWritableDatabase();
        int deleted;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case MOVIES: {
                deleted = sqLiteDatabase.delete(MovieContract.MovieEntry.TABLE_NAME, null, null);
                break;
            }
            case MOVIE_WITH_ID: {
                String id = uri.getPathSegments().get(1);
                String [] mSelectionArgs = { id } ;
                deleted = sqLiteDatabase.delete(MovieContract.MovieEntry.TABLE_NAME, "_id = ?", mSelectionArgs);
            }
            default: {
                throw new UnsupportedOperationException("Unknown URI: " + uri);
            }
        }
        if( deleted != 0 ) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return deleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }
}
