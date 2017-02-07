package com.example.android.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.example.android.popularmovies.data.PopularMoviesContract.ImageEntry;
import static com.example.android.popularmovies.data.PopularMoviesContract.MovieEntry;
import static com.example.android.popularmovies.data.PopularMoviesContract.ReviewEntry;
import static com.example.android.popularmovies.data.PopularMoviesContract.TrailerEntry;

/**
 * Created by Jacek on 1/8/2017.
 */

public class PopularMoviesDbHelper extends SQLiteOpenHelper {

    private static final String LOG_TAG = PopularMoviesDbHelper.class.getSimpleName();

    // If the database schema changes, increment the database version!
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "PopularMovies.db";

    private static final String SQL_CREATE_MOVIE_ENTRIES =
            "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                    MovieEntry._ID + " INTEGER PRIMARY KEY," +
                    MovieEntry.COLUMN_MOVIE_ID + " TEXT," +
                    MovieEntry.COLUMN_ORIGINAL_TITLE + " TEXT," +
                    MovieEntry.COLUMN_RUNTIME + " TEXT," +
                    MovieEntry.COLUMN_USER_RATING + " TEXT," +
                    MovieEntry.COLUMN_OVERVIEW + " TEXT," +
                    MovieEntry.COLUMN_RELEASE_DATE + " TEXT, " +
    " UNIQUE (" + MovieEntry.COLUMN_MOVIE_ID + ") ON CONFLICT REPLACE);";

    private static final String SQL_CREATE_TRAILER_ENTRIES =
            "CREATE TABLE " + TrailerEntry.TABLE_NAME + " (" +
                    TrailerEntry._ID + " INTEGER PRIMARY KEY," +
                    TrailerEntry.COLUMN_TRAILER_ID + " TEXT," +
                    TrailerEntry.COLUMN_MOVIE_KEY + " INTEGER," +
                    TrailerEntry.COLUMN_KEY + " TEXT," +
                    TrailerEntry.COLUMN_SITE + " TEXT," +
                    TrailerEntry.COLUMN_NAME + " TEXT, " +
                    // A foreign key to movie table:
                    " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                    MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") ON DELETE CASCADE, " +
                    "UNIQUE (" + TrailerEntry.COLUMN_TRAILER_ID + ", " +
                            TrailerEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE);";

    private static final String SQL_CREATE_REVIEW_ENTRIES =
            "CREATE TABLE " + ReviewEntry.TABLE_NAME + " (" +
                    ReviewEntry._ID + " INTEGER PRIMARY KEY," +
                    ReviewEntry.COLUMN_REVIEW_ID + " TEXT," +
                    ReviewEntry.COLUMN_MOVIE_KEY + " INTEGER," +
                    ReviewEntry.COLUMN_AUTHOR + " TEXT," +
                    ReviewEntry.COLUMN_CONTENT + " TEXT, " +
                    //A foreign key to movie table:
                    " FOREIGN KEY (" + TrailerEntry.COLUMN_MOVIE_KEY + ") REFERENCES " +
                    MovieEntry.TABLE_NAME + " (" + MovieEntry._ID + ") ON DELETE CASCADE, " +
                    "UNIQUE (" + ReviewEntry.COLUMN_REVIEW_ID + ", " +
                    ReviewEntry.COLUMN_MOVIE_KEY + ") ON CONFLICT REPLACE);";

    private static final String SQL_CREATE_IMAGE_ENTRIES =
            "CREATE TABLE " + ImageEntry.TABLE_NAME + " (" +
                    ImageEntry._ID + " INTEGER PRIMARY KEY," +
                    ImageEntry.COLUMN_MOVIE_ID + " TEXT," +
                    ImageEntry.COLUMN_MOVIE_CAT + " TEXT," +
                    ImageEntry.COLUMN_MOVIE_FAV + " INTEGER," +
                    ImageEntry.COLUMN_IMAGE + " BLOB, " +
    //Insert images only for new movies
    " UNIQUE (" + ImageEntry.COLUMN_MOVIE_ID + ") ON CONFLICT IGNORE);";

    private static final String SQL_DELETE_MOVIE_ENTRIES =
            "DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME;
    private static final String SQL_DELETE_TRAILER_ENTRIES =
            "DROP TABLE IF EXISTS " + TrailerEntry.TABLE_NAME;
    private static final String SQL_DELETE_REVIEW_ENTRIES =
            "DROP TABLE IF EXISTS " + ReviewEntry.TABLE_NAME;
    private static final String SQL_DELETE_IMAGE_ENTRIES =
            "DROP TABLE IF EXISTS " + ImageEntry.TABLE_NAME;

    public PopularMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(SQL_CREATE_IMAGE_ENTRIES);
        db.execSQL(SQL_CREATE_MOVIE_ENTRIES);
        db.execSQL(SQL_CREATE_TRAILER_ENTRIES);
        db.execSQL(SQL_CREATE_REVIEW_ENTRIES);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_IMAGE_ENTRIES);
        db.execSQL(SQL_DELETE_TRAILER_ENTRIES);
        db.execSQL(SQL_DELETE_REVIEW_ENTRIES);
        db.execSQL(SQL_DELETE_MOVIE_ENTRIES);
        onCreate(db);

    }
    @Override

    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);
        //This is required for 'on delete cascade' to work!
        db.execSQL("PRAGMA foreign_keys = ON;");
    }

}
