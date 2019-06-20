package com.bsecure.scsm_mobile;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.TextUtils;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.adapters.StudentsAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.callbacks.OfflineDataInterface;
import com.bsecure.scsm_mobile.common.NetworkInfoAPI;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StudentsView extends AppCompatActivity implements HttpHandler, StudentsAdapter.ContactAdapterListener, OfflineDataInterface, NetworkInfoAPI.OnNetworkChangeListener {

    private String class_name, class_id, section, teacher_id, ss_d = "";
    private DB_Tables db_tables;
    private StudentsAdapter adapter;
    private List<StudentModel> studentModelList;
    private RecyclerView mRecyclerView;
    String Student_Id = "", roll_no = "", student_ids = "", roll_nos = "";
    ArrayList<String> student_list_id = new ArrayList<>();
    ArrayList<String> rollno_list_id = new ArrayList<>();
    private long time_stamp;
    String att_date = "";
    private List<WeakReference<OfflineDataInterface>> mObservers = new ArrayList<WeakReference<OfflineDataInterface>>();
    String action_type_data;
    private NetworkInfoAPI networkInfoAPI;
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

            class_name = getData.getStringExtra("class_name");
            class_id = getData.getStringExtra("class_id");
            section = getData.getStringExtra("section");
            teacher_id = getData.getStringExtra("teacher_id");
        }

        att_date = String.valueOf(System.currentTimeMillis());
        networkInfoAPI = new NetworkInfoAPI();
        networkInfoAPI.initialize(this);
        networkInfoAPI.setOnNetworkChangeListener(this);
        addObserver(this);

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
                if (student_list_id.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    for (String s : student_list_id) {
                        db_tables.updateVV(s, "0", class_id, att_date);
                    }
                }
                finish();
                break;
            case R.id.st_add:
                addAttendance();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void addAttendance() {
        try {

            //SweetAlertDialog dialog = new SweetAlertDialog(this);

            Cdialogue dialogu = new Cdialogue(this);
            final Dialog dialog = new Dialog(this);
            dialog.setContentView(R.layout.dialog);
            dialog.setTitle("Alert!");
            String desc;
            /*if (rollno_list_id.size() > 0) {
                desc = "Roll no's"+rollno_list_id.toString()+"<br/>";
                dialog.findViewById(R.id.data).setContentDescription("" + Html.fromHtml(desc + " Are Being Marked Absent."));
            } else {
                desc = "All Students <br/>Present";
                dialog.setContentText("" + Html.fromHtml(desc));
            }
            dialog.setCancelable(false);
            dialog.setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    addAttAlert();
                    sweetAlertDialog.dismiss();
                }
            });
            dialog.setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    // StudentsView.this.finish();
                }
            });*/

            if (rollno_list_id.size() > 0) {
                desc = rollno_list_id.toString();
                //dialog.setContentText("Roll No.'s: " + Html.fromHtml(desc + " Are Being Marked Absent."));
                TextView tv = dialog.findViewById(R.id.data);
                tv.setText("Roll No's:" + Html.fromHtml(desc + " Are Being Marked Absent. Are You Sure You Want To Submit?"));
            }
            else {
                desc = "All Students <br/>Present";
                TextView tv = dialog.findViewById(R.id.data);
                tv.setText("Roll No.'s: " + Html.fromHtml(desc));
            }
            TextView tv = dialog.findViewById(R.id.data);
            tv.setText("Roll No.'s: " + Html.fromHtml(desc)+ "Are Being Marked Absent. Are You Sure You Want To Submit?");
            dialog.setCancelable(false);
            Button yes = dialog.findViewById(R.id.yes);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    addAttAlert();
                    dialog.dismiss();
                }
            });

            Button no = dialog.findViewById(R.id.no);
            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //attUpdateAlert();
                    dialog.dismiss();
                }
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void addAttAlert() {
        try {


            if (student_list_id.size() > 0) {
                StringBuilder builder = new StringBuilder();
                for (String s : student_list_id) {
                    builder.append("," + s);
                    ss_d = s;
                    db_tables.updateVV(s, "1", class_id, att_date);
                    //  db_tables.updateVV(s, "1");
                }
                String fis = builder.toString();
                student_ids = fis.substring(1);
            }
            if (rollno_list_id.size() > 0) {
                StringBuilder builder1 = new StringBuilder();
                for (String s : rollno_list_id) {
                    builder1.append("," + s);
                }
                String fis1 = builder1.toString();
                roll_nos = fis1.substring(1);
            }
            time_stamp = System.currentTimeMillis();
            // attendance_id,class_id, attendance_date, student_ids, teacher_id, school_id
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("attendance_id", String.valueOf(time_stamp));
            object.put("class_id", class_id);
            object.put("attendance_date", String.valueOf(time_stamp));
            object.put("student_ids", student_ids);
            object.put("teacher_id", teacher_id);
            object.put("roll_nos", roll_nos);
            if (isNetworkAvailable()) {
                HTTPNewPost task = new HTTPNewPost(this, this);
                task.userRequest("Processing...", 2, Paths.add_attendance, object.toString(), 1);
            } else {
                Toast.makeText(this, "No Network Found", Toast.LENGTH_SHORT).show();
                db_tables.syncMarksData("AA", String.valueOf(time_stamp) + "*" + class_id + "*" + String.valueOf(time_stamp) + "*" + teacher_id + "*" + student_ids + "*" + roll_nos);
                finish();
            }
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


                            adapter = new StudentsAdapter(studentModelList, this, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
                        }
                    }
                    else {
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 2:
                    JSONObject object1 = new JSONObject(results.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        String attendDate = getDate(time_stamp);
                        if (TextUtils.isEmpty(ss_d)) {
                            String student_id = db_tables.getStudentIds(class_id);
                            db_tables.updateAttendance(String.valueOf(time_stamp), class_id, student_id, String.valueOf(time_stamp), teacher_id, roll_nos, attendDate);

                        } else {
                            db_tables.updateAttendance(String.valueOf(time_stamp), class_id, ss_d, String.valueOf(time_stamp), teacher_id, roll_nos, attendDate);
                        }
                        Toast.makeText(this, "Attendance Details Submitted Successfully", Toast.LENGTH_SHORT).show();
                       Intent ber=new Intent("com.set.refrsh");
                       sendBroadcast(ber);
                       finish();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String getDate_N(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
    }

    private String getDate(long time) {
        Calendar cal = Calendar.getInstance(Locale.ENGLISH);
        cal.setTimeInMillis(time);
        String date = DateFormat.format("dd-MM-yyyy", cal).toString();
        return date;
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
                    studentModel.setSelected(false);
                    studentModelList.add(studentModel);

                }


                adapter = new StudentsAdapter(studentModelList, this, this);
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

        if (chk_name.isChecked()) {
            Student_Id = matchesList.get(position).getStudent_id();
            student_list_id.add(Student_Id);
            roll_no = matchesList.get(position).getRoll_no();
            rollno_list_id.add(roll_no);
            db_tables.updateVV(Student_Id, "1", class_id, att_date);
            chk_name.setBackground(getResources().getDrawable(R.mipmap.ic_uncheck));
        } else {
            Student_Id = matchesList.get(position).getStudent_id();
            student_list_id.remove(Student_Id);
            roll_no = matchesList.get(position).getRoll_no();
            rollno_list_id.remove(roll_no);
            db_tables.updateVV(Student_Id, "0", class_id, att_date);
            chk_name.setBackground(getResources().getDrawable(R.mipmap.ic_check));
        }

    }

    @Override
    public void onBackPressed() {

        if (student_list_id.size() > 0) {
            StringBuilder builder = new StringBuilder();
            for (String s : student_list_id) {
                db_tables.updateVV(s, "0", class_id, att_date);
            }
        }
        super.onBackPressed();
    }

    @Override
    public void loadOfflineData() {

    }

    @Override
    public void loadActualData() {
        getStudents();
        try {
            action_type_data = db_tables.getActionData("AA");
            if (action_type_data != null || !TextUtils.isEmpty(action_type_data)) {
                String arry_data[] = action_type_data.split("\\*");
                JSONObject object = new JSONObject();
                object.put("school_id", SharedValues.getValue(this, "school_id"));
                object.put("attendance_id", arry_data[0]);
                object.put("class_id", arry_data[1]);
                object.put("attendance_date", arry_data[2]);
                object.put("student_ids", arry_data[4]);
                object.put("teacher_id", arry_data[3]);
                object.put("roll_nos", arry_data[5]);

                HTTPNewPost task = new HTTPNewPost(this, this);
                task.disableProgress();
                task.userRequest("Processing...", 2, Paths.add_attendance, object.toString(), 1);
               String id= db_tables.getSyncId("AA");
              db_tables.deleteSyncItems(id);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hideOfflineOptions() {

    }

    @Override
    public void showOptions() {

    }

    @Override
    public void onNetworkChange(String status) {
        if (status.equalsIgnoreCase("none")) {
            for (WeakReference<OfflineDataInterface> observer : mObservers) {
                if (observer.get() != null) {
                    observer.get().loadOfflineData();
                    observer.get().hideOfflineOptions();
                }
            }
        } else if (status.equalsIgnoreCase("wifi") || status.equalsIgnoreCase("3g") || status.equalsIgnoreCase("4g") || status.equalsIgnoreCase("2g")) {
            for (WeakReference<OfflineDataInterface> observer : mObservers) {
                if (observer.get() != null) {
                    observer.get().showOptions();
                    observer.get().loadActualData();
                }
            }
        }
    }

    private boolean isNetworkAvailable() {

        ConnectivityManager manager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }

        NetworkInfo net = manager.getActiveNetworkInfo();
        return net != null && net.isConnected();

    }
    public void addObserver(OfflineDataInterface observer) {
        if (hasObserver(observer) == -1) {
            mObservers.add(new WeakReference<>(
                    observer));
        }
    }
    public int hasObserver(OfflineDataInterface observer) {
        final int size = mObservers.size();

        for (int n = size - 1; n >= 0; n--) {
            OfflineDataInterface potentialMatch = mObservers.get(n).get();

            if (potentialMatch == null) {
                mObservers.remove(n);
                continue;
            }

            if (potentialMatch == observer) {
                return n;
            }
        }

        return -1;
    }
}
