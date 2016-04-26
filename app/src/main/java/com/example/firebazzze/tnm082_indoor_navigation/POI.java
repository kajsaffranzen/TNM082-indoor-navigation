package com.example.firebazzze.tnm082_indoor_navigation;

import java.util.ArrayList;
import java.util.List;

/**
 *  Class for interest points
 */
public class POI {

    public String category;
    public String description;
    public int floor;
    public boolean official;
    public List<String> path;

    //Default constructor, needed for firebase
    public POI(){
    }

    //Constructor for POI
    public POI(String category, String description, int floor, boolean isOfficial, List<String> path){

        this.path = new ArrayList<String>();
        this.category = category;
        this.description = description;
        this.floor = floor;
        this.official = isOfficial;
        this.path = path;

    }

    // Returns the type/category of the POI
    public String getCategory(){
        return category;
    }

    // Returns the description/information of the POI
    public String getDescription(){
        return description;
    }

    // Returns which floor the POI is on
    public int getFloor(){
        return floor;
    }

    //returns true if POI is official
    public boolean getOfficial() { return official; }

    public List<String> getPath(){
        return path;
    }

}
