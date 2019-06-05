package com.bsecure.scsm_mobile;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class StudentsMarks extends AppCompatActivity implements HttpHandler, StudentsMarksAdapter.ContactAdapterListener {

    private String subject, class_id, total_marks, teacher_id, exam_id, comma_marks;
    private DB_Tables db_tables;
    private StudentsMarksAdapter adapter;
    private List<StudentModel> studentModelList;
    private RecyclerView mRecyclerView;
    String Student_Id = "", roll_no = "", student_ids, roll_nos, makes_comma;
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
    private int sizeList;
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
            total_marks = getData.getStringExtra("total_marks");
            subject = getData.getStringExtra("subject");
            exam_id = getData.getStringExtra("exam_id");
            teacher_id = getData.getStringExtra("teacher_id");


        }
        toolbar.setTitle(subject + "(Total Marks: " + total_marks + ")");
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
            case R.id.st_add:
                String my_sub_marks = db_tables.getMarks(exam_id, class_id, subject);
                if (my_sub_marks == null || my_sub_marks.length() == 0) {
                    addAttendance();
                } else {
                    updateAttandence();
                }
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    private void addAttendance() {

        try {
            String desc;
            if (roll_ids.size() > 0) {
                desc = "Marks Are Going To <br/> Be Submitted";
            } else {
                desc = "Please Fill Required  <br/>Details";
            }
            SweetAlertDialog dialog = new SweetAlertDialog(this,SweetAlertDialog.NORMAL_TYPE);
            dialog.setTitle("Alert!");
            dialog.setContentText(String.valueOf(Html.fromHtml(desc)));
            dialog.setCancelable(false);
            dialog.setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    conformAlert();
                    sweetAlertDialog.dismiss();
                }
            });
            dialog.setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    StudentsMarks.this.finish();
                }
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void conformAlert() {
        try {
            if (marks_list_vv.size() == 0) {
                finish();
                return;
            }
            if (sizeList == marks_list_vv.size() && sizeList == student_names.size()) {

                if (marks_list_vv.size() > 0) {
                    StringBuilder builder = new StringBuilder();
                    Collections.sort(roll_ids);
                    Map<String, String> treeMap = new TreeMap<String, String>(marks_list_vv);
                    for (Map.Entry<String, String> entry : treeMap.entrySet()) {

                        String student_id = entry.getKey();
                        String marksv = entry.getValue();
                        builder.append("," + marksv);
                       // Collections.sort(student_names);

                        String roll_no = roll_ids.get(i);
                        String name = studentModelList.get(i).getStudent_name();
                        i = i + 1;
                        db_tables.insertMarks(student_id, exam_id, marksv, teacher_id, class_id, subject, name, roll_no);
                        // db_tables.updateStudents1(key, exam_id, marksv, teacher_id, class_id, subject);
                    }
                    String fis = builder.toString();
                    makes_comma = fis.substring(1);
                }
                // marks_id, class_id, examination_time_table_id, teacher_id
                JSONObject object = new JSONObject();
                object.put("school_id", SharedValues.getValue(this, "school_id"));
                object.put("marks_id", String.valueOf(time_stamp));
                object.put("examination_time_table_id", exam_id);
                object.put("marks_obtained", makes_comma);
                object.put("teacher_id", teacher_id);
                object.put("class_id", class_id);
                object.put("subject", subject);
                HTTPNewPost task = new HTTPNewPost(this, this);
                task.userRequest("Processing...", 2, Paths.add_marks, object.toString(), 1);
            } else {
                Toast.makeText(this, "Please Fill Required Details", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateAttandence() {
        try {
            String desc;
            if (roll_ids.size() > 0) {
                desc = "Marks Are Going <br/>To Be Submitted";
            } else {
                desc = "Nothing To Update";
            }
            SweetAlertDialog dialog = new SweetAlertDialog(this);
            dialog.setTitle("Alert!");
            dialog.setContentText(String.valueOf(Html.fromHtml(desc)));
            dialog.setCancelable(false);
            dialog.setConfirmButton("Yes", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    updateAlert();
                    sweetAlertDialog.dismiss();
                }
            });
            dialog.setCancelButton("No", new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismiss();
                    StudentsMarks.this.finish();
                }
            });
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();

        }

    }

    private void updateAlert() {
        try {
            // if (sizeList == marks_list_vv.size()) {

            if (marks_list_vv.size() > 0) {
                builder = new StringBuilder();
                builder11 = new StringBuilder();

                Map<String, String> treeMap = new TreeMap<String, String>(marks_list_vv);
                for (Map.Entry<String, String> entry : treeMap.entrySet()) {
                    String student_id = entry.getKey();
                    String marksv = entry.getValue();
//                    for (StudentModel studentModel : studentModelList) {
//                        if (studentModel.getStudent_id().equalsIgnoreCase(student_id)) {
//                            builder.append("," + marksv);
//                        } else {
//                            builder.append("," + studentModel.getMarkslist());
//                        }
//                    }
                    db_tables.updateStudents11(student_id, exam_id, marksv, teacher_id, class_id, subject);


                }
                ArrayList<String> fis = db_tables.listMarks(subject, exam_id);
                for (String s : fis) {
                    builder.append("," + s);
                }
                makes_comma = builder.substring(1);
                StringBuilder bb = new StringBuilder();
                if (roll_ids.size() > 0) {
                    ArrayList<String> fis1 = db_tables.listRollNo(subject, exam_id);
                    for (String s : fis1) {
                        bb.append("," + s);
                    }
                    roll_nos = bb.substring(1);
//                    StringBuilder bb = new StringBuilder();
//                    for (String roll_numbers : roll_ids) {
//                        for (StudentModel studentModel : studentModelList) {
//                            if (studentModel.getRoll_no().equalsIgnoreCase(roll_numbers)) {
//                                builder.append("," + studentModel.getRoll_no());
//                            } else {
//                                builder.append("," + roll_numbers);
//                            }
//                        }
//                        builder = null;
//                        builder = new StringBuilder();
//                    }
//                    roll_nos = bb.substring(1);
                }
                // marks_id, class_id, examination_time_table_id, teacher_id
                JSONObject object = new JSONObject();
                object.put("school_id", SharedValues.getValue(this, "school_id"));
                object.put("marks_id", String.valueOf(time_stamp));
                object.put("examination_time_table_id", exam_id);
                object.put("marks", makes_comma);
                object.put("teacher_id", teacher_id);
                object.put("class_id", class_id);
                object.put("subject", subject);
                object.put("roll_nos", roll_nos);
                HTTPNewPost task = new HTTPNewPost(this, this);
                task.userRequest("Processing...", 2, Paths.edit_marks, object.toString(), 1);
            } else {
                finish();
            }

//            } else {
//                Toast.makeText(this, "Please set all student marks", Toast.LENGTH_SHORT).show();
//            }
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
                            adapter = new StudentsMarksAdapter(studentModelList, this, this);
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


                adapter = new StudentsMarksAdapter(studentModelList, this, this);
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

                adapter = new StudentsMarksAdapter(studentModelList, this, this);
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

        // String student_id = matchesList.get(position).getStudent_id();
        sizeList = matchesList.size();
        roll_no = matchesList.get(position).getRoll_no();
        if (checked.isChecked()) {
            // student_names = new ArrayList<>();
            student_names.add(matchesList.get(position).getStudent_name());
            marks_list = new ArrayList<>();
            marks_list.add("NA");
            marks_list.add("AB");

            for (int i = 0; i <= Integer.valueOf(total_marks); i++) {
                marks_list.add(String.valueOf(i));
            }
            marks_list.add("A+");
            marks_list.add("A");
            marks_list.add("B+");
            marks_list.add("B");
            marks_list.add("C+");
            marks_list.add("C");
            marks_list.add("F");
            marks_Diloag = new Dialog(this, R.style.MyAlertDialogStyle);
            marks_Diloag.setContentView(R.layout.marks_list);
            marks_Diloag.setCancelable(false);
            marks_Diloag.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            marks_Diloag.getWindow().setGravity(Gravity.BOTTOM);
            marks_Diloag.findViewById(R.id.close_pp).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    marks_Diloag.dismiss();

                    String my_sub_marks = db_tables.getMarks(exam_id, class_id, subject);
                    if (my_sub_marks == null || my_sub_marks.length() == 0) {
                        marks_edit.setText("");
                        checked.setChecked(false);
                    } else {
                        checked.setChecked(false);
                    }

                }
            });
            ListView dynamic = marks_Diloag.findViewById(R.id.marks_cn);
            final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, marks_list);
            //Insert Adapter into List
            dynamic.setAdapter(adapter);
            dynamic.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
                    remov_mark = marks_list.get(pos);
                    marks_edit.setText(remov_mark);
                    marks_list_vv.put(matchesList.get(position).getStudent_id(), remov_mark);

                    marks_list = null;
                    marks_Diloag.dismiss();
                }
            });
            roll_ids.add(roll_no);
            marks_Diloag.show();
        } else {
            marks_list_vv.remove(matchesList.get(position).getStudent_id());
            student_names.remove(matchesList.get(position).getStudent_name());
            String my_sub_marks = db_tables.getMarks2(exam_id, class_id, subject,matchesList.get(position).getStudent_id());
            if (my_sub_marks == null || my_sub_marks.length() == 0) {
                marks_edit.setText("");
            } else {
                marks_edit.setText(my_sub_marks);
            }
            roll_ids.remove(roll_no);
            student_ids_l.remove(matchesList.get(position).getStudent_id());
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
