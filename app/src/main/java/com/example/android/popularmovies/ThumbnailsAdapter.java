package com.example.android.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Created by Jacek on 1/15/2017.
 */

public class ThumbnailsAdapter extends CursorAdapter {

    /**
     * Cache of the children views for a movie list item.
     */
    public static class ViewHolder {
        public final ImageView posterView;


        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.poster_image);
        }
    }


    public ThumbnailsAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.thumbnail_item, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {

        ViewHolder viewHolder = (ViewHolder) view.getTag();

        byte[] imageBlob = cursor.getBlob(ThumbnailsFragment.COL_IMAGE);
        Bitmap bMap = Utils.getImage(imageBlob);

        viewHolder.posterView.setImageBitmap(bMap);

    }


}
