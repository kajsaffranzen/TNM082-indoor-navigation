package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Scroller;
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

    private int counter;

    private Button addPathBtn;
    private ImageButton createPOI;
    private EditText POIname, POIdesc, POIpath;
    private ListView lv;
    private Spinner spinner;


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

        final List<String> listOfPath = new ArrayList<String>();
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listOfPath);

        lv = (ListView) view.findViewById(R.id.poi_info);

        counter = 0;

        lv.setAdapter(adapter);

        POIname = (EditText) view.findViewById(R.id.POIname);
        POIpath = (EditText) view.findViewById(R.id.POIpath);
        POIdesc = (EditText) view.findViewById(R.id.POIdesc);
        addPathBtn = (Button) view.findViewById(R.id.addPath);
        createPOI = (ImageButton) view.findViewById(R.id.createPOI);
        spinner = (Spinner) view.findViewById(R.id.catSpinner);

        fillScroller();


        //TODO: kolla ifall POIpath har text om true - kolla om användaren fill lägga till den
        //add a new POI to firebase, checks if the user has done it right or not
        createPOI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chosenCat = spinner.getSelectedItem().toString();

                if(!POIdesc.getText().toString().equals("") && !POIname.getText().toString().equals("") && chosenCat != null
                        && !listOfPath.isEmpty()){

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

                    //go back to ListAndSearchView
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    fm.popBackStack();
                }

                else
                    Toast.makeText(getActivity(), "FYLL I ALLA FÄLT DÅE", Toast.LENGTH_SHORT).show();

            }
        });

        //add new path to the POI
        //TODO: uppdatera fragmentet direkt när det har lagts till, annars måste användaren
        //själv trycka någonstans vilket är störigt
        addPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!POIpath.getText().toString().equals("")){

                    listOfPath.add(POIpath.getText().toString());
                    Toast.makeText(getActivity(), "SUCCESFULLY ADDED", Toast.LENGTH_SHORT).show();
                    counter++;

                    //Reset text field
                    POIpath.setText("");
                    POIpath.setHint("Lägg till punkt nr " + (counter + 1));
                }
                else{
                    //TODO: ändra bakgrundsfärg på textfältet & att det blir vitt igen när en fixar att
                    Toast.makeText(getActivity(), "Fyll i fältet korrekt din ko", Toast.LENGTH_SHORT).show();
                    POIpath.setHint("Fyll i korrekt");
                    //POIpath.setBackgroundResource(Color.RED);
                    //POIpath.setBackgroundColor(Color.RED);
                }

            }
        });

        //TODO Implementera så man inte kan klicka bort objekten från listan med ett vanligt klick
        //removes the chosen item from the list and updates it
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                listOfPath.remove(position);
                adapter.notifyDataSetChanged();
                counter--;
                POIpath.setHint("Lägg till punkt nr " + (counter + 1));
            }

        });




        return view;
    }


    //fill the scroller with categories
    public void fillScroller(){
        if(!categoryList.contains("Övrigt"))
            categoryList.add("Övrigt");

        spinner.setAdapter(new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categoryList));
    }

    public void checkAdmin(){}



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
