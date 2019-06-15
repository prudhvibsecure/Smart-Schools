package com.bsecure.scsm_mobile;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import com.bsecure.scsm_mobile.adapters.StudentsAdapter;
import com.bsecure.scsm_mobile.adapters.StudentsEditAdapter;
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
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StudentsViewEdit extends AppCompatActivity implements HttpHandler, StudentsEditAdapter.ContactAdapterListener {

    private String class_name, class_id, roll_ids, teacher_id, att_date, att_id, st_ids;
    private DB_Tables db_tables;
    private StudentsEditAdapter adapter;
    private List<StudentModel> studentModelList;
    private RecyclerView mRecyclerView;
    String Student_Id = "", roll_no = "", student_ids, roll_nos;
    ArrayList<String> student_list_id = new ArrayList<>();
    ArrayList<String> rollno_list_id = new ArrayList<>();
    ArrayList<String> student_list_id2 = new ArrayList<>();
    ArrayList<String> rollno_list_id2 = new ArrayList<>();
    private long time_stamp;
    List<StudentModel> matchesList_new;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);

        db_tables = new DB_Tables(this);
        db_tables.openDB();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Students");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.content_list);
        Intent getData = getIntent();
        if (getData != null) {

            student_ids = getData.getStringExtra("st_ids");
            class_id = getData.getStringExtra("class_id");
            roll_nos = getData.getStringExtra("roll_ids");
            teacher_id = getData.getStringExtra("teacher_id");
            att_date = getData.getStringExtra("att_date");
            att_id = getData.getStringExtra("att_id");
            st_ids = getData.getStringExtra("st_ids");
//            String[] allIdsArray = TextUtils.split(student_ids, ",");
//            ArrayList<String> st_idsList = new ArrayList<String>(Arrays.asList(allIdsArray));
//            for (String element : st_idsList) {
//                student_list_id2.add(element);
//            }
//
//            String[] roll_nos_arr = TextUtils.split(roll_nos, ",");
//            ArrayList<String> ro_idsList = new ArrayList<String>(Arrays.asList(roll_nos_arr));
//            for (String element_ro : ro_idsList) {
//                rollno_list_id2.add(element_ro);
//            }
        }

        if (roll_nos.length() == 0 && student_ids.length() == 0) {
            getStudents();
            //getStudentsList("0");

        } else {
            getStudentsList("1");
            //syncStudents();
        }


    }

    private void syncStudents() {

        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("class_id", class_id);
            object.put("teacher_id", teacher_id);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 3, Paths.sync_attendance, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
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
//                if (student_list_id.size() > 0) {
//                    StringBuilder builder = new StringBuilder();
//                    for (String s : student_list_id) {
//                        String con=db_tables.getcheckCondition(s);
//                        if (con.equalsIgnoreCase("1")){
//                            continue;
//                        }else {
//                            db_tables.updateVV(s, "0");
//                        }
//                    }
//                }
                finish();
                break;
            case R.id.st_add:
                if (student_list_id.size() > 0) {
                    addAttendance();
                } else {
                    finish();
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addAttendance() {

        try {
//            String desc;
//            if (rollno_list_id.size()>0){
//                desc=rollno_list_id.toString();
//            }else{
//                desc="All students<br/> present";
//            }
            SweetAlertDialog dialog = new SweetAlertDialog(this);
            dialog.setTitle("Alert!");
            String desc;
            if (rollno_list_id.size() > 0) {
                desc = rollno_list_id.toString();
                dialog.setContentText("Roll No.'s: " + Html.fromHtml(desc + " Are Being Marked Absent."));
            } else {
                desc = "All Students <br/>Present";
                dialog.setContentText("Roll No.'s: " + Html.fromHtml(desc));
            }
            dialog.setContentText("Roll No.'s: " + Html.fromHtml(desc));
            dialog.setCancelable(false);
            dialog.setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    attUpdateAlert();
                    sweetAlertDialog.dismiss();
                }
            });
            dialog.setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    // StudentsViewEdit.this.finish();
                }
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void attUpdateAlert() {

        try {

            StringBuilder builder = new StringBuilder();
            if (student_list_id.size() > 0) {

                for (String s : student_list_id) {

                    // db_tables.updateVV(Student_Id, "1");
                    String con = db_tables.getcheckCondition_Att(s, class_id,att_date);
                    if (con.equalsIgnoreCase("1")) {
                        db_tables.updateVV(s, "0", class_id,att_date);
                        Log.e("present", "0");
                        builder.append("," + s);
                    } else {
                        Log.e("absent", "1");
                        builder.append("," + s);
                        db_tables.updateVV(s, "1", class_id,att_date);
                    }
                }
                String fis = builder.toString();
                student_ids = fis.substring(1);
            }
//            else {
//                for (String s : student_list_id2) {
//                    builder.append("," + s);
//                    String con = db_tables.getcheckCondition(s);
//                    if (con.equalsIgnoreCase("1")) {
//                        db_tables.updateVV(s, "0");
//                    } else {
//                        db_tables.updateVV(s, "1");
//                    }
//                }
//                String fis = builder.toString();
//                student_ids = fis.substring(1);
//            }
            if (rollno_list_id.size() > 0) {
                StringBuilder bb = new StringBuilder();
                for (String s : rollno_list_id) {
                    String con = db_tables.getcheckCondition_roll(s, class_id,att_date);
                    if (con.equalsIgnoreCase("1")) {
                        Log.e("present", "0");
                        bb.append("," + s);
                    } else {
                        Log.e("opsit", "1");
                        bb.append("," + s);
                    }
//                    builder1.append("," + s);
                }
                String fis1 = bb.toString();
                roll_nos = fis1.substring(1);
            }
//            else {
//                for (String s : rollno_list_id2) {
//                    builder1.append("," + s);
//                }
//                String fis1 = builder1.toString();
//                roll_nos = fis1.substring(1);
//            }
            // attendance_id,class_id, attendance_date, student_ids, teacher_id, school_id
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("attendance_id", att_id);
            object.put("class_id", class_id);
            object.put("attendance_date", att_date);
            object.put("student_ids", student_ids);
            object.put("teacher_id", teacher_id);
            object.put("roll_nos", roll_nos);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 2, Paths.edit_attendance, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

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

    private String getDateN(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
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
                            String attendDate = getDateN(Long.parseLong(att_date));
                            String datef = db_tables.getSyncStudents(attendDate, class_id);
                            if (TextUtils.isEmpty(datef)) {
                                for (int i = 0; i < jsonarray2.length(); i++) {
                                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                    db_tables.addstudentsAttandence(jsonobject.optString("student_id"), jsonobject.optString("roll_no"), jsonobject.optString("student_name"), jsonobject.optString("status"), jsonobject.optString("class_id"), att_date);
                                }
                            }
                            syncStudents();
                        }

                        // getStudentsList();
                    }
                    break;
                case 2:
                    JSONObject object1 = new JSONObject(results.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        time_stamp = System.currentTimeMillis();
                        String attendDate = getDate(time_stamp);
                        db_tables.updateAttendance(att_date, class_id, st_ids, String.valueOf(time_stamp), teacher_id, roll_nos, attendDate);
                        finish();

                    }
                    break;

                case 3:
                    JSONObject object2 = new JSONObject(results.toString());
                    if (object2.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = object2.getJSONArray("attendance_details");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject obja = array.getJSONObject(i);
                            time_stamp = System.currentTimeMillis();
                            String attendDate = getDate(Long.parseLong(obja.optString("attendance_date")));
                            String att_date1 = getDate(Long.parseLong(att_date));
                            String datef = db_tables.getSyncStudents(attendDate, class_id);
                            if (TextUtils.isEmpty(datef) && att_date1.startsWith(attendDate)) {
                                if (obja.optString("student_ids").contains(",") || obja.optString("roll_nos").contains(",")) {
                                    String[] st_ids = obja.optString("student_ids").split(",");
                                    String[] roll_nos = obja.optString("roll_nos").split(",");
                                    for (int k = 0; k < st_ids.length; k++) {
                                        db_tables.updateSyncAttendance(obja.optString("attendance_date"), obja.optString("attendance_id"), obja.optString("class_id"), st_ids[k], "1", roll_nos[k], attendDate);
                                        db_tables.updateVV(st_ids[k], "1", class_id,att_date);
                                    }
                                } else {
                                    db_tables.updateSyncAttendance(obja.optString("attendance_date"), obja.optString("attendance_id"), obja.optString("class_id"), obja.optString("student_ids"), "1", obja.optString("roll_nos"), attendDate);
                                    db_tables.updateVV(obja.optString("student_ids"), "1", class_id,att_date);
                                }
                            }
                        }
                        getStudentsList("1");

                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    private void getStudentsList(String val) {

        try {
            String attendDate = getDateN(System.currentTimeMillis());
            String msg_list = db_tables.getstudentsList_Attend(class_id, att_date);
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
                    studentModel.setCondition(jsonobject.optString("condition"));
                    studentModelList.add(studentModel);

                }

                adapter = new StudentsEditAdapter(studentModelList, this, this);
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
    public void onRowClicked(List<StudentModel> matchesList, boolean value, CheckBox chk_name, int position) {

//        if (chk_name.isChecked()) {
//            if (matchesList.get(position).getCondition().equalsIgnoreCase("1")) {
//                Student_Id = matchesList.get(position).getStudent_id();
//                student_list_id.remove(Student_Id);
//                roll_no = matchesList.get(position).getRoll_no();
//                rollno_list_id.remove(roll_no);
//                db_tables.updateVV(Student_Id, "0");
//                chk_name.setChecked(true);
//                chk_name.setBackground(ContextCompat.getDrawable(this, R.mipmap.ic_check));
//
//            } else {
//                Student_Id = matchesList.get(position).getStudent_id();
//                student_list_id.remove(Student_Id);
//                roll_no = matchesList.get(position).getRoll_no();
//                rollno_list_id.remove(roll_no);
//                db_tables.updateVV(Student_Id, "0");
//                chk_name.setChecked(true);
//                chk_name.setBackground(ContextCompat.getDrawable(this, R.mipmap.ic_check));
//            }
//
//        } else {
//            if (matchesList.get(position).getCondition().equalsIgnoreCase("1")) {
//                chk_name.setChecked(false);
//                chk_name.setBackground(ContextCompat.getDrawable(this, R.mipmap.ic_uncheck));
//                Student_Id = matchesList.get(position).getStudent_id();
//                student_list_id.add(Student_Id);
//                roll_no = matchesList.get(position).getRoll_no();
//                rollno_list_id.add(roll_no);
//                db_tables.updateVV(Student_Id, "0");
//
//            } else {
//                chk_name.setChecked(false);
//                chk_name.setBackground(ContextCompat.getDrawable(this, R.mipmap.ic_uncheck));
//                Student_Id = matchesList.get(position).getStudent_id();
//                student_list_id.add(Student_Id);
//                roll_no = matchesList.get(position).getRoll_no();
//                rollno_list_id.add(roll_no);
//                db_tables.updateVV(Student_Id, "0");
//
//            }
//        }

        if (chk_name.isChecked()) {
            Student_Id = matchesList.get(position).getStudent_id();
            student_list_id.add(Student_Id);
            roll_no = matchesList.get(position).getRoll_no();
            rollno_list_id.add(roll_no);
            if (matchesList.get(position).getCondition().equalsIgnoreCase("1")) {
                chk_name.setBackground(getResources().getDrawable(R.mipmap.ic_check));
//                student_list_id.remove(Student_Id);
//                rollno_list_id.remove(roll_no);
                // db_tables.updateVV(Student_Id,"0",class_id);
            } else {
                chk_name.setBackground(getResources().getDrawable(R.mipmap.ic_uncheck));
            }
        } else {
            Student_Id = matchesList.get(position).getStudent_id();
            student_list_id.remove(Student_Id);
            roll_no = matchesList.get(position).getRoll_no();
            rollno_list_id.remove(roll_no);
            // db_tables.updateVV(Student_Id, "0");
            if (matchesList.get(position).getCondition().equalsIgnoreCase("1")) {
                chk_name.setBackground(getResources().getDrawable(R.mipmap.ic_uncheck));
                student_list_id.add(Student_Id);
                rollno_list_id.add(roll_no);

            } else {
                chk_name.setBackground(getResources().getDrawable(R.mipmap.ic_check));
            }
//
        }
        //else {
//
//        if (matchesList.get(position).getCondition().equalsIgnoreCase("1")) {
//            Student_Id = matchesList.get(position).getStudent_id();
//            student_list_id.add(Student_Id);
//            roll_no = matchesList.get(position).getRoll_no();
//            rollno_list_id.add(roll_no);
//            // db_tables.updateVV(Student_Id, "1");
//            chk_name.setBackground(getResources().getDrawable(R.mipmap.ic_check));
//        } else {
//            Student_Id = matchesList.get(position).getStudent_id();
//            student_list_id.remove(Student_Id);
//            roll_no = matchesList.get(position).getStudent_id();
//            rollno_list_id.remove(roll_no);
//            db_tables.updateVV(Student_Id, "1");
//            chk_name.setBackground(getResources().getDrawable(R.mipmap.ic_uncheck));
//
//        }

//    }
    }

    @Override
    public void onBackPressed() {

//        if (student_list_id.size() > 0) {
//            StringBuilder builder = new StringBuilder();
//            for (String s : student_list_id) {
//                String con=db_tables.getcheckCondition(s);
//                if (con.equalsIgnoreCase("1")){
//                    continue;
//                }else {
//                    db_tables.updateVV(s, "0");
//                }
//            }
//        }
        super.onBackPressed();
    }
}
