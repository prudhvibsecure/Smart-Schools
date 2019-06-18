package com.bsecure.scsm_mobile.adapters;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.models.NonScholasticSubject;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.modules.NonScholasticTeacher;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class NonScholasticAdapter extends BaseExpandableListAdapter {

    Context context;
    LayoutInflater inflater;
    ArrayList<String>cat;
    HashMap<String, ArrayList<NonScholasticSubject>> data;
    private ContactAdapterListener listener;
    String flag;

    public NonScholasticAdapter(Context context, HashMap<String, ArrayList<NonScholasticSubject>> data, ArrayList<String>cat, ContactAdapterListener listener, String flag)
    {
        this.context = context;
        this.data = data;
        inflater = LayoutInflater.from(context);
        this.cat = cat;
        this.listener = listener;
        this.flag = flag;
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
     // if (convertView == null) {
            convertView = inflater.inflate(R.layout.skill_category, null);
      // }

        TextView Category = convertView.findViewById(R.id.catname);
        Category.setText(category);
        return convertView;
    }

    @Override
    public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        final String name = data.get(cat.get(groupPosition)).get(childPosition).getName();
        final String grade = data.get(cat.get(groupPosition)).get(childPosition).getGrade();
        final String comments = data.get(cat.get(groupPosition)).get(childPosition).getComment();
        //if (convertView == null) {
                convertView = inflater.inflate(R.layout.skill_name, null);
        //}
        TextView skillName = convertView.findViewById(R.id.skill);
        TextView Grade = convertView.findViewById(R.id.grade);
        final EditText comment = convertView.findViewById(R.id.comment);

        comment.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().length() != 0) {
                    data.get(cat.get(groupPosition)).get(childPosition).setComment(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        Button update = convertView.findViewById(R.id.bt_update);
        if(flag.equalsIgnoreCase("1"))
        {
            update.setVisibility(View.VISIBLE);
        }
        //update.setVisibility(View.VISIBLE);
        update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                data.get(cat.get(groupPosition)).get(childPosition).setComment(comment.getText().toString());
                listener.onRowClicked(groupPosition, childPosition, v);
            }
        });

        Grade.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listener.onRowClicked(groupPosition, childPosition, v);
            }
        });


        skillName.setText(name);
        Grade.setText(grade);
        if(comments == null|| comments.equalsIgnoreCase("null"))
        {
            comment.setText("");
        }
        else
        {
            comment.setText(comments);
        }

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
