package com.bsecure.scsm_mobile.modules;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.bsecure.scsm_mobile.View_Exam_List;
import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.StudentsViewAttendence;
import com.bsecure.scsm_mobile.adapters.ParentStudentsListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.chat.ViewChatSingle;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.provider.WebVPage;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback_Parent;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParentView extends AppCompatActivity implements HttpHandler, ParentStudentsListAdapter.ContactAdapterListener {

    private DB_Tables db_tables;
    private List<StudentModel> studentModelList;
    private ParentStudentsListAdapter adapter;
    private RecyclerView mRecyclerView;
    public ItemTouchHelperExtension mItemTouchHelper;
    public ItemTouchHelperExtension.Callback mCallback;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);

        db_tables = new DB_Tables(this);
        db_tables.openDB();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Students List");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.content_list);
        mCallback = new ItemTouchHelperCallback_Parent();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);


        getStudents();
    }

    private void getStudents() {

        try {
            JSONObject object = new JSONObject();
            object.put("phone_number", SharedValues.getValue(this, "ph_number"));
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.get_parent_students, object.toString(), 1);
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
                        JSONArray jsonarray2 = object.getJSONArray("student_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                db_tables.addstudents(jsonobject.optString("student_id"), jsonobject.optString("roll_no"), jsonobject.optString("student_name"), jsonobject.optString("status"), jsonobject.optString("class_id"), jsonobject.optString("section"), jsonobject.optString("class_name"));
                            }
                        }
                        getStudentsList();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getStudentsList() {

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
                    studentModel.setStatus(jsonobject.optString("status"));
                    studentModel.setClass_id(jsonobject.optString("class_id"));
                    studentModel.setStudent_id(jsonobject.optString("student_id"));
                    studentModelList.add(studentModel);

                }


                adapter = new ParentStudentsListAdapter(studentModelList, this, this);
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
    public void onMessageRowClicked(List<StudentModel> matchesList, int position) {
        Intent next = new Intent(getApplicationContext(), ViewChatSingle.class);
        next.putExtra("student_id", matchesList.get(position).getStudent_id());
        next.putExtra("roll_no", matchesList.get(position).getRoll_no());
        next.putExtra("class_id", matchesList.get(position).getClass_id());
        next.putExtra("name", matchesList.get(position).getStudent_name());
        startActivity(next);
    }

    @Override
    public void swipeToSyllabus(int position, List<StudentModel> classModelList) {
        Intent next = new Intent(getApplicationContext(), View_Exam_List.class);
        next.putExtra("student_id", classModelList.get(position).getStudent_id());
        next.putExtra("roll_no", classModelList.get(position).getRoll_no());
        next.putExtra("class_id", classModelList.get(position).getClass_id());
        next.putExtra("check", "1");
        startActivity(next);
    }

    @Override
    public void swipeToMarks(int position, List<StudentModel> classModelList) {
        Intent next = new Intent(getApplicationContext(), View_Exam_List.class);
        next.putExtra("student_id", classModelList.get(position).getStudent_id());
        next.putExtra("roll_no", classModelList.get(position).getRoll_no());
        next.putExtra("class_id", classModelList.get(position).getClass_id());
        next.putExtra("check", "0");
        startActivity(next);
    }

    @Override
    public void swipeToAttendence(int position, List<StudentModel> classModelList) {

        Intent next = new Intent(getApplicationContext(), StudentsViewAttendence.class);
        next.putExtra("student_id", classModelList.get(position).getStudent_id());
        next.putExtra("roll_no", classModelList.get(position).getRoll_no());
        next.putExtra("class_id", classModelList.get(position).getClass_id());
        startActivity(next);

    }

    @Override
    public void swipeToprofomance(int position, List<StudentModel> classModelList) {
        Intent next = new Intent(getApplicationContext(), WebVPage.class);
        next.putExtra("student_id", classModelList.get(position).getStudent_id());
        next.putExtra("name", classModelList.get(position).getStudent_name());
        startActivity(next);
    }

    @Override
    public void onMessageTimeTable(int position, List<StudentModel> classModelList) {

    }

    @Override
    public void swipeToMore(int position, List<StudentModel> classModelList, View view_list_repo_action_more) {

    }


}
