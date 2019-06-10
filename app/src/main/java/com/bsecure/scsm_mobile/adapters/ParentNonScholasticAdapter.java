package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.models.NonScholasticSubject;

import java.util.ArrayList;
import java.util.HashMap;

public class ParentNonScholasticAdapter extends BaseExpandableListAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<String> cat;
    HashMap<String, ArrayList<NonScholasticSubject>> data;
    private ContactAdapterListener listener;

    public ParentNonScholasticAdapter(Context context, HashMap<String, ArrayList<NonScholasticSubject>> data, ArrayList<String>cat, ContactAdapterListener listener)
    {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
        this.cat = cat;
        this.listener = listener;
    }



    @Override
    public int getGroupCount() {
        return data.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return data.get(cat.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return data.get(cat.get(groupPosition));
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return data.get(cat.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String category = cat.get(groupPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.skill_category, null);
        }

        TextView Category = convertView.findViewById(R.id.catname);
        Category.setText(category);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String name = data.get(cat.get(groupPosition)).get(childPosition).getName();
        final String grade = data.get(cat.get(groupPosition)).get(childPosition).getGrade();
        final String comments = data.get(cat.get(groupPosition)).get(childPosition).getComment();
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.skill_row_parent, null);
        }
        TextView skillName = convertView.findViewById(R.id.name);
        TextView Grade = convertView.findViewById(R.id.grade);
        TextView comment = convertView.findViewById(R.id.comment);

        skillName.setText(name);
        Grade.setText(grade);
        comment.setText(comments);
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    public interface ContactAdapterListener {

        void onRowClicked(int group, int child, View v);
    }
}
