package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ExpandableListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ListAndSearchFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ListAndSearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ListAndSearchFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String KEY = "housename";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String houseName;

    private OnFragmentInteractionListener mListener;

    private ExpandableListView myExpandableListView;
    private ExpandableListAdapter myExpandableListAdapter;

    private List<String> categoryList;
    private HashMap<String, List<String>> interestPointsList;
    private List<List<String>> dynamicCategoryList;

    private House newHouse;

    public ListAndSearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ListAndSearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ListAndSearchFragment newInstance(String param1, String param2) {
        ListAndSearchFragment fragment = new ListAndSearchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            houseName = getArguments().getString(KEY);
           // mParam1 = getArguments().getString(ARG_PARAM1);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_list_and_search, container, false);

        categoryList = new ArrayList<String>();
        interestPointsList = new HashMap<String, List<String>>();

        myExpandableListView = (ExpandableListView) view.findViewById(R.id.expList);
        myExpandableListAdapter = new ExpandableListAdapter(getActivity(), categoryList, interestPointsList);
        myExpandableListView.setAdapter(myExpandableListAdapter);

        dynamicCategoryList = new ArrayList<List<String>>();

        Button b = (Button)view.findViewById(R.id.buttoncreatepoi);

        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //TODO: Call new fragment from here
                Log.i("testing", "Call On New Fragment");
            }
        });

        fillListWithData( houseName );

        return view;
    }

    private void fillListWithData(String houseName){

        Log.d("test", "string " + houseName + " came through!");

        //This is only for testing
        newHouse = new House(houseName);

        /*b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: MOVE THIS
                //Should start new view and/or activity instead
                //And move this to the new activity/view
                //TODO: change to different fragment
                //startActivity(new Intent(ListAndSearchView.this, CreatePOIFragment.class));

                newHouse.addPOI("new", "category", "go left", 156);
            }
        });*/

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

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
           // throw new RuntimeException(context.toString()
            //        + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
