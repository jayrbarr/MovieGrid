package com.example.jayrb.moviegrid;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jayrb.moviegrid.data.FavoritesContract;
import com.example.jayrb.moviegrid.data.FavoritesContract.FavoritesEntry;
import com.example.jayrb.moviegrid.utilities.DbUtils;
import com.example.jayrb.moviegrid.utilities.MovieJsonUtils;
import com.example.jayrb.moviegrid.utilities.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

import static com.example.jayrb.moviegrid.MovieAdapter.FETCH_IMAGE_URL_BASE;

/*
*  DetailActivity displays a selected movie's details
*  thrown from the Intent in a String array
* */

public class DetailActivity extends AppCompatActivity {

    public static final int ID = 0;
    public static final int THUMB_URL = 1;
    public static final int TITLE = 0;
    public static final int SYNOPSIS = 1;
    public static final int YEAR_OF_RELEASE = 2;
    public static final int RUNTIME = 3;
    public static final int VOTER_AVERAGE = 4;
    public static final int VIDEO_KEY = 0;
    public static final int VIDEO_NAME = 1;
    public static final int REVIEW_AUTHOR = 0;
    public static final int REVIEW_CONTENT = 1;
    private static final int MOVIE_DETAILS_LOADER = 88;
    private static final int MOVIE_TRAILERS_LOADER = 89;
    private static final int MOVIE_REVIEWS_LOADER = 90;
    private static final int FAVORITES_CURSOR_LOADER = 66;
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String YOUTUBE_BASE = "http://www.youtube.com/watch";
    private static final String YOUTUBE_QUERY_PARAM = "v";
    private static final int LENGTH_OF_STRING_ADD = 3;
    private static final int BEGIN_INDEX = 0;
    private static String mMovieID;
    private static String mTrailersJSON;
    private static String mReviewsJSON;
    private int mSelected;
    private SharedPreferences mSharedPreferences;
    private LoaderCallbacks<ArrayList<String[]>> movieDbLoaderListener =
            new LoaderCallbacks<ArrayList<String[]>>() {
                @Override
                public Loader<ArrayList<String[]>> onCreateLoader(final int id, Bundle args) {
                    return new AsyncTaskLoader<ArrayList<String[]>>(getBaseContext()) {
                        @Override
                        public ArrayList<String[]> loadInBackground() {
                            {
                                URL selectionUrl;
                                switch (id) {
                                    case MOVIE_DETAILS_LOADER:
                                        selectionUrl = NetworkUtils.movieUrl(mMovieID);
                                        break;
                                    case MOVIE_REVIEWS_LOADER:
                                        selectionUrl = NetworkUtils.reviewsUrl(mMovieID);
                                        break;
                                    case MOVIE_TRAILERS_LOADER:
                                        selectionUrl = NetworkUtils.videosUrl(mMovieID);
                                        break;
                                    default:
                                        return null;
                                }
                                String jsonMovieResponse = null;
                                try {
                                    jsonMovieResponse = NetworkUtils.getResponseFromHttpUrl(selectionUrl);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                                try {
                                    switch (id) {
                                        case MOVIE_DETAILS_LOADER:
                                            return MovieJsonUtils.getMovieDetailsFromJson(DetailActivity.this, jsonMovieResponse);
                                        case MOVIE_REVIEWS_LOADER:
                                            mReviewsJSON = jsonMovieResponse;
                                            return MovieJsonUtils.getMovieReviewsFromJson(DetailActivity.this, jsonMovieResponse);
                                        case MOVIE_TRAILERS_LOADER:
                                            mTrailersJSON = jsonMovieResponse;
                                            return MovieJsonUtils.getMovieTrailersFromJson(DetailActivity.this, jsonMovieResponse);
                                        default:
                                            return null;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    return null;
                                }
                            }
                        }
                    };
                }

                @Override
                public void onLoadFinished(Loader<ArrayList<String[]>> loader, ArrayList<String[]> movieData) {
                    if (movieData == null) return;
                    switch (loader.getId()) {
                        case MOVIE_DETAILS_LOADER:
                            loadMovieDetails(movieData);
                            break;
                        case MOVIE_TRAILERS_LOADER:
                            loadMovieTrailers(movieData);
                            break;
                        case MOVIE_REVIEWS_LOADER:
                            loadMovieReviews(movieData);
                            break;
                        default:
                            break;
                    }
                }

                @Override
                public void onLoaderReset(Loader<ArrayList<String[]>> loader) {

                }
            };
    private LoaderCallbacks<Cursor> favoritesLoaderListener =
            new LoaderCallbacks<Cursor>() {
                @Override
                public Loader<Cursor> onCreateLoader(final int id, Bundle args) {
                    String[] projection = {
                            FavoritesEntry.COLUMN_MOVIE_ID,
                            FavoritesEntry.COLUMN_TITLE,
                            FavoritesEntry.COLUMN_OVERVIEW,
                            FavoritesEntry.COLUMN_RUNTIME,
                            FavoritesEntry.COLUMN_VOTE,
                            FavoritesEntry.COLUMN_YEAR,
                            FavoritesEntry.COLUMN_TRAILERS,
                            FavoritesEntry.COLUMN_REVIEWS,
                            FavoritesEntry.COLUMN_THUMB
                    };

                    Uri builtUri = FavoritesEntry.CONTENT_URI.buildUpon()
                            .appendPath(mMovieID)
                            .build();
        /*
         * Takes action based on the ID of the Loader that's being created
         */
                    switch (id) {
                        case FAVORITES_CURSOR_LOADER:
                            return new android.support.v4.content.CursorLoader(
                                    getBaseContext(),           // Parent activity context
                                    builtUri,                   // Row (movie) to query
                                    projection,                 // Projection to return
                                    null,                       // No selection clause (in Uri)
                                    null,                       // No selection arguments (in Uri)
                                    null                        // Default sort order
                            );
                        default:
                            // An invalid id was passed in
                            return null;
                    }
                }

                @Override
                public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

                    if (cursor.moveToFirst()) {
                        String trailersJSON = cursor.getString(cursor
                                .getColumnIndexOrThrow(FavoritesEntry.COLUMN_TRAILERS));
                        String reviewsJSON = cursor.getString(cursor
                                .getColumnIndexOrThrow(FavoritesEntry.COLUMN_REVIEWS));
                        try {
                            loadMovieTrailers(MovieJsonUtils.getMovieTrailersFromJson(getBaseContext(), trailersJSON));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            loadMovieReviews(MovieJsonUtils.getMovieReviewsFromJson(getBaseContext(), reviewsJSON));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        TextView title = (TextView) findViewById(R.id.movie_title);
                        title.setText(cursor.getString(cursor.getColumnIndexOrThrow(FavoritesEntry.COLUMN_TITLE)));
                        TextView year = (TextView) findViewById(R.id.display_year_released);
                        year.setText(Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(FavoritesEntry.COLUMN_YEAR))));
                        TextView synopsis = (TextView) findViewById(R.id.detail_synopsis);
                        synopsis.setText(cursor.getString(cursor.getColumnIndexOrThrow(FavoritesEntry.COLUMN_OVERVIEW)));
                        TextView vote = (TextView) findViewById(R.id.detail_rating);
                        vote.setText(Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(FavoritesEntry.COLUMN_VOTE))) + getString(R.string.voter_average));
                        TextView runtime = (TextView) findViewById(R.id.detail_runtime);
                        runtime.setText(Integer.toString(cursor.getInt(cursor.getColumnIndexOrThrow(FavoritesEntry.COLUMN_RUNTIME))) + getString(R.string.runtime));
                        byte[] thumbBlob = cursor.getBlob(cursor
                                .getColumnIndexOrThrow(FavoritesEntry.COLUMN_THUMB));
                        ImageView detailThumb = (ImageView) findViewById(R.id.detail_thumb);
                        detailThumb.setImageBitmap(DbUtils.byteArrayToBitmap(thumbBlob));
                    }
                }

                @Override
                public void onLoaderReset(Loader<Cursor> loader) {
                }


            };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mSelected = mSharedPreferences.getInt(getString(R.string.settings_selection_key), MainActivity.DEFAULT_SPINNER_POSITION);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                String[] movieDetails = intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);
                mMovieID = movieDetails[ID];

                getSupportLoaderManager().initLoader(FAVORITES_CURSOR_LOADER, null, favoritesLoaderListener).forceLoad();

                Uri builtUri = FavoritesEntry.CONTENT_URI.buildUpon()
                        .appendPath(mMovieID)
                        .build();

                Cursor cursor = getContentResolver().query(
                        builtUri,
                        null,
                        null,
                        null,
                        null);

                if (cursor.moveToFirst()) {
                    Button favoriteButton = (Button) findViewById(R.id.button_favorite);
                    favoriteButton.setText(getString(R.string.remove_favorite));
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Drawable buttonDrawable = getResources().getDrawable(R.drawable.remove_favorite, getTheme());
                        buttonDrawable.mutate();
                        favoriteButton.setBackground(buttonDrawable);
                    }
                }
                cursor.close();

                if (mSelected != MainActivity.FAVORITES_SELECTED) {
                    ImageView detailThumb = (ImageView) findViewById(R.id.detail_thumb);
                    Uri uri = Uri.parse(FETCH_IMAGE_URL_BASE + movieDetails[THUMB_URL]);
                    Picasso.with(this)
                            .load(uri)
                            .placeholder(R.mipmap.ic_launcher)
                            .error(R.mipmap.ic_launcher)
                            .into(detailThumb);

                    getSupportLoaderManager().initLoader(MOVIE_DETAILS_LOADER, null, movieDbLoaderListener).forceLoad();
                    getSupportLoaderManager().initLoader(MOVIE_TRAILERS_LOADER, null, movieDbLoaderListener).forceLoad();
                    getSupportLoaderManager().initLoader(MOVIE_REVIEWS_LOADER, null, movieDbLoaderListener).forceLoad();
                }
            }
        }
    }

    public void checkFavorite(View view) {
        Button favoriteButton = (Button) findViewById(R.id.button_favorite);
        if (favoriteButton.getText().equals(getString(R.string.button_favorite))) {
            saveFavorite();
            favoriteButton.setText(getString(R.string.remove_favorite));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Drawable buttonDrawable = getResources().getDrawable(R.drawable.remove_favorite, getTheme());
                buttonDrawable.mutate();
                favoriteButton.setBackground(buttonDrawable);
            }
        } else {
            deleteFavorite();
            favoriteButton.setText(getString(R.string.button_favorite));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Drawable buttonDrawable = getResources().getDrawable(R.drawable.favorite_button, getTheme());
                buttonDrawable.mutate();
                favoriteButton.setBackground(buttonDrawable);
            }
        }
    }

    private void loadMovieDetails(ArrayList<String[]> movieData) {
        TextView title = (TextView) findViewById(R.id.movie_title);
        title.setText(movieData.get(0)[TITLE]);
        TextView year = (TextView) findViewById(R.id.display_year_released);
        year.setText(movieData.get(0)[YEAR_OF_RELEASE]);
        TextView synopsis = (TextView) findViewById(R.id.detail_synopsis);
        synopsis.setText(movieData.get(0)[SYNOPSIS]);
        TextView vote = (TextView) findViewById(R.id.detail_rating);
        vote.setText(movieData.get(0)[VOTER_AVERAGE] + getString(R.string.voter_average));
        TextView runtime = (TextView) findViewById(R.id.detail_runtime);
        runtime.setText(movieData.get(0)[RUNTIME] + getString(R.string.runtime));
    }

    private void loadMovieTrailers(final ArrayList<String[]> movieData) {
        ListView trailersLayout = (ListView) findViewById(R.id.trailers);
        trailersLayout.setFocusableInTouchMode(false);

        int numTrailers = movieData.size();

        if (numTrailers != 0) {
            TrailerAdapter adapter = new TrailerAdapter(DetailActivity.this, movieData);
            trailersLayout.setAdapter(adapter);
            trailersLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Uri builtUri = Uri.parse(YOUTUBE_BASE).buildUpon()
                            .appendQueryParameter(YOUTUBE_QUERY_PARAM,
                                    movieData.get(position)[VIDEO_KEY])
                            .build();
                    startActivity(new Intent(Intent.ACTION_VIEW, builtUri));
                }
            });
        }
    }

    private void loadMovieReviews(ArrayList<String[]> movieData) {
        ListView reviewsLayout = (ListView) findViewById(R.id.reviews);
        reviewsLayout.setFocusableInTouchMode(false);

        int numReviews = movieData.size();

        if (numReviews != 0) {
            ReviewAdapter adapter = new ReviewAdapter(DetailActivity.this, movieData);
            reviewsLayout.setAdapter(adapter);
        }
    }

    /*
        When button_favorite is pressed to mark as favorite, save movie info in ContentProvider
    */
    private void saveFavorite() {
        ContentValues values = new ContentValues();
        TextView title = (TextView) findViewById(R.id.movie_title);
        TextView year = (TextView) findViewById(R.id.display_year_released);
        TextView synopsis = (TextView) findViewById(R.id.detail_synopsis);
        TextView vote = (TextView) findViewById(R.id.detail_rating);
        TextView runtime = (TextView) findViewById(R.id.detail_runtime);
        ImageView thumb = (ImageView) findViewById(R.id.detail_thumb);

        values.put(FavoritesContract.FavoritesEntry.COLUMN_MOVIE_ID, mMovieID);
        values.put(FavoritesContract.FavoritesEntry.COLUMN_TITLE, title.getText().toString());
        values.put(FavoritesContract.FavoritesEntry.COLUMN_OVERVIEW, synopsis.getText().toString());
        String voteAll = vote.getText().toString();
        int voteLength = voteAll.length();
        values.put(FavoritesContract.FavoritesEntry.COLUMN_VOTE,
                voteAll.substring(BEGIN_INDEX, voteLength - LENGTH_OF_STRING_ADD));

        values.put(FavoritesContract.FavoritesEntry.COLUMN_YEAR, year.getText().toString());
        String runtimeAll = runtime.getText().toString();
        int runtimeLength = runtimeAll.length();
        values.put(FavoritesContract.FavoritesEntry.COLUMN_RUNTIME,
                runtimeAll.substring(BEGIN_INDEX, runtimeLength - LENGTH_OF_STRING_ADD));
        values.put(FavoritesContract.FavoritesEntry.COLUMN_TRAILERS, mTrailersJSON);
        values.put(FavoritesContract.FavoritesEntry.COLUMN_REVIEWS, mReviewsJSON);

        Bitmap thumbBitmap = ((BitmapDrawable) thumb.getDrawable()).getBitmap();
        byte[] thumbArray = DbUtils.bitmapToByteArray(thumbBitmap);
        values.put(FavoritesContract.FavoritesEntry.COLUMN_THUMB, thumbArray);

        if (getContentResolver()
                .insert(FavoritesContract.FavoritesEntry.CONTENT_URI, values)
                != null) {
            Toast.makeText(this, getString(R.string.favorite_saved),
                    Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, getString(R.string.favorite_error),
                    Toast.LENGTH_SHORT).show();
        }
    }

    /*
       When button_favorite is pressed to remove favorite, delete movie info in ContentProvider
   */
    private void deleteFavorite() {
        Uri builtUri = FavoritesContract.FavoritesEntry.CONTENT_URI.buildUpon()
                .appendPath(mMovieID)
                .build();
        int rowsDeleted = getContentResolver().delete(
                builtUri,
                null,
                null
        );
        if (rowsDeleted == 0) {
            // If no rows deleted, error in removing favorite.
            Toast.makeText(this, getString(R.string.remove_favorite_error),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, deletion successful and favorite was removed.
            Toast.makeText(this, getString(R.string.favorite_removed),
                    Toast.LENGTH_SHORT).show();
        }
    }

}
