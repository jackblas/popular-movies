package com.example.android.popularmovies.web;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;

import com.example.android.popularmovies.BuildConfig;
import com.example.android.popularmovies.ThumbnailsFragment;
import com.example.android.popularmovies.Utils;
import com.example.android.popularmovies.data.MovieProvider;
import com.example.android.popularmovies.data.PopularMoviesContract;
import com.example.android.popularmovies.data.PopularMoviesDbHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Jacek on 2/3/2017.
 */

public class MoviesUriQueryBuilder {

    private final String LOG_TAG = MoviesUriQueryBuilder.class.getSimpleName();
    private final Context mContext;

    // Base Urls for The Movies DB
    private static final String BASE_URL_DETAILS = "https://api.themoviedb.org/3/movie/";
    private static final String BASE_URL_POPULAR = "https://api.themoviedb.org/3/movie/popular?language=en-US&page=undefined";
    private static final String BASE_URL_TOP_RATED = "https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=undefined";

    private static final String BASE_URL_IMAGES_W185 = "http://image.tmdb.org/t/p/w185/";

    // Paths
    private static final String URL_TRAILERS = "videos";
    private static final String URL_REVIEWS = "reviews";

    // Params names:
    private static final String LANGUAGE_URL_PARAM = "language";
    private static final String LANGUAGE_URL_PARAM_VALUE = "en-US";
    private static final String API_KEY_URL_PARAM = "api_key";

    //Date types - determine which URL is used:
    private static final String POPULAR = "popular";
    private static final String TOP_RATED = "rated";
    private static final String DETAILS = "details";
    private static final String TRAILER = "trailer";
    private static final String REVIEW = "review";

    private static final String VIEW_TYPE_COL =  "view_type";

    public MoviesUriQueryBuilder(Context context){
        mContext=context;
    }

