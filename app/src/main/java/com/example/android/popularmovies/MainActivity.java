package com.example.android.popularmovies;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

/**
 * Project Popular Movies.
 * This app lets users to discover the most popular movies playing. The main screen displays a grid
 * of movie poster images. The detail screen displays movie poster along with original title,
 * synopsis, user rating and release date.
 *
 * The data for this app is fetched using API provided by themoviedb.org
 * Images are loaded using the Picasso library.
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // MainActivity is a 'host' for the ThumbnailsFragment
        //Add ThumbnailsFragment:
        if(savedInstanceState==null){
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.activity_main, new ThumbnailsFragment())
                    .commit();
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

    }

    private void setMainActivityTitle(SharedPreferences sp){

        String sortBy = sp.getString(getString(R.string.pref_sort_by_key),getString(R.string.pref_sort_by_popular));
        if(sortBy.equals(getString(R.string.pref_sort_by_popular))){
            setTitle(getString(R.string.title_popular_movies));

        } else {
            setTitle(getString(R.string.title_top_rated_movies));

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
    public void onFragmentInteraction(Uri uri) {

    }


}
