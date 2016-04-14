package com.example.firebazzze.tnm082_indoor_navigation;

import android.util.Log;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

/**
 * This class represents a house object. A house object has a name and a list of interest points.
 *
 */
public class House {

    private String houseName;
    private POI[] POIs;

    public House(String houseName, POI[] POIs){
        this.houseName = houseName;
        this.POIs = POIs;
    }

    public House(String houseName){
        this.houseName = houseName;
        getData();
    }

    private void getData(){

        //Change this into our database
        //Reference to Database
        Firebase DB = new Firebase("https://tnm082-indoor.firebaseio.com/" + this.houseName);

        Query queryRef = DB.orderByChild("height");


        //Eventlistener to listen if the data is changed.
        //snapshot.getValue() contains the whole tree of the clicked house at the moment
        //Add extra .child("Skrivare") after .child(this.houseName), to go further down the tree
        queryRef.addChildEventListener(new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot snapshot, String s) {

                POI newPOI = snapshot.getValue(POI.class);
                Log.i("test", ""+newPOI.getDescription());

                System.out.println(snapshot.getValue());
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

    public String getHouseName(){
        return houseName;
    }

    public POI[] getPOIs(){
        return POIs;
    }

    public POI getOnePOI(int index) {
        return POIs[index];
    }
}
