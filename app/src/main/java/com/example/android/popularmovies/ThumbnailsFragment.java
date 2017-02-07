package com.example.android.popularmovies;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;

import com.example.android.popularmovies.data.PopularMoviesContract;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ThumbnailsFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ThumbnailsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ThumbnailsFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // Class variables:
    private static final String LOG_TAG = ThumbnailsFragment.class.getSimpleName();

    private static final int IMAGE_LOADER = 0;

    public static final String[] IMAGE_COLUMNS = {
            PopularMoviesContract.ImageEntry._ID,
            PopularMoviesContract.ImageEntry.COLUMN_MOVIE_ID,
            PopularMoviesContract.ImageEntry.COLUMN_IMAGE

    };

    // These indices are tied to IMAGE_COLUMNS.
    public static final int COL_REC_ID = 0;
    public static final int COL_MOVIE_ID = 1;
    public static final int COL_IMAGE = 2;

    private static final String SELECTED_KEY = "selected_position";


    // Instance variables:
    private OnFragmentInteractionListener mCallback;
    private ThumbnailsAdapter mThumbnailsAdapter;

    private int mPosition = GridView.INVALID_POSITION;
    private GridView mGridView;

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

        mThumbnailsAdapter = new ThumbnailsAdapter(getActivity(), null, 0);

        // Get a reference to the ListView, and attach this adapter to it.
        //final GridView gridView = (GridView) rootView.findViewById(R.id.thumbnails_grid);
        mGridView = (GridView) rootView.findViewById(R.id.thumbnails_grid);
        mGridView.setAdapter(mThumbnailsAdapter);

        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        final String category = sharedPrefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_popular));

        // Set click listener
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                String movieId = cursor.getString(COL_MOVIE_ID);
                Uri contentUri = PopularMoviesContract.MovieEntry.buildMovieByMovieId(movieId);

                //If not showing Favorites from the local db,
                //check if movie details can be downloaded from the web:
                if(category.equals(getString(R.string.pref_sort_by_popular)) || category.equals(getString(R.string.pref_sort_by_rated))) {
                    if (Utils.isOnline(getContext())) {
                        mCallback.onFragmentInteraction(contentUri);
                        mPosition = position;
                    } else {
                        // Show alert
                        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getContext());
                        alertBuilder.setTitle(R.string.no_connection_alert_title);
                        alertBuilder.setMessage(R.string.no_connection_alert_message_three);
                        alertBuilder.setPositiveButton(android.R.string.ok, null);
                        alertBuilder.create().show();
                    }
                } else {
                    mCallback.onFragmentInteraction(contentUri);
                    mPosition = position;
                }

            }
        });

        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {

            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        return rootView;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mCallback != null) {
            mCallback.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mCallback = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallback = null;
    }

    @Override
    public void onStart() {
        super.onStart();

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


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(IMAGE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {

        if (mPosition != GridView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        SharedPreferences sharedPrefs =
                PreferenceManager.getDefaultSharedPreferences(getActivity());
        String category = sharedPrefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_popular));
        category = sharedPrefs.getString(getString(R.string.pref_sort_by_key), getString(R.string.pref_sort_by_popular));

        String sortOrder=null;
        Uri imagesByCategoryUri = PopularMoviesContract.ImageEntry.buildImagesByCategory(category);

        return new CursorLoader(getActivity(),
                imagesByCategoryUri,
                IMAGE_COLUMNS,
                null,
                null,
                sortOrder);

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mThumbnailsAdapter.swapCursor(data);

        if (mPosition != GridView.INVALID_POSITION) {

            mGridView.smoothScrollToPosition(mPosition);
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mThumbnailsAdapter.swapCursor(null);

    }


}
