package com.example.firebazzze.tnm082_indoor_navigation;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by Rickard on 2016-04-12.
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

        //This thing should probably be moved somewhere else!
        //Change this into our database
        //Reference to Database
        Firebase DB = new Firebase("https://kalle.firebaseio.com/");

        //Eventlistener to listen if the data is changed.
        //snapshot.getValue() contains the whole tree at the moment
        //Change "Tappan" depending on house choosen
        //Add extra .child("Skrivare") after .child("Tappan"), to go further down the tree
        DB.child("Tappan").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                System.out.println(snapshot.getValue());
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
