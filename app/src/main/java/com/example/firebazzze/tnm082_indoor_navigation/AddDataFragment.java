package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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

public class AddDataFragment extends Fragment {

    private OnFragmentInteractionListener mListener;

    private ArrayList<String> categoryList;
    private List<String> listOfPath;
    private ArrayAdapter<String> adapter;
    private final String CAT_LIST = "catlist";
    private View view;
    private int counter;
    private String chosenCat;
    private Button addPathBtn;
    private ImageButton createPOI;
    private EditText POIname, POIdesc, POIpath;
    private ListView lv;
    private Spinner spinner;
    private boolean officialPOI = false;


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

        listOfPath = new ArrayList<>();
        adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_layout_add, R.id.Itemname, listOfPath);

        lv = (ListView) view.findViewById(R.id.poi_info);
        //lv.getSelectedItem().

        counter = 0;

        lv.setAdapter(adapter);

        POIname = (EditText) view.findViewById(R.id.POIname);
        POIpath = (EditText) view.findViewById(R.id.POIpath);
        POIdesc = (EditText) view.findViewById(R.id.POIdesc);
        addPathBtn = (Button) view.findViewById(R.id.addPath);
        createPOI = (ImageButton) view.findViewById(R.id.createPOI);
        spinner = (Spinner) view.findViewById(R.id.catSpinner);

        fillScroller();



        createPOI.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!POIpath.getText().toString().equals(""))
                    checkPathField();
                else
                    addNewPOI();
            }
        });

        addPathBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!POIpath.getText().toString().equals(""))
                    addPath();

                else
                    Toast.makeText(getActivity(), "Fyll i fältet", Toast.LENGTH_SHORT).show();
            }
        });

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

    //adds the path to the list
    public void addPath(){
        listOfPath.add(POIpath.getText().toString());
        counter++;
        adapter.notifyDataSetChanged();

        //Reset text field
        POIpath.setText("");
        POIpath.setHint("Lägg till punkt nr " + (counter + 1));

    }

    //checks if the the last PathDescription should be added to the path or not
    public void checkPathField(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setMessage("Vill du lägga till den sista vägbeskrivningen?");

        alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                addPath();
                Toast.makeText(getActivity(),"Vägbeskrivningen har lagts till!",Toast.LENGTH_LONG).show();
                addNewPOI();
            }
        });

        alertDialogBuilder.setNegativeButton("Nej",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                POIpath.setText("");
                POIpath.setHint("Lägg till punkt nr " + (counter));
                addNewPOI();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }


    //adds the new POI to firebazzze
    public void addNewPOI(){
        chosenCat = spinner.getSelectedItem().toString();

        if(!POIname.getText().toString().equals("") && chosenCat != null && !listOfPath.isEmpty()){

            //get AddDataChildFragment and its functions
            FragmentManager fm = getActivity().getSupportFragmentManager();
            AddDataChildFragment addDataChildFragment = (AddDataChildFragment) fm.findFragmentById(R.id.isOfficialCheckBox);

            officialPOI = addDataChildFragment.getOfficial();
            Log.i("official", ""+officialPOI);

            House h = ((MainActivity)getActivity()).getHouse();
            h.addPOI(POIname.getText().toString(), chosenCat, POIdesc.getText().toString(), 1, officialPOI, listOfPath);

            Toast.makeText(getActivity(), "SUCCESFULLY ADDED", Toast.LENGTH_SHORT).show();

            //Reset text field
            POIname.setText("");
            POIdesc.setText("");

            POIname.setHint("Namn");
            POIdesc.setHint("Beskrivning");
            chosenCat = null;

            //go back to ListAndSearchView
            fm.popBackStack();
        }

        else{
            if(POIname.getText().toString().equals("")){
                POIname.setBackgroundColor(Color.RED);

                POIname.addTextChangedListener(POInameWatcher);
            }
            if (listOfPath.isEmpty()){
                POIpath.setBackgroundColor(Color.RED);
                POIpath.addTextChangedListener(POIpathWatcher);
            }

            Toast.makeText(getActivity(), "Fyll i alla fält!", Toast.LENGTH_SHORT).show();
        }

    }

    private final TextWatcher POInameWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            POIname.setBackgroundColor(Color.WHITE);
        }

        public void afterTextChanged(Editable s) {}
    };

    private final TextWatcher POIpathWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            POIpath.setBackgroundColor(Color.WHITE);
        }

        public void afterTextChanged(Editable s) {}
    };

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
