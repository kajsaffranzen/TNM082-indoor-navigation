package com.example.firebazzze.tnm082_indoor_navigation;

/**
 * Created by Rickard on 2016-04-12.
 */
public class POI {

    private String name, category, description;
    int floor;

    // Creates an object for a POI with it's name, category, description and floor as variables
    public POI(String name, String category, String description, int floor){
        this.name = name;
        this.category = category;
        this.description = description;
        this.floor = floor;
    }

    // Returns the name of the POI
    public String getName(){
        return name;
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

}
