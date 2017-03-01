package com.example.jayrb.moviegrid;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static com.example.jayrb.moviegrid.MovieAdapter.FETCH_IMAGE_URL_BASE;

/*
*  DetailActivity displays a selected movie's details
*  thrown from the Intent in a String array
* */

public class DetailActivity extends AppCompatActivity {

    private TextView mMovieTitle;
    private TextView mMovieDisplay;
    private String[] mMovieDetails;
    private ImageView mDetailThumb;
    private static final int THUMB_URL = 0;
    private static final int TITLE = 1;
    private static final int SYNOPSIS = 2;
    private static final int RELEASE_DATE = 3;
    private static final int VOTER_AVERAGE = 4;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        /* find appropriate views in layout */

        mMovieTitle = (TextView) findViewById(R.id.movie_title);
        mMovieDisplay = (TextView) findViewById(R.id.display_movie_details);
        mDetailThumb = (ImageView) findViewById(R.id.detail_thumb);

        Intent intentThatStartedThisActivity = getIntent();

        if (intentThatStartedThisActivity != null) {
            if (intentThatStartedThisActivity.hasExtra(Intent.EXTRA_TEXT)) {
                mMovieDetails = intentThatStartedThisActivity.getStringArrayExtra(Intent.EXTRA_TEXT);

                Uri uri = Uri.parse(FETCH_IMAGE_URL_BASE + mMovieDetails[THUMB_URL]);
                Picasso.with(this).load(uri).into(mDetailThumb);

                mMovieTitle.setText(mMovieDetails[TITLE]);

                String movieDetails = mMovieDetails[SYNOPSIS]
                        + getString(R.string.double_line_feed)
                        + getString(R.string.release_date)
                        + mMovieDetails[RELEASE_DATE]
                        + getString(R.string.double_line_feed)
                        + getString(R.string.voter_average)
                        + mMovieDetails[VOTER_AVERAGE];

                mMovieDisplay.setText(movieDetails);
            }
        }


    }
}
