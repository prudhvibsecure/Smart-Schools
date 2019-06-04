package com.bsecure.scsm_mobile.modules;

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
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.ExamsListAdapter;
import com.bsecure.scsm_mobile.adapters.StudentMarkViewListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.Exams;
import com.bsecure.scsm_mobile.models.MarksModel;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewStudentMarks extends AppCompatActivity implements HttpHandler {
    private String class_id, student_id, roll_no, exams_name, ename;
    private StudentMarkViewListAdapter adapter;
    private ArrayList<MarksModel> marksModelArrayList;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_two);

        Intent getData = getIntent();
        if (getData != null) {

            roll_no = getData.getStringExtra("roll_no");
            class_id = getData.getStringExtra("class_id");
            student_id = getData.getStringExtra("student_id");
            exams_name = getData.getStringExtra("exams_name");
            ename = getData.getStringExtra("ename");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle(ename+"\tMarks");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.content_list);
        marksModelArrayList = new ArrayList<>();

        getMarks();
    }

    private void getMarks() {
        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("roll_no", roll_no);
            object.put("class_id", class_id);
            object.put("student_id", student_id);
            object.put("examination_name", exams_name);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.view_marks, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Object results, int requestType) {
        try {
            switch (requestType) {
                case 1:
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        findViewById(R.id.bottom_list).setVisibility(View.VISIBLE);
                        findViewById(R.id.header).setVisibility(View.VISIBLE);
                        JSONArray jsonarray2 = object.getJSONArray("marks_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                MarksModel marksModel = new MarksModel();
                                marksModel.setMarks(jsonobject.optString("marks"));
                                marksModel.setTotal_marks(jsonobject.optString("total_marks"));
                                marksModel.setRank(jsonobject.optString("rank"));
                               // marksModel.setGrade(jsonobject.optString("grade"));
                                marksModel.setSubject(jsonobject.optString("subject"));
                                marksModelArrayList.add(marksModel);
                            }
                            adapter = new StudentMarkViewListAdapter(marksModelArrayList, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
                        }
                        JSONArray jsonarray3 = object.getJSONArray("total_marks_details");
                        if (jsonarray3.length() > 0) {
                            for (int i = 0; i < jsonarray3.length(); i++) {
                                JSONObject jsonobject = jsonarray3.getJSONObject(i);
                                ((TextView) findViewById(R.id.marks_oo)).setText(jsonobject.optString("total_marks") + "/" + jsonobject.optString("max_marks"));
                                ((TextView) findViewById(R.id.grade)).setText("Grade : " + jsonobject.optString("grade"));
                                ((TextView) findViewById(R.id.percentage)).setText("Percentage : " + jsonobject.optString("percentile") + "%");
                                ((TextView) findViewById(R.id.rank)).setText("Rank :" + jsonobject.optString("rank"));
                                ((TextView) findViewById(R.id.feed_back)).setText("Feedback : " + jsonobject.optString("feedback"));
                            }
                        }
                    }else{
                        findViewById(R.id.bottom_list).setVisibility(View.GONE);
                        findViewById(R.id.header).setVisibility(View.GONE);
                        Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
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
}
