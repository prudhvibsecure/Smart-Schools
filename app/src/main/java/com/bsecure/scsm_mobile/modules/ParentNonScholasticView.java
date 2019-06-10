package com.bsecure.scsm_mobile.modules;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.ParentNonScholasticAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.Exams;
import com.bsecure.scsm_mobile.models.NonScholasticSubject;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParentNonScholasticView extends AppCompatActivity implements HttpHandler, ParentNonScholasticAdapter.ContactAdapterListener {

    String school_id, exam_id, student_id;
    ExpandableListView list;
    ParentNonScholasticAdapter adapter;
    ArrayList<String>categories;
    // ArrayList<String>grades;
    String[] grades;
    ArrayList<NonScholasticSubject>subjects;
    HashMap<String, ArrayList<NonScholasticSubject>> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_parent_non_scholastic_view);

        Intent getData = getIntent();
        if (getData != null) {

            exam_id = getData.getStringExtra("eid");
            student_id = getData.getStringExtra("sid");
            school_id = getData.getStringExtra("school_id");

        }
        list = findViewById(R.id.list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Non Scholastic Marks");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        getMarks();
    }

    private void getMarks() {

        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("exam_id", exam_id);
            object.put("student_id", student_id);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.get_non_scholastic_details, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResponse(Object results, int requestType) {

        try {
            switch (requestType) {
                case 1:
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {

                        JSONArray jsonarray = object.getJSONArray("non_scholastic_subjects");
                        if (jsonarray.length() > 0) {
                            data = new HashMap<>();
                            categories = new ArrayList<>();
                            for (int i = 0; i < jsonarray.length(); i++) {
                                JSONObject obj = jsonarray.getJSONObject(i);
                                String cat = obj.optString("category_name");
                                categories.add(cat);
                                JSONArray subarray = obj.getJSONArray("subject_details");
                                subjects = new ArrayList<>();
                                for(int j = 0; j< subarray.length(); j++)
                                {
                                    JSONObject obj2 = subarray.getJSONObject(j);
                                    NonScholasticSubject nsubject = new NonScholasticSubject();
                                    nsubject.setId(obj2.optString("non_scholastic_subject_id"));
                                    nsubject.setName(obj2.optString("subject"));
                                    nsubject.setGrade(obj2.optString("grade"));
                                    nsubject.setComment(obj2.optString("comments"));
                                    subjects.add(nsubject);

                                }
                                data.put(cat, subjects);
                            }

                            JSONArray garray = object.getJSONArray("non_scholastic_grade");
                            if(garray.length()>0)
                            {
                                grades = new String[garray.length()];
                                for(int p = 0; p< garray.length(); p++)
                                {
                                    JSONObject gobj = garray.getJSONObject(p);
                                    grades[p] = gobj.optString("grade");
                                }
                            }

                            adapter = new ParentNonScholasticAdapter(this, data, categories, this);
                            list.setAdapter(adapter);

                            for(int k = 0 ; k < categories.size(); k++)
                            {
                                list.expandGroup(k);
                            }

                            list.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener()
                            {
                                public boolean onGroupClick(ExpandableListView arg0, View itemView, int itemPosition, long itemId)
                                {
                                    list.expandGroup(itemPosition);
                                    return true;
                                }
                            });

                            list.setGroupIndicator(null);

                        }

                    } else {
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }

    @Override
    public void onRowClicked(int group, int child, View v) {

    }
}
