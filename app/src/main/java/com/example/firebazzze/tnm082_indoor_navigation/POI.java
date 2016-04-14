package com.example.firebazzze.tnm082_indoor_navigation;

/**
 * Created by Rickard on 2016-04-12.
 */
public class POI {

    public String category;
    public String description;
    public int floor;

    public POI(){
    }

    /*public POI(String name, String category, String description, int floor){
        this.name = name;
        this.category = category;
        this.description = description;
        this.floor = floor;
    }*/

    /*public String getName(){
        return name;
    }*/

    public String getCategory(){
        return category;
    }

    public String getDescription(){
        return description;
    }

    public int getFloor(){
        return floor;
    }

}
