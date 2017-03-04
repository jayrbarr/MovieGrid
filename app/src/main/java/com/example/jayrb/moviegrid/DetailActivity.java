package com.example.jayrb.moviegrid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

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

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<String[]>> {

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
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final String YOUTUBE_BASE = "http://www.youtube.com/watch";
    private static final String YOUTUBE_QUERY_PARAM = "v";
    private static String mMovieID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        ImageView detailThumb = (ImageView) findViewById(R.id.detail_thumb);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                String[] movieDetails = intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);

                Uri uri = Uri.parse(FETCH_IMAGE_URL_BASE + movieDetails[THUMB_URL]);
                Picasso.with(this).load(uri).into(detailThumb);

                mMovieID = movieDetails[ID];

                getSupportLoaderManager().initLoader(MOVIE_DETAILS_LOADER, null, this).forceLoad();
                getSupportLoaderManager().initLoader(MOVIE_TRAILERS_LOADER, null, this).forceLoad();
                getSupportLoaderManager().initLoader(MOVIE_REVIEWS_LOADER, null, this).forceLoad();


            }
        }
    }

    @Override
    public Loader<ArrayList<String[]>> onCreateLoader(final int id, Bundle args) {
        return new AsyncTaskLoader<ArrayList<String[]>>(this) {
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
                                return MovieJsonUtils.getMovieReviewsFromJson(DetailActivity.this, jsonMovieResponse);
                            case MOVIE_TRAILERS_LOADER:
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
}
