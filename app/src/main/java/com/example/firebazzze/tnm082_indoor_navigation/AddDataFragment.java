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
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/*
* Fragment to create new POI
* checks if the user has filled in all correct fields and
 * uses House to add a new POI to the House
*/

//TODO: fixa så att skapa knappen först kan tryckas på när fälten är ifyllda
public class AddDataFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ArrayList<String> categoryList;
    private List<String> listOfPath;
    private final String CAT_LIST = "catlist";

    private View view;

    private Button createPOI, addPathBtn;
    private EditText POIname, POIdesc, POIpath;



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


        ((MainActivity)getActivity()).setToolbarTitle("Täppan");

        listOfPath = new ArrayList<String>();
        int i = 1;


        fillScroller();

        POIdesc = (EditText) view.findViewById(R.id.POIdesc);
        POIname = (EditText) view.findViewById(R.id.POIname);
        POIpath = (EditText) view.findViewById(R.id.POIpath);

        createPOI = (Button) view.findViewById(R.id.createPOI);
        addPathBtn = (Button) view.findViewById(R.id.addPath);



        //add a new POI to firebase, checks if the user has done it right or not
        createPOI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: kolla om POIpath är tom - om den ej är det så är det ännu en beskrivning som behövs läggas till

                if(!POIdesc.getText().toString().equals("") && !POIname.getText().toString().equals("") && chosenCat != null){
                    House h = ((MainActivity)getActivity()).getHouse();

                    //TODO: Check if admin, then change false to true
                    h.addPOI(POIname.getText().toString(), chosenCat, POIdesc.getText().toString(), 1, false, listOfPath);

                    Toast.makeText(getActivity(), "SUCCESFULLY ADDED", Toast.LENGTH_SHORT).show();

                    //Reset text field
                    POIname.setText("");
                    POIdesc.setText("");

                    POIname.setHint("Namn");
                    POIdesc.setHint("Beskrivning");
                    chosenCat = null;
                }

                //TODO: felmarkera vilket fält som ej är korrekt ifyllt genom en röd bakgrundsfärg
                else
                    Toast.makeText(getActivity(), "FYLL I ALLA FÄLT DÅE", Toast.LENGTH_SHORT).show();

            }
        });

        //add new path to the POI
        addPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!POIpath.getText().toString().equals("")){

                    listOfPath.add(POIpath.getText().toString());
                    Toast.makeText(getActivity(), "SUCCESFULLY ADDED", Toast.LENGTH_SHORT).show();
                }

                //Reset text field
                POIpath.setText("");
                POIpath.setHint("Vägbeskrivning");

            }
        });

        return view;
    }


    //fill the scroller with categories
    public void fillScroller(){
        Spinner scroller = (Spinner) view.findViewById(R.id.catSpinner);

        if(!categoryList.contains("Övrigt"))
            categoryList.add("Övrigt");

        scroller.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categoryList));

        //getString from category
        //scroller.getSelectedItem().toString();
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
