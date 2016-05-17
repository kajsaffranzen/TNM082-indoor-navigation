package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
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
    private static final String KEY = "housename";
    private String currentMarkerName = null;

    private Map<String, House> houseMap;
    private List<String> houseNameList;

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
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_house, container, false);

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

        //Add POI button
        addPOIBtn = (Button) view.findViewById(R.id.addPoiBtn);

        //Add qr view button
        goToQRBtn = (Button) view.findViewById(R.id.qrViewBtn);

        //Add progressbar
        mapsLoadingPanel = (ProgressBar) view.findViewById(R.id.mapsLoadingPanel);

        setListeners();

        // Inflate the layout for this fragment
        return view;
    }

    private void findCar() {
        Car c = ((MainActivity) getActivity()).getCar(platenr);

        List<String> coords = Arrays.asList(c.getLatlng().split(","));
        LatLng carPos = new LatLng(Double.parseDouble(coords.get(0)), Double.parseDouble(coords.get(1)));

        //Car unavailable
        if(c.getUsed()) {
            mMap.addMarker(new MarkerOptions()
                    .title(platenr)
                    .snippet("Upptagen")
                    .position(carPos)
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_car_gray))
            );
        }

        //Car available
        else {
            mMap.addMarker(new MarkerOptions()
                    .title(platenr)
                    .position(carPos)
                    .snippet("Ledig")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_car))
            );
        }
    }

    @Override
    public void onDestroyView() {
        // TODO Auto-generated method stub
        super.onDestroyView();

        try {
            SupportMapFragment fragment = (SupportMapFragment) getChildFragmentManager()
                    .findFragmentById(R.id.map);

            FragmentTransaction ft = getActivity().getSupportFragmentManager()
                    .beginTransaction();
            ft.remove(fragment);
            ft.commit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addMarkers() {
        String DBUrl = "https://tnm082-indoor.firebaseio.com/";
        //String DBUrl = "https://coord-test.firebaseio.com/"; //dummy
        Firebase DB = new Firebase(DBUrl);

        //Display device location
        Location deviceLocation = ((MainActivity) getActivity()).publicPos;
        LatLng devicePos = new LatLng(0.0, 0.0);

        if(deviceLocation != null){
            devicePos = new LatLng(deviceLocation.getLatitude(), deviceLocation.getLongitude());
        }
        else
            Toast.makeText((MainActivity)getActivity(), "Din position kunde inte hittas",
                    Toast.LENGTH_LONG).show();

        //Maybe make use of the circle for device location
        /*mMap.addCircle(new CircleOptions()
                        .center(devicePos)
                        .radius(10000)
                        .strokeColor(Color.RED)
                        .fillColor(Color.BLUE));*/

        mMap.addMarker(new MarkerOptions()
                .title("Din position")
                .position(devicePos)
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
        );

        //Display all buildings on the map
        DB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                //TODO - remove this when all Houses/Cars has latLng
                if(dataSnapshot.child("latlng").getValue() == null)
                    return;

                List<String> coordList = Arrays.asList(dataSnapshot.child("latlng").getValue().toString().split(","));
                LatLng newMarkerCoords = new LatLng( Double.parseDouble(coordList.get(0)), Double.parseDouble(coordList.get(1)));

                //Set marker on map
                mMap.addMarker(new MarkerOptions()
                        .title(dataSnapshot.getKey())
                        .snippet("klicka för att se mer")
                        .position(newMarkerCoords)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_house_medium))
                );

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

            //Car Unavailable
            if(c.getValue().getUsed()) {
                mMap.addMarker(new MarkerOptions()
                        .title(c.getKey())
                        .snippet("Upptagen")
                        .position(newMarkerCoords)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_car_gray))
                );
            }

            //Car available
            else {
                mMap.addMarker(new MarkerOptions()
                        .title(c.getKey())
                        .position(newMarkerCoords)
                        .snippet("Ledig")
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_car))
                );
            }
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

        goToQRBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotToQrView();
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

        if(platenr == null) {
            // Add a marker in Sydney and move the camera

            LatLng nkpg = new LatLng(58, 16);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(nkpg));
        }else{
            //Remove progress bar as data is loaded
            mapsLoadingPanel.setVisibility(View.GONE);

            List<String> coords = Arrays.asList(((MainActivity)getActivity()).getCar(platenr).getLatlng().split(","));
            LatLng carPos = new LatLng(Double.parseDouble(coords.get(0)), Double.parseDouble(coords.get(1)));

            //Move camera to marker, pretty zoomed out
            mMap.moveCamera(CameraUpdateFactory.newLatLng(carPos));
            //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(carPos,2));

//            mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//                @Override
//                public void onMapLoaded() {
//                    // Zoom in on marker
//                    mMap.animateCamera(CameraUpdateFactory.zoomIn());
//                    mMap.animateCamera(CameraUpdateFactory.zoomTo(10), 4000, null);
//                }
//            });
        }
        setMapListeners();

        if(platenr == null) {
            addMarkers();
        }else{
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
                    mMap.addMarker(new MarkerOptions()
                            .title(inputName.getText().toString())
                            .snippet("Klicka för att se mer")
                            .position(latLng)
                            .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_house_medium))
                    );
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
                    mMap.addMarker(new MarkerOptions()
                        .title(inputName.getText().toString())
                        .position(latLng)
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.maps_icon_car))
                    );
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
