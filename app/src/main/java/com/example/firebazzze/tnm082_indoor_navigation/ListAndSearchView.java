package com.example.firebazzze.tnm082_indoor_navigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ListAndSearchView extends AppCompatActivity {

    private ExpandableListView myExpandableListView;
    private ExpandableListAdapter myExpandableListAdapter;

    private List<String> categoryList;
    private HashMap<String, List<String>> interestPointsList;

    private List<List<String>> dynamicCategoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        setContentView(R.layout.activity_list_and_search_view);

        init();
    }

    private void init() {
        categoryList = new ArrayList<String>();
        interestPointsList = new HashMap<String, List<String>>();

        myExpandableListView = (ExpandableListView) findViewById(R.id.expList);
        myExpandableListAdapter = new ExpandableListAdapter(this, categoryList, interestPointsList);
        myExpandableListView.setAdapter(myExpandableListAdapter);

        dynamicCategoryList = new ArrayList<List<String>>();

        addTempData(myExpandableListView);

        fillListWithData();
    }

    private void fillListWithData(){
        House newHouse = new House("tappan");

        newHouse.setOnDataLoadedListener(new House.OnDataLoaded() {
            @Override
            public void onLoaded() {
                Log.d("data", "Data is loaded");
            }
        });
    }

    //add some dummy data to the expandable list
    private void addTempData(ExpandableListView expList) {
        List<String> kategoryList0 = new ArrayList<String>();

        addCategory("Rum");
        addCategory("Skrivare");
        addCategory("Entréer");

        addItemToCategoryByName("Skrivare", "skrivare1");
        addItemToCategoryByName("Skrivare", "skrivare2");
        addItemToCategoryByName("Skrivare", "skrivareTrasig");

        for(int i=0; i<dynamicCategoryList.size(); i++)
            interestPointsList.put(categoryList.get(i), dynamicCategoryList.get(i));
    }

    //add a category to the categoryList and creating a list for the category's interest points
    private void addCategory(String name) {

        List<String> newList = new ArrayList<String>();

        dynamicCategoryList.add(newList);
        categoryList.add(name);
    }

    //Add an item to a category list
    private void addItemToCategory(List<String> kList, String name) {
        kList.add(name);
    }

    //add an item to a category specified by name
    private void addItemToCategoryByName(String categoryName, String itemName) {
        int index = -1;

        for(int i=0; i<categoryList.size(); i++)
            if(categoryList.get(i).equals(categoryName))
                index = i;

        dynamicCategoryList.get(index).add(itemName);
    }
}
