package com.example.jayrb.moviegrid.utilities;

import android.content.Context;

import com.example.jayrb.moviegrid.DetailActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Utilities to parse JSON from Movie Database into useful ArrayList of Strings.
 */

public final class MovieJsonUtils {

    private static final String MDB_RESULTS = "results";

    public static ArrayList<String[]> getMovieStringsFromJson(Context context, String movieJsonStr) throws JSONException {

        /*  Constants to help with JSON parsing */

        final String MDB_ID = "id";  // movieArray param 0
        final String MDB_THUMBPATH = "poster_path";  // movieArray param 1
        final int NUM_PARAMS = 2;

        JSONObject movieJson = new JSONObject(movieJsonStr);

        JSONArray resultsArray = movieJson.getJSONArray(MDB_RESULTS);

        int numMovies = resultsArray.length();

        /* ArrayList to hold all movies details */
        ArrayList<String[]> parsedMovieData = new ArrayList<>(numMovies);

        for (int i = 0; i < numMovies; i++) {
            /* get the ith movie */
            String[] movieArray = new String[NUM_PARAMS];
            JSONObject movieObject = resultsArray.getJSONObject(i);
            movieArray[DetailActivity.ID] = movieObject.getString(MDB_ID);
            movieArray[DetailActivity.THUMB_URL] = movieObject.getString(MDB_THUMBPATH);

            /* add ith movie to ArrayList*/
            parsedMovieData.add(movieArray);
        }
        return parsedMovieData;
        }

    public static ArrayList<String[]> getMovieDetailsFromJson(Context context, String movieJsonStr) throws JSONException {

        /*  Constants to help with JSON parsing */

        final String MDB_TITLE = "title";
        final String MDB_OVERVIEW = "overview";
        final String MDB_REL_DATE = "release_date";
        final String MDB_RUNTIME = "runtime";
        final String MDB_VOTE = "vote_average";
        final int NUM_PARAMS = 5;
        final int YEAR_BEGIN = 0;
        final int YEAR_END = 4;

        JSONObject movieJSONObject = new JSONObject(movieJsonStr);

        String[] movieArray = new String[NUM_PARAMS];
        movieArray[DetailActivity.TITLE] = movieJSONObject.getString(MDB_TITLE);
        movieArray[DetailActivity.SYNOPSIS] = movieJSONObject.getString(MDB_OVERVIEW);
        movieArray[DetailActivity.YEAR_OF_RELEASE] = movieJSONObject.getString(MDB_REL_DATE).substring(YEAR_BEGIN, YEAR_END);
        movieArray[DetailActivity.RUNTIME] = movieJSONObject.getString(MDB_RUNTIME);
        movieArray[DetailActivity.VOTER_AVERAGE] = movieJSONObject.getString(MDB_VOTE);

        ArrayList<String[]> parsedMovieData = new ArrayList<>(1);
        parsedMovieData.add(movieArray);
        return parsedMovieData;
    }

    public static ArrayList<String[]> getMovieReviewsFromJson(Context context, String movieJsonStr) throws JSONException {

        /*  Constants to help with JSON parsing */

        final String MDB_REVIEW_AUTHOR = "author";
        final String MDB_REVIEW_CONTENT = "content";
        final int NUM_PARAMS = 2;

        JSONObject movieJson = new JSONObject(movieJsonStr);

        JSONArray resultsArray = movieJson.getJSONArray(MDB_RESULTS);

        int numReviews = resultsArray.length();

        /* ArrayList to hold all video details */
        ArrayList<String[]> parsedMovieData = new ArrayList<>(numReviews);

        for (int i = 0; i < numReviews; i++) {
            /* get the ith review */
            String[] reviewArray = new String[NUM_PARAMS];
            JSONObject reviewObject = resultsArray.getJSONObject(i);
            reviewArray[DetailActivity.REVIEW_AUTHOR] = reviewObject.getString(MDB_REVIEW_AUTHOR);
            reviewArray[DetailActivity.REVIEW_CONTENT] = reviewObject.getString(MDB_REVIEW_CONTENT);
            /* add ith review to ArrayList*/
            parsedMovieData.add(reviewArray);
        }

        return parsedMovieData;
    }

    public static ArrayList<String[]> getMovieTrailersFromJson(Context context, String movieJsonStr) throws JSONException {

        /*  Constants to help with JSON parsing */

        final String MDB_VIDEO_KEY = "key";
        final String MDB_VIDEO_NAME = "name";
        final int NUM_PARAMS = 2;

        JSONObject movieJson = new JSONObject(movieJsonStr);

        JSONArray resultsArray = movieJson.getJSONArray(MDB_RESULTS);

        int numVideos = resultsArray.length();

        /* ArrayList to hold all video details */
        ArrayList<String[]> parsedMovieData = new ArrayList<>(numVideos);

        for (int i = 0; i < numVideos; i++) {
            /* get the ith video */
            String[] videoArray = new String[NUM_PARAMS];
            JSONObject videoObject = resultsArray.getJSONObject(i);
            videoArray[DetailActivity.VIDEO_KEY] = videoObject.getString(MDB_VIDEO_KEY);
            videoArray[DetailActivity.VIDEO_NAME] = videoObject.getString(MDB_VIDEO_NAME);

            /* add ith video to ArrayList*/
            parsedMovieData.add(videoArray);
        }

        return parsedMovieData;
    }
}

