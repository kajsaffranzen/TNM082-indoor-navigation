package com.example.firebazzze.tnm082_indoor_navigation;

/**
 * A class descripbing a car object
 * The car has a position on the map: latlng
 * The car also can be set as used with the "used" parameter
 */
public class Car {
    private String latlng;
    private boolean used;


    //Needed for firebase
    public Car(){
    }

    public String getLatlng(){ return this.latlng; }

    public boolean getUsed() {return this.used; }

    public void setUsed() {this.used = !this.used; }

}
