package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.firebase.client.DataSnapshot;


import com.firebase.client.FirebaseError;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        QRFragment.OnFragmentInteractionListener,
        ListAndSearchFragment.OnFragmentInteractionListener,
        AddDataFragment.OnFragmentInteractionListener,
        DetailFragment.OnFragmentInteractionListener,
        AddHouseFragment.OnFragmentInteractionListener{

    public House house;
    public boolean isAdmin = false;
    public DetailFragment detailFragment;

    public boolean fromMaps = false;

    public House garage;

    public Map<String, Car> Cars = new HashMap<String, Car>();
    public Map<String, House> Houses = new HashMap<String, House>();

    public Location publicPos;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);

        getAllCars();

        //add the toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                updateLocation(location);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        // Register the listener with the Location Manager to receive location updates
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);


        //Navigate to the camera view
        getSupportFragmentManager().popBackStack();
        QRFragment fragment = new QRFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();

    }

    private void updateLocation(Location location) {

        publicPos = location;

    }

    //Fetch all cars from the database and store them in a map,
    // which later can be accessed locally
    private void getAllCars() {

        String DBUrl = "https://tnm082-indoor.firebaseio.com/";
        Firebase DB = new Firebase(DBUrl);

        DB.child("bilar").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                for (DataSnapshot postSnapshot: dataSnapshot.getChildren()) {

                    Car car = postSnapshot.getValue(Car.class);
                    addCar(postSnapshot.getKey(), car);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }

    //Override the back button when on qr fragment
    @Override
    public void onBackPressed() {

        //get current fragment
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);


        //Ugly fix: Map fragment could not be created twice so when backing to that fragment, it would crach
        //Instead map is now removed onDestroy, and on back the view is recreated.
        if(f instanceof ListAndSearchFragment && fromMaps) {
            getSupportFragmentManager().popBackStack();
            AddHouseFragment fragment = new AddHouseFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
            return;
        }

        //Even uglier fix... Dont try this at home
        else if(f instanceof AddHouseFragment && fromMaps) {
            getSupportFragmentManager().popBackStack();
            QRFragment fragment = new QRFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
            return;
        }

        //Default
        if (!(f instanceof QRFragment)) {//the fragment on which you want to handle your back press
            super.onBackPressed();
        }

        //If the current fragment is the first fragment opened,
        //do the following so that we're not left with a blank activity
        else{
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

    public Map<String, Car> getCars() { return Cars; }

    //set House
    public void setHouse(House h){ house = h; }

    public void addCar(String reg, Car c){
        Cars.put(reg, c);
    }

    public Car getCar(String reg){
        return Cars.get(reg);
    }

    public void setToolbarTitle(String s){
        Toolbar mToolbar = (Toolbar)findViewById(R.id.toolbar);
        mToolbar.setTitle(s);
    }
    public void updateCar(String platenr){
        String DBUrl = "https://tnm082-indoor.firebaseio.com/";

        //Probably not needed
        if(publicPos.getLongitude() != 0.0 && publicPos.getLatitude() != 0.0)
            Cars.get(platenr).updateLatlng(publicPos);

        Firebase DB = new Firebase(DBUrl);

        String s = publicPos.getLatitude() + ", " + publicPos.getLongitude();

        //if(Cars.get(platenr).getUsed())
        DB.child("bilar").child(platenr).child("latlng").setValue(s);

        DB.child("bilar").child(platenr).child("used").setValue(Cars.get(platenr).getUsed());
    }
}
