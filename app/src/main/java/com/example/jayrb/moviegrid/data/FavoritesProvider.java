package com.example.jayrb.moviegrid.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;

import com.example.jayrb.moviegrid.R;

/**
 * Serves Favorites info stored in SQLiteDatabase
 */

public class FavoritesProvider extends ContentProvider {

    private static final String TAG = FavoritesProvider.class.getSimpleName();
    private static final int FAVORITES = 100;
    private static final int FAVORITE_MOVIE_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(FavoritesContract.CONTENT_AUTHORITY,
                FavoritesContract.PATH_FAVORITES, FAVORITES);
        sUriMatcher.addURI(FavoritesContract.CONTENT_AUTHORITY,
                FavoritesContract.PATH_FAVORITES + "/#", FAVORITE_MOVIE_ID);
    }

    private FavoritesDbHelper mFavoritesDbHelper;


    @Override
    public boolean onCreate() {
        mFavoritesDbHelper = new FavoritesDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection,
                        String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase database = mFavoritesDbHelper.getReadableDatabase();
        Cursor cursor;
        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIE_ID:
                selection = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
            case FAVORITES:
                cursor = database.query(FavoritesContract.FavoritesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new IllegalArgumentException(getContext()
                        .getString(R.string.unknown_uri) + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITES:
                return FavoritesContract.FavoritesEntry.CONTENT_LIST_TYPE;
            case FAVORITE_MOVIE_ID:
                return FavoritesContract.FavoritesEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException(getContext().getString(R.string.unknown_uri_type)
                        + uri
                        + getContext().getString(R.string.with_match)
                        + match);
        }
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        if (match == FAVORITES) {
            return insertFavorite(uri, values);
        } else {
            throw new IllegalArgumentException(getContext()
                    .getString(R.string.insert_not_supported) + uri);
        }
    }

    private Uri insertFavorite(Uri uri, ContentValues values) {
        Integer movieId = values.getAsInteger(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID);
        if (movieId == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.no_movie_id_error));
        }
        String title = values.getAsString(FavoritesContract.FavoritesEntry.COLUMN_TITLE);
        if (title == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.no_title_error));
        }
        String synopsis = values.getAsString(FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW);
        if (synopsis == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.no_synopsis_error));
        }
        Integer year = values.getAsInteger(FavoritesContract.FavoritesEntry.COLUMN_YEAR);
        if (year == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.no_year_error));
        }
        Integer runtime = values.getAsInteger(FavoritesContract.FavoritesEntry.COLUMN_RUNTIME);
        if (runtime == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.no_runtime_error));
        }
        Float vote = values.getAsFloat(FavoritesContract.FavoritesEntry.COLUMN_VOTE);
        if (vote == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.no_vote_error));
        }
        byte[] thumb = values.getAsByteArray(FavoritesContract.FavoritesEntry.COLUMN_THUMB);
        if (thumb == null) {
            throw new IllegalArgumentException(getContext().getString(R.string.no_thumb_error));
        }
        SQLiteDatabase database = mFavoritesDbHelper.getWritableDatabase();
        long id = database.insert(FavoritesContract.FavoritesEntry.TABLE_NAME, null, values);

        if (id == -1) {
            throw new IllegalArgumentException(getContext()
                    .getString(R.string.no_row_inserted) + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mFavoritesDbHelper.getWritableDatabase();
        // Track number of rows deleted
        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVORITE_MOVIE_ID:
                // Delete a single row given by the ID in the URI
                selection = FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(FavoritesContract.FavoritesEntry.TABLE_NAME,
                        selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException(getContext()
                        .getString(R.string.deletion_unsupported) + uri);
        }
        if (rowsDeleted != 0) {
            // Notify all listeners that the data has changed for the pet content URI
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // update not implemented
        return 0;
    }
}
