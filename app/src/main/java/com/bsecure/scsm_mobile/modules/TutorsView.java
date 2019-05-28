package com.bsecure.scsm_mobile.modules;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.StudentsView;
import com.bsecure.scsm_mobile.View_Exam_List;
import com.bsecure.scsm_mobile.adapters.ClassListAdapter;
import com.bsecure.scsm_mobile.adapters.TutorAssignStudentsListAdapter;
import com.bsecure.scsm_mobile.adapters.TutorsListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.chat.TutorsChatSingle;
import com.bsecure.scsm_mobile.chat.ViewChatSingle;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.ClassModel;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.models.TutorsModel;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback_Tutors;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TutorsView extends AppCompatActivity implements HttpHandler, TutorAssignStudentsListAdapter.ContactAdapterListener {
    private DB_Tables db_tables;
    ArrayList<StudentModel> studentModelArrayList;
    private TutorAssignStudentsListAdapter adapter;
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
        toolbar.setTitle("Tutors");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.content_list);
        mCallback = new ItemTouchHelperCallback_Tutors();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        getTurotos();
    }

    private void getTurotos() {

        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("tutor_id", SharedValues.getValue(this, "id"));

            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.get_tutor_students, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Object results, int requestType) {
        try {
            switch (requestType) {
                case 1:
                    studentModelArrayList = new ArrayList<>();
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        findViewById(R.id.no_data).setVisibility(View.GONE);
                        JSONArray jsonarray2 = object.getJSONArray("tutor_students_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                StudentModel studentModel = new StudentModel();
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                studentModel.setClass_name(jsonobject.optString("class"));
                                studentModel.setClass_id(jsonobject.optString("class_id"));
                                studentModel.setStudent_id(jsonobject.optString("student_id"));
                                studentModel.setStudent_name(jsonobject.optString("student_name"));
                                studentModel.setSection(jsonobject.optString("section"));
                                studentModelArrayList.add(studentModel);
                            }
                            adapter = new TutorAssignStudentsListAdapter(studentModelArrayList, this, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
                        }
                    } else {
                        findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    JSONObject object1 = new JSONObject(results.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        Toast.makeText(this, "Student Removed successfully", Toast.LENGTH_SHORT).show();
                        getTurotos();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

//    private void getListTutors() {
//
//        try {
//            String msg_list = db_tables.getTutorsList();
//            tutorsModelArrayList = new ArrayList<>();
//            JSONObject obj = new JSONObject(msg_list);
//            JSONArray jsonarray2 = obj.getJSONArray("tutor_body");
//
//            if (jsonarray2.length() > 0) {
//                for (int i = 0; i < jsonarray2.length(); i++) {
//                    TutorsModel tutorsModel = new TutorsModel();
//
//                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
//                    tutorsModel.setTutor_id(jsonobject.optString("tutor_id"));
//                    tutorsModel.setPhone_number(jsonobject.optString("phone_number"));
//                    tutorsModel.setSchool_id(jsonobject.optString("school_id"));
//                    tutorsModel.setStudent_id(jsonobject.optString("student_id"));
//                    tutorsModel.setTutor_name(jsonobject.optString("tutor_name"));
//                    tutorsModel.setTutor_status(jsonobject.optString("tutor_status"));
//                    tutorsModelArrayList.add(tutorsModel);
//
//                }
//
//
//                adapter = new TutorsListAdapter(tutorsModelArrayList, this, this);
//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//                mRecyclerView.setLayoutManager(linearLayoutManager);
//                mRecyclerView.setAdapter(adapter);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }
//    @Override
//    public void onMessageRowClicked(List<TutorsModel> matchesList, int position) {
//        Intent nxt = new Intent(this, TutorsChatSingle.class);
//        nxt.putExtra("name", matchesList.get(position).getTutor_name());
//        nxt.putExtra("t_id", matchesList.get(position).getTutor_id());
//        nxt.putExtra("student_id", matchesList.get(position).getStudent_id());
//        startActivity(nxt);
//
//    }
//
//    @Override
//    public void swipeToEdit(int position, List<TutorsModel> classModelList) {
//
//    }
//
//    @Override
//    public void swipeToDelete(int position, List<TutorsModel> classModelList) {
//
//    }

    @Override
    public void onMessageRowClicked(List<StudentModel> matchesList, int position) {
        Intent nxt = new Intent(this, TutorsChatSingle.class);
        nxt.putExtra("name", matchesList.get(position).getStudent_name());
        nxt.putExtra("t_id", matchesList.get(position).getClass_id());
        nxt.putExtra("student_id", matchesList.get(position).getStudent_id());
        startActivity(nxt);
    }

    @Override
    public void swipeToSyllabus(int position, List<StudentModel> classModelList) {

        Intent next = new Intent(TutorsView.this, View_Exam_List.class);
        next.putExtra("student_id", classModelList.get(position).getStudent_id());
        next.putExtra("roll_no", classModelList.get(position).getRoll_no());
        next.putExtra("class_id", classModelList.get(position).getClass_id());
        next.putExtra("check", "1");
        startActivity(next);
    }

   /* @Override
    public void swipeToMarks(int position, List<StudentModel> classModelList) {

    }*/

    @Override
    public void swipeToDelete(int position, List<StudentModel> classModelList) {
        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("tutor_id", SharedValues.getValue(this, "id"));
            object.put("student_id", classModelList.get(position).getStudent_id());
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 2, Paths.tutor_delete_student, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
