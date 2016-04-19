package com.example.firebazzze.tnm082_indoor_navigation;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class ListAndSearchView extends AppCompatActivity {

    private ExpandableListView myExpandableListView;
    private ExpandableListAdapter myExpandableListAdapter;

    private List<String> categoryList;
    private HashMap<String, List<String>> interestPointsList;
    private List<List<String>> dynamicCategoryList;

    private House newHouse;

    private Button b;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_and_search_view);

        b = (Button)findViewById(R.id.addPOI);

        init();
    }

    private void init() {
        categoryList = new ArrayList<String>();
        interestPointsList = new HashMap<String, List<String>>();

        myExpandableListView = (ExpandableListView) findViewById(R.id.expList);
        myExpandableListAdapter = new ExpandableListAdapter(this, categoryList, interestPointsList);
        myExpandableListView.setAdapter(myExpandableListAdapter);

        dynamicCategoryList = new ArrayList<List<String>>();

        Bundle bundle = getIntent().getExtras();
        if(bundle == null)
            Log.d("error","Intent was null");
        else
            fillListWithData( bundle.getString("HOUSE_NAME") );
    }

    private void fillListWithData(String houseName){

        Log.d("test", "string " + houseName + " came through!");

        //This is only for testing
        newHouse = new House(houseName);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Should start new view and/or activity instead
                //And move this to the new activity/view
                newHouse.addPOI("rilletestar", "category", "go left", 156);
            }
        });

        //samesame...
        newHouse.setOnDataLoadedListener(new House.OnDataLoaded() {
            @Override
            public void onLoaded() {
                Log.d("data", "Data is loaded");
                addData(newHouse);
            }
        });
    }

    private void addData(House newHouse) {

        for(int i = 0; i < dynamicCategoryList.size(); i++)
            dynamicCategoryList.get(i).clear();

        for(int i = 0; i < newHouse.getPOIs().size(); i++) {
            boolean flag = false;

            for(int k = 0; k < categoryList.size(); k++) {
                if(categoryList.get(k).equals( newHouse.getPOIs().get(i).getCategory() ))
                    flag = true;
            }

            if(!flag)
            addCategory( newHouse.getPOIs().get(i).getCategory() );
            addItemToCategoryByName( newHouse.getPOIs().get(i).getCategory(), newHouse.getPOIs().get(i).getDescription() );

            myExpandableListAdapter.notifyDataSetChanged();
        }

        for(int k=0; k<dynamicCategoryList.size(); k++)
            interestPointsList.put(categoryList.get(k), dynamicCategoryList.get(k));
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
