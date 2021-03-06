package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.gesture.GestureOverlayView;
import android.graphics.Color;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import java.util.List;


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

    private OnFragmentInteractionListener mListener;
    private boolean officialPOI;

    private int itemPos;
    private String test;

    //GUI Elements
    private ImageButton doneButton;
    private TextView poiName;
    private TextView poiDescription;
    private TextView poiFindText;
    private RelativeLayout offRelLay;
    private ListView lv;
    private CheckBox checkBox;


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
        setHasOptionsMenu(true);
        if (getArguments() != null) {
            POIkey = getArguments().getString(KEY);
            POIkey = Character.toLowerCase(POIkey.charAt(0)) + POIkey.substring(1);
            Log.i("TOOBE", POIkey);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //inflate view
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        //pass fragment to main activity
        ((MainActivity)getActivity()).detailFragment = this;

        //set toolbar title
        try {
            ((MainActivity) getActivity()).setToolbarTitle(((MainActivity) getActivity()).getHouse().getHouseName());
        } catch (Exception err) {
            Log.d("error", "OnCreateView get stuff from main activity" + err.getMessage());
        }

        //GUI Elements
        poiName = (TextView)view.findViewById(R.id.detailPoiNameText);
        poiDescription = (TextView)view.findViewById(R.id.detailPoiDescriptionText);
        poiFindText = (TextView)view.findViewById(R.id.detailFindText);
        checkBox = (CheckBox) view.findViewById(R.id.officalBoxDetail);
        doneButton = (ImageButton) view.findViewById(R.id.detailDoneButton);

        lv = (ListView)view.findViewById(R.id.listView);


        //get properties from the poiList and set text
        try {
            poiFindText.setText("Hitta till");// + " " + ((MainActivity) getActivity()).getHouse().getPOIs2().get(POIkey).getCategory());
            setPOIName(POIkey);


            if(((MainActivity)getActivity()).getHouse().getPOIs2().get(POIkey).getDescription().length() > 1){
                String s = ((MainActivity) getActivity()).getHouse().getPOIs2().get(POIkey).getDescription();
                s =  Character.toUpperCase(s.charAt(0)) + s.substring(1);
                poiDescription.setText(s);
            }


        } catch(Exception err) {
            Log.d("error", "OnCreateView get poi stuff " + err.getMessage());
        }
        if(((MainActivity) getActivity()).getHouse().getPOIs2().get(POIkey).getPath() != null) {
            //Add the path description from the POI in question and add to the adapter
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                    getActivity(),

                    //android.R.layout.simple_list_item_1,
                    R.layout.path_list_item_layout,

                    R.id.pathText,

                    //android.R.layout.simple_list_item_checked,
                    ((MainActivity) getActivity()).getHouse().getPOIs2().get(POIkey).getPath()
            );
            lv.setAdapter(arrayAdapter);

            //when user click on POI
            lv.setOnItemClickListener(new OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    pathCheck(lv, position);
                }
            });
        }


        //check if user is admin and show make official checkbox visible if user is admin
        officialPOI = ((MainActivity)getActivity()).getAdmin();

        if(((MainActivity) getActivity()).getHouse().getPOIs2().get(POIkey).getOfficial())
            checkBox.setChecked(true);
        else
            checkBox.setChecked(false);

        if(officialPOI == true)
            checkBox.setVisibility(View.VISIBLE);
        else
            checkBox.setVisibility(View.GONE);

        //add listeners to buttons ect
        setListeners();

        ((MainActivity)getActivity()).poi = ((MainActivity) getActivity()).getHouse().getPOIs2().get(POIkey);
        ((MainActivity)getActivity()).poiName = POIkey;

        return view;
    }

    private void setPOIName(String s){
        if(s.length() > 15 ){
            poiName.setTextSize(38);
        }
        s = Character.toUpperCase(s.charAt(0)) + s.substring(1);
        poiName.setText(s);
    }


    private void setListeners() {

        //update official in database whn button is clicked
        checkBox.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).getHouse().setOfficial(POIkey);
            }
        });

        //Done button
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Todo - Define what happens when done button is clicked
                //go back to ListAndSearchView
                FragmentManager fm = getActivity().getSupportFragmentManager();
                fm.popBackStack();
            }
        });
    }

    public void refreshFragment() {

        final FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(this);
        ft.attach(this);
        ft.commit();
    }

    public void pathCheck (ListView mView, int c) {
        for(int i = 0; i <= c; i++){
            View v;
            if(mView.getChildAt(i) != null){
                v = mView.getChildAt(i);
                //change background color
                v.setBackgroundColor(Color.parseColor("#f2f2f2"));

                //change text color
                RelativeLayout rl = (RelativeLayout) v.findViewById(R.id.pathListLayout);
                TextView tv = (TextView) rl.findViewById(R.id.pathText);
                tv.setTextColor(Color.parseColor("#cccccc"));

                //make icon invisible
                ImageView iv = (ImageView) v.findViewById(R.id.checkPath);
                iv.setVisibility(View.GONE);
            }
        }
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
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public void onCreateOptionsMenu(
            Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
    }


}
