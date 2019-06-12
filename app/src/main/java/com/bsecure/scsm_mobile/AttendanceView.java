package com.bsecure.scsm_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CheckBox;
import android.widget.Toast;

import com.bsecure.scsm_mobile.adapters.AttandenceListAdapter;
import com.bsecure.scsm_mobile.adapters.StudentsAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.Attandence;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AttendanceView extends AppCompatActivity implements HttpHandler, AttandenceListAdapter.ContactAdapterListener {

    private String class_name, class_id, section, teacher_id;
    private DB_Tables db_tables;
    private AttandenceListAdapter adapter;
    private List<Attandence> attandenceList;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);

        db_tables = new DB_Tables(this);
        db_tables.openDB();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Attendance");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.content_list);
        Intent getData = getIntent();
        if (getData != null) {

            class_name = getData.getStringExtra("class_name");
            class_id = getData.getStringExtra("class_id");
            section = getData.getStringExtra("section");
            teacher_id = getData.getStringExtra("teacher_id");
        }
        // SharedValues.saveValue(this, "firstCall", "No");
        getAttandenceList();
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

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy");
                String current_date = df.format(c);

                //String date_a = db_tables.getAttandeceDate(section, class_id);
                String date_a = db_tables.getAttandecClassId(class_id, getDateN(System.currentTimeMillis()));

                if (date_a == null || date_a.equalsIgnoreCase("null") || date_a.isEmpty()) {
                    String current = getDateDay();
                    if (current.equalsIgnoreCase("Sunday")) {
                        Toast.makeText(this, "Today Holiday", Toast.LENGTH_SHORT).show();
                        return true;
                    }
                    getAttcheck();
//                    getStudents();
//                    Intent stk = new Intent(getApplicationContext(), StudentsView.class);
//                    stk.putExtra("class_id", class_id);
//                    stk.putExtra("section", section);
//                    stk.putExtra("class_name", class_name);
//                    stk.putExtra("teacher_id", teacher_id);
//                    startActivity(stk);

                } else {
                    String date_aa = db_tables.getAttandeceDate(section, class_id);
                    String add_att_date = getDate(Long.valueOf(date_aa));
                    String current = getDate(System.currentTimeMillis());
                    if (current_date.equalsIgnoreCase(add_att_date)) {
                        Toast.makeText(this, "Today Attendence Already Added", Toast.LENGTH_SHORT).show();
                    } else if (current.equalsIgnoreCase(add_att_date)) {
                        Toast.makeText(this, "Holiday", Toast.LENGTH_SHORT).show();
                    } else {
                        Intent stk = new Intent(getApplicationContext(), StudentsView.class);
                        stk.putExtra("class_id", class_id);
                        stk.putExtra("section", section);
                        stk.putExtra("class_name", class_name);
                        stk.putExtra("teacher_id", teacher_id);
                        startActivity(stk);
                    }
                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getAttcheck() {

        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("day", getDateN(System.currentTimeMillis()));
            object.put("class_id", class_id);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 4, Paths.check_holiday, object.toString(), 1);
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

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MMM-yyyy", cal).toString();
        return date;
    }

    private String getDateN(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    private String getDateNN(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    private String getDateDay() {
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        String dayString = sdf.format(new Date());
        return dayString;
    }


    @Override
    public void onResponse(Object results, int requestType) {
        try {
            switch (requestType) {
                case 1:
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        SharedValues.saveValue(this, "firstCall", "Yes");
                        JSONArray jsonarray2 = object.getJSONArray("student_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                db_tables.addstudentsAttandence(jsonobject.optString("student_id"), jsonobject.optString("roll_no"), jsonobject.optString("student_name"), jsonobject.optString("status"), jsonobject.optString("class_id"));
                            }
                        }
                    }
                    break;
                case 4:
                    JSONObject object11 = new JSONObject(results.toString());
                    if (object11.optString("statuscode").equalsIgnoreCase("200")) {
                        getStudents();
                        Intent stk = new Intent(getApplicationContext(), StudentsView.class);
                        stk.putExtra("class_id", class_id);
                        stk.putExtra("section", section);
                        stk.putExtra("class_name", class_name);
                        stk.putExtra("teacher_id", teacher_id);
                        startActivity(stk);
                    } else {
                        Toast.makeText(this, object11.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 3:
                    JSONObject object12 = new JSONObject(results.toString());
                    if (object12.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray att_array = object12.getJSONArray("attendance_details");
                        for (int j = 0; j < att_array.length(); j++) {
                            JSONObject aobj = att_array.getJSONObject(j);
                            String date = String.valueOf(getDateN(Long.parseLong(aobj.optString("attendance_date"))));
                            db_tables.addSyncAttendance("", class_id, "", aobj.optString("attendance_date"), teacher_id, "", date);
                           // db_tables.updateAttendance("", class_id, "", aobj.optString("attendance_date"), teacher_id, "");
                        }
                        getAttandenceList();
                    } else {
                        Toast.makeText(this, object12.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
       // getAttandenceList();
    }

    private void getAttandenceList() {

        try {
            String attendDate = getDateN(System.currentTimeMillis());
            String msg_list = db_tables.getAttandenceList(class_id, attendDate);
            //if (msg_list.length() > 0) {
            attandenceList = new ArrayList<>();
            JSONObject obj = new JSONObject(msg_list);
            JSONArray jsonarray2 = obj.getJSONArray("attendance_details");
            if (jsonarray2.length() > 0) {
                for (int i = 0; i < jsonarray2.length(); i++) {
                    Attandence attandence = new Attandence();

                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
                    attandence.setAttendance_id(jsonobject.optString("attendance_id"));
                    attandence.setStudent_ids(jsonobject.optString("student_ids"));
                    attandence.setAttendance_date(jsonobject.optString("attendance_date"));
                    attandence.setClass_id(jsonobject.optString("class_id"));
                    attandence.setTeacher_id(jsonobject.optString("teacher_id"));
                    attandence.setRoll_no_ids(jsonobject.optString("roll_no_ids"));
                    attandenceList.add(attandence);

                }

                adapter = new AttandenceListAdapter(attandenceList, this, this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);
            } else {
                //sync data from server
                JSONObject object = new JSONObject();
                object.put("school_id", SharedValues.getValue(this, "school_id"));
                object.put("class_id", class_id);
                object.put("teacher_id", teacher_id);
                HTTPNewPost task = new HTTPNewPost(this, this);
                task.userRequest("Processing...", 3, Paths.sync_dates, object.toString(), 1);
            }
            // }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }

    @Override
    public void onMessageRowClicked(List<Attandence> matchesList, int position) {

        Intent st_edit = new Intent(getApplicationContext(), StudentsViewEdit.class);
        st_edit.putExtra("class_id", matchesList.get(position).getClass_id());
        st_edit.putExtra("teacher_id", teacher_id);
        st_edit.putExtra("st_ids", matchesList.get(position).getStudent_ids());
        st_edit.putExtra("roll_ids", matchesList.get(position).getRoll_no_ids());
        st_edit.putExtra("att_date", matchesList.get(position).getAttendance_date());
        st_edit.putExtra("att_id", matchesList.get(position).getAttendance_id());
        startActivity(st_edit);
    }

    @Override
    public void swipeToEdit(int position, List<Attandence> classModelList) {

    }

    @Override
    public void swipeToDelete(int position, List<Attandence> classModelList) {

    }
}
