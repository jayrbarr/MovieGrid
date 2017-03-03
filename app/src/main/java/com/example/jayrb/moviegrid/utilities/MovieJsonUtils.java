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

    public static ArrayList<String[]> getMovieStringsFromJson(Context context, String movieJsonStr) throws JSONException {

        /*  Constants to help with JSON parsing */

        final String MDB_RESULTS = "results";
        final String MDB_THUMBPATH = "poster_path";  // movieArray param 0
        final String MDB_OVERVIEW = "overview";  // movieArray param 2
        final String MDB_TITLE = "title";  // movieArray param 1
        final String MDB_RELDATE = "release_date";  // movieArray param 3
        final String MDB_VOTE = "vote_average";  // movieArray param 4
        final int NUM_PARAMS = 5;
        final int BEGIN_YEAR = 0;
        final int END_YEAR = 4;

        JSONObject movieJson = new JSONObject(movieJsonStr);

        JSONArray resultsArray = movieJson.getJSONArray(MDB_RESULTS);

        int numMovies = resultsArray.length();

        /* ArrayList to hold all movies details */
        ArrayList<String[]> parsedMovieData = new ArrayList<>(numMovies);

        for (int i = 0; i < numMovies; i++) {
            /* get the ith movie */
            String[] movieArray = new String[NUM_PARAMS];
            JSONObject movieObject = resultsArray.getJSONObject(i);
            movieArray[DetailActivity.THUMB_URL] = movieObject.getString(MDB_THUMBPATH);
            movieArray[DetailActivity.SYNOPSIS] = movieObject.getString(MDB_OVERVIEW);
            movieArray[DetailActivity.TITLE] = movieObject.getString(MDB_TITLE);
            movieArray[DetailActivity.YEAR_OF_RELEASE] = movieObject.getString(MDB_RELDATE).substring(BEGIN_YEAR, END_YEAR);
            movieArray[DetailActivity.VOTER_AVERAGE] = movieObject.getString(MDB_VOTE);

            /* add ith movie to ArrayList*/
            parsedMovieData.add(movieArray);
        }
        return parsedMovieData;
        }
    }

