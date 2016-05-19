package com.example.firebazzze.tnm082_indoor_navigation;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.ValueEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.FirebaseError;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements
        QRFragment.OnFragmentInteractionListener,
        ListAndSearchFragment.OnFragmentInteractionListener,
        AddDataFragment.OnFragmentInteractionListener,
        AddDataChildFragment.OnFragmentInteractionListener,
        DetailFragment.OnFragmentInteractionListener,
        AboutFragment.OnFragmentInteractionListener,
        HelpFragment.OnFragmentInteractionListener,
        LoginFragment.OnFragmentInteractionListener,
        AddHouseFragment.OnFragmentInteractionListener{


    public House house;
    public boolean isAdmin = false;
    public DetailFragment detailFragment;
    public AddDataChildFragment addDataChild;
    public LoginFragment loginFragment;

    private static final String KEY = "housename";

    //Navigation drawer
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private NavigationView nvDrawer;

    public ListAndSearchFragment myCatList;
    private static final String CAT_LIST = "catlist";

    private ArrayList<String> historyList = new ArrayList<String>();

    private String mActivityTitle;
    private CharSequence mTitle;

    private Toolbar toolbar;

    public boolean fromMaps = false;

    public boolean fromUpdate = false;

    private MenuItem oldMenuItem;

    public House garage;

    public Map<String, Car> Cars = new HashMap<String, Car>();
    public Map<String, House> Houses = new HashMap<String, House>();

    public Location publicPos;

    public POI poi;
    public String poiName;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        Firebase.setAndroidContext(this);

        setContentView(R.layout.activity_main);

        getAllCars();

        //add the toolbar
        toolbar = (Toolbar) findViewById(R.id.toolbar);
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

        mActivityTitle = getTitle().toString();

        //This is for the drawerMenu
        mDrawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        mDrawerToggle = setUpDrawerToggle();

        //Content in drawerMenu
        nvDrawer = (NavigationView) findViewById(R.id.nvView);
        setUpDrawer(nvDrawer);

        mDrawerLayout.addDrawerListener(mDrawerToggle);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //Navigate to the camera view
        getSupportFragmentManager().popBackStack();
        QRFragment fragment = new QRFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragment).commit();

        /*DrawerLayout layout = (DrawerLayout) findViewById(R.id.drawer_layout);

        layout.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent ev)
            {
                hideKeyboard(view);
                return false;
            }
        });*/
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

                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {

                    Car car = postSnapshot.getValue(Car.class);
                    addCar(postSnapshot.getKey(), car);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

       /* addDataChild = (AddDataChildFragment) getSupportFragmentManager().findFragmentById(R.id.isOfficialCheckBox);

        //refresh the detail view in order to show/hide admin button
        if(detailFragment != null && detailFragment.isAdded())
            detailFragment.refreshFragment();
        else
            Log.d("test","check works");

        //refresh addDataChildFragment in order to show/hide officialPOI checkbox
        if(addDataChild != null && addDataChild.isAdded())
            addDataChild.refreshFragment();
        else
            Log.d("test", ""+addDataChild);

        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        // Pass the event to ActionBarDrawerToggle, if it returns
        // true, then it has handled the app icon touch event
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }*/
        int id = item.getItemId();


        switch (id) {

            case R.id.deletePOI:
                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
                alertDialogBuilder.setMessage("Är du säker på att du vill ta bort " + poiName);

                alertDialogBuilder.setPositiveButton("Ja", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        FragmentManager fm = getSupportFragmentManager();
                        //fm.popBackStack();
                        Fragment ListAndSearchFragment = new ListAndSearchFragment();
                        Bundle bundle = new Bundle();
                        Log.i("testing", getHouse().getHouseName());
                        bundle.putString(KEY, getHouse().getHouseName());

                        ListAndSearchFragment.setArguments(bundle);
                        fm.beginTransaction().replace(R.id.fragmentContainer, ListAndSearchFragment)
                                .addToBackStack(null)
                                .commit();

                        getHouse().deletePOI(poiName);
                        Toast.makeText(MainActivity.this, "Du har nu tagit bort " + poiName, Toast.LENGTH_LONG).show();
                    }
                });

                alertDialogBuilder.setNegativeButton("Nej", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                AlertDialog alertDialog = alertDialogBuilder.create();
                alertDialog.show();


                break;


            case R.id.updatePOI:

                Bundle newBundle = new Bundle();

                //TODO: Fullösning tillsvidare...
                ArrayList<String> temp = new ArrayList<String>();

                for (int i = 0; i < myCatList.categoryList.size(); i++)
                    temp.add(myCatList.categoryList.get(i));

                newBundle.putStringArrayList(CAT_LIST, temp);
                fromUpdate = true;

                getSupportFragmentManager().popBackStack();
                AddDataFragment fragmentData = new AddDataFragment();
                fragmentData.setArguments(newBundle);
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, fragmentData).addToBackStack(null).commit();
        }
                return super.onOptionsItemSelected(item);

    }


    //hides keyboard whenever the user clicks outside the keyboard
    //TODO: in addData, if you click on the addPath-button you sometimes have to do it twice because the first time it only hides the keyboard
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
        return super.dispatchTouchEvent(ev);
    }

    //Override the back button when on qr fragment
    @Override
    public void onBackPressed() {

        //get current fragment
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.fragmentContainer);


        //Ugly fix
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

    /******************************************************/
    /** Navigation drawer methods */

    private void setUpDrawer(NavigationView navigationView){

        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        changeFragment(menuItem);
                        return true;
                    }
                });
    }


    //Animation for opening and closing the drawer
    private ActionBarDrawerToggle setUpDrawerToggle() {
        return new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.drawer_open,  R.string.drawer_close);
    }

    //Sync animation
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

    //Swaps between the different fragments
    private void changeFragment(MenuItem menuItem){

        Fragment fragment = null;
        FragmentManager fragmentManager = getSupportFragmentManager();


        switch(menuItem.getItemId()) {
            default:
            case R.id.nav_qr_fragment:
                fragment = new QRFragment();
                break;
            case R.id.nav_map_fragment:
                fragment = new AddHouseFragment();
                break;
            case R.id.nav_list_and_search_fragment:
                fragment = new ListAndSearchFragment();
                break;
            case R.id.nav_login_fragment:
                fragment = new LoginFragment();
                break;
            case R.id.nav_about_fragment:
                fragment = new AboutFragment();
                break;
            case R.id.nav_help_fragment:
                fragment = new HelpFragment();
                break;
        }

        if(fragment != null){
            fragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(menuItem.toString())
                    .commit();
        }


        // Highlight the selected item has been done by NavigationView
        menuItem.setChecked(true);
        if(oldMenuItem != null) {
            oldMenuItem.setChecked(false);
        }
        oldMenuItem = menuItem;
        // Set action bar title
        //setTitle(menuItem.getTitle());
        // Close the navigation drawer
        mDrawerLayout.closeDrawers();
    }

    /******************************************************/

    public Map<String, Car> getCars() { return Cars; }

    //set House
    public void setHouse(House h){ house = h; }

    //Used to have a public house, Get House
    public House getHouse(){ return house; }

    //set Admin
    public void setAdmin(boolean b){ isAdmin = b; }

    //get Admin
    public boolean getAdmin(){ return isAdmin; }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }

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

        String s = "0, 0";

        if(publicPos != null)
            s = publicPos.getLatitude() + ", " + publicPos.getLongitude();
        else {
            Toast.makeText(this, "Din position kan inte hittas, därmed är parkeringspositionen inte korrekt",
                    Toast.LENGTH_LONG).show();
        }

        //if(Cars.get(platenr).getUsed())
        DB.child("bilar").child(platenr).child("latlng").setValue(s);

        DB.child("bilar").child(platenr).child("used").setValue(Cars.get(platenr).getUsed());

    }
    public void addToHistory(String s){
        if(!historyList.contains(s))
            historyList.add(s);
    }
    public ArrayList<String> getHistoryList(){
        return historyList;
    }
}
