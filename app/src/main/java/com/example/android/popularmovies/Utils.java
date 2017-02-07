package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.example.android.popularmovies.data.PopularMoviesDbHelper;

import java.io.ByteArrayOutputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Jacek on 1/25/2017.
 */

public class Utils {

    private static final String LOG_TAG = Utils.class.getSimpleName();
    public static final String DATE_PATTERN = "yyyy-MM-dd";


    // Converts image byte array to bitmap
    public static Bitmap getImage(byte[] image) {
        return BitmapFactory.decodeByteArray(image, 0, image.length);
    }


    // Convert from bitmap to byte array
    // TODO: change quality for tablets (70) and phones (50)
    public static byte[] getImageAsBytes(Bitmap bitmap) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, stream);
        return stream.toByteArray();

    }

    /**
     * This utility functions formats date string.
     * @param dateStrIn Date string
     * @param pattern   Date pattern
     * @return Localized date string
     */
    public static String formatDate(String dateStrIn, String pattern){

        SimpleDateFormat oldFormat = new SimpleDateFormat(pattern);
        //DateFormat newFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.DEFAULT, Locale.getDefault());
        DateFormat newFormat = new SimpleDateFormat("yyyy");

        String dateStrOut = dateStrIn;
        try {
            Date date = oldFormat.parse(dateStrIn);
            dateStrOut = newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateStrOut;
    }

    /**
     * This utility method checks to see whether a network connection is available
     * before the app attempts to connect to the network.
     *
     */
    public static boolean isOnline(Context context) {

        ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
        //return  false;
    }

    /**
     * List data in all tables in the Popular Movies database
     * @param context
     */

    public static void testDb(Context context){
        Log.d(LOG_TAG, "Content of all database tables:");

        PopularMoviesDbHelper dbHelper = new PopularMoviesDbHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor1 = db.rawQuery("SELECT * FROM image", null);
        Cursor cursor2 = db.rawQuery("SELECT * FROM movie", null);
        Cursor cursor3 = db.rawQuery("SELECT * FROM trailer", null);
        Cursor cursor4 = db.rawQuery("SELECT * FROM review", null);

        try {
            if (cursor1.moveToFirst()) {
                Log.d(LOG_TAG, "IAMGES TABLE:");

                do {
                    String row ="";
                    for(int i=0; i<(cursor1.getColumnCount()-1);i++){
                        row = row + " || " + cursor1.getString(i);
                    }
                    Log.d(LOG_TAG, row);
                }while(cursor1.moveToNext());

            } else {
                Log.d(LOG_TAG, "IMAGES TABLE EMPTY.");
            }


            if (cursor2.moveToFirst()) {
                Log.d(LOG_TAG, "MOVIE TABLE:");

                do {
                    String row ="";
                    for(int i=0; i<(cursor2.getColumnCount());i++){
                        row = row + " || " + cursor2.getString(i);
                    }
                    Log.d(LOG_TAG, row);
                }while(cursor2.moveToNext());

            } else {
                Log.d(LOG_TAG, "MOVIE TABLE EMPTY.");
            }


            if (cursor3.moveToFirst()) {
                Log.d(LOG_TAG, "TRAILER TABLE:");

                do {
                    String row ="";
                    for(int i=0; i<(cursor3.getColumnCount());i++){
                        row = row + " || " + cursor3.getString(i);
                    }
                    Log.d(LOG_TAG, row);
                }while(cursor3.moveToNext());

            } else {
                Log.d(LOG_TAG, "TRAILER TABLE EMPTY.");
            }


            if (cursor4.moveToFirst()) {
                Log.d(LOG_TAG, "REVIEW TABLE:");

                do {
                    String row ="";
                    for(int i=0; i<(cursor4.getColumnCount()-1);i++){
                        row = row + " || " + cursor4.getString(i);
                    }
                    Log.d(LOG_TAG, row);
                }while(cursor4.moveToNext());

            } else {
                Log.d(LOG_TAG, "REVIEW TABLE EMPTY.");
            }


        } finally{
            try {
                cursor1.close();
                cursor2.close();
                cursor3.close();
                cursor4.close();
            }catch(Exception e){}
            try {
                db.close();
            } catch(Exception e){}
        }
    }
}
