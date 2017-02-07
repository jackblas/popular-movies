package com.example.android.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.SharedPreferences;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.util.Log;

import com.example.android.popularmovies.R;
import com.example.android.popularmovies.web.MoviesUriQueryBuilder;

/**
 * Created by Jacek on 1/14/2017.
 */

public class MovieProvider extends ContentProvider {

    private static final String LOG_TAG = MovieProvider.class.getSimpleName();

    // This Constants indicate record type in the cursor
    // used by Details fragment
    public static final String DETAILS = "details";
    public static final String TRAILER = "trailer";
    public static final String REVIEW = "review";

    // Projection for the MatrixCursor.
    // This cursor includes details, trailers and reviews
    // It is a data source for the DetailsAdapter
    public static final String[] MTRX_COLUMNS = {
                BaseColumns._ID,
                "view_type",
                PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID,
                PopularMoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
                PopularMoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
                PopularMoviesContract.MovieEntry.COLUMN_USER_RATING,
                PopularMoviesContract.MovieEntry.COLUMN_RUNTIME,
                PopularMoviesContract.MovieEntry.COLUMN_OVERVIEW,

                PopularMoviesContract.ImageEntry.COLUMN_MOVIE_FAV,
                PopularMoviesContract.ImageEntry.COLUMN_IMAGE,
                PopularMoviesContract.TrailerEntry.COLUMN_KEY,
                PopularMoviesContract.TrailerEntry.COLUMN_NAME,
                PopularMoviesContract.ReviewEntry.COLUMN_AUTHOR,
                PopularMoviesContract.ReviewEntry.COLUMN_CONTENT
    };

    public static final int MTRX_COL_ID = 0;
    public static final int MTRX_COL_VIEW_TYPE = 1;
    public static final int MTRX_COL_MOVIE_ID = 2;
    public static final int MTRX_COL_ORIGINAL_TITLE = 3;
    public static final int MTRX_COL_RELEASE_DATE = 4;
    public static final int MTRX_COL_USER_RATING = 5;
    public static final int MTRX_COL_RUNTIME = 6;
    public static final int MTRX_COL_OVERVIEW = 7;
    public static final int MTRX_COL_MOVIE_FAV = 8;
    public static final int MTRX_COL_IMAGE = 9;

    public static final int MTRX_COL_KEY = 10;
    public static final int MTRX_COL_NAME = 11;

    public static final int MTRX_COL_AUTHOR = 12;
    public static final int MTRX_COL_CONTENT = 13;

    // Projection for details database query:
    private static final String[] DETAIL_COLUMNS = {
            PopularMoviesContract.MovieEntry.TABLE_NAME + "." + PopularMoviesContract.MovieEntry._ID,
            "'details' AS view_type",
            PopularMoviesContract.MovieEntry.TABLE_NAME + "." + PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            PopularMoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            PopularMoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            PopularMoviesContract.MovieEntry.COLUMN_USER_RATING,
            PopularMoviesContract.MovieEntry.COLUMN_RUNTIME,
            PopularMoviesContract.MovieEntry.COLUMN_OVERVIEW,
            PopularMoviesContract.ImageEntry.COLUMN_MOVIE_FAV,
            PopularMoviesContract.ImageEntry.COLUMN_IMAGE

    };

    // Column names for matrix cursor built using data loaded via http request
    public static final String[] DETAIL_COLUMNS_MTRX = {
            PopularMoviesContract.MovieEntry._ID,
            "view_type",
            PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID,
            PopularMoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE,
            PopularMoviesContract.MovieEntry.COLUMN_RELEASE_DATE,
            PopularMoviesContract.MovieEntry.COLUMN_USER_RATING,
            PopularMoviesContract.MovieEntry.COLUMN_RUNTIME,
            PopularMoviesContract.MovieEntry.COLUMN_OVERVIEW,
            PopularMoviesContract.ImageEntry.COLUMN_MOVIE_FAV,
            PopularMoviesContract.ImageEntry.COLUMN_IMAGE

    };

