package com.example.jayrb.moviegrid;

import android.content.Context;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/*
*  (@link MovieAdapter) exposes a list of movies to a
*  (@link android.support.v7.widget.RecyclerView)
* */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    public static final String FETCH_IMAGE_URL_BASE = "http://image.tmdb.org/t/p/w185";
    /*
    * An on-click handler to make it easy for an Activity to interface with
    * our RecyclerView
    */
    private final MovieAdapterOnClickHandler mClickHandler;
    private ArrayList<String[]> mMovieList;

    public MovieAdapter(MovieAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_movie, null);
        return new MovieViewHolder(layoutView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        String thumbPath = mMovieList.get(position)[0];
        String thumbUrl = FETCH_IMAGE_URL_BASE + thumbPath;
        Uri uri = Uri.parse(thumbUrl);
        Context context = holder.mMovieThumb.getContext();
        Picasso.with(context)
                .load(uri)
                .placeholder(R.mipmap.ic_launcher)
                .error(R.mipmap.ic_launcher)
                .into(holder.mMovieThumb);
    }

    @Override
    public int getItemCount() {
        if (null == mMovieList) return 0;
        return mMovieList.size();
    }

    /**
     * This method is used to set the movie data on a MovieAdapter if we've already
     * created one. This is handy when we get new data from the web but don't want to create a
     * new MovieAdapter to display it.
     *
     * @param movieData The new movie data to be displayed.
     */
    public void setMovieData(ArrayList<String[]> movieData) {
        mMovieList = movieData;
        notifyDataSetChanged();
    }

    /**
     * The interface that receives onClick messages.
     */
    public interface MovieAdapterOnClickHandler {
        void onClick(String[] movieDetail);
    }

    /**
     * A cache of the children views for a movie list
     */

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public final ImageView mMovieThumb;

        public MovieViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mMovieThumb = (ImageView) itemView.findViewById(R.id.movie_thumb);
        }
        /**
         * This gets called by the child views during a click.
         *
         * @param v The View that was clicked.
         */
        @Override
        public void onClick(View v) {
            int adapterPosition = getAdapterPosition();
            String[] movieDetail = mMovieList.get(adapterPosition);
            mClickHandler.onClick(movieDetail);
        }
    }
}
