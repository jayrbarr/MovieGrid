package com.example.jayrb.moviegrid;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.jayrb.moviegrid.data.FavoritesContract.FavoritesEntry;
import com.example.jayrb.moviegrid.utilities.DbUtils;

/**
 * {@link FavoritesAdapter} is an adapter for a grid view
 * that uses a {@link Cursor} of favorites movie data as its data source. This adapter knows
 * how to create card view items for each row of movie data in the {@link Cursor}.
 */
public class FavoritesAdapter extends CursorRecyclerViewAdapter<FavoritesAdapter.ViewHolder> {

    private static final String TAG = FavoritesAdapter.class.getSimpleName();
    private Cursor mCursor;
    private CursorAdapterOnClickHandler mClickHandler;

    /**
     * Constructs a new {@link FavoritesAdapter}.
     *
     * @param context The context
     * @param cursor  The cursor from which to get the data.
     */
    public FavoritesAdapter(Context context, Cursor cursor, CursorAdapterOnClickHandler clickHandler) {
        super(context, cursor, clickHandler);
        mClickHandler = clickHandler;
        mCursor = cursor;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.card_movie, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final Cursor cursor) {
        byte[] thumbBlob = cursor.getBlob(cursor
                .getColumnIndexOrThrow(FavoritesEntry.COLUMN_THUMB));
        viewHolder.mThumbView.setImageBitmap(DbUtils.byteArrayToBitmap(thumbBlob));
        viewHolder.mThumbView.setTag(cursor.getPosition());
        viewHolder.mThumbView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] movieDetail = new String[2];
                int position = (Integer) v.getTag();
                cursor.moveToPosition(position);
                movieDetail[0] = Integer.toString(cursor.getInt(cursor
                        .getColumnIndexOrThrow(FavoritesEntry.COLUMN_MOVIE_ID)));
                movieDetail[1] = "/z09QAf8WbZncbitewNk6lKYMZsh.jpg";
                mClickHandler.onClick(movieDetail);
            }
        });
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView mThumbView;

        public ViewHolder(View view) {
            super(view);
            mThumbView = (ImageView) view.findViewById(R.id.movie_thumb);
        }
    }
}
