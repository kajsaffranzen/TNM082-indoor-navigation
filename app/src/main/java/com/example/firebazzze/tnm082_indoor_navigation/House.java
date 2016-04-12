package com.example.firebazzze.tnm082_indoor_navigation;

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