    // These constants correspond to the projection defined above, and must change if the
    // projection changes
    public static final int COL_REC_ID = 0;
    public static final int COL_VIEW_TYPE = 1;
    public static final int COL_MOVIE_ID = 2;
    public static final int COL_ORIGINAL_TITLE = 3;
    public static final int COL_RELEASE_DATE = 4;
    public static final int COL_USER_RATING = 5;
    public static final int COL_RUNTIME = 6;
    public static final int COL_OVERVIEW = 7;
    public static final int COL_MOVIE_FAV = 8;
    public static final int COL_IMAGE = 9;

    // Projection for trailers database query:
    private static final String[] TRAILER_COLUMNS = {
            PopularMoviesContract.TrailerEntry.TABLE_NAME + "." + PopularMoviesContract.TrailerEntry._ID,
            "'trailer' AS view_type",
            PopularMoviesContract.TrailerEntry.COLUMN_KEY,
            PopularMoviesContract.TrailerEntry.COLUMN_NAME,
            PopularMoviesContract.TrailerEntry.COLUMN_SITE
    };


    // Column names for matrix cursor built using data loaded via http request
    public static final String[] TRAILER_COLUMNS_MTRX = {
            PopularMoviesContract.TrailerEntry._ID,
            "view_type",
            PopularMoviesContract.TrailerEntry.COLUMN_KEY,
            PopularMoviesContract.TrailerEntry.COLUMN_NAME,
            PopularMoviesContract.TrailerEntry.COLUMN_SITE
    };

    public static final int TRL_REC_ID = 0;
    public static final int TRL_COL_VIEW = 1;
    public static final int TRL_COL_KEY = 2;
    public static final int TRL_COL_NAME = 3;
    public static final int TRL_COL_SITE = 4;

    // Projection for reviews database query:
    private static final String[] REVIEW_COLUMNS = {
            PopularMoviesContract.ReviewEntry.TABLE_NAME + "." + PopularMoviesContract.ReviewEntry._ID,
            "'review' AS view_type",
            PopularMoviesContract.ReviewEntry.COLUMN_AUTHOR,
            PopularMoviesContract.ReviewEntry.COLUMN_CONTENT

    };

    // Column names for matrix cursor built using data loaded via http request
    public static final String[] REVIEW_COLUMNS_MTRX = {
            PopularMoviesContract.ReviewEntry._ID,
            "view_type",
            PopularMoviesContract.ReviewEntry.COLUMN_AUTHOR,
            PopularMoviesContract.ReviewEntry.COLUMN_CONTENT

    };

    // These constants correspond to the projection defined above, and must change if the
    // projection changes
    public static final int REV_REC_ID = 0;
    public static final int REV_COL_VIEW = 1;
    public static final int REV_COL_AUTHOR = 2;
    public static final int REV_COL_CONTENT = 3;


    // The URI Matcher used by this content provider.
    static final UriMatcher sUriMatcher = buildUriMatcher();
    static final int IMAGE = 100;
    static final int IMAGE_WITH_ID = 101;
    static final int IMAGE_WITH_CAT = 102;

    static final int MOVIE = 200;
    static final int MOVIE_WITH_ID = 201;
    static final int MOVIE_TRAILER = 202;
    static final int MOVIE_REVIEW = 203;

    // Set TABLES - 'from image'
    private static final SQLiteQueryBuilder sMovieImagesQueryBuilder;

    static {
        sMovieImagesQueryBuilder = new SQLiteQueryBuilder();

        sMovieImagesQueryBuilder.setTables(PopularMoviesContract.ImageEntry.TABLE_NAME);
    }

    // Set WHERE clause:
    // image.movie_cat = ?
    private static final String sMovieCategorySelection = PopularMoviesContract.ImageEntry.TABLE_NAME+
            "." + PopularMoviesContract.ImageEntry.COLUMN_MOVIE_CAT + " = ? ";

    // image.movie_fav = 1
    private static final String sFavoriteMoviesSelection = PopularMoviesContract.ImageEntry.TABLE_NAME+
            "." + PopularMoviesContract.ImageEntry.COLUMN_MOVIE_FAV + " = 1 ";

