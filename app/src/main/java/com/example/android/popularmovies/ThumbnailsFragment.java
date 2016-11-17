package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThumbnailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ThumbnailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThumbnailsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Class variables:
    private static final String LOG_TAG = ThumbnailsFragment.class.getSimpleName();
    private static final String EXTRA_ORIGINAL_TITLE = "com.example.android.popularmovies.original_title";
    private static final String EXTRA_OVERVIEW = "com.example.android.popularmovies.overview";
    private static final String EXTRA_USER_RATING = "com.example.android.popularmovies.user_rating";
    private static final String EXTRA_RELEASE_DATE = "com.example.android.popularmovies.release_date";
    private static final String EXTRA_POSTER_PATH = "com.example.android.popularmovies.poster_path";

    private static final String BASE_URL_POPULAR = "https://api.themoviedb.org/3/movie/popular?language=en-US&page=undefined";
    private static final String BASE_URL_TOP_RATED = "https://api.themoviedb.org/3/movie/top_rated?language=en-US&page=undefined";

    // Instance variables:
    private OnFragmentInteractionListener mListener;
    private MovieRecordAdapter mMovieRecordAdapter;


    public ThumbnailsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ThumbnailsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ThumbnailsFragment newInstance(String param1, String param2) {
        ThumbnailsFragment fragment = new ThumbnailsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_thumbnails, container, false);

        mMovieRecordAdapter = new MovieRecordAdapter(getActivity(), new ArrayList<MovieRecord>());

        // Get a reference to the ListView, and attach this adapter to it.
        final GridView gridView = (GridView) rootView.findViewById(R.id.thumbnails_grid);
        gridView.setAdapter(mMovieRecordAdapter);

        // Set click listener
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                MovieRecord movie =  mMovieRecordAdapter.getItem(i);
                // Create intent to start detail activity
                showMovieDetail(movie);
            }
        });

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        //Check whether a network connection is available
        // before the app attempts to fetch data:
        if (isOnline()) {
            // Execute fetch movies task:
            FetchMoviesTask fetchMoviesTask = new FetchMoviesTask();
            fetchMoviesTask.execute();
        } else {
            // Show alert
            AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
            alertBuilder.setTitle(R.string.no_connection_alert_title);
            alertBuilder.setMessage(R.string.no_connection_alert_message_one);
            alertBuilder.setPositiveButton(android.R.string.ok,null);
            alertBuilder.create().show();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void showMovieDetail(MovieRecord movie) {

        Intent intent = new Intent(getActivity(),DetailActivity.class);

        intent.putExtra(EXTRA_ORIGINAL_TITLE, movie.originalTitle);
        intent.putExtra(EXTRA_POSTER_PATH,movie.posterPath);
        intent.putExtra(EXTRA_OVERVIEW,movie.overview);
        intent.putExtra(EXTRA_USER_RATING,movie.voteAverage);
        intent.putExtra(EXTRA_RELEASE_DATE,movie.releaseDate);

        startActivity(intent);

    }

    private MovieRecord[] getDataFromJson(String jsonStr)
            throws JSONException {

        // These are the names of the JSON objects that need to be extracted.
        final String TMDB_RESULTS = "results";
        final String TMDB_ID = "id";
        final String TMDB_ORIGINAL_TITLE = "original_title";
        final String TMDB_RELEASE_DATE = "release_date";
        final String TMDB_VOTE_AVERAGE = "vote_average";
        final String TMDB_OVERVIEW = "overview";
        final String TMDB_POSTER_PATH = "poster_path";

        JSONObject forecastJson = new JSONObject(jsonStr);
        JSONArray resultsArray = forecastJson.getJSONArray(TMDB_RESULTS);

        MovieRecord[] movieRecords = new MovieRecord[resultsArray.length()];

        for(int i = 0; i < resultsArray.length(); i++) {

            // Get the JSON object representing a movie
            JSONObject movie = resultsArray.getJSONObject(i);

            MovieRecord movieRecord = new MovieRecord(movie.getString(TMDB_ID));

            movieRecord.originalTitle=movie.getString(TMDB_ORIGINAL_TITLE);
            movieRecord.posterPath=movie.getString(TMDB_POSTER_PATH);
            movieRecord.overview=movie.getString(TMDB_OVERVIEW);
            movieRecord.voteAverage=movie.getString(TMDB_VOTE_AVERAGE);
            movieRecord.releaseDate=movie.getString(TMDB_RELEASE_DATE);

            movieRecords[i]=movieRecord;

        }

        return movieRecords;
    }

    public class FetchMoviesTask extends AsyncTask<Void,Void,MovieRecord[]> {
        @Override
        protected MovieRecord[] doInBackground(Void... voids) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // The raw JSON response as a string.
            String moviesJsonStr = null;

            SharedPreferences sharedPrefs =
                    PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = sharedPrefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_popular));

            Uri builtUri;
            String API_KEY_PARAM="api_key";

            if(sortBy.equals(getString(R.string.pref_sort_by_popular))){
                builtUri = Uri.parse(BASE_URL_POPULAR).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                        .build();

            } else {
                builtUri = Uri.parse(BASE_URL_TOP_RATED).buildUpon()
                        .appendQueryParameter(API_KEY_PARAM, BuildConfig.THEMOVIEDB_API_KEY)
                        .build();

            }

            try {
                // Construct the URL for the query
                URL url = new URL(builtUri.toString());

                // Create the request to OpenWeatherMap, and open the connection
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
                moviesJsonStr = buffer.toString();
                Log.v(LOG_TAG,"moviesJsonSrt= " + moviesJsonStr);

            } catch (IOException e) {
                    Log.e(LOG_TAG, "Error ", e);
                    // If the code didn't successfully get the data, there's no point in attempting
                    // to parse it.
                    return null;
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

                try {
                    return getDataFromJson(moviesJsonStr);
                } catch (JSONException e) {
                    Log.e(LOG_TAG, e.getMessage(), e);
                    e.printStackTrace();
                }

                //If error  getting or parsing data:
                return null;
        }

        @Override
        protected void onPostExecute(MovieRecord[] moviesArray) {
            //super.onPreExecute();
            if(moviesArray != null){
                mMovieRecordAdapter.clear();
                for(MovieRecord movie : moviesArray){
                    mMovieRecordAdapter.add(movie);
                }
            } else {
                // Show alert
                AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                alertBuilder.setTitle(R.string.no_data_alert_title);
                alertBuilder.setMessage(R.string.no_data_alert_message);
                alertBuilder.setPositiveButton(android.R.string.ok,null);
                alertBuilder.create().show();

            }
        }
    }

    /**
     * This utility method check to see whether a network connection is available
     * before the app attempts to connect to the network.
     *
     */
    private boolean isOnline() {

        ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}
