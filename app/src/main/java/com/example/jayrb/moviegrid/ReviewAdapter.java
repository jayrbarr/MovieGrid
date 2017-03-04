package com.example.jayrb.moviegrid;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by jayrb on 3/3/2017.
 */

public class ReviewAdapter extends ArrayAdapter<String[]> {

    private ArrayList<String[]> mMovieData;

    public ReviewAdapter(@NonNull Context context, @NonNull ArrayList<String[]> movieData) {
        super(context, 0, movieData);
        mMovieData = movieData;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View reviewItemView = convertView;
        if (reviewItemView == null) {
            reviewItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.review_item, parent, false);
        }

        TextView reviewName = (TextView) reviewItemView.findViewById(R.id.review_author);
        reviewName.setText(mMovieData.get(position)[DetailActivity.REVIEW_AUTHOR]);

        TextView reviewContent = (TextView) reviewItemView.findViewById(R.id.review_content);
        reviewContent.setText(mMovieData.get(position)[DetailActivity.REVIEW_CONTENT]);

        return reviewItemView;
    }
}
