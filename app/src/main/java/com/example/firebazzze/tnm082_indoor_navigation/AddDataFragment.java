package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.FocusFinder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/*
* Fragment to create new POI
* checks if the user has filled in all correct fields and
 * uses House to add a new POI to the House
*/
public class AddDataFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ArrayList<String> categoryList;

    private final String CAT_LIST = "catlist";

    private View view;

    private Button createPOI, addPath;
    private EditText POIname, POIdesc, POIpath;

    private int pathCounter;


    private String chosenCat;

    public AddDataFragment() {
        // Required empty public constructor
    }

    public static AddDataFragment newInstance(String param1, String param2) {
        AddDataFragment fragment = new AddDataFragment();
        Bundle args = new Bundle();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //get bundle here
            categoryList = getArguments().getStringArrayList(CAT_LIST);

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_data, container, false);

        //((MainActivity)getActivity()).setToolbarTitle("Täppan");

        fillScroller();

        POIdesc = (EditText) view.findViewById(R.id.POIdesc);
        POIname = (EditText) view.findViewById(R.id.POIname);
        POIpath = (EditText) view.findViewById(R.id.POIpath);
        addPath = (Button) view.findViewById(R.id.addPath);
        createPOI = (Button) view.findViewById(R.id.createPOI);


        //add a new POI to firebase, checks if the user has done it right or not
        createPOI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!POIdesc.getText().toString().equals("") && !POIname.getText().toString().equals("") && chosenCat != null){
                    House h = ((MainActivity)getActivity()).getHouse();

                    //TODO: Check if admin, then change false to true
                    h.addPOI(POIname.getText().toString(), chosenCat, POIdesc.getText().toString(), 1, false);

                    Toast.makeText(getActivity(), "SUCCESFULLY ADDED", Toast.LENGTH_SHORT).show();

                    //Reset text field
                    POIname.setText("");
                    POIdesc.setText("");

                    POIname.setHint("Namn");
                    POIdesc.setHint("Beskrivning");
                    chosenCat = null;
                }

                else
                    Toast.makeText(getActivity(), "FYLL I ALLA FÄLT DÅE", Toast.LENGTH_SHORT).show();

            }
        });

        return view;
    }



    public void fillScroller(){

        ListView scroller = (ListView) view.findViewById(R.id.catScroller);

        scroller.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, categoryList));


        scroller.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,long arg3)
            {
                chosenCat = categoryList.get(arg2);
                Log.i("tester3", chosenCat);
            }
        });

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
}
