package com.bsecure.scsm_mobile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.adapters.StudentSylViewListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.graphs.graphadapters.SyllabusAdapter;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.Subjects;
import com.bsecure.scsm_mobile.models.SyllabusModel;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ViewSyllabusMarks extends AppCompatActivity implements HttpHandler, StudentSylViewListAdapter.ContactAdapterListener {

    private String class_id, student_id, roll_no, exams_name;
    private StudentSylViewListAdapter adapter;
    private ArrayList<Subjects> marksModelArrayList, topicsList=null;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_two);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Exams Timings");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.content_list);
        findViewById(R.id.bottom_list).setVisibility(View.GONE);
        findViewById(R.id.header).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.a11)).setText("From");
        ((TextView) findViewById(R.id.a111)).setText("To");
        ((TextView) findViewById(R.id.a1111)).setText("Total Marks");

        Intent getData = getIntent();
        if (getData != null) {

            roll_no = getData.getStringExtra("roll_no");
            class_id = getData.getStringExtra("class_id");
            student_id = getData.getStringExtra("student_id");
            exams_name = getData.getStringExtra("exams_name");
        }
        getMarks();

    }

    private void getMarks() {
        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("exam_name", exams_name);
            object.put("class_id", class_id);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.view_syllabus, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Object results, int requestType) {
        try {
            switch (requestType) {
                case 1:
                    topicsList = new ArrayList<>();
                    marksModelArrayList = new ArrayList<>();
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray jsonarray2 = object.getJSONArray("examination_time_table_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                Subjects subjects = new Subjects();
                                subjects.setExam_date(jsonobject.optString("exam_date"));
                                subjects.setFrom_time(jsonobject.optString("from_time"));
                                subjects.setSubject(jsonobject.optString("subject"));
                                subjects.setTo_time(jsonobject.optString("to_time"));
                                subjects.setTotal_marks(jsonobject.optString("total_marks"));
                                marksModelArrayList.add(subjects);
                            }
                            adapter = new StudentSylViewListAdapter(marksModelArrayList, this, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
                        }
                        else
                        {
                            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
                        }
                        JSONArray jsonarray3 = object.getJSONArray("syllabus_details");
                        if(jsonarray3.length() > 0) {
                            for (int i = 0; i < jsonarray3.length(); i++) {
                                JSONObject jsonobject = jsonarray3.getJSONObject(i);

                                // subjects.setSubject(jsonobject.optString("subject"));
                                JSONArray mArray = jsonobject.getJSONArray("lesson_details");
                                for (int k = 0; k < mArray.length(); k++) {
                                    JSONObject js = mArray.getJSONObject(k);
                                    Subjects subjects = new Subjects();
                                    subjects.setLesson(js.optString("lesson"));
                                    subjects.setDescription(js.optString("description"));
                                    subjects.setSubject(jsonobject.optString("subject"));
                                    topicsList.add(subjects);
                                }
                                // topicsList.add(subjects);
                            }
                        }
                        else
                        {
                            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
                        }
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRowClicked(List<Subjects> matchesList, int position) {
        ArrayList<SyllabusModel> mDes = new ArrayList<>();
        String sub1 = matchesList.get(position).getSubject();
//        String sub2 = topicsList.get(position).getSubject();
        for (int i = 0; i < topicsList.size(); i++) {
            if (sub1.equalsIgnoreCase(topicsList.get(i).getSubject())) {
                SyllabusModel subjects = new SyllabusModel();
                subjects.setLesson(topicsList.get(i).getLesson());
                subjects.setDescription(topicsList.get(i).getDescription());
                subjects.setSubject(topicsList.get(i).getSubject());
                mDes.add(subjects);
            }
        }
        SyllabusAdapter syllabusAdapter = new SyllabusAdapter(mDes, this);
        Dialog mDialog = new Dialog(this);
        mDialog.setContentView(R.layout.contect_main);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        RecyclerView mRec = mDialog.findViewById(R.id.my_list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRec.setLayoutManager(linearLayoutManager);
        mRec.setAdapter(syllabusAdapter);
        mDialog.show();

    }
}