    // image.movie_id = ?
    private static final String sFavoriteMovieIdSelection = PopularMoviesContract.ImageEntry.TABLE_NAME+
            "." + PopularMoviesContract.ImageEntry.COLUMN_MOVIE_ID + " = ? ";


    // Set TABLES - 'from movie inner join image'
    private static final SQLiteQueryBuilder sMovieDetailsQueryBuilder;

    static {
        sMovieDetailsQueryBuilder = new SQLiteQueryBuilder();

        sMovieDetailsQueryBuilder.setTables(PopularMoviesContract.MovieEntry.TABLE_NAME +
                " INNER JOIN " + PopularMoviesContract.ImageEntry.TABLE_NAME + " ON " +
                PopularMoviesContract.MovieEntry.TABLE_NAME + "." + PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = " +
                PopularMoviesContract.ImageEntry.TABLE_NAME + "." + PopularMoviesContract.ImageEntry.COLUMN_MOVIE_ID

        );
    }

    // Set TABLES - 'from movie inner join trailer'
    private static final SQLiteQueryBuilder sMovieTrailersQueryBuilder;

    static {
        sMovieTrailersQueryBuilder = new SQLiteQueryBuilder();

        sMovieTrailersQueryBuilder.setTables(PopularMoviesContract.MovieEntry.TABLE_NAME +
                " INNER JOIN " + PopularMoviesContract.TrailerEntry.TABLE_NAME + " ON " +
                PopularMoviesContract.MovieEntry.TABLE_NAME + "." + PopularMoviesContract.MovieEntry._ID + " = " +
                PopularMoviesContract.TrailerEntry.TABLE_NAME + "." + PopularMoviesContract.TrailerEntry.COLUMN_MOVIE_KEY

        );
    }

    // Set TABLES - 'from movie inner join review'
    private static final SQLiteQueryBuilder sMovieReviewsQueryBuilder;

    static {
        sMovieReviewsQueryBuilder = new SQLiteQueryBuilder();

        sMovieReviewsQueryBuilder.setTables(PopularMoviesContract.MovieEntry.TABLE_NAME +
                " INNER JOIN " + PopularMoviesContract.ReviewEntry.TABLE_NAME + " ON " +
                PopularMoviesContract.MovieEntry.TABLE_NAME + "." + PopularMoviesContract.MovieEntry._ID + " = " +
                PopularMoviesContract.ReviewEntry.TABLE_NAME + "." + PopularMoviesContract.ReviewEntry.COLUMN_MOVIE_KEY
        );
    }

    // Set WHERE clause:
    // movie.movie_id = ?
    private static final String sMovieIdSelection = PopularMoviesContract.MovieEntry.TABLE_NAME+
            "." + PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID + " = ? ";

    // INSTANCE VARIABLES:
    private PopularMoviesDbHelper mMoviesDbHelper;
    private MoviesUriQueryBuilder mMoviesUriQueryBuilder;

    // Get all movie images by category.
    // Movie categories: popular, top rated, favorites
    private Cursor getMovieImagesByCategory(Uri uri, String[] projection, String sortOrder) {

        String movieCategory = PopularMoviesContract.ImageEntry.getMovieCategoryFromUri(uri);

        String[] selectionArgs=new String[]{movieCategory};
        String selection=sMovieCategorySelection;

        if(movieCategory.equals(PopularMoviesContract.TYPE_FAVORITE)) {

            selection = sFavoriteMoviesSelection;
            selectionArgs = null;
            //}

            return sMovieImagesQueryBuilder.query(mMoviesDbHelper.getReadableDatabase(),
                    projection,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);

        } else {
            // Get Popular or Rated movies from the web:

            return mMoviesUriQueryBuilder.queryDataFromServer(movieCategory,null);
        }
    }


    /**
     * Get movie details including trailers and reviews.
     *
     * Merge three cursors, using MatrixCursor, into one projection
     * to be used be Details fragment cursor loader.
     *
     * @param uri
     * @param projection
     * @param sortOrder
     * @return              MatrixCursor
     */
    private Cursor getMovieById(Uri uri, String[] projection, String sortOrder) {

        String movieId = PopularMoviesContract.MovieEntry.getMovieIdFromUri(uri);

        String[] selectionArgs=new String[]{movieId};
        String selection=sMovieIdSelection;

        Cursor detailsCursor = null;
        Cursor trailersCursor = null;
        Cursor reviewsCursor = null;

        /*
        If preferences = popular or rated, get details, trailers and reviews from the web,
         else get from database.
         */
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getContext());
        String sortBy = sp.getString(getContext().getString(R.string.pref_sort_by_key),getContext().getString(R.string.pref_sort_by_popular));

