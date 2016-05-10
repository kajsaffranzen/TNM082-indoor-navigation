package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
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

    private GoogleMap mMap;
    private Button addPOIBtn;
    private Button goToQRBtn;
    private static final String KEY = "housename";
    private String currentMarkerName = null;

    private Map<String, House> houseMap;
    private List<String> houseNameList;

    private ProgressBar mapsLoadingPanel;

    // TODO: Rename and change types of parameters
    private String mParam1;
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_house, container, false);

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
                        .snippet("Click to see more")
                        .position(newMarkerCoords));

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

        // Add a marker in Sydney and move the camera
        LatLng nkpg = new LatLng(58, 16);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(nkpg));

        setMapListeners();

        //Add markers for existing POIs
        addMarkers();
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

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {

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
        alert.setTitle("Add new point of interest");

        LinearLayout linearLayout = new LinearLayout(getContext());
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        TextView textName = new TextView(getContext());
        TextView textDesc = new TextView(getContext());

        textName.setText("Name");
        //textDesc.setText("Description");

        final EditText inputName = new EditText(getContext());
        //final EditText inputDesc = new EditText(getContext());

        linearLayout.addView(textName);
        linearLayout.addView(inputName);
        linearLayout.addView(textDesc);
        //linearLayout.addView(inputDesc);

        alert.setView(linearLayout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                String coords = latLng.toString().replace("lat/lng: (", "");
                coords = coords.replace(")", "");

                House newHouse = new House(inputName.getText().toString(), coords);
                mMap.addMarker(new MarkerOptions()
                        .title(inputName.getText().toString())
                        //.snippet(inputDesc.getText().toString())
                        .snippet("Click to see more")
                        .position(latLng));
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

        alert.setTitle("Add new point of interest");
        alert.setMessage("You must be admin to add new points");
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
            alert.setMessage("Must be admin to add points of interest");
        else
            alert.setMessage("Hold on map to create a point of interest at that location");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }
}
