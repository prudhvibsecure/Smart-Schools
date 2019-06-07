package com.bsecure.scsm_mobile.modules;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.StudentsMarks;
import com.bsecure.scsm_mobile.StudentsViewEdit;
import com.bsecure.scsm_mobile.SyllabusView;
import com.bsecure.scsm_mobile.adapters.ExamsListAdapter;
import com.bsecure.scsm_mobile.adapters.SubjectListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.Exams;
import com.bsecure.scsm_mobile.models.Subjects;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExamsSubjects extends AppCompatActivity implements HttpHandler, SubjectListAdapter.ContactAdapterListener {

    private RecyclerView mRecyclerView;
    private SubjectListAdapter adapter;
    private List<Subjects> examsList;
    String exam_id, class_id, teacher_id, value;
    Button bt_nscholastic;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);

        Intent getdata = getIntent();
        exam_id = getdata.getStringExtra("exams_id");
        class_id = getdata.getStringExtra("class_id");
        teacher_id = getdata.getStringExtra("teacher_id");
        value = getdata.getStringExtra("value");
        String exam_name = getdata.getStringExtra("exams_name");
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle(exam_name);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        bt_nscholastic = findViewById(R.id.bt_nscholastic);
        bt_nscholastic.setVisibility(View.VISIBLE);
        bt_nscholastic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(ExamsSubjects.this, StudentsNonscholastic.class);
                in.putExtra("class_id", class_id);
                in.putExtra("exam_id", exam_id);
                in.putExtra("teacher_id", teacher_id);
                startActivity(in);
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.content_list);
        examsList = new ArrayList<>();
        getExams();
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

    private void getExams() {

        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("examinations_id", exam_id);
            object.put("class_id", class_id);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.get_examination_time_table, object.toString(), 1);
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
                        JSONArray jsonarray2 = object.getJSONArray("examination_time_table_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                Subjects subjects = new Subjects();
                                subjects.setExam_id(jsonobject.optString("examinations_time_table_id"));
                                subjects.setExam_date(jsonobject.optString("exam_date"));
                                subjects.setFrom_time(jsonobject.optString("from_time"));
                                subjects.setSubject(jsonobject.optString("subject"));
                                subjects.setTo_time(jsonobject.optString("to_time"));
                                subjects.setTotal_marks(jsonobject.optString("total_marks"));
                                examsList.add(subjects);
                            }
                            adapter = new SubjectListAdapter(examsList, this, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
                        }
                    } else {
                        Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
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
    public void onMessageRowClicked(List<Subjects> matchesList, int position) {
        String exam_date = matchesList.get(position).getExam_date();
        long ex_date = Integer.parseInt(exam_date);
        long curr_time = System.currentTimeMillis()/1000;
        if (value.equalsIgnoreCase("1")) {
            if (ex_date < curr_time) {
                Intent st_edit = new Intent(getApplicationContext(), StudentsMarks.class);
                st_edit.putExtra("class_id", class_id);
                st_edit.putExtra("total_marks", matchesList.get(position).getTotal_marks());
                st_edit.putExtra("subject", matchesList.get(position).getSubject());
                st_edit.putExtra("exam_id", matchesList.get(position).getExam_id());
                st_edit.putExtra("exam_date", matchesList.get(position).getExam_date());
                st_edit.putExtra("teacher_id", teacher_id);
                startActivity(st_edit);
            } else {
                Toast.makeText(this, "We Cannot Add Marks For Exam Not Happened", Toast.LENGTH_SHORT).show();
            }
        } else {
            Intent st_edit = new Intent(getApplicationContext(), SyllabusView.class);
            st_edit.putExtra("class_id", class_id);
            st_edit.putExtra("total_marks", matchesList.get(position).getTotal_marks());
            st_edit.putExtra("subject", matchesList.get(position).getSubject());
            st_edit.putExtra("exam_id", matchesList.get(position).getExam_id());
            st_edit.putExtra("teacher_id", teacher_id);
            startActivity(st_edit);
        }
    }
}
