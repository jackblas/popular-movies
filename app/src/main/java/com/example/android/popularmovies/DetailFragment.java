package com.example.android.popularmovies;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.android.popularmovies.data.MovieProvider;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    /*
     * Class variables
     */
    private static final String LOG_TAG = DetailFragment.class.getSimpleName();

    private static final int DETAIL_LOADER = 0;

    private static final String DETAILS_KEY = "saved_indices";

    /*
     * Instance variables
     */
    private OnFragmentInteractionListener mListener;
    static final String DETAILS_URI = "URI";

    private DetailsAdapter mDetailsAdapter;
    private ListView mListView;

    Uri mUri;


    public DetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DetailFragment newInstance(String param1, String param2) {
        DetailFragment fragment = new DetailFragment();
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


    /**
     * Detail screen displays:
     * - movie poster image
     * - original title
     * - synopsis
     * - user rating
     * - release date
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(DetailFragment.DETAILS_URI);
        }

        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);

        // Get a reference to the ListView, and attach this adapter to it.
        mListView = (ListView) rootView.findViewById(R.id.listview_details);

        if(savedInstanceState !=null && savedInstanceState.containsKey(DETAILS_KEY)){
            String[] values = savedInstanceState.getStringArray(DETAILS_KEY);

            mDetailsAdapter = new DetailsAdapter(getActivity(),null,0);
            mDetailsAdapter.setValues(values);

        } else {
            mDetailsAdapter = new DetailsAdapter(getActivity(),null,0);
        }

        mListView.setAdapter(mDetailsAdapter);

        return rootView;
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
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
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        String[] values = mDetailsAdapter.getValues();
        // Save Favorite Flag, first trailer index, and first review index
        // Favorite Flag determines Favorite button's text.
        // First Trailer and review indices determine when to show Trailers and Reviews headers
        outState.putStringArray(DETAILS_KEY, values);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        if(mUri != null ){
            return new CursorLoader(
                    getActivity(),
                    //intent.getData(),
                    mUri,
                    //Since MatrixCursor is created in MovieProvider
                    //the projection is defined there rather than in DetailFragment
                    MovieProvider.MTRX_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mDetailsAdapter.swapCursor(data);
        //Log.d(LOG_TAG, "DUMP: " + DatabaseUtils.dumpCursorToString(data));
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mDetailsAdapter.swapCursor(null);

    }

}
