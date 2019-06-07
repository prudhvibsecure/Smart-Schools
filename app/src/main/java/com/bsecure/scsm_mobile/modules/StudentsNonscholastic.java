package com.bsecure.scsm_mobile.modules;

import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.StudentsMarks;
import com.bsecure.scsm_mobile.adapters.NonScholasticStudentsAdapter;
import com.bsecure.scsm_mobile.adapters.StudentsMarksAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StudentsNonscholastic extends AppCompatActivity implements HttpHandler, NonScholasticStudentsAdapter.ContactAdapterListener{

    private String subject, class_id, total_marks, teacher_id, exam_id, comma_marks;
    private DB_Tables db_tables;
    private NonScholasticStudentsAdapter adapter;
    private List<StudentModel> studentModelList;
    private RecyclerView mRecyclerView;
   /* String Student_Id = "", roll_no = "", student_ids, roll_nos, makes_comma;
    ArrayList<String> student_list_id = new ArrayList<>();
    ArrayList<String> rollno_list_id = new ArrayList<>();
    private long time_stamp = System.currentTimeMillis();
    private ArrayList<String> marks_list = null;
    private ArrayList<String> student_names = new ArrayList<>();
    private Map<String, String> marks_list_vv = new HashMap<>();
    private ArrayList<String> student_ids_l = new ArrayList<>();
    private ArrayList<String> roll_ids = new ArrayList<>();
    private Dialog marks_Diloag;
    private String remov_mark;
    private int sizeList;*/
    int i = 0;
    StringBuilder builder = null, builder11 = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);

        db_tables = new DB_Tables(this);
        db_tables.openDB();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        Intent getData = getIntent();
        if (getData != null) {
            class_id = getData.getStringExtra("class_id");
            //total_marks = getData.getStringExtra("total_marks");
            //subject = getData.getStringExtra("subject");
            exam_id = getData.getStringExtra("exam_id");
            teacher_id = getData.getStringExtra("teacher_id");
        }
        toolbar.setTitle("Students");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.content_list);

        String my_sub_marks = db_tables.getMarks(exam_id, class_id, subject);
        if (my_sub_marks == null || my_sub_marks.length() == 0) {
            getStudents();
        } else {
            getStudentsList2();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_common, menu);
        return true;
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


    private void getStudents() {

        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("class_id", class_id);
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
                        JSONArray jsonarray2 = object.getJSONArray("student_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                StudentModel studentModel = new StudentModel();
                                studentModel.setStudent_name(jsonobject.optString("student_name"));
                                studentModel.setRoll_no(jsonobject.optString("roll_no"));
                                //studentModel.setStatus(jsonobject.optString("status"));
                                studentModel.setClass_id(jsonobject.optString("class_id"));
                                studentModel.setStudent_id(jsonobject.optString("student_id"));
                                // studentModel.setMarkslist(jsonobject.optString("marks_obtained"));
                                studentModelList.add(studentModel);
                                // db_tables.addstudents(jsonobject.optString("student_id"), jsonobject.optString("roll_no"), jsonobject.optString("student_name"), jsonobject.optString("status"), jsonobject.optString("class_id"));
                            }
                            Collections.sort(studentModelList, new Comparator<StudentModel>() {
                                @Override
                                public int compare(StudentModel lhs, StudentModel rhs) {
                                    return Integer.valueOf(lhs.getRoll_no()).compareTo(Integer.valueOf(rhs.getRoll_no()));
                                }
                            });
                            adapter = new NonScholasticStudentsAdapter(studentModelList, this, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);

                        }
                        // getStudentsList(exam_id);
                    }
                    break;
                case 2:
                    JSONObject object1 = new JSONObject(results.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        // db_tables.addAttendance(String.valueOf(time_stamp), class_id, student_ids, String.valueOf(time_stamp), teacher_id, roll_nos);
                        Toast.makeText(this, "Marks Submitted Successfully", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getStudentsList(String e_id) {

        try {
            String msg_list = db_tables.getstudentsList();
            studentModelList = new ArrayList<>();
            JSONObject obj = new JSONObject(msg_list);
            JSONArray jsonarray2 = obj.getJSONArray("student_details");
            if (jsonarray2.length() > 0) {
                for (int i = 0; i < jsonarray2.length(); i++) {
                    StudentModel studentModel = new StudentModel();
                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
                    studentModel.setStudent_name(jsonobject.optString("student_name"));
                    studentModel.setRoll_no(jsonobject.optString("roll_no"));
                    // studentModel.setStatus(jsonobject.optString("status"));
                    studentModel.setClass_id(jsonobject.optString("class_id"));
                    studentModel.setStudent_id(jsonobject.optString("student_id"));
                    studentModelList.add(studentModel);

                }


                adapter = new NonScholasticStudentsAdapter(studentModelList, this, this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getStudentsList2() {

        try {
            String msg_list = db_tables.getstudentsListMarks(subject, exam_id);
            studentModelList = new ArrayList<>();
            JSONObject obj = new JSONObject(msg_list);
            JSONArray jsonarray2 = obj.getJSONArray("student_details");
            if (jsonarray2.length() > 0) {
                for (int i = 0; i < jsonarray2.length(); i++) {
                    StudentModel studentModel = new StudentModel();
                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
                    studentModel.setStudent_name(jsonobject.optString("student_name"));
                    studentModel.setRoll_no(jsonobject.optString("roll_no"));
                    //studentModel.setStatus(jsonobject.optString("status"));
                    studentModel.setClass_id(jsonobject.optString("class_id"));
                    studentModel.setStudent_id(jsonobject.optString("student_id"));
                    // String st_name=db_tables.get
                    studentModel.setMarkslist(jsonobject.optString("marks_obtained"));
                    studentModelList.add(studentModel);


                }

                Collections.sort(studentModelList, new Comparator<StudentModel>() {
                    @Override
                    public int compare(StudentModel lhs, StudentModel rhs) {
                        return Integer.valueOf(lhs.getRoll_no()).compareTo(Integer.valueOf(rhs.getRoll_no()));
                    }
                });

                adapter = new NonScholasticStudentsAdapter(studentModelList, this, this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }

    @Override
    public void onRowClicked(final List<StudentModel> matchesList, final boolean value, final int position, final EditText marks_edit, final CheckBox checked) {

        String student_id = matchesList.get(position).getStudent_id();
        String class_id = matchesList.get(position).getClass_id();

        Intent in = new Intent(StudentsNonscholastic.this, NonScholasticTeacher.class);
        in.putExtra("sid", student_id);
        in.putExtra("cid", class_id);
        in.putExtra("tid", teacher_id);
        in.putExtra("eid", exam_id);
        startActivity(in);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
