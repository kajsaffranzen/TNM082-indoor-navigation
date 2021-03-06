package com.example.firebazzze.tnm082_indoor_navigation;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class represents a house object. A house object has a name and a list of interest points.
 *
 */

public class House {

    private String houseName;

    private String latLng;

    private List<POI> POIs;

    private String DBUrl = "https://tnm082-indoor.firebaseio.com/";
    //private String DBUrl = "https://coord-test.firebaseio.com/";


    private Map<String, POI> POIs2;
    private Map<String, Car> Cars;

    private OnDataLoaded listener;

    // Creates an object for the house with the name of the house and the listof POI
    public House(String houseName, List<POI> POIs){
        this.houseName = houseName;
        this.POIs = POIs;
    }

    // Creates an object for the house and then gets the data for it
    public House(String houseName){
        this.houseName = houseName;
        POIs = new ArrayList<POI>();
        POIs2 = new HashMap<String, POI>();
        POIs = new ArrayList<POI>();

        if(houseName != null){
            houseExists();
        }
    }

    // Creates a house object with coodinates, called from map view
    public House(String houseName, String latLng){

        this.houseName = houseName;
        this.latLng = latLng;
        POIs = new ArrayList<POI>();
        POIs2 = new HashMap<String, POI>();
        Cars = new HashMap<String, Car>();

        if(houseName.contains("/"))
            getCars();
        else
            getData();

        POIs = new ArrayList<POI>();
        addData();
    }



    private void houseExists() {
        Firebase DB = new Firebase(DBUrl + this.houseName);

        DB.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists())
                    getData();
                else
                    addData();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }


    //Function to get Cars, this should probably move
    private void getCars() {

        String test = "";

        if(this.houseName.contains("/")){
            test = this.houseName.split("/")[0];
        }else test = this.houseName;

        Firebase DB = new Firebase(DBUrl + test);

        DB.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Car newCar = dataSnapshot.getValue(Car.class);
                Cars.put(dataSnapshot.getKey(), newCar);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
    }

    // Gets the data from the firebase database
    private void getData(){


        //Change this into our database
        //Reference to Database
        Firebase DB = new Firebase(DBUrl + this.houseName);

        Query queryRef = DB.orderByChild("floor");
        //Eventlistener to listen if the data is changed.
        //snapshot.getValue() contains the whole tree of the clicked house at the moment
        //Add extra .child("Skrivare") after .child(this.houseName), to go further down the tree
        queryRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {

                //TODO - should not need this, replace with try catch
                if (!snapshot.getKey().equals("latlng")) {

                    POI newPOI = snapshot.getValue(POI.class);

                    //Needed since firebase expects that we add the key
                    //"path" to the first element of the array, really stupid

                    if (newPOI.getPath() != null && newPOI.getPath().contains("null")) {
                        newPOI.getPath().remove("null");
                    }

                    POIs2.put(snapshot.getKey(), newPOI);
                    POIs.add(newPOI); //NOT USING THIS //TODO- remove

                    if (listener != null)
                        listener.onLoaded();
                }
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(FirebaseError error) {
            }
        });
    }

    //Add the new house to DB
    private void addData() {

        Firebase DB = new Firebase(DBUrl);
        Firebase newPoiRef = DB.child(this.houseName);
        Firebase newLatLng = newPoiRef.child("latlng");
        newLatLng.setValue(this.latLng);
    }

    //Add new Car to the garage
    public void addCar(String carName, String coords){

    }

    //Return the list of POIs
    public Map<String, POI> getPOIs2(){
        return this.POIs2;
    }

    // Add a new POI to the database
    public void addPOI(String name, String cat, String desc, int floor, boolean official, List<String> path) {

        name = Character.toLowerCase(name.charAt(0)) + name.substring(1);
        cat = Character.toLowerCase(cat.charAt(0)) + cat.substring(1);
        desc = Character.toLowerCase(desc.charAt(0)) + desc.substring(1);

        Firebase DB = new Firebase(DBUrl + this.houseName);

        Firebase newPOSDBref = DB.child(name);

        //TODO: ugly solution for now

        POI test = new POI(cat, desc, floor, official, path);

        newPOSDBref.setValue(test);

    }

    public void updatePOI(String POIname, String oldPOIname, String cat, String desc, int floor, boolean official, List<String> path){

        deletePOI(oldPOIname);

        addPOI(POIname,cat,desc,floor,official,path);


    }

    public void deletePOI(String POIname){

        Firebase DB = new Firebase(DBUrl + this.houseName + "/" + POIname);

        DB.removeValue();


    }

    //Edit official value of POI
    public void setOfficial(String POIkey) {
        Firebase DB = new Firebase(DBUrl + this.houseName + "/" + POIkey);

        DB.child("official").setValue(!POIs2.get(POIkey).getOfficial());
    }

    // Returns the name of the house
    public String getHouseName(){
        return houseName;
    }

    // Returns an array of all the POI that exists in the house
    public List<POI> getPOIs(){
        return POIs;
    }

    //Listener interface if data is loaded
    public interface OnDataLoaded{
        void onLoaded();
    }

    public void setOnDataLoadedListener(OnDataLoaded listener){
        this.listener = listener;
    }

    //get house name
    public String getName() { return this.houseName; }
}
