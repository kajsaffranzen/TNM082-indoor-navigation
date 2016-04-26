package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ListView;

import com.firebase.client.Firebase;

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
    private static final String CAT_LIST = "catlist";
    private static final String KEY = "housename";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String houseName;

    //used to update list when new data is loaded
    private OnFragmentInteractionListener mListener;

    private ExpandableListView myExpandableListView;
    private ExpandableListAdapter myExpandableListAdapter;

    private List<String> categoryList;
    private HashMap<String, List<String>> interestPointsList;
    private List<List<String>> dynamicCategoryList;

    private House newHouse;

    private EditText searchField;
    private ListView listSearch;
    private Button searchInflaterB;
    private Button addPOIBtn;

    // Required empty public constructor
    public ListAndSearchFragment() {
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
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            houseName = getArguments().getString(KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_and_search, container, false);

        //lists for handling names/categories
        categoryList = new ArrayList<String>();
        interestPointsList = new HashMap<String, List<String>>();
        dynamicCategoryList = new ArrayList<List<String>>();

        //Expandable list
        myExpandableListView = (ExpandableListView) view.findViewById(R.id.expList);
        myExpandableListAdapter = new ExpandableListAdapter(getActivity(), categoryList, interestPointsList);
        myExpandableListView.setAdapter(myExpandableListAdapter);

        //search field in toolbar
        searchField = (EditText) getActivity().findViewById(R.id.toolbarSearchField);

        searchField.setVisibility(View.GONE);

        //search inflater button
        searchInflaterB = (Button) getActivity().findViewById(R.id.searchInflaterButton);
        searchInflaterB.setVisibility(View.VISIBLE);

        //add poi button
        addPOIBtn = (Button)view.findViewById(R.id.buttoncreatepoi);

        fillListWithData(houseName);
        setListeners(newHouse);


        return view;
    }

    private void fillListWithData(String houseName){
        //This is only for testing
        newHouse = new House(houseName);

        ((MainActivity)getActivity()).setHouse(newHouse);

        newHouse.setOnDataLoadedListener(new House.OnDataLoaded() {
            @Override
            public void onLoaded() {
                addData(newHouse);
            }
        });
    }

    private void addData(House newHouse){

        for(int i = 0; i < dynamicCategoryList.size(); i++)
            dynamicCategoryList.get(i).clear();

        int i = 0;

        for(String key : newHouse.getPOIs2().keySet()) {

            boolean flag = false;

            for(int k = 0; k < categoryList.size(); k++) {
                if(categoryList.get(k).equals( newHouse.getPOIs2().get(key).getCategory() ))
                    flag = true;
            }

            if(!flag)
                addCategory( newHouse.getPOIs2().get(key).getCategory() );

            if(newHouse.getPOIs2().get(key).getOfficial())
                addItemToCategoryByName( newHouse.getPOIs2().get(key).getCategory(), "***" + key );
            else
                addItemToCategoryByName( newHouse.getPOIs2().get(key).getCategory(), key );

            myExpandableListAdapter.notifyDataSetChanged();

            i++;
        }

        for(int k=0; k<dynamicCategoryList.size(); k++){

            interestPointsList.put(categoryList.get(k), dynamicCategoryList.get(k));

        }
    }

    //add a category to the categoryList and creating a list for the category's interest points
    private void addCategory(String name) {

        List<String> newList = new ArrayList<String>();

        dynamicCategoryList.add(newList);
        categoryList.add(name);
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
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
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

    private void setListeners(final House newHouse){

        //Handle onClick for list item
        myExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            //Handle on child click event in expandable list
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                //hide search field
                searchField.setVisibility(View.GONE);

                //Go to DetailViewIP
                String POIkey = dynamicCategoryList.get(groupPosition).get(childPosition);
                POIkey = POIkey.replace("***", "");
                goToDetailFragmet(POIkey);

                return false;
            }
        });

        //Handle searches
        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                //string empty, dont search
                if (s.toString().equals("")) {
                    searchField.setHint("Sök intressepunkt");
                    return;
                }

                //Loop throught the POIs to find search matches
                for (String key : newHouse.getPOIs2().keySet()) {

                    //compare name
                    if (key.contains(s.toString())) {
                        Log.d("search", "Name match: " + key);
                    }

                    //compare category (else: don't match twice)
                    else if (newHouse.getPOIs2().get(key).getCategory().contains(s.toString())) {
                        Log.d("search", "Cat. match: " + newHouse.getPOIs2().get(key).getCategory());
                    }
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //Handle add-button clicks
        addPOIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FragmentManager fm = getActivity().getSupportFragmentManager();

                Fragment addDataFragment = new AddDataFragment();
                Bundle bundle = new Bundle();

                //TODO: Fullösning tillsvidare...
                ArrayList<String> temp = new ArrayList<String>();

                for (int i = 0; i < categoryList.size(); i++)
                    temp.add(categoryList.get(i));

                bundle.putStringArrayList(CAT_LIST, temp);

                addDataFragment.setArguments(bundle);
                fm.beginTransaction().replace(R.id.fragmentContainer, addDataFragment).addToBackStack("AddDataFragment").commit();
            }
        });

        //Handle the toolbar search button
        searchInflaterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchField.setVisibility(View.VISIBLE);
            }
        });
    }

    private void goToDetailFragmet(String POIkey) {

        //hide search field and button
        searchField.setVisibility(View.GONE);
        searchInflaterB.setVisibility(View.GONE);

        FragmentManager fm = getActivity().getSupportFragmentManager();

        Fragment DetailFragment = new DetailFragment();

        Bundle bundle = new Bundle();
        //Change the variable to send, it should be house and POI
        bundle.putString(KEY, POIkey);

        //TODO - Change the variable to send, it should be house and POI
        DetailFragment.setArguments(bundle);

        fm.beginTransaction().replace(R.id.fragmentContainer, DetailFragment).addToBackStack("DetailFragment").commit();
    }

}
