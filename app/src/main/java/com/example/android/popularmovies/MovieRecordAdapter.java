package com.example.android.popularmovies;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Jack Blaszkowski on 11/5/2016. *
 *
 */

public class MovieRecordAdapter extends ArrayAdapter<MovieRecord> {

    // Class variables:
    private static final String BASE_URL_IMAGES_W185 = "http://image.tmdb.org/t/p/w185/";

    /**
     * Custom constructor
     *
     * This custom adapter inflated the thumbnails grid with images of movie posters
     *
     * @param context           The current context.
     * @param movieRecordList   A list of MovieRecord objects
     *
     */
    public MovieRecordAdapter(Context context, List<MovieRecord> movieRecordList) {
        super(context, 0, movieRecordList);
    }

    /**
     * Provides a view for an AdapterView (here: GridView)
     *
     * @param position      Position that is requesting a view
     * @param convertView   View to populate
     * @param parent        The parent ViewGroup
     * @return              Grid view cell inflated with an image
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get MovieRecord object from the ArrayAdapter
        MovieRecord movieRecord = getItem(position);

        // Inflate the layout if a new View
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(
            R.layout.thumbnail_item, parent, false);
         }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.poster_image);
        Picasso.with(getContext()).load(BASE_URL_IMAGES_W185 + movieRecord.posterPath).into(imageView);

        return convertView;
    }


}
