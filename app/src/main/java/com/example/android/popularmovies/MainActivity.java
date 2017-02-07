package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Project Popular Movies.
 *
 * Stage One:
 * This app lets users to discover the most popular movies playing. The main screen displays a grid
 * of movie poster images. The detail screen displays movie poster along with original title,
 * synopsis, user rating and release date.
 *
 * The data for this app is fetched using API provided by themoviedb.org
 * Images are loaded using the Picasso library.
 *
 * Stage Two:
 * - Implement two-pane layout for tablets
 * - Add trailers and reviews to the Details fragment
 * - Allow users to mark a movie a favorite. Save favorite movies in local database
 *  to make them available off-line.
 * - Assess to both on-line and off-line data is managed by Content Provider
 *   and handled by Cursor Loader
 *
 *
 * @author Jack Blaszkowski
 * @version 1.0
 *
 *
 */

public class MainActivity extends AppCompatActivity implements ThumbnailsFragment.OnFragmentInteractionListener {

    @SuppressWarnings("FieldCanBeLocal")
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private SharedPreferences sp;

    private final String THUMBNAILSFRAGMENT_TAG = "TFTAG";
    private static final String DETAILFRAGMENT_TAG = "DFTAG";

    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(findViewById(R.id.movie_details_container) != null) {
            mTwoPane = true;
            //Add Details Fragment (No need to add Thumbnails Fragment since its static)
            if(savedInstanceState==null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_details_container, new DetailFragment(),DETAILFRAGMENT_TAG)
                        .commit();
            }

        } else {
            mTwoPane = false;

        }

        sp = PreferenceManager.getDefaultSharedPreferences(this);
        setMainActivityTitle(sp);

        listener = new SharedPreferences.OnSharedPreferenceChangeListener(){
            @Override
            public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
                setMainActivityTitle(sp);

            }
        };

        sp.registerOnSharedPreferenceChangeListener(listener);

        // Uncomment if using SYNC ADAPTER
        //PopularMoviesSyncAdapter.initializeSyncAdapter(this);

    }

    private void setMainActivityTitle(SharedPreferences sp){

        String sortBy = sp.getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_popular));
        if(sortBy.equals(getString(R.string.pref_sort_by_popular))){
            setTitle(getString(R.string.title_popular_movies));

        } else if (sortBy.equals(getString(R.string.pref_sort_by_rated))){
            setTitle(getString(R.string.title_top_rated_movies));

        } else {
            setTitle(getString(R.string.title_favorite_movies));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main,menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks
        int id = item.getItemId();
        if (id == R.id.action_settings){
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onFragmentInteraction(Uri contentUri) {

        if (mTwoPane) {
            // In two-pane mode, show the detail view in this activity by
            // adding or replacing the detail fragment using a
            // fragment transaction.
            Bundle args = new Bundle();
            args.putParcelable(DetailFragment.DETAILS_URI, contentUri);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(args);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_details_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .setData(contentUri);
            startActivity(intent);
        }

    }

    @Override
    protected void onResume() {
        super.onResume();

        String sortBy = sp.getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_popular));
        if(sortBy.equals(getString(R.string.pref_sort_by_popular)) || sortBy.equals(getString(R.string.pref_sort_by_rated))) {
            if (!(Utils.isOnline(this))) {
                // Show alert
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
                alertBuilder.setTitle(R.string.no_connection_alert_title);
                alertBuilder.setMessage(R.string.no_connection_alert_message_three);
                alertBuilder.setPositiveButton(android.R.string.ok, null);
                alertBuilder.create().show();
            }
        }

    }
}
