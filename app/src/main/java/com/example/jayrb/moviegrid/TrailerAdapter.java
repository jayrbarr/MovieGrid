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

public class TrailerAdapter extends ArrayAdapter<String[]> {

    private ArrayList<String[]> mMovieData;

    public TrailerAdapter(@NonNull Context context, @NonNull ArrayList<String[]> movieData) {
        super(context, 0, movieData);
        mMovieData = movieData;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View trailerItemView = convertView;
        if (trailerItemView == null) {
            trailerItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.trailer_item, parent, false);
        }

        TextView trailerName = (TextView) trailerItemView.findViewById(R.id.trailer_name);
        trailerName.setText(mMovieData.get(position)[DetailActivity.VIDEO_NAME]);


        return trailerItemView;
    }
}
