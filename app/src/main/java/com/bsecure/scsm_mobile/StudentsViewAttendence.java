package com.bsecure.scsm_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bsecure.scsm_mobile.adapters.StudentAttendanceListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.modules.ViewAttandence;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class StudentsViewAttendence extends AppCompatActivity implements HttpHandler {
    private RecyclerView mRecyclerView;
    private StudentAttendanceListAdapter attendanceListAdapter;
    private ArrayList<ViewAttandence> viewAttandenceArrayList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Attendance List");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.content_list);
        viewAttandenceArrayList = new ArrayList<>();
        Intent data = getIntent();
        String student_id = data.getStringExtra("student_id");
        String roll_no = data.getStringExtra("roll_no");
        String class_id = data.getStringExtra("class_id");
        getAttance(student_id,roll_no,class_id);
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


    private void getAttance(String student_id,String roll_no,String class_id) {
        try {
            JSONObject object = new JSONObject();
            object.put("student_id", student_id);
            object.put("roll_no", roll_no);
            object.put("class_id", class_id);
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.view_attendance, object.toString(), 1);
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
                        JSONArray jsonarray2 = object.getJSONArray("attendance_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                ViewAttandence viewAttandence = new ViewAttandence();
                                viewAttandence.setMonth(jsonobject.optString("month"));
                                viewAttandence.setYear(jsonobject.optString("year"));
                                viewAttandence.setNo_working_days(jsonobject.optString("no_working_days"));
                                viewAttandence.setNo_of_absents(jsonobject.optString("no_of_absents"));
                                viewAttandenceArrayList.add(viewAttandence);
                            }
                            attendanceListAdapter = new StudentAttendanceListAdapter(viewAttandenceArrayList, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(attendanceListAdapter);
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
}
