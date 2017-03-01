package com.example.jayrb.moviegrid.utilities;

import android.net.Uri;

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

    private static final String MOVIES_TOP_RATED = "http://api.themoviedb.org/3/movie/top_rated";
    private static final String MOVIES_POPULAR = "http://api.themoviedb.org/3/movie/popular";
    private static final String API_PARAM = "api_key";
    private static final String API_KEY = BuildConfig.API_KEY;


    /*
    * Builds the URL to talk to the Movie Database server.
    * */
    public static URL buildUrl (boolean selectPopular) {
        Uri builtUri;
        if (selectPopular) {
            builtUri = Uri.parse(MOVIES_POPULAR).buildUpon().appendQueryParameter(API_PARAM, API_KEY).build();
        } else {
            builtUri = Uri.parse(MOVIES_TOP_RATED).buildUpon().appendQueryParameter(API_PARAM, API_KEY).build();
        }
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