        if(sortBy.equals(getContext().getString(R.string.pref_sort_by_favorite))){
            // Favorites  - read from database
            //Get details cursor
            detailsCursor = sMovieDetailsQueryBuilder.query(mMoviesDbHelper.getReadableDatabase(),
                    //projection,
                    DETAIL_COLUMNS,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);

            //Get trailers cursor
            trailersCursor = sMovieTrailersQueryBuilder.query(mMoviesDbHelper.getReadableDatabase(),
                    //projection,
                    TRAILER_COLUMNS,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);

            //Get reviews cursor
            reviewsCursor = sMovieReviewsQueryBuilder.query(mMoviesDbHelper.getReadableDatabase(),
                    //projection,
                    REVIEW_COLUMNS,
                    selection,
                    selectionArgs,
                    null,
                    null,
                    sortOrder);

        } else {
            // Popular or Top Rated - get from the web

            detailsCursor = mMoviesUriQueryBuilder.queryDataFromServer(DETAILS, movieId);
            trailersCursor = mMoviesUriQueryBuilder.queryDataFromServer(TRAILER, movieId);
            reviewsCursor = mMoviesUriQueryBuilder.queryDataFromServer(REVIEW, movieId);
        }
        // ***************************************************************
        //Build Matrix Cursor to merge results and add a 'mock' _id column:
        //*****************************************************************
        long rowIndex =0L;
        //Object[] columnValues = null;
        MatrixCursor matrixCursor = new MatrixCursor(projection);

        matrixCursor.moveToFirst();

        if (detailsCursor.moveToFirst()) {
            do {

                Object[] columnValues = new Object[MTRX_COLUMNS.length];

                columnValues[MTRX_COL_ID] = rowIndex++;

                columnValues[MTRX_COL_VIEW_TYPE] = detailsCursor.getString(COL_VIEW_TYPE);
                columnValues[MTRX_COL_MOVIE_ID] = detailsCursor.getString(COL_MOVIE_ID);
                columnValues[MTRX_COL_ORIGINAL_TITLE] = detailsCursor.getString(COL_ORIGINAL_TITLE);
                columnValues[MTRX_COL_RELEASE_DATE] = detailsCursor.getString(COL_RELEASE_DATE);
                columnValues[MTRX_COL_USER_RATING] = detailsCursor.getString(COL_USER_RATING);
                columnValues[MTRX_COL_RUNTIME] = detailsCursor.getString(COL_RUNTIME);
                columnValues[MTRX_COL_OVERVIEW] = detailsCursor.getString(COL_OVERVIEW);
                columnValues[MTRX_COL_MOVIE_FAV] = detailsCursor.getInt(COL_MOVIE_FAV);
                columnValues[MTRX_COL_IMAGE] = detailsCursor.getBlob(COL_IMAGE);

                columnValues[MTRX_COL_KEY] = null;
                columnValues[MTRX_COL_NAME] = null;
                columnValues[MTRX_COL_AUTHOR] = null;
                columnValues[MTRX_COL_CONTENT] = null;

                matrixCursor.addRow(columnValues);

            } while (detailsCursor.moveToNext());
        }

