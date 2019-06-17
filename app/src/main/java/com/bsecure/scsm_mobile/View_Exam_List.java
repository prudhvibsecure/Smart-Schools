package com.bsecure.scsm_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.bsecure.scsm_mobile.adapters.ExamsListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.Exams;
import com.bsecure.scsm_mobile.modules.ExamsSubjects;
import com.bsecure.scsm_mobile.modules.ViewStudentMarks;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class View_Exam_List extends AppCompatActivity implements HttpHandler, ExamsListAdapter.ContactAdapterListener {

    private String class_id, student_id, roll_no, check;
    private RecyclerView mRecyclerView;
    private ExamsListAdapter adapter;
    private List<Exams> examsList;
    Intent st_edit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Exams");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


        Intent getData = getIntent();
        if (getData != null) {

            roll_no = getData.getStringExtra("roll_no");
            class_id = getData.getStringExtra("class_id");
            student_id = getData.getStringExtra("student_id");
            check = getData.getStringExtra("check");
        }
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
            object.put("class_id", class_id);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.get_examinations, object.toString(), 1);
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
                        JSONArray jsonarray2 = object.getJSONArray("examination_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                Exams exams = new Exams();
                                exams.setExam_name(jsonobject.optString("exam_name"));
                                exams.setExaminations_id(jsonobject.optString("examinations_id"));
                                examsList.add(exams);
                            }
                            adapter = new ExamsListAdapter(examsList, this, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
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

    @Override
    public void onExamRowClicked(List<Exams> matchesList, int position) {

        if (check.equalsIgnoreCase("0")) {
            st_edit = new Intent(getApplicationContext(), ViewStudentMarks.class);
        } else {
            st_edit = new Intent(getApplicationContext(), ViewSyllabusMarks.class);
        }
        st_edit.putExtra("exams_id", matchesList.get(position).getExaminations_id());
        st_edit.putExtra("exams_name", matchesList.get(position).getExam_name());
        st_edit.putExtra("class_id", class_id);
        st_edit.putExtra("roll_no", roll_no);
        st_edit.putExtra("student_id", student_id);
        st_edit.putExtra("ename", matchesList.get(position).getExam_name());
        startActivity(st_edit);
    }
}
