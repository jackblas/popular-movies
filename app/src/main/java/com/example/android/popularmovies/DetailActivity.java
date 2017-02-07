package com.example.android.popularmovies;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * DetailActivity is a 'host' activity for the DetailFragment
 */

public class DetailActivity extends AppCompatActivity implements DetailFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //Add DetailFragment:
        if(savedInstanceState==null){

            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAILS_URI, getIntent().getData());

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_details_container, fragment)
                    .commit();


        }
        //No Action bar shadow
        //getSupportActionBar().setElevation(0f);
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
