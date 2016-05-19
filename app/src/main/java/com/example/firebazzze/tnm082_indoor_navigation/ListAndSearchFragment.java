package com.example.firebazzze.tnm082_indoor_navigation;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ExpandableListView;

import android.widget.ProgressBar;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

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
    private static final String CAT_LIST = "catlist";
    private static final String KEY = "housename";

    private String houseName;

    private ProgressBar loadingPanel;

    //used to update list when new data is loaded
    private OnFragmentInteractionListener mListener;

    private ExpandableListView myExpandableListView;
    private ExpandableListAdapter myExpandableListAdapter;

    private ArrayList<String> categoryList;
    private HashMap<String, List<String>> interestPointsList;
    private List<List<String>> dynamicCategoryList;

    private House newHouse;

    private EditText searchField;
    private Button searchInflaterB;
    private Button addPOIBtn;

    private LoginFragment loginFragment;

    private ListView listSearch;
    private TextView infoText;
    private ArrayAdapter<String> searchListAdapter;
    private List<String> searchResults;
    private final int maxSearchResults = 10;

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
            Log.d("TAG", "onCreate: " + houseName);
            houseName = getArguments().getString(KEY);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.d("TAG", "onCreateView: " + houseName);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_list_and_search, container, false);

        loadingPanel = (ProgressBar)view.findViewById(R.id.loadingPanel);

        //loads firebase in order to cancel progress bar if there is no data
        tryLoad();

        //Change the toolbar title to housename
        ((MainActivity)getActivity()).setToolbarTitle("Intressepunkter");

        //Check if house is null
        if(houseName != null){
            TextView titel = (TextView)view.findViewById(R.id.infoText);
            titel.setText(houseName);
            loadingPanel.setVisibility(View.VISIBLE);
        }

        categoryList = new ArrayList<String>();
        interestPointsList = new HashMap<String, List<String>>();
        dynamicCategoryList = new ArrayList<List<String>>();

        myExpandableListView = (ExpandableListView) view.findViewById(R.id.expList);
        myExpandableListAdapter = new ExpandableListAdapter(getActivity(), categoryList, interestPointsList);
        myExpandableListView.setAdapter(myExpandableListAdapter);

        //search field in toolbar
        searchField = (EditText) getActivity().findViewById(R.id.toolbarSearchField);
        searchField.setVisibility(View.GONE);

        //List for search results
        searchResults = new ArrayList<String>();
        listSearch = (ListView) view.findViewById(R.id.listSearch);
        searchListAdapter = new ArrayAdapter<String>(getContext(),R.layout.item_layout_search,searchResults);
        listSearch.setAdapter(searchListAdapter);

        // textview with infotext
        infoText = (TextView) view.findViewById(R.id.infoText);

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
                loadingPanel.setVisibility(View.GONE);
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
        myExpandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            //Handle on child click event in expandable list
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {

                String POIkey = dynamicCategoryList.get(groupPosition).get(childPosition);
                POIkey = POIkey.replace("***", "");
                goToDetailFragment(POIkey);
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
                    listSearch.setVisibility(View.GONE);
                    infoText.setVisibility(View.VISIBLE);
                    myExpandableListView.setVisibility(View.VISIBLE);

                    return;
                }

                searchResults.clear();

                //Loop throught the POIs to find search matches
                for (String key : newHouse.getPOIs2().keySet()) {

                    //compare name
                    if (key.toLowerCase().contains(s.toString().toLowerCase())) {
                        searchResults.add(key.toString());
                    }

                    //compare category (else: don't match twice)
                    else if (newHouse.getPOIs2().get(key).getCategory().toLowerCase().contains(s.toString().toLowerCase())) {
                        searchResults.add(key.toString());
                    }
                }

                listSearch.setVisibility(View.VISIBLE);
                myExpandableListView.setVisibility(View.GONE);
                infoText.setVisibility(View.GONE);
                searchListAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        //On enter click for search
        searchField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                //string empty, dont search
                if (searchField.getText().toString().equals("")) {
                    searchField.setHint("Sök intressepunkt");
                    listSearch.setVisibility(View.GONE);
                    infoText.setVisibility(View.VISIBLE);
                    myExpandableListView.setVisibility(View.VISIBLE);
                    return false;
                }

                //If exact match, go to item
                for(int i = 0; i < searchResults.size(); i++) {
                    if(searchField.getText().toString().equals(searchResults.get(i))) {
                        goToDetailFragment(searchResults.get(i));
                        return false;
                    }
                }

                //Else, no exact match found
                String toastMessage = searchField.getText().toString() + " finns inte";
                Toast toast = Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT);
                toast.show();

                //Close keyboard
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(getView().getWindowToken(), 0);

                return false;
            }
        });

        //Handle onClick for searchList items
        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                //Go to DetailViewIP pass the POI key
                goToDetailFragment(searchResults.get(position));
            }
        });

        //Handle add-button clicks
        addPOIBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                searchField.setVisibility(View.GONE);
                listSearch.setVisibility(View.GONE);
                searchInflaterB.setVisibility(View.GONE);

                FragmentManager fm = getActivity().getSupportFragmentManager();

                Fragment addDataFragment = new AddDataFragment();
                Fragment addDataChildFragment = new AddDataChildFragment();
                Bundle bundle = new Bundle();

                //TODO: Fullösning tillsvidare...
                ArrayList<String> temp = new ArrayList<String>();

                for (int i = 0; i < categoryList.size(); i++)
                    temp.add(categoryList.get(i));

                bundle.putStringArrayList(CAT_LIST, temp);

                addDataFragment.setArguments(bundle);
                fm.beginTransaction().replace(R.id.fragmentContainer, addDataFragment).addToBackStack("AddDataFragment").add(R.id.isOfficialCheckBox, addDataChildFragment).addToBackStack("AddDataChildFragment").commit();
            }
        });

        //Handle the toolbar search button
        searchInflaterB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchField.setVisibility(View.VISIBLE);
                searchField.setText("");

                //set focus in search field and pop up keyboard
                showSoftKeyboard( searchField );
            }
        });
    }

    //Show keyboard
    private void showSoftKeyboard(View view){
        if(view.requestFocus()){
            InputMethodManager imm =(InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInput(view,InputMethodManager.SHOW_IMPLICIT);
        }
    }

    //Go to Detail view and send stuff
    private void goToDetailFragment(String POIkey) {

        //hide search field and button
        searchField.setVisibility(View.GONE);
        searchInflaterB.setVisibility(View.GONE);

        FragmentManager fm = getActivity().getSupportFragmentManager();
        Fragment DetailFragment = new DetailFragment();
        loginFragment = new LoginFragment();

        Bundle bundle = new Bundle();
        bundle.putString(KEY, POIkey);

        DetailFragment.setArguments(bundle);
       // fm.beginTransaction().replace(R.id.fragmentContainer, DetailFragment).addToBackStack("DetailFragment").add(R.id.officalBoxLogin, loginFragment).addToBackStack("LoginFragment").commit();
        fm.beginTransaction().replace(R.id.fragmentContainer, DetailFragment).addToBackStack("DetailFragment").commit();
    }

    //load database to check if its empty
    private void tryLoad() {

        String DBUrl = "https://tnm082-indoor.firebaseio.com/";
        Firebase ref = new Firebase(DBUrl + this.houseName);

        final String hName = this.houseName;

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {

                //If there are no POI's
                if(dataSnapshot.getChildrenCount() == 1) {
                    loadingPanel.setVisibility(View.GONE);

                    String toastMessage = hName + " innehåller inga intressepunkter!";
                    Toast toast = Toast.makeText(getContext(), toastMessage, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
            public void onCancelled(FirebaseError firebaseError) { }
        });
        ref.addChildEventListener(new ChildEventListener() {
            public void onChildAdded(DataSnapshot dataSnapshot, String previousKey) {}
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
            public void onChildRemoved(DataSnapshot dataSnapshot) {}
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
            public void onCancelled(FirebaseError firebaseError) {}
        });
    }

}
