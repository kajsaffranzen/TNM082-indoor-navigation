package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

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

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Add POI button
        addPOIBtn = (Button) view.findViewById(R.id.addPoiBtn);

        setListeners();

        // Inflate the layout for this fragment
        return view;
    }

    //Set Listeners
    private void setListeners() {
        addPOIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPoiPopup();
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
    }

    //Add listeners to the map
    private void setMapListeners() {

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if( ((MainActivity)getActivity()).isAdmin )
                    addPoiPopup(latLng);
                else
                    mustBeAdminPopup();
            }
        });
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
        textDesc.setText("Description");

        final EditText inputName = new EditText(getContext());
        final EditText inputDesc = new EditText(getContext());

        linearLayout.addView(textName);
        linearLayout.addView(inputName);
        linearLayout.addView(textDesc);
        linearLayout.addView(inputDesc);

        alert.setView(linearLayout);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {

                mMap.addMarker(new MarkerOptions()
                        .title(inputName.getText().toString())
                        .snippet(inputDesc.getText().toString())
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
        alert.setMessage("Hold on map to create a point of interest at that location");
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
            }
        });

        alert.show();
    }
}