    // Data types: popular, top rated, details,trailers,reviews
    public Cursor queryDataFromServer(String dataType, String movieId) {
        //Log.d(LOG_TAG, "queryDataFromServer for: " + dataType);

        Uri builtUri;

        if (dataType.equals(POPULAR)) {
            builtUri = Uri.parse(BASE_URL_POPULAR).buildUpon()
                    .appendQueryParameter(API_KEY_URL_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                    .build();
        } else if (dataType.equals(DETAILS)) {
            builtUri = Uri.parse(BASE_URL_DETAILS).buildUpon().appendPath(movieId)
                    .appendQueryParameter(API_KEY_URL_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                    .appendQueryParameter(LANGUAGE_URL_PARAM, LANGUAGE_URL_PARAM_VALUE)
                    .build();

        } else if (dataType.equals(TRAILER)) {
            builtUri = Uri.parse(BASE_URL_DETAILS).buildUpon().appendPath(movieId)
                    .appendPath(URL_TRAILERS)
                    .appendQueryParameter(API_KEY_URL_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                    .appendQueryParameter(LANGUAGE_URL_PARAM, LANGUAGE_URL_PARAM_VALUE)
                    .build();

        } else if (dataType.equals(REVIEW)) {
            builtUri = Uri.parse(BASE_URL_DETAILS).buildUpon().appendPath(movieId)
                    .appendPath(URL_REVIEWS)
                    .appendQueryParameter(API_KEY_URL_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                    .appendQueryParameter(LANGUAGE_URL_PARAM, LANGUAGE_URL_PARAM_VALUE)
                    .build();

        } else {
            builtUri = Uri.parse(BASE_URL_TOP_RATED).buildUpon()
                    .appendQueryParameter(API_KEY_URL_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                    .build();
        }

        String jsonString=getJSONString(builtUri);
        //Log.d(LOG_TAG, "queryDataFromServer jsonString: " + jsonString );
        // If network connection fails, return empty cursor, not null
        Cursor cursor=new MatrixCursor(MovieProvider.DETAIL_COLUMNS_MTRX);

        // If network connection not successful, jsonString is null:
        if(jsonString != null) {
            try {

                if (dataType.equals(POPULAR) || dataType.equals(TOP_RATED)) {
                    cursor = getMoviesFromJson(jsonString, dataType);
                } else if (dataType.equals(DETAILS)) {
                    cursor = getDetailsFromJson(jsonString);
                } else if (dataType.equals(TRAILER)) {
                    cursor = getTrailersFromJson(jsonString);
                } else if (dataType.equals(REVIEW)) {
                    cursor = getReviewsFromJson(jsonString);
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        // If jsonString is null, return empty cursor
        return cursor;
    }

    private String getJSONString(Uri uri) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            // Construct the URL for the query
            URL url = new URL(uri.toString());

            // Create the request to TMDb, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();
            if (inputStream == null) {
                // Nothing to do.
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line).append("\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return null;
            }

            // The raw JSON response as a string.
            String moviesJsonStr = buffer.toString();
            //Log.v(LOG_TAG,"moviesJsonSrt= " + moviesJsonStr);

            return moviesJsonStr;

        } catch (IOException e) {
            Log.e(LOG_TAG, "Error ", e);
        } finally{
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream.", e);
                }
            }
        }
        //If error  getting or parsing data:
        return null;

    }

    //Get movie data from json string,
    //Build Matrix Cursor (add a 'mock' _id column).
    //This cursor will be used by ThumbnailsAdapter.
    private Cursor getMoviesFromJson(String jsonStr, String category)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_POSTER_PATH = "poster_path";

        long rowIndex =0L;
        MatrixCursor matrixCursor = new MatrixCursor(ThumbnailsFragment.IMAGE_COLUMNS);
        matrixCursor.moveToFirst();

        JSONObject jasonObject = new JSONObject(jsonStr);
        JSONArray resultsArray = jasonObject.getJSONArray(TMDB_RESULTS);

        for(int i = 0; i < resultsArray.length(); i++) {

            Object[] columnValues = new Object[ThumbnailsFragment.IMAGE_COLUMNS.length];
            // Get the JSON object representing a movie
            JSONObject movie = resultsArray.getJSONObject(i);

            Bitmap bMap=null;
            HttpURLConnection urlConnection = null;
            //Get Image and convert into byte array here
            try {
                //InputStream in = new java.net.URL(BASE_URL_IMAGES_W185 + movie.getString(TMDB_POSTER_PATH)).openStream();
                URL url = new URL(BASE_URL_IMAGES_W185 + movie.getString(TMDB_POSTER_PATH));
                // Create the request to TMDb, and open the connection
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                // Read the input stream into a String
                InputStream in = urlConnection.getInputStream();

                bMap = BitmapFactory.decodeStream(in);
            } catch (Exception e) {
                Log.e("Error", e.getMessage());
                e.printStackTrace();

            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

            byte[] image = Utils.getImageAsBytes(bMap);

            columnValues[ThumbnailsFragment.COL_REC_ID] = rowIndex++;
            columnValues[ThumbnailsFragment.COL_MOVIE_ID] = movie.getString(TMDB_ID);
            columnValues[ThumbnailsFragment.COL_IMAGE] = image;

            matrixCursor.addRow(columnValues);

        }

        return  matrixCursor;

    }

    //Get movie details from json string,
    //Build Matrix Cursor (add a 'mock' _id column).
    //This cursor will be merged with trailers cursor and reviews cursor.
    //Final merged cursor will be used by the DetailsAdapter
    private Cursor getDetailsFromJson(String jsonStr)
            throws JSONException {

        final String TMDB_ID = "id";
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_RUNTIME = "runtime";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_POSTER_PATH = "poster_path";

        JSONObject movie = new JSONObject(jsonStr);

        long rowIndex =0L;
        MatrixCursor matrixCursor = new MatrixCursor(MovieProvider.DETAIL_COLUMNS_MTRX);
        matrixCursor.moveToFirst();

        Object[] columnValues = new Object[MovieProvider.DETAIL_COLUMNS_MTRX.length];

        columnValues[MovieProvider.COL_REC_ID] = rowIndex;
        columnValues[MovieProvider.COL_VIEW_TYPE] = DETAILS;
        columnValues[MovieProvider.COL_MOVIE_ID] = movie.getString(TMDB_ID);
        columnValues[MovieProvider.COL_ORIGINAL_TITLE] = movie.getString(TMDB_ORIGINAL_TITLE);
        columnValues[MovieProvider.COL_RELEASE_DATE] = movie.getString(TMDB_RELEASE_DATE);
        columnValues[MovieProvider.COL_USER_RATING] = movie.getString(TMDB_VOTE_AVERAGE);
        columnValues[MovieProvider.COL_RUNTIME] = movie.getString(TMDB_RUNTIME);
        columnValues[MovieProvider.COL_OVERVIEW] = movie.getString(TMDB_OVERVIEW);

        //check local database for favorite flag
        int favoriteFlag = checkFavoriteFlag(movie.getString(TMDB_ID));
        columnValues[MovieProvider.COL_MOVIE_FAV] = favoriteFlag;

        Bitmap bMap=null;
        //Get Image and convert into byte array here
        try {
            InputStream in = new java.net.URL(BASE_URL_IMAGES_W185 + movie.getString(TMDB_POSTER_PATH)).openStream();
            bMap = BitmapFactory.decodeStream(in);
        } catch (Exception e) {
            Log.e("Error", e.getMessage());
            e.printStackTrace();
        }
        byte[] image = Utils.getImageAsBytes(bMap);

        columnValues[MovieProvider.COL_IMAGE] = image;

        matrixCursor.addRow(columnValues);

        return  matrixCursor;

    }

    //Get movie trailers from json string,
    //Build Matrix Cursor (add a 'mock' _id column).
    //This cursor will be merged with details cursor and reviews cursor.
    //Final merged cursor will be used by the DetailsAdapter
    private Cursor getTrailersFromJson(String jsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_ID = "id";
        final String TMDB_RESULTS = "results";
        final String TMDB_KEY = "key";
        final String TMDB_SITE = "site";
        final String TMDB_NAME = "name";

        JSONObject jasonObject = new JSONObject(jsonStr);
        JSONArray resultsArray = jasonObject.getJSONArray(TMDB_RESULTS);

        long rowIndex =0L;
        MatrixCursor matrixCursor = new MatrixCursor(MovieProvider.TRAILER_COLUMNS_MTRX);
        matrixCursor.moveToFirst();

        for(int i = 0; i < resultsArray.length(); i++) {
            // Get the JSON object representing a trailer
            JSONObject trailer = resultsArray.getJSONObject(i);

            Object[] columnValues = new Object[MovieProvider.TRAILER_COLUMNS_MTRX.length];

            columnValues[MovieProvider.TRL_REC_ID] = rowIndex++;
            columnValues[MovieProvider.TRL_COL_VIEW] = TRAILER;
            columnValues[MovieProvider.TRL_COL_KEY] = trailer.getString(TMDB_KEY);
            columnValues[MovieProvider.TRL_COL_NAME] = trailer.getString(TMDB_NAME);

            matrixCursor.addRow(columnValues);

        }

        return  matrixCursor;

    }

    //Get movie reviews from json string,
    //Build Matrix Cursor (add a 'mock' _id column).
    //This cursor will be merged with trailers cursor and details cursor.
    //Final merged cursor will be used by the DetailsAdapter
    private Cursor getReviewsFromJson(String jsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_ID = "id";
        final String TMDB_RESULTS = "results";
        final String TMDB_AUTHOR = "author";
        final String TMDB_CONTENT = "content";

        JSONObject jasonObject = new JSONObject(jsonStr);
        JSONArray resultsArray = jasonObject.getJSONArray(TMDB_RESULTS);

        long rowIndex =0L;
        MatrixCursor matrixCursor = new MatrixCursor(MovieProvider.REVIEW_COLUMNS_MTRX);
        matrixCursor.moveToFirst();

        for(int i = 0; i < resultsArray.length(); i++) {

            // Get the JSON object representing a review
            JSONObject review = resultsArray.getJSONObject(i);

            Object[] columnValues = new Object[MovieProvider.REVIEW_COLUMNS_MTRX.length];

            columnValues[MovieProvider.REV_REC_ID] = rowIndex++;
            columnValues[MovieProvider.REV_COL_VIEW] = REVIEW;
            columnValues[MovieProvider.REV_COL_AUTHOR] = review.getString(TMDB_AUTHOR);
            columnValues[MovieProvider.REV_COL_CONTENT] = review.getString(TMDB_CONTENT);

            matrixCursor.addRow(columnValues);

        }

        return  matrixCursor;
    }


    // Query local database for favorite flag
    private int checkFavoriteFlag(String movieId) {

        int flag =0;

        String [] projection = new String[]{PopularMoviesContract.ImageEntry.COLUMN_MOVIE_ID};
        String selection = PopularMoviesContract.ImageEntry.TABLE_NAME+
                "." + PopularMoviesContract.ImageEntry.COLUMN_MOVIE_ID + " = ? ";
        String [] selectionArgs = new String[]{movieId};

        PopularMoviesDbHelper dbHelper = new PopularMoviesDbHelper(mContext);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor = db.query(
                PopularMoviesContract.ImageEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        if(cursor != null && cursor.getCount()> 0){
            flag = 1;
        } else {
            flag = 0;
        }
        cursor.close();
        db.close();
        return flag;

    }
}
