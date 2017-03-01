package com.example.jayrb.moviegrid;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.jayrb.moviegrid.utilities.MovieJsonUtils;
import com.example.jayrb.moviegrid.utilities.NetworkUtils;

import java.net.URL;
import java.util.ArrayList;

/*
*  MainActivity displays a GridView of movie posters from Movie Database API
*  controlled by RecyclerView. The adapter's OnClickHandler listens for click
*  on each poster ImageView and, if clicked, sends EXTRA of movie details to
*  DetailActivity through Intent.
*
*  Loading indicator displays while data being captured in Async background thread.
*
*  Error message displays if no movie data returned.
*
*  Also sets up ActionBar spinner to select top-rated or most-popular movies and
*  sets OnItemSelectListener to listen for selection and reset data as needed.
*
* */

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieAdapterOnClickHandler {

    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private RecyclerView mRecyclerView;
    private MovieAdapter mMovieAdapter;
    private boolean mPopularSelected;
    private boolean isConnected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        isConnected = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        /* This TextView is used to display errors and will be hidden if there are no errors */
        mErrorMessageDisplay = (TextView) findViewById(R.id.error_message_display);

        GridLayoutManager layoutManager = new GridLayoutManager(this, calculateNumOfColumns(getBaseContext()));

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(layoutManager);

        mMovieAdapter = new MovieAdapter(this);
        mRecyclerView.setAdapter(mMovieAdapter);

        /*
         * The ProgressBar that will indicate to the user that we are loading data. It will be
         * hidden when no data is loading.
         */
        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);

        loadMovieData();
    }

/*
    Calculates number of columns for GridView based on width of display and density.
    Adapted from Stack Overflow at:
    http://stackoverflow.com/questions/33575731/gridlayoutmanager-how-to-auto-fit-columns
*/
    private int calculateNumOfColumns(Context baseContext) {
        DisplayMetrics displayMetrics = baseContext.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        return (int) dpWidth / 180;
    }

    private void loadMovieData() {
        showMovieGridView();
        new FetchMovieTask().execute(mPopularSelected);
    }

    /**
     * This method is overridden by our MainActivity class in order to handle RecyclerView item
     * clicks.
     *
     * @param movieDetails The movie details for the thumb that was clicked
     */
    @Override
    public void onClick(String[] movieDetails) {
        startActivity( new Intent(this, DetailActivity.class).putExtra(Intent.EXTRA_TEXT, movieDetails) );
    }

    /**
     * This method will make the View for the movie grid visible and
     * hide the error message.
     */
    private void showMovieGridView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the movie grid is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    /**
     * This method will make the error message visible and
     * hide the movie grid View.
     */
    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        if(!isConnected) {
            mErrorMessageDisplay.setText(R.string.no_internet);
        }
        mErrorMessageDisplay.setVisibility(View.VISIBLE);

    }

    public class FetchMovieTask extends AsyncTask<Boolean, Void, ArrayList<String[]>> {

        @Override
        protected ArrayList<String[]> doInBackground(Boolean... selectPopular) {
            URL movieRequestUrl = NetworkUtils.buildUrl(selectPopular[0]);

            try {
                String jsonMovieResponse = NetworkUtils
                        .getResponseFromHttpUrl(movieRequestUrl);

                ArrayList<String[]> simpleJsonMovieData = MovieJsonUtils
                        .getMovieStringsFromJson(MainActivity.this, jsonMovieResponse);

                return simpleJsonMovieData;

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mLoadingIndicator.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(ArrayList<String[]> movieData) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null && !movieData.isEmpty()) {
                showMovieGridView();
                mMovieAdapter.setMovieData(movieData);
            } else {
                if (!isConnected) {
                    showErrorMessage();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);

        MenuItem item = menu.findItem(R.id.spinner);
        Spinner spinner = (Spinner) MenuItemCompat.getActionView(item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = (String) parent.getItemAtPosition(position);
                mPopularSelected = selected.equals(getString(R.string.most_popular));
                loadMovieData();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mPopularSelected = true;
                loadMovieData();
            }
        });

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        return true;
    }
}