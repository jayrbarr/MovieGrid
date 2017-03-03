package com.example.jayrb.moviegrid;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.jayrb.moviegrid.MovieAdapter.FETCH_IMAGE_URL_BASE;

/*
*  DetailActivity displays a selected movie's details
*  thrown from the Intent in a String array
* */

public class DetailActivity extends AppCompatActivity {

    public static final int THUMB_URL = 0;
    public static final int TITLE = 1;
    public static final int SYNOPSIS = 2;
    public static final int YEAR_OF_RELEASE = 3;
    public static final int VOTER_AVERAGE = 4;
    private TextView mMovieTitle;
    private TextView mMovieYear;
    private TextView mMovieSynopsis;
    private TextView mMovieRanking;
    private String[] mMovieDetails;
    private ImageView mDetailThumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /* find appropriate views in layout */

        mMovieTitle = (TextView) findViewById(R.id.movie_title);
        mMovieYear = (TextView) findViewById(R.id.display_year_released);
        mDetailThumb = (ImageView) findViewById(R.id.detail_thumb);
        mMovieSynopsis = (TextView) findViewById(R.id.detail_synopsis);
        mMovieRanking = (TextView) findViewById(R.id.detail_rating);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                mMovieDetails = intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);

                Uri uri = Uri.parse(FETCH_IMAGE_URL_BASE + mMovieDetails[THUMB_URL]);
                Picasso.with(this).load(uri).into(mDetailThumb);

                mMovieTitle.setText(mMovieDetails[TITLE]);

                mMovieSynopsis.setText(mMovieDetails[SYNOPSIS]);
                mMovieYear.setText(mMovieDetails[YEAR_OF_RELEASE]);
                mMovieRanking.setText(mMovieDetails[VOTER_AVERAGE] + getString(R.string.voter_average));
            }
        }


    }
}
