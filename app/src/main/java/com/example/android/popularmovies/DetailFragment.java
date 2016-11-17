package com.example.android.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DetailFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment {
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
    private static final String LOG_TAG = ThumbnailsFragment.class.getSimpleName();
    // Package name used as qualifier in keys for extras to prevent name collision with extras from other apps:
    private static final String EXTRA_ORIGINAL_TITLE = "com.example.android.popularmovies.original_title";
    private static final String EXTRA_OVERVIEW = "com.example.android.popularmovies.overview";
    private static final String EXTRA_USER_RATING = "com.example.android.popularmovies.user_rating";
    private static final String EXTRA_RELEASE_DATE = "com.example.android.popularmovies.release_date";
    private static final String EXTRA_POSTER_PATH = "com.example.android.popularmovies.poster_path";

    private static final String BASE_URL_IMAGES_W500 = "http://image.tmdb.org/t/p/w500/";
    private static final String DATE_PATTERN = "yyyy-MM-dd";

    /*
     * Instance variables
     */
    private OnFragmentInteractionListener mListener;

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
        // Inflate the layout for this fragment
        View rootView =  inflater.inflate(R.layout.fragment_detail, container, false);

        // Get extras:
        Intent intent = getActivity().getIntent();
        if (intent != null) {
            if(intent.hasExtra(EXTRA_POSTER_PATH)) {
                String posterPath = intent.getStringExtra(EXTRA_POSTER_PATH);
                ImageView imageView = (ImageView) rootView.findViewById(R.id.poster_image_detail);
                ImageView imageViewBg = (ImageView) rootView.findViewById(R.id.poster_image_detail_bg);

                if(isOnline()) {
                    // Fetch the image
                    Picasso.with(getContext()).load(BASE_URL_IMAGES_W500 + posterPath).into(imageViewBg);
                    Picasso.with(getContext()).load(BASE_URL_IMAGES_W500 + posterPath).into(imageView);
                } else {
                    // Show alert
                    AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());
                    alertBuilder.setTitle(R.string.no_connection_alert_title);
                    alertBuilder.setMessage(R.string.no_connection_alert_message_two);
                    alertBuilder.setPositiveButton(android.R.string.ok, null);
                    alertBuilder.create().show();
                }
            }
            if(intent.hasExtra(EXTRA_OVERVIEW)){
                String overviewString = intent.getStringExtra(EXTRA_OVERVIEW);
                TextView textView = (TextView) rootView.findViewById(R.id.overview);
                textView.setText(overviewString);
            }
            if(intent.hasExtra(EXTRA_ORIGINAL_TITLE)){
                String title = intent.getStringExtra(EXTRA_ORIGINAL_TITLE);
                TextView textView = (TextView) rootView.findViewById(R.id.original_title_detail);
                textView.setText(title);
            }
            if(intent.hasExtra(EXTRA_USER_RATING)){
                String rating = intent.getStringExtra(EXTRA_USER_RATING);
                TextView textView = (TextView) rootView.findViewById(R.id.user_rating);
                textView.setText(rating);
            }
            if(intent.hasExtra(EXTRA_RELEASE_DATE)){
                String releaseDate = intent.getStringExtra(EXTRA_RELEASE_DATE);
                TextView textView = (TextView) rootView.findViewById(R.id.release_date);
                textView.setText(formatDate(releaseDate, DATE_PATTERN));
            }

        }

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

    /**
     * This utility functions formats date string to a default locale style.
     * @param dateStrIn Date string
     * @param pattern   Date pattern
     * @return Localized date string
     */
    private String formatDate(String dateStrIn, String pattern){

        SimpleDateFormat oldFormat = new SimpleDateFormat(pattern);
        DateFormat newFormat = SimpleDateFormat.getDateInstance(SimpleDateFormat.DEFAULT, Locale.getDefault());

        String dateStrOut = dateStrIn;
        try {
            Date date = oldFormat.parse(dateStrIn);
            dateStrOut = newFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dateStrOut;
    }
}
