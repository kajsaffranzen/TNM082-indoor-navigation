package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.content.Intent;
import android.gesture.GestureOverlayView;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;


/**
 * TODO: add proper description
 */
public class DetailFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String KEY = "housename";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String POIkey;
    TextView txtViewCategory;

    private OnFragmentInteractionListener mListener;

    private Button makeOfficialButton;

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
            POIkey = getArguments().getString(KEY);
            Log.i("TOOBE", POIkey);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //pass to main activity
        ((MainActivity)getActivity()).detailFragment = this;

        View view = inflater.inflate(R.layout.fragment_detail, container, false);


        // Inflate the layout for this fragment
        GestureOverlayView gov = (GestureOverlayView)view.findViewById(R.id.gestureOverlayView);

        if (gov != null) {
            gov.setGestureVisible(false);
        }

        ImageView iv = (ImageView)view.findViewById(R.id.imageView);
        if (iv != null) {
            iv.setImageResource(R.drawable.tp5);
        }

        //Need some new design, ugly as a hairless cat.
        txtViewCategory = (TextView) view.findViewById(R.id.detail_category);

        //TODO: funkar ej att komma åt knappen på alla mobiler
        //Admin-knapp
        makeOfficialButton = (Button) view.findViewById(R.id.makeOfficialButton);

        //Change the text on the button depending on if the POI is official or not
        if(((MainActivity) getActivity()).getHouse().getPOIs2().get(POIkey).getOfficial())
            makeOfficialButton.setText("Gör inofficiell");
        else makeOfficialButton.setText("Gör officiell");

        makeOfficialButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO - updatera databasen med official-Tagg
                ((MainActivity)getActivity()).getHouse().setOfficial(POIkey);
            }
        });

        if( ((MainActivity)getActivity()).isAdmin )
            makeOfficialButton.setVisibility(View.VISIBLE);
        else
            makeOfficialButton.setVisibility(View.INVISIBLE);

        return view;
    }

    public void refreshFragment() {

                final FragmentTransaction ft = getFragmentManager().beginTransaction();
                ft.detach(this);
                ft.attach(this);
                ft.commit();
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
            //throw new RuntimeException(context.toString()
                    //+ " must implement OnFragmentInteractionListener");
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}