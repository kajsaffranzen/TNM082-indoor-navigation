package com.example.firebazzze.tnm082_indoor_navigation;

/**
 * Created by ricka on 2016-05-03.
 */
public class Car {
    private String coords;
    private boolean used;


    //Needed for firebase
    public Car(){
    }

    public Car(boolean used, String coords){
        this.used = used;
        this.coords = coords;
    }

    public String getCoords(){ return this.coords; }

    public boolean getUsed() {return this.used; }

}