       if (trailersCursor.moveToFirst()) {
            do {

                Object[] columnValues = new Object[MTRX_COLUMNS.length];

                columnValues[MTRX_COL_ID] = rowIndex++;

                columnValues[MTRX_COL_VIEW_TYPE] = trailersCursor.getString(COL_VIEW_TYPE);
                columnValues[MTRX_COL_MOVIE_ID] = null;
                columnValues[MTRX_COL_ORIGINAL_TITLE] = null;
                columnValues[MTRX_COL_RELEASE_DATE] = null;
                columnValues[MTRX_COL_USER_RATING] = null;
                columnValues[MTRX_COL_RUNTIME] = null;
                columnValues[MTRX_COL_OVERVIEW] = null;
                columnValues[MTRX_COL_MOVIE_FAV] = null;
                columnValues[MTRX_COL_IMAGE] = null;

                columnValues[MTRX_COL_KEY] = trailersCursor.getString(TRL_COL_KEY);
                columnValues[MTRX_COL_NAME] = trailersCursor.getString(TRL_COL_NAME);

                columnValues[MTRX_COL_AUTHOR] = null;
                columnValues[MTRX_COL_CONTENT] = null;

                matrixCursor.addRow(columnValues);

            } while (trailersCursor.moveToNext());
        }

        if (reviewsCursor.moveToFirst()) {
            do {

                Object[] columnValues = new Object[MTRX_COLUMNS.length];

                columnValues[MTRX_COL_ID] = rowIndex++;

                columnValues[MTRX_COL_VIEW_TYPE] = reviewsCursor.getString(COL_VIEW_TYPE);
                columnValues[MTRX_COL_MOVIE_ID] = null;
                columnValues[MTRX_COL_ORIGINAL_TITLE] = null;
                columnValues[MTRX_COL_RELEASE_DATE] = null;
                columnValues[MTRX_COL_USER_RATING] = null;
                columnValues[MTRX_COL_RUNTIME] = null;
                columnValues[MTRX_COL_OVERVIEW] = null;
                columnValues[MTRX_COL_MOVIE_FAV] = null;
                columnValues[MTRX_COL_IMAGE] = null;

                columnValues[MTRX_COL_KEY] = null;
                columnValues[MTRX_COL_NAME] = null;

                columnValues[MTRX_COL_AUTHOR] = reviewsCursor.getString(REV_COL_AUTHOR);
                columnValues[MTRX_COL_CONTENT] = reviewsCursor.getString(REV_COL_CONTENT);

                matrixCursor.addRow(columnValues);

            } while (reviewsCursor.moveToNext());
        }

