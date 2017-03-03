package com.example.jayrb.moviegrid.utilities;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import com.example.jayrb.moviegrid.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Network utilities to access Movie Database API
 */

public final class NetworkUtils {

    /* Constants to access Movie Database API */

    private static final String MOVIES_TOP_RATED = "top_rated";
    private static final String MOVIES_POPULAR = "popular";
    private static final String MOVIES_REVIEWS = "reviews";
    private static final String MOVIES_VIDEOS = "videos";

    private static final String MOVIES_BASE = "https://api.themoviedb.org/3/movie";
    private static final String API_PARAM = "api_key";
    private static final String API_KEY = BuildConfig.API_KEY;

    private static final int MOST_POPULAR = 0;
    private static final int TOP_RATED = 1;
    private static final int FAVORITES = 2;

    private static final String TAG = NetworkUtils.class.getSimpleName();

    /*
    * Builds the URL to talk to the Movie Database server.
    * */
    public static URL buildUrl(Context context, int selection) {
        Uri builtUri;
        Log.d(TAG, "selection = " + selection);
        if (selection == MOST_POPULAR) {
            builtUri = Uri.parse(MOVIES_BASE)
                    .buildUpon()
                    .appendPath(MOVIES_POPULAR)
                    .appendQueryParameter(API_PARAM, API_KEY).build();
        } else if (selection == TOP_RATED) {
            builtUri = Uri.parse(MOVIES_BASE)
                    .buildUpon()
                    .appendPath(MOVIES_TOP_RATED)
                    .appendQueryParameter(API_PARAM, API_KEY).build();
        } else
        // if (selection == FAVORITES) //
        {
            builtUri = Uri.parse(MOVIES_BASE)
                    .buildUpon()
                    .appendPath(MOVIES_TOP_RATED)
                    .appendQueryParameter(API_PARAM, API_KEY).build();
        }
        return uriToUrl(builtUri);
    }

    public static URL reviewsUrl(String id) {
        Uri builtUri = Uri.parse(MOVIES_BASE)
                .buildUpon()
                .appendPath(id)
                .appendPath(MOVIES_REVIEWS)
                .appendQueryParameter(API_PARAM, API_KEY).build();
        return uriToUrl(builtUri);
    }

    public static URL videosUrl(String id) {
        Uri builtUri = Uri.parse(MOVIES_BASE)
                .buildUpon()
                .appendPath(id)
                .appendPath(MOVIES_VIDEOS)
                .appendQueryParameter(API_PARAM, API_KEY).build();
        return uriToUrl(builtUri);
    }

    public static URL movieUrl(String id) {
        Uri builtUri = Uri.parse(MOVIES_BASE)
                .buildUpon()
                .appendPath(id)
                .appendQueryParameter(API_PARAM, API_KEY).build();
        return uriToUrl(builtUri);
    }

    private static URL uriToUrl(Uri builtUri) {
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
