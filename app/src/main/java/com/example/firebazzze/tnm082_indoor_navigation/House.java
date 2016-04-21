package com.example.firebazzze.tnm082_indoor_navigation;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a house object. A house object has a name and a list of interest points.
 *
 */

public class House {

    private String houseName;
    private List<POI> POIs;
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
        getData();
        POIs = new ArrayList<POI>();
    }

    // Gets the data from the firebase database
    private void getData(){

        //Change this into our database
        //Reference to Database
        Firebase DB = new Firebase("https://tnm082-indoor.firebaseio.com/" + this.houseName);

        Query queryRef = DB.orderByChild("floor");
        //Eventlistener to listen if the data is changed.
        //snapshot.getValue() contains the whole tree of the clicked house at the moment
        //Add extra .child("Skrivare") after .child(this.houseName), to go further down the tree
        queryRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {

                POI newPOI = snapshot.getValue(POI.class);
                POIs.add(newPOI);

                if(listener != null)
                    listener.onLoaded();
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

            @Override public void onCancelled(FirebaseError error) { }
        });

    }

    // Add a new POI to the database
    public void addPOI(String name, String cat, String desc, int floor, boolean isOfficial){

        Firebase DB = new Firebase("https://tnm082-indoor.firebaseio.com/" + this.houseName);

        Firebase newPOSDBref = DB.child(name);

        POI test = new POI(cat, desc, floor, isOfficial);

        newPOSDBref.setValue(test);

    }

    // Returns the name of the house
    public String getHouseName(){
        return houseName;
    }

    // Returns an array of all the POI that exists in the house
    public List<POI> getPOIs(){
        return POIs;
    }

    // Returns one POI that exists in the house correlating to a specific index
    public POI getOnePOI(int index) {
        return POIs.get(index);
    }

    //Listener interface if data is loaded
    public interface OnDataLoaded{
        void onLoaded();
    }

    public void setOnDataLoadedListener(OnDataLoaded listener){
        this.listener = listener;
    }
}
