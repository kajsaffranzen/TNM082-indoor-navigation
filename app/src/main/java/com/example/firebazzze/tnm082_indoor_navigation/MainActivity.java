package com.example.firebazzze.tnm082_indoor_navigation;


import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.firebase.client.DataSnapshot;

import com.example.firebazzze.tnm082_indoor_navigation.House;

import com.firebase.client.FirebaseError;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class MainActivity extends AppCompatActivity implements
        QRFragment.OnFragmentInteractionListener,
        ListAndSearchFragment.OnFragmentInteractionListener,
        AddDataFragment.OnFragmentInteractionListener,
        DetailFragment.OnFragmentInteractionListener{

    public House house;
    public boolean isAdmin = false;
    public DetailFragment detailFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);

        //add the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Navigate to the camera view
        getSupportFragmentManager().popBackStack();
        QRFragment fragment = new QRFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }

    //Override the back button when on qr fragment
    @Override
    public void onBackPressed() {

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);
        if (!(f instanceof QRFragment)) {//the fragment on which you want to handle your back press
            super.onBackPressed();
        }else{
            moveTaskToBack(true);
        }
    }

    //to make the fragments work
    public void onFragmentInteraction(Uri uri){
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.menuItemAdminMode:
                item.setChecked(!item.isChecked());
                isAdmin = item.isChecked();

                //refresh the detail view in order to show/hide admin button
                if(detailFragment != null && detailFragment.isAdded())
                    detailFragment.refreshFragment();
                else
                    Log.d("test","check works");

                break;
        }

        return super.onOptionsItemSelected(item);
    }

    //Used to have a public house, Get House
    public House getHouse(){ return house; }

    //set House
    public void setHouse(House h){ house = h; }

    public void setToolbarTitle(String s){
        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle(s);
    }

}
