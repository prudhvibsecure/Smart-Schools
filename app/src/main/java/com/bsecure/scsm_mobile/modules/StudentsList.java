package com.bsecure.scsm_mobile.modules;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.ExamsListAdapter;
import com.bsecure.scsm_mobile.adapters.StudentsAdapter;
import com.bsecure.scsm_mobile.adapters.StudentsListPerformanceAdapter;
import com.bsecure.scsm_mobile.callbacks.ClickListener;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.Exams;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentsList extends AppCompatActivity implements HttpHandler, ExamsListAdapter.ContactAdapterListener {

    private ArrayList<StudentModel> studentModelList;
    private RecyclerView mRecyclerView;
    private StudentsListPerformanceAdapter adapter;
    private DB_Tables db_tables;
    private String class_id, id, name, roll_no;
    private Dialog mDialog;
    private List<Exams> examsList = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_students_list);

        db_tables = new DB_Tables(this);
        db_tables.openDB();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Students");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent in = getIntent();

        class_id = in.getStringExtra("class_id");

        mRecyclerView = findViewById(R.id.content_list);

        getStudents();

    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_common, menu);
        return true;
    }*/

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:

                finish();
                break;
            case R.id.st_add:

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getStudents() {

        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("class_id", class_id);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.get_students, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Object results, int requestType) {

        try {
            switch (requestType) {
                case 1:
                    studentModelList = new ArrayList<>();
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        SharedValues.saveValue(this, "firstCall", "Yes");
                        JSONArray jsonarray2 = object.getJSONArray("student_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                StudentModel studentModel = new StudentModel();

                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                studentModel.setStudent_name(jsonobject.optString("student_name"));
                                studentModel.setRoll_no(jsonobject.optString("roll_no"));
                                studentModel.setStatus(jsonobject.optString("status"));
                                studentModel.setClass_id(jsonobject.optString("class_id"));
                                studentModel.setStudent_id(jsonobject.optString("student_id"));
                                studentModel.setSelected(true);

                                studentModelList.add(studentModel);

                            }

                            adapter = new StudentsListPerformanceAdapter(this, studentModelList, new ClickListener() {
                                @Override
                                public void OnRowClicked(int position, View view)
                                {
                                    id = studentModelList.get(position).getStudent_id();
                                    name = studentModelList.get(position).getStudent_name();
                                    roll_no = studentModelList.get(position).getRoll_no();
                                    class_id = studentModelList.get(position).getClass_id();
                                    getExams();
                                }
                            });
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
                        }
                    }
                    else {
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 101:
                    examsList = new ArrayList<>();
                    JSONObject object1 = new JSONObject(results.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray jsonarray2 = object1.getJSONArray("examination_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                Exams exams = new Exams();
                                exams.setExam_name(jsonobject.optString("exam_name"));
                                exams.setExaminations_id(jsonobject.optString("examinations_id"));
                                examsList.add(exams);
                            }

                            getDialog(examsList);
//
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

    private void getExams() {
        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(StudentsList.this, "school_id"));
            object.put("class_id", class_id);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(StudentsList.this, this);
            task.userRequest("Processing...", 101, Paths.get_examinations, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDialog(List<Exams> examsList) {
        mDialog = new Dialog(StudentsList.this);
        mDialog.setContentView(R.layout.content_main_view);
        Toolbar toolbar = mDialog.findViewById(R.id.toolset);
        toolbar.setTitle("Exams");
        toolbar.setTitleTextColor(Color.WHITE);
        mDialog.show();
        RecyclerView recyclerView = mDialog.findViewById(R.id.content_list);
        ExamsListAdapter adapter = new ExamsListAdapter(examsList, StudentsList.this, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(StudentsList.this);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }


    @Override
    public void onExamRowClicked(List<Exams> matchesList, int position) {

        mDialog.dismiss();
        String exam_name = matchesList.get(position).getExam_name();
        // chekGraphs(matchesList.get(position).getExam_name());
        String school_id = SharedValues.getValue(StudentsList.this, "school_id");
        Intent in = new Intent(StudentsList.this, StudentPerformance.class);
        in.putExtra("class_id", class_id);
        in.putExtra("exam_name", exam_name);
        in.putExtra("roll_no", roll_no);
        in.putExtra("school_id", school_id);
        startActivity(in);

    }
}
