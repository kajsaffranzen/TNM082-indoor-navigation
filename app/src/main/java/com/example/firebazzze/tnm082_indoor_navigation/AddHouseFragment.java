package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.InflateException;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AddHouseFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AddHouseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AddHouseFragment extends Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final String CAR_KEY = "carkey";

    private GoogleMap mMap;
    private Button addPOIBtn;
    private Button goToQRBtn;
    private CheckBox carFilt, houseFilt;
    private static final String KEY = "housename";
    private String currentMarkerName = null;

    private SupportMapFragment mapFragment;
    private View view;
    private ViewGroup mContainter;

    private List<String> houseNameList;
    private Map<String, House> houseMap;
    private Map<String, Marker> markerMap;

    //Layouts
    RelativeLayout mapsFragmentLayout;

    //Toolbar
    private EditText searchField;
    private Button searchInflaterB;

    //Searching items
    private ListView listSearch;
    private List<String> searchResults;
    private ArrayAdapter<String> searchListAdapter;

    private ProgressBar mapsLoadingPanel;

    // TODO: Rename and change types of parameters
    private String platenr;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    public AddHouseFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AddHouseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static AddHouseFragment newInstance(String param1, String param2) {
        AddHouseFragment fragment = new AddHouseFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            platenr = getArguments().getString(CAR_KEY);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        if(view != null && this.mContainter == container){
            ViewGroup parent = (ViewGroup) view.getParent();
            if(parent != null){
                parent.removeView(view);
            }
        }
        else{
            try{
                // Inflate the layout for this fragment
                view = inflater.inflate(R.layout.fragment_add_house, container, false);
                this.mContainter = container;
            } catch (InflateException e){
                Log.d("e", "Inflateexception");
            }
        }


        ((MainActivity)getActivity()).setToolbarTitle("Karta");

        //To get the back button to work properly
        ((MainActivity)getActivity()).fromMaps = true;

        //Set toolbar title
        ((MainActivity)getActivity()).setToolbarTitle("Karta");

        //Get Map component
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Initialize the house map
        houseMap = new HashMap<>();
        houseNameList = new ArrayList<>();
        markerMap = new HashMap<>();

        //Add POI button
        addPOIBtn = (Button) view.findViewById(R.id.addPoiBtn);

        //Add qr view button
        goToQRBtn = (Button) view.findViewById(R.id.qrViewBtn);

        carFilt = (CheckBox) view.findViewById(R.id.carFilter);
        houseFilt = (CheckBox) view.findViewById(R.id.houseFilter);

        //Layout_______________________
        mapsFragmentLayout = (RelativeLayout) view.findViewById(R.id.mapsFragmentLayout);

        //Toolbar______________________
        //search field in toolbar
        searchField = (EditText) getActivity().findViewById(R.id.toolbarSearchField);
        searchField.setVisibility(View.GONE);

        //search inflater button
        searchInflaterB = (Button) getActivity().findViewById(R.id.searchInflaterButton);
        searchInflaterB.setVisibility(View.VISIBLE);

        //Search List Stuff_____________
        //List for search results
        searchResults = new ArrayList<String>();
        listSearch = (ListView) view.findViewById(R.id.mapsViewSearchList);
        searchListAdapter = new ArrayAdapter<String>(
                getContext(),R.layout.item_layout_search,searchResults);
        listSearch.setAdapter(searchListAdapter);

        Log.i("tester", "here: " + platenr);

        if(platenr != null){
            carFilt.setChecked(false);
            houseFilt.setChecked(false);
        }else{
            carFilt.setChecked(true);
            houseFilt.setChecked(true);
        }



        //Add progressbar
        mapsLoadingPanel = (ProgressBar) view.findViewById(R.id.mapsLoadingPanel);

        setListeners();

        return view;
    }

    private void findCar() {
        Car c = ((MainActivity) getActivity()).getCar(platenr);

        List<String> coords = Arrays.asList(c.getLatlng().split(","));
        LatLng carPos = new LatLng(Double.parseDouble(coords.get(0)), Double.parseDouble(coords.get(1)));
        Marker m;
        //Car unavailable
        if(c.getUsed()) {
            m = mMap.addMarker(new MarkerOptions()
                    .title(platenr)
                    .snippet("Upptagen")
                    .position(carPos)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_car_gray))
            );
        }//Car available
        else {
            m = mMap.addMarker(new MarkerOptions()
                    .title(platenr)
                    .position(carPos)
                    .snippet("Ledig")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_car))
            );
        }
        markerMap.put(platenr, m);

        // Zoom in on car position when map is finished loaded
        final LatLng dPos = carPos;
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(dPos.latitude, dPos.longitude), 15));
            }
        });
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();
    }

    private void addMarkers() {
        String DBUrl = "https://tnm082-indoor.firebaseio.com/";
        //String DBUrl = "https://coord-test.firebaseio.com/"; //dummy
        Firebase DB = new Firebase(DBUrl);

        //Display device location
        Location deviceLocation = ((MainActivity) getActivity()).publicPos;
        LatLng devicePos = new LatLng(58.410318, 15.614802); //LKPG default

        if(deviceLocation != null){
            devicePos = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
        }
        else
            Toast.makeText((MainActivity)getActivity(), "Din position kunde inte hittas",
                    Toast.LENGTH_LONG).show();

        Marker m = mMap.addMarker(new MarkerOptions()
                .title("Din position")
                .position(devicePos)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        );

        markerMap.put("DeviceLoc", m);

        //Display all buildings on the map
        DB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //TODO - remove this when all Houses/Cars has latLng
                if(dataSnapshot.child("latlng").getValue() == null)
                    return;

                List<String> coordList = Arrays.asList(dataSnapshot.child("latlng").getValue().toString().split(","));
                LatLng newMarkerCoords = new LatLng( Double.parseDouble(coordList.get(0)), Double.parseDouble(coordList.get(1)));

                //*****
                    //Store houses in a list in order to make search smoother
                    houseMap.put(dataSnapshot.getKey(), new House(dataSnapshot.getKey()));
                //*****

                //Set marker on map
                Marker m = mMap.addMarker(new MarkerOptions()
                        .title(dataSnapshot.getKey())
                        .snippet("klicka för att se mer")
                        .position(newMarkerCoords)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_house_medium))
                );

                if(!houseFilt.isChecked()) m.setVisible(false);

                markerMap.put(dataSnapshot.getKey(), m);

                //Remove progress bar as data is loaded
                mapsLoadingPanel.setVisibility(View.GONE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            @Override
            public void onCancelled(FirebaseError firebaseError) {}
        });

        //Display all cars on the map
        Map<String, Car> carMap = ((MainActivity)getActivity()).getCars();
        for(Map.Entry<String, Car> c : carMap.entrySet()){

            List<String> coordList = Arrays.asList(c.getValue().getLatlng().split(","));
            LatLng newMarkerCoords = new LatLng( Double.parseDouble(coordList.get(0)), Double.parseDouble(coordList.get(1)));

            BitmapDescriptor markerColor;

            Marker ms;

            //Car Unavailable
            if(c.getValue().getUsed()) {
                ms = mMap.addMarker(new MarkerOptions()
                        .title(c.getKey())
                        .snippet("Upptagen")
                        .position(newMarkerCoords)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_car_gray))
                );
            }//Car available
            else {
                ms = mMap.addMarker(new MarkerOptions()
                        .title(c.getKey())
                        .position(newMarkerCoords)
                        .snippet("Ledig")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_car))
                );
            }
            if(!carFilt.isChecked()) ms.setVisible(false);

            markerMap.put(c.getKey(), ms);
        }
    }

    //Set Listeners
    private void setListeners() {
        addPOIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPoiPopup();
            }
        });

        carFilt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (Map.Entry<String, Marker> entry : markerMap.entrySet()) {
                        String s = entry.getKey();
                        if (s.length() > 5 && !s.substring(0, 3).matches("[0-9]+") && s.substring(3, 6).matches("[0-9]+")) {
                            entry.getValue().setVisible(true);
                        }
                    }
                }
                else{
                    for (Map.Entry<String, Marker> entry : markerMap.entrySet()) {
                        String s = entry.getKey();
                        if (s.length() > 5 && !s.substring(0, 3).matches("[0-9]+") && s.substring(3, 6).matches("[0-9]+")) {
                            entry.getValue().setVisible(false);
                        }
                    }
                }
            }
        });

        houseFilt.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    for (Map.Entry<String, Marker> entry : markerMap.entrySet()) {
                        String s = entry.getKey();
                        if (s.equals("DeviceLoc") || s.length() > 5 && !s.substring(0, 3).matches("[0-9]+") && s.substring(3, 6).matches("[0-9]+")) {
                            //Stupid
                        }else{
                            entry.getValue().setVisible(true);
                        }
                    }
                }
                else{
                    for (Map.Entry<String, Marker> entry : markerMap.entrySet()) {
                        String s = entry.getKey();
                        if (s.equals("DeviceLoc") || s.length() > 5 && !s.substring(0, 3).matches("[0-9]+") && s.substring(3, 6).matches("[0-9]+")) {
                            //Stupid
                        }else{
                            entry.getValue().setVisible(false);
                        }
                    }
                }
            }
        });

        goToQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotToQrView();
            }
        });

        //Handle searches
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //string empty, dont search
                if (s.toString().equals("")) {
                    searchField.setHint("Sök");
                    listSearch.setVisibility(View.GONE);
                    mapsFragmentLayout.setVisibility(View.VISIBLE);
                    return;
                }

                searchResults.clear();

                //Loop through markers to find search matches
                for (Map.Entry<String, Marker> entry : markerMap.entrySet()) {
                    final String markerName = entry.getKey();
                    if(markerName.equals("DeviceLoc")) continue; //ignore user position marker name

                    //Is car
                    if(markerName.length() > 5 && !markerName.substring(0, 3).matches("[0-9]+") &&
                            markerName.substring(3, 6).matches("[0-9]+") &&
                            markerName.toString().toLowerCase().contains(s.toString().toLowerCase())) {
                        searchResults.add(markerName + " (bil)");
                    }
                }

                //HouseLoop
                for (Map.Entry<String, House> HOUSE : houseMap.entrySet()) {
                    final String hName = HOUSE.getKey();
                    boolean addAllPois = false;

                    if(hName.toLowerCase().contains(s.toString().toLowerCase())) {
                        searchResults.add(hName);
                        addAllPois = true;
                    }

                    //POIloop
                    Map<String, POI> poiMap = HOUSE.getValue().getPOIs2();
                    for(Map.Entry<String, POI> POI : poiMap.entrySet()) {
                        final String pName = POI.getKey();

                        if(pName.toLowerCase().contains(s.toString().toLowerCase()) || addAllPois)
                            searchResults.add(pName + " (" + hName + ")");
                    }
                }

                listSearch.setVisibility(View.VISIBLE);
                mapsFragmentLayout.setVisibility(View.GONE);
                searchListAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //On enter click for search
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //string empty, dont search
                if (searchField.getText().toString().equals("")) {
                    searchField.setHint("Sök");
                    listSearch.setVisibility(View.GONE);
                    mapsFragmentLayout.setVisibility(View.VISIBLE);
                    return false;
                }

                //If exact match, go to item
                for (Map.Entry<String, Marker> entry : markerMap.entrySet()) {
                    String markerName = entry.getKey();
                    if(markerName.equals("DeviceLoc")) continue;

                    if (markerName.length() > 5 && !markerName.substring(0, 3).matches("[0-9]+") &&
                            markerName.substring(3, 6).matches("[0-9]+")) {
                        //TODO - Set what happens when car is selected
                        String toastMessage = "Implement this car action";
                        Toast toast = Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    else if(markerName.contains("(")) {
                        //TODO - Set what happens when POI is selected
                        String toastMessage = "Implement this POI action";
                        Toast toast = Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT);
                        toast.show();
                    }

                    else if(markerName.toString().toLowerCase().equals(searchField.getText().toString().toLowerCase())) {
                        goToListAndSearch(markerName);
                        return false;
                    }
                }

                //Else, no exact match found
                String toastMessage = searchField.getText().toString() + " finns inte";
                Toast toast = Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT);
                toast.show();

                //Close keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                return false;
            }
        });

        //Handle onClick for searchList items
        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Dont go to listNSearch if item is a car
                if (searchResults.get(position).length() > 5 &&
                        !searchResults.get(position).substring(0, 3).matches("[0-9]+") &&
                        searchResults.get(position).substring(3, 6).matches("[0-9]+")) {

                    //TODO - Set what happens when car is selected
                    String toastMessage = "Implement this car action";
                    Toast toast = Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }

                else if(searchResults.get(position).contains("(")) {
                    //TODO - Set what happens when POI is selected
                    String toastMessage = "Implement this POI action";
                    Toast toast = Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }

                else {
                    //Go to DetailViewIP pass the POI key
                    goToListAndSearch(searchResults.get(position));
                }
            }
        });

        //Handle the toolbar search button
        searchInflaterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchField.setVisibility(View.VISIBLE);
                searchField.setText("");

                //set focus in search field and pop up keyboard
                showSoftKeyboard( searchField );
            }
        });
    }

    //Show keyboard
    private void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
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

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        Log.d("ok", "BOKAY!");

        //Remove progress bar as data is loaded
        mapsLoadingPanel.setVisibility(View.GONE);

        //get device location
        Location deviceLocation = ((MainActivity) getActivity()).publicPos;
        LatLng devicePos = new LatLng(58.410318, 15.614802); //LKPG default

        if(deviceLocation != null) {
            devicePos = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
        }

        // Zoom in on user position when map is finished loaded
        final LatLng dPos = devicePos;
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                        new LatLng(dPos.latitude, dPos.longitude), 15));
            }
        });

        setMapListeners();
        addMarkers();
        if(platenr != null) {
            findCar();
            mapsLoadingPanel.setVisibility(View.GONE);
        }



    }

    //Add listeners to the map
    private void setMapListeners() {

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if( ((MainActivity)getActivity()).isAdmin ) {

                    //Show popup and add the new house to the database
                    addPoiPopup(latLng);
                }
                else
                    mustBeAdminPopup();
                Log.d("latlng", ""+latLng.toString());
            }
        });

        //The user clicks a marker, if its not a car, its name is stored for future use
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

                //Check if marker is car, if so dont make the infoWindow clickable
                if(((MainActivity)getActivity()).getCar(marker.getTitle()) == null)
                    currentMarkerName = marker.getTitle();
                return false;
            }
        });

        //The user clicks the map to deselect a marker, and goToMarker option is hidden
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                currentMarkerName = null;
            }
        });

        //The user clicks on the infoWindow and navigates to ListAndSearch
        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                if(currentMarkerName != null)
                    goToListAndSearch(currentMarkerName);
            }
        });
    }

    //Navigate to listAndSearch view
    private void goToListAndSearch(String title) {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment ListAndSearchFragment = new ListAndSearchFragment();

        Bundle bundle = new Bundle();
        bundle.putString(KEY, title);

        ListAndSearchFragment.setArguments(bundle);
        fm.beginTransaction().replace(R.id.fragmentContainer, ListAndSearchFragment)
                .addToBackStack("ListAndSearchFragment")
                .commit();
    }

    //Navigate to QR view
    private void gotToQrView() {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment QRFragment = new QRFragment();

        Bundle bundle = new Bundle();

        QRFragment.setArguments(bundle);
        fm.beginTransaction().replace(R.id.fragmentContainer, QRFragment)
                .addToBackStack("ListAndSearchFragment")
                .commit();
    }

    //Shows a popup dialogue where user can enter input for the new POI
    private void addPoiPopup(final LatLng latLng) {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
        alert.setTitle("Lägg till ny intressepunkt");

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textName = new TextView(getContext());
        textName.setText("Name");
        TextView textType = new TextView(getContext());
        textType.setText("Type");

        //Types of items that can be added onto the map view
        List<String> typeList = new ArrayList<>();
        typeList.add("Hus");
        typeList.add("Bil");

        //A drop down menu  for adding items of specific types, cars ect.
        final Spinner typeDropDown = new Spinner(getContext());

        ArrayAdapter<String> typeAdapter = new ArrayAdapter<String>(
                getActivity(), android.R.layout.simple_spinner_dropdown_item, typeList);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeDropDown.setAdapter(typeAdapter);

        final EditText inputName = new EditText(getContext());

        linearLayout.addView(textName);
        linearLayout.addView(inputName);
        linearLayout.addView(textType);
        linearLayout.addView(typeDropDown);

        alert.setView(linearLayout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String coords = latLng.toString().replace("lat/lng: (", "");
                coords = coords.replace(")", "");

                if(typeDropDown.getSelectedItem().toString() == "Hus") {
                    House newHouse = new House(inputName.getText().toString(), coords);
                    Marker m = mMap.addMarker(new MarkerOptions()
                            .title(inputName.getText().toString())
                            .snippet("Klicka för att se mer")
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_house_medium))
                    );

                    if(!houseFilt.isChecked()) m.setVisible(false);

                    markerMap.put(inputName.getText().toString(), m);

                }

                else if(typeDropDown.getSelectedItem().toString() == "Bil") {

                    //Add new car to DB
                    String DBUrl = "https://tnm082-indoor.firebaseio.com/bilar/";
                    Firebase DB = new Firebase(DBUrl);
                    Firebase carsRef = DB.child(inputName.getText().toString());
                    Firebase newLatLng = carsRef.child("latlng");
                    newLatLng.setValue(coords);
                    Firebase newUsed = carsRef.child("used");
                    newUsed.setValue(false);

                    //Convert coord-string to latnlg
                    List<String> coordList = Arrays.asList(coords.split(","));
                    LatLng carPos = new LatLng(Double.parseDouble(coordList.get(0)), Double.parseDouble(coordList.get(1)));

                    //Add the marker to the map
                    Marker m = mMap.addMarker(new MarkerOptions()
                        .title(inputName.getText().toString())
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_car))
                    );

                    if(!carFilt.isChecked()) m.setVisible(false);

                    markerMap.put(inputName.getText().toString(), m);

                }
            }
        });

        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    //Shows a popup dialogue when user tries to add point without being admin
    private void mustBeAdminPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        alert.setTitle("Lägg till ny intressepunkt");
        alert.setMessage("Du måste vara admin för att lägga till ny punkt.");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }

    //
    private void addPoiPopup() {
        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());

        alert.setTitle("");
        if( !((MainActivity)getActivity()).isAdmin )
            alert.setMessage("Du måste vara admin för att lägga till ny punkt.");
        else
            alert.setMessage("Tryck och håll in på kartan för att skapa en ny punkt.");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }
}
