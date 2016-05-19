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

import android.widget.LinearLayout;

import android.widget.ImageButton;

import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
    private final String NEW_CATEGORY = "Lägg till ny kategori";

    private View view;
    //private int counter;
    private String chosenCat;
    private Button addPathBtn;
    private Button btnAddPath;

    private ImageButton createPOI;
    private EditText POIname, POIdesc, POIpath;
    private ListView lv;
    private Spinner spinner;

    private String spinnerText;
    private String addCat;
    private String oldPOIname;

    private boolean officialPOI = false;

    private ArrayAdapter<String> spinnerAdapter;

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

        ((MainActivity)getActivity()).setToolbarTitle("Lägg till intressepunkter");

        listOfPath = new ArrayList<>();
       // adapter = new ArrayAdapter<String>(getActivity(), R.layout.item_layout_add, R.id.Itemname, listOfPath);


        lv = (ListView) view.findViewById(R.id.poi_info);
        //lv.getSelectedItem().

       // counter = 0;

        //lv.setAdapter(adapter);

        POIname = (EditText) view.findViewById(R.id.POIname);
        POIpath = (EditText) view.findViewById(R.id.POIpath);
        POIdesc = (EditText) view.findViewById(R.id.POIdesc);
        addPathBtn = (Button) view.findViewById(R.id.addPath);
        createPOI = (ImageButton) view.findViewById(R.id.createPOI);
        spinner = (Spinner) view.findViewById(R.id.catSpinner);

        fillScroller();

        if(((MainActivity)getActivity()).poi != null && ((MainActivity)getActivity()).poiName !=null && ((MainActivity)getActivity()).fromUpdate){
            POIname.setText(((MainActivity)getActivity()).poiName);
            oldPOIname = ((MainActivity)getActivity()).poiName;
            POIdesc.setText(( (MainActivity)getActivity()).poi.getDescription());
            ArrayAdapter myAdap = (ArrayAdapter) spinner.getAdapter();
            int spinnerPos = myAdap.getPosition(((MainActivity)getActivity()).poi.category);
            spinner.setSelection(spinnerPos);
            for(int i = 0; i < ((MainActivity) getActivity()).poi.getPath().size() ; i++) {
                listOfPath.add(((MainActivity) getActivity()).poi.getPath().get(i));
            }

        }

        adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, listOfPath);
        lv.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                spinnerText = spinner.getItemAtPosition(position).toString();

                if(spinnerText == NEW_CATEGORY)
                    addCatPopup(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        //final Button
                btnAddPath = (Button) view.findViewById(R.id.POIaddPath);
        ImageButton btnPathDone = (ImageButton) view.findViewById(R.id.POIpathDone);
        final RelativeLayout rl = (RelativeLayout) view.findViewById(R.id.test);
        final RelativeLayout rl2 = (RelativeLayout) view.findViewById(R.id.test2);

        btnAddPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //btnAddPath.setBackgroundColor(Color.color);
                btnAddPath.setBackgroundColor(getContext().getResources().getColor(R.color.colorPrimary));
                rl2.setVisibility(View.GONE);
                rl.setVisibility(View.VISIBLE);
            }
        });

        btnPathDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!POIpath.getText().toString().equals(""))
                    checkPathField();

                rl.setVisibility(View.GONE);
                rl2.setVisibility(View.VISIBLE);
                btnAddPath.setText("Ändra vägbeskrivning");
            }
        });

        btnAddPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl2.setVisibility(View.GONE);
                rl.setVisibility(View.VISIBLE);
            }
        });

        btnPathDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rl.setVisibility(View.GONE);
                rl2.setVisibility(View.VISIBLE);
                btnAddPath.setText("Ändra vägbeskrivning");
            }
        });

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
               // counter--;
                POIpath.setHint("Lägg till punkt nr " + (listOfPath.size() + 1));
            }

        });

        return view;
    }


    //fill the scroller with categories
    public void fillScroller(){
        if(!categoryList.contains("Övrigt") && !categoryList.contains("övrigt"))
            categoryList.add("Övrigt");
        if(!categoryList.contains(NEW_CATEGORY))
            categoryList.add(NEW_CATEGORY);

        spinnerAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, categoryList);
        spinner.setAdapter(spinnerAdapter);
    }

    //adds the path to the list
    public void addPath(){
        listOfPath.add(POIpath.getText().toString());
        //counter++;
        adapter.notifyDataSetChanged();

        //Reset text field
        POIpath.setText("");
        POIpath.setHint("Lägg till punkt nr " + (listOfPath.size() + 1));

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
            }
        });

        alertDialogBuilder.setNegativeButton("Nej",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                POIpath.setText("");

                POIpath.setHint("Lägg till punkt nr " + (listOfPath.size() + 1));
                addNewPOI();
            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }




    //adds the new POI to firebazzze
    public void addNewPOI(){
        chosenCat = spinner.getSelectedItem().toString();

        //add new category if the user chose "Lägg till ny kategori"
        if(chosenCat == NEW_CATEGORY)
            chosenCat = addCat;

        if(!POIname.getText().toString().equals("") && chosenCat != null && !listOfPath.isEmpty()){

            //get AddDataChildFragment and its functions
            FragmentManager fm = getActivity().getSupportFragmentManager();
            AddDataChildFragment addDataChildFragment = (AddDataChildFragment) fm.findFragmentById(R.id.isOfficialCheckBox);

            House h = ((MainActivity)getActivity()).getHouse();
            if (POIname.getText().toString() != oldPOIname) {
                h.updatePOI(POIname.getText().toString(),oldPOIname ,chosenCat, POIdesc.getText().toString(), 1, false, listOfPath);
                ((MainActivity)getActivity()).fromUpdate = false;
            } else {
                h.addPOI(POIname.getText().toString(), chosenCat, POIdesc.getText().toString(), 1, false, listOfPath);
            }

            Toast.makeText(getActivity(), "" + POIname.getText().toString() + "har lagts till", Toast.LENGTH_SHORT).show();

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

                if(btnAddPath != null) {
                    btnAddPath.setBackgroundColor(Color.RED);
                    btnAddPath.setText("Lägg till en vägbeskrivning");
                } else {
                    Log.d("ok", "btnAddPath is null!");
                }

                //POIpath.setBackgroundColor(Color.RED);
                POIpath.addTextChangedListener(POIpathWatcher);
            }

            Toast.makeText(getActivity(), "Fyll i alla fält!", Toast.LENGTH_SHORT).show();
        }

    }

    //add popup where you can add a new category
    private void addCatPopup(final int position) {

        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle(NEW_CATEGORY);

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        final EditText newCat = new EditText(getContext());

        linearLayout.addView(newCat);

        alert.setView(linearLayout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                addCat = newCat.getText().toString();
                categoryList.remove(position);
                categoryList.add(addCat);
                spinner.setSelection(position);
                categoryList.add(NEW_CATEGORY);
                spinnerAdapter.notifyDataSetChanged();
            }
        });

        alert.setNegativeButton("Avbryt", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();

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
        ((MainActivity)getActivity()).poiName = "";
        ((MainActivity)getActivity()).poi = null;
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
