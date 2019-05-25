package com.bsecure.scsm_mobile.graphs;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.SubjectNonscolosticAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.Subjects;
import com.bsecure.scsm_mobile.utils.SharedValues;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CompetativeGraph extends AppCompatActivity implements HttpHandler {
    BarChart chart;
    ArrayList<BarEntry> BARENTRY;
    ArrayList<String> BarEntryLabels;
    BarDataSet Bardataset;
    BarData BARDATA;
    String class_id, roll_no, examination_name = "";
    RecyclerView mRecyclerView;
    List<Subjects> mSubjects;
    private SubjectNonscolosticAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_bar_graph_one);
        Intent data = getIntent();
        class_id = data.getStringExtra("class_id");
        roll_no = data.getStringExtra("roll_no");
        examination_name = data.getStringExtra("examination_name");

        BARENTRY = new ArrayList<>();
        BarEntryLabels = new ArrayList<>();

        chart = findViewById(R.id.chart1);
        mRecyclerView = findViewById(R.id.content_scol);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mSubjects=new ArrayList<>();
        getSubjectWiseMarks();

    }

    private void getSubjectWiseMarks() {
        try {
            //class_id, examination_name, roll_no, school_id
            JSONObject object = new JSONObject();
            object.put("class_id", class_id);
            object.put("examination_name", examination_name);
            object.put("roll_no", roll_no);
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.disableProgress();
            task.userRequest("", 2, Paths.base + "overall_marks", object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Object results, int requestType) {

        try {
            switch (requestType) {
                case 2:
                    JSONObject ob = new JSONObject(results.toString());
                    if (ob.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = ob.getJSONArray("marks_details");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obs = array.getJSONObject(i);
                                ((TextView) findViewById(R.id.marks_tv)).setText(Html.fromHtml("Marks <br/>" + obs.optString("marks_obtained") + "/" + obs.optString("total_marks")));
                                ((TextView) findViewById(R.id.percentage_tv)).setText(Html.fromHtml("Percentage <br/>" + obs.optString("percentage") + "%"));
                                ((TextView) findViewById(R.id.grade_tv)).setText(Html.fromHtml("Grade<br/> " + obs.optString("grade")));
                                ((TextView) findViewById(R.id.attance_tv)).setText(Html.fromHtml("Attendance <br/>Percentage <br/>" + obs.optString("attendance_percentage")+ "%"));
                            }
                        }
                        getSubjectListGraph();
                    }
                    break;
                case 3:
                    JSONObject ob1 = new JSONObject(results.toString());
                    if (ob1.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = ob1.getJSONArray("marks_details");
                        if (array.length() > 0) {

                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obs = array.getJSONObject(i);
                                float ob_m = obs.getInt("marks_obtained");
                                float total = obs.getInt("total_marks");
                                BARENTRY.add(new BarEntry(ob_m, i));
                                BarEntryLabels.add(obs.optString("subject"));
                            }
                            Bardataset = new BarDataSet(BARENTRY, "Subjects");
                            Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                            Bardataset.setBarSpacePercent(70f);
                            BARDATA = new BarData(BarEntryLabels, Bardataset);
                            chart.setData(BARDATA);
                            chart.animateY(5000);
                            getscholostcs();

                        }
                    }
                    break;
                case 4:
                    JSONObject ob2 = new JSONObject(results.toString());
                    if (ob2.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = ob2.getJSONArray("marks_details");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                Subjects subjects=new Subjects();
                                JSONObject obss = array.getJSONObject(i);
                                subjects.setSubject(obss.optString("subject"));
                                mSubjects.add(subjects);

                            }
                            adapter = new SubjectNonscolosticAdapter(this,mSubjects);
                            mRecyclerView.setAdapter(adapter);
                        }
                    }
                    break;

            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getscholostcs() {

        try {
            //class_id, examination_name, roll_no, school_id
            JSONObject object = new JSONObject();
            object.put("class_id", class_id);
            object.put("examination_name", examination_name);
            object.put("roll_no", roll_no);
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.disableProgress();
            task.userRequest("", 4, Paths.base + "overall_non_scholastic_marks", object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getSubjectListGraph() {

        try {
            //class_id, examination_name, roll_no, school_id
            JSONObject object = new JSONObject();
            object.put("class_id", class_id);
            object.put("examination_name", examination_name);
            object.put("roll_no", roll_no);
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.disableProgress();
            task.userRequest("", 3, Paths.base + "overall_subject_marks", object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }
}