        //Return merged cursor
        return  matrixCursor;

    }

    static UriMatcher buildUriMatcher() {

        // All paths added to the UriMatcher have a corresponding code to return when a match is
        // found.  The code passed into the constructor represents the code to return for the root
        // URI.  It's common to use NO_MATCH as the code for this case.

        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = PopularMoviesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, PopularMoviesContract.PATH_IMAGE, IMAGE);
        matcher.addURI(authority, PopularMoviesContract.PATH_IMAGE + "/#", IMAGE_WITH_ID);
        matcher.addURI(authority, PopularMoviesContract.PATH_IMAGE + "/*", IMAGE_WITH_CAT);

        matcher.addURI(authority, PopularMoviesContract.PATH_MOVIE, MOVIE);
        matcher.addURI(authority, PopularMoviesContract.PATH_MOVIE + "/#", MOVIE_WITH_ID);
        matcher.addURI(authority, PopularMoviesContract.PATH_MOVIE + "/#/" + PopularMoviesContract.PATH_TRAILER
                ,MOVIE_TRAILER);
        matcher.addURI(authority, PopularMoviesContract.PATH_MOVIE + "/#/" + PopularMoviesContract.PATH_REVIEW
                ,MOVIE_REVIEW);

        return matcher;

    }

    @Override
    public boolean onCreate() {
        mMoviesDbHelper = new PopularMoviesDbHelper(getContext());
        mMoviesUriQueryBuilder = new MoviesUriQueryBuilder(getContext());
        return true;
    }

    //@Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            // "movie/#"
            case MOVIE_WITH_ID:
            {
                retCursor = getMovieById(uri, projection, sortOrder);
                break;
            }
            // "image/*" - IMAGE_WITH_CATEGORY : image/popular; image/rated; IMAGE/favorite
            case IMAGE_WITH_CAT: {
                retCursor = getMovieImagesByCategory(uri, projection, sortOrder);
                break;
            }
            // "movie"
            case MOVIE: {
                retCursor = mMoviesDbHelper.getReadableDatabase().query(
                        PopularMoviesContract.MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            }
            // "image"
            case IMAGE: {
                retCursor = mMoviesDbHelper.getReadableDatabase().query(
                        PopularMoviesContract.ImageEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;

            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //Register to watch a content URI for changes:
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    //@Nullable
    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is
        final int match = sUriMatcher.match(uri);

        switch (match) {

            case IMAGE:
                return PopularMoviesContract.ImageEntry.CONTENT_TYPE;
            case MOVIE:
                return PopularMoviesContract.ImageEntry.CONTENT_TYPE;
            case IMAGE_WITH_CAT:
                return PopularMoviesContract.ImageEntry.CONTENT_TYPE;
            case MOVIE_TRAILER:
                return PopularMoviesContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_REVIEW:
                return PopularMoviesContract.MovieEntry.CONTENT_TYPE;
            case MOVIE_WITH_ID:
                return PopularMoviesContract.MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

    }

    //@Nullable
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        //Log.d(LOG_TAG, "insert() called.");

        final SQLiteDatabase db = mMoviesDbHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case IMAGE: {
                long _id = db.insert(PopularMoviesContract.ImageEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = PopularMoviesContract.ImageEntry.buildImageUri(_id);
                else
                    //throw new android.database.SQLException("Failed to insert row into " + uri);
                    returnUri=null;
                break;
            }
            case MOVIE: {
                long _id = db.insert(PopularMoviesContract.MovieEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = PopularMoviesContract.MovieEntry.buildMovieUri(_id);
                else
                    //throw new android.database.SQLException("Failed to insert row into " + uri);
                    returnUri=null;
                break;
            }
            case MOVIE_TRAILER: {
                long _id = db.insert(PopularMoviesContract.TrailerEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = PopularMoviesContract.TrailerEntry.buildTrailerUri(_id);
                else
                    //throw new android.database.SQLException("Failed to insert row into " + uri);
                    returnUri=null;
                break;
            }
            case MOVIE_REVIEW: {
                long _id = db.insert(PopularMoviesContract.ReviewEntry.TABLE_NAME, null, contentValues);
                if ( _id > 0 )
                    returnUri = PopularMoviesContract.ReviewEntry.buildReviewUri(_id);
                else
                    //throw new android.database.SQLException("Failed to insert row into " + uri);
                    returnUri=null;
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {

        int rowsDeleted;
        switch (sUriMatcher.match(uri)) {

            // "image/#"
            case IMAGE_WITH_ID: {

                rowsDeleted = mMoviesDbHelper.getReadableDatabase().delete(
                        PopularMoviesContract.ImageEntry.TABLE_NAME,
                        sFavoriteMovieIdSelection,
                        selectionArgs

                );
                //notifyChange DOESN'T WORK IF URI INCLUDES MOVIE ID ?
                uri=PopularMoviesContract.ImageEntry.CONTENT_URI;
                Log.d(LOG_TAG, "delete() rowsDeleted: " + rowsDeleted);
                break;

            }
            // "movie/#"
            case MOVIE_WITH_ID: {
                rowsDeleted = mMoviesDbHelper.getReadableDatabase().delete(
                        PopularMoviesContract.MovieEntry.TABLE_NAME,
                        sMovieIdSelection,
                        selectionArgs

                );
                Log.d(LOG_TAG, "delete() rowsDeleted: " + rowsDeleted);
                break;
            }
            // "image"
            case IMAGE: {

            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        //DOESN'T WORK IF URI INCLUDES MOVIE ID ?
        //Register to watch a content URI for changes:
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsDeleted;

    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        int rowsUpdated;
        switch (sUriMatcher.match(uri)) {

            // "image/#"
            case IMAGE_WITH_ID: {

            }
            // "image"
            case IMAGE: {

                rowsUpdated = mMoviesDbHelper.getReadableDatabase().update(
                        PopularMoviesContract.ImageEntry.TABLE_NAME,
                        contentValues,
                        sFavoriteMovieIdSelection,
                        selectionArgs

                );
                Log.d(LOG_TAG, "update() rowsUpdated: " + rowsUpdated);
                break;

            }

            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        //Register to watch a content URI for changes:
        getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }
}
