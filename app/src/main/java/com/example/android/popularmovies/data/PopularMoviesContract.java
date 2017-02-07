package com.example.android.popularmovies.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Jacek on 1/8/2017.
 */

public final class PopularMoviesContract {

    //Content Provider constants:
    public static final String CONTENT_AUTHORITY = "com.example.android.popularmovies";
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    //Tables paths
    public static final String PATH_IMAGE = "image";
    public static final String PATH_MOVIE = "movie";
    public static final String PATH_TRAILER = "trailer";
    public static final String PATH_REVIEW = "review";

    //Constants for URI query params:
    public static final String TYPE_FAVORITE = "favorite";

    // To prevent someone from accidentally instantiating the contract class,
    // make the constructor private.
    private PopularMoviesContract() {}

    /* Inner class that defines the Movies table contents */
    public static class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
            BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "movie";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_ORIGINAL_TITLE = "original_title";
        public static final String COLUMN_RUNTIME = "runtime";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_RELEASE_DATE = "release_date";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieByMovieId(String movieId) {

            return CONTENT_URI.buildUpon().appendPath(movieId).build();
        }


        public static Uri buildTrailersByMovieId(String movieRecId) {

            return CONTENT_URI.buildUpon().appendPath(movieRecId).appendPath(PATH_TRAILER).build();
        }

        public static Uri buildReviewsByMovieId(String movieRecId) {

            return CONTENT_URI.buildUpon().appendPath(movieRecId).appendPath(PATH_REVIEW).build();

        }

        public static String getMovieRecIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
        public static String getMovieIdFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    /* Inner class that defines the Reviews table contents */
    public static class ReviewEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_REVIEW).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_REVIEW;

        public static final String TABLE_NAME = "review";

        public static final String COLUMN_REVIEW_ID = "review_id";
        public static final String COLUMN_MOVIE_KEY = "movie_record_id";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_CONTENT = "content";

        public static Uri buildReviewUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

    /* Inner class that defines the Trailers table contents */
    public static class TrailerEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_TRAILER).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_TRAILER;

        public static final String TABLE_NAME = "trailer";

        public static final String COLUMN_TRAILER_ID = "trailer_id";
        public static final String COLUMN_MOVIE_KEY = "movie_record_id";
        public static final String COLUMN_KEY = "key";
        public static final String COLUMN_SITE = "site";
        public static final String COLUMN_NAME = "name";

        public static Uri buildTrailerUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }
    }

    /* Inner class that defines the Images table contents */
    public static class ImageEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_IMAGE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMAGE;
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_IMAGE;

        public static final String TABLE_NAME = "image";

        public static final String COLUMN_MOVIE_ID = "movie_id";
        public static final String COLUMN_MOVIE_CAT = "movie_cat";
        public static final String COLUMN_MOVIE_FAV = "movie_fav";
        public static final String COLUMN_IMAGE = "image";

        public static Uri buildImageUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildImagesByCategory(String category) {

            return CONTENT_URI.buildUpon().appendPath(category).build();
        }

        public static String getMovieCategoryFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }
}
