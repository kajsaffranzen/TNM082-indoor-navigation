package com.example.firebazzze.tnm082_indoor_navigation;

/**
 *
 */
public class POI {

    public String category;
    public String description;
    public int floor;
    public boolean isOfficial;


    public POI(){
    }

    public POI(String category, String description, int floor, boolean isOfficial){

        this.category = category;
        this.description = description;
        this.floor = floor;
        this.isOfficial = isOfficial;

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
    public boolean isOfficial() { return isOfficial; }

}
