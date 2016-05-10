package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.nearby.Nearby;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;


/**
 * This fragment contains a QR-reader and connects to ListAndSearchFragment
 *
 */

//TODO: om man trycker på tillbaka knappen så kommer en till MainActivity
public class QRFragment extends Fragment {


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private static final String KEY = "housename";
    private static final String CAR_KEY = "carkey";
    private OnFragmentInteractionListener mListener;
    private Button goToMapsBtn;

    //This boolean prevents the app from continously scan a qr code
    // after it already has been successfully scanned
    private boolean scanned = false;

    CameraSource cameraSource;
    SurfaceView cameraView;
    TextView barcodeInfo;
    ImageView focusImage;

    public QRFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment QRFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static QRFragment newInstance(String param1, String param2) {
        QRFragment fragment = new QRFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            //IF WE WANT TO PASS ARGUMENTS
        }

        scanned = false;
    }

    //Start the map fragment and send the scaned car key.
    // The map fragment will then only display that car,
    // and no other markers, making it easier to find that car
    public void showCarOnMap(String platenr){

        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment AddHouseFragment = new AddHouseFragment();

        Bundle bundle = new Bundle();
        bundle.putString(CAR_KEY, platenr);

        AddHouseFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.fragmentContainer, AddHouseFragment)
                .addToBackStack("AddHouseFragment")
                .commit();
    }

    //go to list and search view
    public void goToListAndSearch(String houseName){

        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment ListAndSearchFragment = new ListAndSearchFragment();

        Bundle bundle = new Bundle();
        bundle.putString(KEY, houseName);

        ListAndSearchFragment.setArguments(bundle);
        fm.beginTransaction().replace(R.id.fragmentContainer, ListAndSearchFragment)
                .addToBackStack("ListAndSearchFragment")
                .commit();
    }

    //Navigate to QR view
    private void goToMapsView() {

        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment AddHouseFragment = new AddHouseFragment();

        Bundle bundle = new Bundle();

        AddHouseFragment.setArguments(bundle);
        fm.beginTransaction().replace(R.id.fragmentContainer, AddHouseFragment)
                .addToBackStack("ListAndSearchFragment")
                .commit();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_qr, container, false);
        scanned = false;

        //Add goToMapsBtn
        goToMapsBtn = (Button) view.findViewById(R.id.mapsViewBtn);
        goToMapsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToMapsView();
            }
        });

        //Hide search field and button in toolbar
        EditText tBarSearchField = (EditText) getActivity().findViewById(R.id.toolbarSearchField);
        Button tBarSearchButton = (Button) getActivity().findViewById(R.id.searchInflaterButton);
        tBarSearchField.setVisibility(View.GONE);
        tBarSearchButton.setVisibility(View.GONE);

        ((MainActivity)getActivity()).setToolbarTitle("TNM082-indoor-navigation");

        //String hus = "test";
        //goToListAndSearch(hus);
        
        cameraView = (SurfaceView) view.findViewById(R.id.camera_view);
        barcodeInfo = (TextView) view.findViewById(R.id.code_info);
        focusImage = (ImageView) view.findViewById(R.id.focusImage);

        BarcodeDetector barcodeDetector =
                new BarcodeDetector.Builder(getActivity())
                        .setBarcodeFormats(Barcode.QR_CODE)
                        .build();

        // TODO: cameraView.getHeight() & cameraView.getWidth()
        cameraSource = new CameraSource
                .Builder(getActivity(), barcodeDetector)
                .setRequestedPreviewSize(540, 540)  // get the size from the SurfaceView
                                                    //Update: can be unecessary now that
                                                    //camera matches QR-fragment
                .build();

        //callback to the surface holder
        cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    cameraSource.start(cameraView.getHolder());
                } catch (IOException ie) {
                    Log.e("CAMERA SOURCE", ie.getMessage());
                }
            }

            //This is where all the POIS will be read
            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override
            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if (barcodes.size() != 0 && !scanned) {
                    scanned = true;
                    barcodeInfo.post(new Runnable() {    // Use the post method of the TextView
                        public void run() {

                            barcodeInfo.setText(    // Update the TextView
                                    barcodes.valueAt(0).displayValue
                            );

                            if(barcodes.valueAt(0).displayValue.length() > 5 && !barcodes.valueAt(0).displayValue.substring(0,3).matches("[0-9]+") && barcodes.valueAt(0).displayValue.substring(3,6).matches("[0-9]+")){
                                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getContext());
                                Car c = ((MainActivity) getActivity()).getCar(barcodes.valueAt(0).displayValue);

                                alertDialog.setTitle("Bil alternativ")
                                        .setCancelable(true)
                                        .setNeutralButton("Använd", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ((MainActivity)getActivity()).getCar(barcodes.valueAt(0).displayValue).setUsed();
                                                dialog.cancel();
                                                scanned = false;
                                            }
                                        }).setNegativeButton("Parkera", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ((MainActivity) getActivity()).getCar(barcodes.valueAt(0).displayValue).setUsed();

                                                //Get
                                                dialog.cancel();
                                                scanned = false;
                                            }
                                         })
                                        .setPositiveButton("Hitta", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                showCarOnMap(barcodes.valueAt(0).displayValue);
                                                dialog.cancel();
                                                scanned = false;
                                            }
                                        });

                                alertDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        scanned = false;
                                    }
                                });

                                AlertDialog alertDialog2 = alertDialog.create();
                                alertDialog2.show();

                            }
                            else {
                                if (barcodes.valueAt(0).displayValue.contains("/")) {
                                    //for cars
                                    House garage = new House(barcodes.valueAt(0).displayValue);

                                } else {
                                    //go to next fragment and send text from qr
                                    goToListAndSearch(barcodes.valueAt(0).displayValue);
                                }
                            }



                        }
                    });
                }
            }
        });

        // Inflate the layout for this fragment
        return view;
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
        }
        else {
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
