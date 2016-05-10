package com.example.firebazzze.tnm082_indoor_navigation;


import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
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
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements
        QRFragment.OnFragmentInteractionListener,
        ListAndSearchFragment.OnFragmentInteractionListener,
        AddDataFragment.OnFragmentInteractionListener,
        DetailFragment.OnFragmentInteractionListener{

    public House house;
    public boolean isAdmin = false;
    public DetailFragment detailFragment;

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private String mActivityTitle;
    private CharSequence mTitle;

    private DrawerAdapter mAdapter;

    //Fragments should sync position with osArray
    //Ugly solution ...
    public String[] frag = {"QRFragment", "ListAndSearchFragment"};

    //Items for drawer
    private String[] osArray = { "QR-skanning", "Intressepunkter"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);

        //add the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mActivityTitle = getTitle().toString();

        //This is for the drawerMenu
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerList = (ListView)findViewById(R.id.left_drawer);
        ArrayList<String> tester = new ArrayList<String>();

        addDrawerItems();
        setUpDrawer();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Navigate to the camera view
        getSupportFragmentManager().popBackStack();
        QRFragment fragment = new QRFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }

    /* Called whenever we call invalidateOptionsMenu() */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // If the nav drawer is open, hide action items related to the content view
        boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
        //menu.findItem(R.id.action_websearch).setVisible(!drawerOpen);
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mDrawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        Log.d("TAG", "ICON CLICK");
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        // Handle your other action bar items...

        return super.onOptionsItemSelected(item);
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

    private void addDrawerItems() {
        mAdapter = new DrawerAdapter(this, osArray);
        mDrawerList.setAdapter(mAdapter);
    }

    private void setUpDrawer(){

        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

        mDrawerToggle = new ActionBarDrawerToggle(
                this, /* host Activity */
                mDrawerLayout, /* DrawerLayout object */
                R.string.drawer_open,
                R.string.drawer_close
        ) {

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                getSupportActionBar().setTitle("Navigation!");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                getSupportActionBar().setTitle(mActivityTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Set the drawer toggle as the DrawerListener
        mDrawerToggle.setDrawerIndicatorEnabled(true);
        mDrawerLayout.addDrawerListener(mDrawerToggle);
    }

    //Used to have a public house, Get House
    public House getHouse(){ return house; }

    //set House
    public void setHouse(House h){ house = h; }

    public void setToolbarTitle(String s){
        getSupportActionBar().setTitle(s);
    }

    /*Feels like this should be moved out of here..*/
    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            
            selectItem(position);
        }
    }

    private void selectItem(int position) {

        changeFragment(frag[position]);

        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerList);
    }

    /** Swaps fragments in the main content view */
    private void changeFragment(String currentFrag){

        Fragment fragment;
        boolean flag = false;
        FragmentManager fragmentManager = getSupportFragmentManager();
        switch(currentFrag) {
            default:
            case "QRFragment":
                fragment = new QRFragment();
                flag = true;
                break;
            case "ListAndSearchFragment":
                fragment = new ListAndSearchFragment();
                flag = true;
                break;
        }

        if(flag){
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(currentFrag)
                    .commit();
        }
    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }
}
