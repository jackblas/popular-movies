package com.example.android.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.popularmovies.data.MovieProvider;
import com.example.android.popularmovies.data.PopularMoviesContract;

/**
 * Created by Jacek on 1/23/2017.
 */

class DetailsAdapter extends CursorAdapter {

    // Class variables:
    private static final String LOG_TAG = DetailsAdapter.class.getSimpleName();

    private static final String URL_YOUTUBE = "https://www.youtube.com/watch?v=";

    private static final String VIEW_TYPE_DETAILS_STR = "details";
    private static final String VIEW_TYPE_TRAILER_STR = "trailer";
    private static final String VIEW_TYPE_REVIEW_STR = "review";

    private static final int VIEW_TYPE_DETAILS = 0;
    private static final int VIEW_TYPE_TRAILER = 1;
    private static final int VIEW_TYPE_REVIEW = 2;

    private static final int VIEW_TYPE_COUNT = 3;


    static class DetailsViewHolder  {
        final TextView titleView;
        final ImageView posterView;
        final TextView dateView;
        final TextView runtimeView;
        final TextView ratingView;
        final Button favoritesButton;
        final TextView overviewView;

        DetailsViewHolder(View view) {
            titleView = (TextView) view.findViewById(R.id.original_title_detail);
            posterView = (ImageView) view.findViewById(R.id.poster_image_detail);
            dateView = (TextView) view.findViewById(R.id.release_date);
            runtimeView = (TextView) view.findViewById(R.id.movie_runtime);
            ratingView = (TextView) view.findViewById(R.id.user_rating);
            favoritesButton = (Button) view.findViewById(R.id.favorites_button);
            overviewView = (TextView) view.findViewById(R.id.overview);
        }
    }

    static class TrailersViewHolder {
        final TextView headerView;
        final ImageView iconView;
        final TextView nameView;

        TrailersViewHolder(View view) {
            headerView = (TextView) view.findViewById(R.id.trailers_header_text);
            iconView = (ImageView) view.findViewById(R.id.trailer_item_icon);
            nameView = (TextView) view.findViewById(R.id.list_item_name_textview);
        }
    }

    static class ReviewsViewHolder {
        final TextView headerView;
        final TextView author;
        final TextView content;

        ReviewsViewHolder(View view) {
            headerView = (TextView) view.findViewById(R.id.reviews_header_text);
            author = (TextView) view.findViewById(R.id.list_item_author_textview);
            content = (TextView) view.findViewById(R.id.list_item_content_textview);

        }
    }

    // Instance variables:
    private String mMovieId;
    private int mFavoriteFlag=2;
    private  long mFirstTrailerIndex=0L;
    private  long mFirstReviewIndex=0L;

    DetailsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    void setValues(String[] values) {

        mFavoriteFlag = Integer.parseInt(values[0]);
        mFirstTrailerIndex = Long.parseLong(values[1]);
        mFirstReviewIndex = Long.parseLong(values[2]);
    }

    String[] getValues() {

        String[] values = new String[3];

        values[0] = Integer.toString(mFavoriteFlag);
        values[1] = Long.toString(mFirstTrailerIndex);
        values[2] = Long.toString(mFirstReviewIndex);

        return values;
    }

    @Override
    public int getItemViewType(int position) {

        int viewType=0;
        Cursor c = (Cursor) getItem(position);

        String viewTypeStr = c.getString(MovieProvider.MTRX_COL_VIEW_TYPE);

        switch(viewTypeStr) {
            case VIEW_TYPE_DETAILS_STR: {
                viewType = VIEW_TYPE_DETAILS;
                break;
            }
            case VIEW_TYPE_TRAILER_STR: {
                viewType = VIEW_TYPE_TRAILER;
                break;
            }
            case VIEW_TYPE_REVIEW_STR: {
                viewType = VIEW_TYPE_REVIEW;
                break;
            }
        }

        return viewType;
    }

