package com.example.jayrb.moviegrid.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Contract for favorites table in favoritesDb.db
 */

public class FavoritesContract {

   /*
    * The "Content authority" is a name for the entire content provider, similar to the
    * relationship between a domain name and its website. A convenient string to use for the
    * content authority is the package name for the app, which is guaranteed to be unique on the
    * Play Store.
    */
   public static final String CONTENT_AUTHORITY = "com.example.jayrb.moviegrid";

   /*
 * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
 * the content provider for MovieGrid.
 */
   public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

   public static final String PATH_FAVORITES = "favorites";

   /**
   *  FavoritesEntry is an inner class that defines the contents of the favorites table
   */
    public static final class FavoritesEntry implements BaseColumns {

       /* The base CONTENT_URI used to query the Favorites table from the content provider */
       public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
               .appendPath(PATH_FAVORITES)
               .build();

       // Favorites table
       public static final String TABLE_NAME = "favorites";

       // Additional columns (_ID created automatically)
       public static final String COLUMN_IDENTIFIER = "identifier";
       public static final String COLUMN_TITLE = "title";
       public static final String COLUMN_OVERVIEW = "overview";
       public static final String COLUMN_YEAR = "year";
       public static final String COLUMN_VOTE = "vote";
       public static final String COLUMN_THUMB = "thumb";
       public static final String COLUMN_TRAILERS = "trailers";
       public static final String COLUMN_REVIEWS = "reviews";
   }
}
