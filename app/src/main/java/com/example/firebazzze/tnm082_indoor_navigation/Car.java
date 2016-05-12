package com.example.firebazzze.tnm082_indoor_navigation;

import android.location.Location;
import android.util.Log;

/**
 * Created by ricka on 2016-05-03.
 */
public class Car {
    public String latlng;
    private boolean used;


    //Needed for firebase
    public Car(){
    }

    public String getLatlng(){ return this.latlng; }

    public boolean getUsed() { return this.used; }

    public void setUsed(boolean used) {this.used = used; }

    public void setLatlng(String s){ this.latlng = s; }

    public void updateLatlng(Location loc){

        if(loc.getLongitude() != 0.0 && loc.getLatitude() != 0.0){
            String usingLoc =  loc.getLatitude() + "";
            usingLoc += ", " + loc.getLongitude();

            setLatlng(usingLoc);
        }

    }

}
