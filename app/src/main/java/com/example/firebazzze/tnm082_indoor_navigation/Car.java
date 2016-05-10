package com.example.firebazzze.tnm082_indoor_navigation;

/**
 * Created by ricka on 2016-05-03.
 */
public class Car {
    private String latlng;
    private boolean used;


    //Needed for firebase
    public Car(){
    }

    public String getLatlng(){ return this.latlng; }

    public boolean getUsed() {return this.used; }

}