    @Override
    public int getViewTypeCount() {
        return VIEW_TYPE_COUNT;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        int viewType = getItemViewType(cursor.getPosition());
        int layoutId = -1;
        View view=null;

        switch(viewType) {

            case VIEW_TYPE_DETAILS: {
                layoutId = R.layout.list_item_details;
                view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                DetailsViewHolder viewHolder = new DetailsViewHolder(view);
                view.setTag(viewHolder);
                break;
            }
            case VIEW_TYPE_TRAILER: {
                layoutId = R.layout.list_item_trailer;
                view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                TrailersViewHolder trailersViewHolder = new TrailersViewHolder(view);
                view.setTag(trailersViewHolder);
                break;
            }
            case VIEW_TYPE_REVIEW: {
                layoutId = R.layout.list_item_review;
                view = LayoutInflater.from(context).inflate(layoutId, parent, false);
                ReviewsViewHolder reviewsViewHolder = new ReviewsViewHolder(view);
                view.setTag(reviewsViewHolder);
                break;
            }
        }

        return view;
    }

    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        int viewType = getItemViewType(cursor.getPosition());

        switch(viewType) {

            case VIEW_TYPE_DETAILS: {

                mMovieId = cursor.getString(MovieProvider.MTRX_COL_MOVIE_ID);

                String title = cursor.getString(MovieProvider.MTRX_COL_ORIGINAL_TITLE);
                String date = cursor.getString(MovieProvider.MTRX_COL_RELEASE_DATE);
                String runtime = cursor.getString(MovieProvider.MTRX_COL_RUNTIME);
                String rating = cursor.getString(MovieProvider.MTRX_COL_USER_RATING);
                String overview = cursor.getString(MovieProvider.MTRX_COL_OVERVIEW);

                final byte[] imageBlob = cursor.getBlob(MovieProvider.MTRX_COL_IMAGE);
                Bitmap bMap = Utils.getImage(imageBlob);

                //final DetailsViewHolder dvh = (DetailsViewHolder) viewHolder;
                final DetailsViewHolder dvh = (DetailsViewHolder) view.getTag();

                dvh.posterView.setImageBitmap(bMap);
                dvh.titleView.setText(title);
                dvh.dateView.setText(Utils.formatDate(date, Utils.DATE_PATTERN));
                dvh.runtimeView.setText(context.getString(R.string.format_runtime,runtime));
                dvh.ratingView.setText(context.getString(R.string.format_rating,rating));
                dvh.overviewView.setText(overview);

                //If flag hasn't been set yet, get value from the cursor
                if(mFavoriteFlag > 1) {
                    mFavoriteFlag = cursor.getInt(MovieProvider.MTRX_COL_MOVIE_FAV);
                }

                if(mFavoriteFlag == 1) {
                    dvh.favoritesButton.setText(R.string.remove_button_text);
                } else {
                    dvh.favoritesButton.setText(R.string.mark_button_text);
                }

                dvh.favoritesButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        if (mFavoriteFlag == 1) {
                            // remove from favorites
                            Toast.makeText(context, context.getString(R.string.remove_from_favotites_toast), Toast.LENGTH_SHORT).show();

                            // Delete - If only Favorites are saved in local db:
                            deleteFavorite(context, mMovieId);

                            /* Update - For version with Sync Adapter - when all data cached:
                            ContentValues contentValues = new ContentValues();
                            contentValues.put(PopularMoviesContract.ImageEntry.COLUMN_MOVIE_FAV, 0);

                            String[] selectionArgs = new String[] {mMovieId};

                            int rowsUpdated = context.getContentResolver().update(
                                    PopularMoviesContract.ImageEntry.CONTENT_URI,
                                    contentValues,
                                    null,
                                    selectionArgs);
                             */

                            dvh.favoritesButton.setText(R.string.mark_button_text);
                            mFavoriteFlag=0;
                        } else {
                            //add to favorites
                            Toast.makeText(context, context.getString(R.string.add_to_favotites_toast), Toast.LENGTH_SHORT).show();

                            //Insert - If only Favorites are saved in local db:
                            addFavorite(context, cursor);

                            /* Update - For version with Sync Adapter - when all data cached

                            ContentValues contentValues = new ContentValues();
                            contentValues.put(PopularMoviesContract.ImageEntry.COLUMN_MOVIE_FAV, 1);

                            String[] selectionArgs = new String[] {mMovieId};

                            int rowsUpdated = context.getContentResolver().update(
                                    PopularMoviesContract.ImageEntry.CONTENT_URI,
                                    contentValues,
                                    null,
                                    selectionArgs);
                             */

                            dvh.favoritesButton.setText(R.string.remove_button_text);
                            mFavoriteFlag=1;
                        }
                    }
                });


                break;
            }
            case VIEW_TYPE_TRAILER: {

                String name = cursor.getString(MovieProvider.MTRX_COL_NAME);
                //TrailersViewHolder tvh = (TrailersViewHolder) viewHolder;
                TrailersViewHolder tvh = (TrailersViewHolder) view.getTag();
                tvh.nameView.setText(name);

                if(mFirstTrailerIndex==0) {
                    mFirstTrailerIndex = cursor.getLong(MovieProvider.MTRX_COL_ID);
                }
                // If first trailer, show header
                if(cursor.getLong(MovieProvider.MTRX_COL_ID) == mFirstTrailerIndex) {
                    tvh.headerView.setText(R.string.trailers);
                    tvh.headerView.setVisibility(View.VISIBLE);
                } else {
                    tvh.headerView.setVisibility(View.GONE);
                }

                tvh.iconView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    String trailerKey  = cursor.getString(MovieProvider.MTRX_COL_KEY);

                    Intent videoIntent = new Intent(Intent.ACTION_VIEW);
                    videoIntent.setData(Uri.parse(URL_YOUTUBE + trailerKey ));

                    if (videoIntent.resolveActivity(context.getPackageManager()) != null) {

                        if(Utils.isOnline(context)){
                            context.startActivity(videoIntent);
                        }else {
                            // Show alert
                            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
                            alertBuilder.setTitle(R.string.no_connection_alert_title);
                            alertBuilder.setMessage(R.string.no_connection_alert_message_three);
                            alertBuilder.setPositiveButton(android.R.string.ok, null);
                            alertBuilder.create().show();
                        }
                    }

                    }
                });

                break;
            }
            case VIEW_TYPE_REVIEW: {

                String author = cursor.getString(MovieProvider.MTRX_COL_AUTHOR);
                String content = cursor.getString(MovieProvider.MTRX_COL_CONTENT);
                //ReviewsViewHolder rvh = (ReviewsViewHolder) viewHolder;
                ReviewsViewHolder rvh = (ReviewsViewHolder)  view.getTag();
                rvh.author.setText(author);
                rvh.content.setText(content);

                if(mFirstReviewIndex==0) {
                    mFirstReviewIndex = cursor.getLong(MovieProvider.MTRX_COL_ID);
                }

                // If first review, show header
                if(cursor.getLong(MovieProvider.MTRX_COL_ID) == mFirstReviewIndex) {
                    rvh.headerView.setText(R.string.reviews);
                    rvh.headerView.setVisibility(View.VISIBLE);
                } else {
                    rvh.headerView.setVisibility(View.GONE);
                }

                break;
            }
        }

    }

    // Add favorite movie to local database
    private void addFavorite(Context context, Cursor cursor){

        String movieRecIdStr=null;

        if(cursor.moveToFirst()){
            do {
                String viewType = cursor.getString(MovieProvider.MTRX_COL_VIEW_TYPE);

                switch (viewType){
                    case MovieProvider.DETAILS: {
                        //Insert into Image
                        ContentValues contentValues = new ContentValues();

                        contentValues.put(PopularMoviesContract.ImageEntry.COLUMN_MOVIE_ID,cursor.getString(MovieProvider.MTRX_COL_MOVIE_ID));
                        contentValues.put(PopularMoviesContract.ImageEntry.COLUMN_MOVIE_CAT, (String) null);
                        contentValues.put(PopularMoviesContract.ImageEntry.COLUMN_MOVIE_FAV,1);
                        contentValues.put(PopularMoviesContract.ImageEntry.COLUMN_IMAGE,cursor.getBlob(MovieProvider.MTRX_COL_IMAGE));

                        Uri insertedImageUri = context.getContentResolver().insert(
                                PopularMoviesContract.ImageEntry.CONTENT_URI,
                                contentValues);

                        //Insert into Movie (Details)
                        ContentValues movieContentValues = new ContentValues();

                        movieContentValues.put(PopularMoviesContract.MovieEntry.COLUMN_MOVIE_ID,cursor.getString(MovieProvider.MTRX_COL_MOVIE_ID));
                        movieContentValues.put(PopularMoviesContract.MovieEntry.COLUMN_ORIGINAL_TITLE,cursor.getString(MovieProvider.MTRX_COL_ORIGINAL_TITLE));
                        movieContentValues.put(PopularMoviesContract.MovieEntry.COLUMN_RUNTIME,cursor.getString(MovieProvider.MTRX_COL_RUNTIME));
                        movieContentValues.put(PopularMoviesContract.MovieEntry.COLUMN_USER_RATING,cursor.getString(MovieProvider.MTRX_COL_USER_RATING));
                        movieContentValues.put(PopularMoviesContract.MovieEntry.COLUMN_OVERVIEW,cursor.getString(MovieProvider.MTRX_COL_OVERVIEW));
                        movieContentValues.put(PopularMoviesContract.MovieEntry.COLUMN_RELEASE_DATE,cursor.getString(MovieProvider.MTRX_COL_RELEASE_DATE));

                        Uri insertedMovieUri = context.getContentResolver().insert(
                                PopularMoviesContract.MovieEntry.CONTENT_URI,
                                movieContentValues);

                        movieRecIdStr=PopularMoviesContract.MovieEntry.getMovieRecIdFromUri(insertedMovieUri);

                        break;
                    }
                    case MovieProvider.TRAILER: {
                        //Insert into Trailer
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(PopularMoviesContract.TrailerEntry.COLUMN_TRAILER_ID,(String) null);
                        contentValues.put(PopularMoviesContract.TrailerEntry.COLUMN_MOVIE_KEY,Long.parseLong(movieRecIdStr));
                        contentValues.put(PopularMoviesContract.TrailerEntry.COLUMN_KEY,cursor.getString(MovieProvider.MTRX_COL_KEY));
                        contentValues.put(PopularMoviesContract.TrailerEntry.COLUMN_SITE, (String) null);
                        contentValues.put(PopularMoviesContract.TrailerEntry.COLUMN_NAME, cursor.getString(MovieProvider.MTRX_COL_NAME));

                        Uri iUri = PopularMoviesContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieRecIdStr).appendPath(PopularMoviesContract.PATH_TRAILER).build();

                        Uri insertedUri = context.getContentResolver().insert(
                                iUri,
                                contentValues);

                        break;
                    }
                    case MovieProvider.REVIEW: {
                        //Insert into Review
                        ContentValues contentValues = new ContentValues();
                        contentValues.put(PopularMoviesContract.ReviewEntry.COLUMN_REVIEW_ID,(String) null);
                        contentValues.put(PopularMoviesContract.ReviewEntry.COLUMN_MOVIE_KEY,Long.parseLong(movieRecIdStr));
                        contentValues.put(PopularMoviesContract.ReviewEntry.COLUMN_AUTHOR,cursor.getString(MovieProvider.MTRX_COL_AUTHOR));
                        contentValues.put(PopularMoviesContract.ReviewEntry.COLUMN_CONTENT, cursor.getString(MovieProvider.MTRX_COL_CONTENT));

                        Uri iUri = PopularMoviesContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieRecIdStr).appendPath(PopularMoviesContract.PATH_REVIEW).build();

                        Uri insertedUri = context.getContentResolver().insert(
                                iUri,
                                contentValues);

                        break;
                    }
                }

            }while(cursor.moveToNext());
        }

        //Restore cursor position
        cursor.moveToFirst();

    }

    // Delete favorite movie from local database
    private void deleteFavorite(Context context, String movieId) {
        String[] selectionArgs = new String[] {movieId};

        Uri iUri = PopularMoviesContract.ImageEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();

        int rowsDeletedI = context.getContentResolver().delete(
                iUri,
                null,
                selectionArgs);

        //Utils.testDb(context);

        //Delete Movie (on delete cascade - deletes records from movie, trailer and review tables.)
        Uri dUri = PopularMoviesContract.MovieEntry.CONTENT_URI.buildUpon().appendPath(movieId).build();

        int rowsDeletedD = context.getContentResolver().delete(
                dUri,
                null,
                selectionArgs);

        //Utils.testDb(context);

    }
}
