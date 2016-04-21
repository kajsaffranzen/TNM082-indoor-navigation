package com.example.firebazzze.tnm082_indoor_navigation;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
    All the functions are android standards for an expandable list view.

 */

public class ExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private List<String> listGroup;
    private HashMap<String, List<String>> listChild;

    // Create an ExpandableListAdapter with values from the ListAndSearchView
    public ExpandableListAdapter(Context context, List<String> listGroup,
                                 HashMap<String, List<String>> listChild) {
        this.context = context;
        this.listGroup = listGroup;
        this.listChild = listChild;
    }

    @Override
    // Returns the number of values in a list
    public int getGroupCount() {
        return listGroup.size();
    }

    @Override
    // Get the number of children that an object has
    public int getChildrenCount(int groupPosition) {
        return listChild.get(listGroup.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return listGroup.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return listChild.get(listGroup.get(groupPosition)).get(childPosition);
    }

    @Override
    // Get the ID of a group
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    // Get the ID of a Child
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.group_layout, null);
        }

        String textGroup = (String) getGroup(groupPosition);

        TextView textViewGroup = (TextView) convertView
                .findViewById(R.id.expListGroup);
        textViewGroup.setText(textGroup);

        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.item_layout, null);
        }

        TextView textViewItem = (TextView) convertView.findViewById(R.id.expListItem);
        ImageView imgView = (ImageView) convertView.findViewById(R.id.officialMarkImg);

        String text = (String) getChild(groupPosition, childPosition);

        if(text.contains("***")) {
            text = text.replace("***", "");
            imgView.setVisibility(View.VISIBLE);
        }
        else {
            imgView.setVisibility(View.INVISIBLE);
        }

        textViewItem.setText(text);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        // TODO Auto-generated method stub
        return true;
    }

}
