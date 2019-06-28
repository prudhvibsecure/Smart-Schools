package com.bsecure.scsm_mobile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.adapters.StudentsMarksAdapter;
import com.bsecure.scsm_mobile.adapters.SyllabusListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.models.SyllabusModel;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallbackSyllabus;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;
import com.bsecure.scsm_mobile.utils.SharedValues;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SyllabusView extends AppCompatActivity implements View.OnClickListener, HttpHandler, SyllabusListAdapter.ContactAdapterListener {

    private String subject, class_id, total_marks, teacher_id, exam_id, syllab_id;
    private DB_Tables db_tables;
    private RecyclerView mRecyclerView;
    private Dialog mDialog;
    LinearLayout section_container;
    ArrayList<String> section_list;
    ArrayList<SyllabusModel> syllabusModelArrayList;
    private SyllabusListAdapter adapter;
    public ItemTouchHelperExtension mItemTouchHelper;
    public ItemTouchHelperExtension.Callback mCallback;
    String add_sy_id;
    TextView bt_nonscholastic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);
        db_tables = new DB_Tables(this);
        db_tables.openDB();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        bt_nonscholastic = findViewById(R.id.bt_nscholastic);
        bt_nonscholastic.setVisibility(View.GONE);

        Intent getData = getIntent();
        if (getData != null) {
            class_id = getData.getStringExtra("class_id");
            total_marks = getData.getStringExtra("total_marks");
            subject = getData.getStringExtra("subject");
            exam_id = getData.getStringExtra("exam_id");
            teacher_id = getData.getStringExtra("teacher_id");

        }

        toolbar.setTitle(subject + " Syllabus");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.content_list);
        mCallback = new ItemTouchHelperCallbackSyllabus();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        getSyllabusList();

    }

    private void getSyllabusList() {

        try {
            String msg_list = db_tables.getSyllabusList(subject,class_id);
            syllabusModelArrayList = new ArrayList<>();
            JSONObject obj = new JSONObject(msg_list);
            JSONArray jsonarray2 = obj.getJSONArray("syllabus_details");
            if (jsonarray2.length() > 0) {
                for (int i = 0; i < jsonarray2.length(); i++) {
                    SyllabusModel studentModel = new SyllabusModel();
                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
                    studentModel.setDescription(jsonobject.optString("description"));
                    studentModel.setLesson(jsonobject.optString("lesson"));
                    studentModel.setSubject(jsonobject.optString("subject"));
                    studentModel.setSyllabus_id(jsonobject.optString("syllabus_id"));
                    syllabusModelArrayList.add(studentModel);

                }


                adapter = new SyllabusListAdapter(syllabusModelArrayList, this, this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);
            } else {
                syncSyllabus();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void syncSyllabus() {
        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("examination_time_table_id", exam_id);
            object.put("class_id", class_id);
            object.put("teacher_id", teacher_id);
            object.put("subject", subject);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 14, Paths.base + "sync_syllabus", object.toString(), 1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getSyllabusData() {
//        try {
//            JSONObject object=new JSONObject();
//            object.put("school_id", SharedValues.getValue(this, "school_id"));
//            object.put("exam_name", exams_name);
//            object.put("class_id", class_id);
//            HTTPNewPost task=new HTTPNewPost(this,this);
//            task.userRequest("",13,Paths.base+"view_syllabus",object.toString(),1);
//
//        } catch (Exception e) {
//
//        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.add_students, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.st_add:
                addsyllabusTab();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addsyllabusTab() {
        section_list = new ArrayList<String>();
        mDialog = new Dialog(this, R.style.MyAlertDialogStyle);
        mDialog.setContentView(R.layout.add_syllabus_form);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.findViewById(R.id.submit_sb).setOnClickListener(this);
        mDialog.findViewById(R.id.sec_img_add).setOnClickListener(this);
        mDialog.getWindow().setGravity(Gravity.TOP);
        section_container = (LinearLayout) mDialog.findViewById(R.id.section_container);
        mDialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.submit_sb:
                if (((EditText) mDialog.findViewById(R.id.sub_et)).getText().toString().length() == 0) {
                    Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (((EditText) mDialog.findViewById(R.id.sub_et_les)).getText().toString().length() == 0) {
                    Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                addSyllabus();
                break;
            case R.id.submit_up:
                if (((EditText) mDialog.findViewById(R.id.sub_et)).getText().toString().length() == 0) {
                    Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (((EditText) mDialog.findViewById(R.id.sub_et_les)).getText().toString().length() == 0) {
                    Toast.makeText(this, "Please Fill Required Fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateSyllabus();
                break;
        }

    }

    private void updateSyllabus() {
        try {

            JSONArray array = new JSONArray();
            JSONObject ob = new JSONObject();
            ob.put("syllabus_lessons_id", System.currentTimeMillis());
            ob.put("lesson", ((EditText) mDialog.findViewById(R.id.sub_et)).getText().toString().trim());
            ob.put("description", ((EditText) mDialog.findViewById(R.id.sub_et_les)).getText().toString().trim());
            array.put(ob);
            JSONObject object = new JSONObject();
            object.put("syllabus_id", syllab_id);
            object.put("examination_time_table_id", exam_id);
            object.put("teacher_id", teacher_id);
            object.put("subject", subject);
            object.put("syllabus", array);
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("domain", ContentValues.DOMAIN);
//            object.put("syllabus_lessons_id", syllab_id);
//            object.put("lesson", ((EditText) mDialog.findViewById(R.id.sub_et)).getText().toString());
//            object.put("description", ((EditText) mDialog.findViewById(R.id.sub_et_les)).getText().toString());
//            object.put("school_id", SharedValues.getValue(getApplicationContext(), "school_id"));
            db_tables.syllabusUpdateV(syllab_id, ((EditText) mDialog.findViewById(R.id.sub_et)).getText().toString(), ((EditText) mDialog.findViewById(R.id.sub_et_les)).getText().toString(), subject,class_id);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 2, Paths.add_syllabus, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addSyllabus() {
        try {

            add_sy_id= String.valueOf(System.currentTimeMillis());
            // syllabus_id,examination_time_table_id, teacher_id , subject, syllabus[syllabus_lessons_id,lesson,description],school_id

            JSONArray array = new JSONArray();
            JSONObject ob = new JSONObject();
            ob.put("syllabus_lessons_id", add_sy_id);
            ob.put("lesson", ((EditText) mDialog.findViewById(R.id.sub_et)).getText().toString().trim());
            ob.put("description", ((EditText) mDialog.findViewById(R.id.sub_et_les)).getText().toString().trim());
            array.put(ob);


            JSONObject object = new JSONObject();
            object.put("syllabus_id", add_sy_id);
            object.put("examination_time_table_id", exam_id);
            object.put("teacher_id", teacher_id);
            object.put("subject", subject);
            object.put("syllabus", array);
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("domain", ContentValues.DOMAIN);
            db_tables.addSyllabus(add_sy_id, ((EditText) mDialog.findViewById(R.id.sub_et)).getText().toString().trim(), ((EditText) mDialog.findViewById(R.id.sub_et_les)).getText().toString(), subject,class_id);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.add_syllabus, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addSectionView(long sy_id, final String newText, final String subject) {
        LayoutInflater layoutInflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View newView = layoutInflater.inflate(R.layout.row_section_item, null);
        TextView textOut = (TextView) newView.findViewById(R.id.tex_section);
        textOut.setText(newText);

        ImageView buttonRemove = (ImageView) newView.findViewById(R.id.remove_sec);
        buttonRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((LinearLayout) newView.getParent())
                        .removeView(newView);
                //section_list.remove(newText);
            }
        });

        section_container.addView(newView);
        section_list.add(newText);
    }

    @Override
    public void onResponse(Object results, int requestType) {
        try {
            switch (requestType) {
                case 1:
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        Toast.makeText(this, "Syllabus Added successfully", Toast.LENGTH_SHORT).show();
                        add_sy_id=null;
                        mDialog.dismiss();
                        getSyllabusList();
                    }
                    break;

                case 2:
                    JSONObject object1 = new JSONObject(results.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        Toast.makeText(this, "Syllabus Updated successfully", Toast.LENGTH_SHORT).show();
                        add_sy_id=null;
                        mDialog.dismiss();
                        getSyllabusList();
                    }
                    break;
                case 13:
                    break;

                case 14:
                    JSONObject obj = new JSONObject(results.toString());
                    if(obj.optString("statuscode").equalsIgnoreCase("200"))
                    {
                        JSONArray array = obj.getJSONArray("syllabus_details");
                        if(array.length() !=0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obj1 = array.getJSONObject(i);
                                db_tables.addSyllabus(obj1.optString("syllabus_lessons_id"), obj1.optString("lesson"), obj1.optString("description"), subject, class_id);
                            }
                            getSyllabusList();
                        }else
                        {
                            Toast.makeText(this, "No Data Found", Toast.LENGTH_SHORT).show();
                        }
                    }
                    else
                    {
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
    public void swipeToEdit(int position, List<SyllabusModel> classModelList) {

        syllab_id = classModelList.get(position).getSyllabus_id();
        mDialog = new Dialog(this, R.style.MyAlertDialogStyle);
        mDialog.setContentView(R.layout.add_syllabus_form);
        mDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        mDialog.setCancelable(true);
        mDialog.setCanceledOnTouchOutside(true);
        mDialog.findViewById(R.id.submit_sb).setOnClickListener(this);
        mDialog.findViewById(R.id.submit_up).setOnClickListener(this);
        mDialog.findViewById(R.id.submit_up).setVisibility(View.VISIBLE);
        mDialog.findViewById(R.id.submit_sb).setVisibility(View.GONE);
        mDialog.findViewById(R.id.sec_img_add).setOnClickListener(this);
        ((EditText) mDialog.findViewById(R.id.sub_et)).setText(classModelList.get(position).getLesson());
        ((EditText) mDialog.findViewById(R.id.sub_et_les)).setText(classModelList.get(position).getDescription());
        mDialog.getWindow().setGravity(Gravity.TOP);
        section_container = (LinearLayout) mDialog.findViewById(R.id.section_container);
        mDialog.show();

    }

    @Override
    public void swipeToDelete(int position, List<SyllabusModel> classModelList) {


    }
}
