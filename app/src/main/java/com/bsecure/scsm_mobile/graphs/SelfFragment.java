package com.bsecure.scsm_mobile.graphs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.SubjectNonscolosticAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.graphs.graphadapters.SubWiseMarksAdapter;
import com.bsecure.scsm_mobile.graphs.graphadapters.SubWisePercentageAdapter;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.MarksModel;
import com.bsecure.scsm_mobile.models.Subjects;
import com.bsecure.scsm_mobile.utils.SharedValues;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SelfFragment extends Fragment implements HttpHandler, View.OnClickListener, SubWisePercentageAdapter.ContactAdapterListener {
    BarChart chart;
    ArrayList<BarEntry> BARENTRY;
    ArrayList<String> BarEntryLabels;
    BarDataSet Bardataset;
    BarData BARDATA;
    String class_id, roll_no, examination_name = "";
    RecyclerView mRecyclerView, mMarksRecycler, rrsubjectRecy;
    List<Subjects> mSubjects;
    List<MarksModel> mMarks, mSub;
    private SubjectNonscolosticAdapter adapter;
    private SubWiseMarksAdapter subWiseMarksAdapter;
    private SubWisePercentageAdapter percentageAdapter;
    private TextView prent_txt;
    private View layout;

    int change_positon = 0;

    public SelfFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Bundle bundle = getArguments();
        class_id = bundle.getString("class_id");
        roll_no = bundle.getString("roll_no");
        examination_name = bundle.getString("examination_name");

        layout = inflater.inflate(R.layout.common_row, container, false);

        if (change_positon == 0) {
            layout.findViewById(R.id.over_all_view).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.subject_wise).setVisibility(View.GONE);

        } else {
            layout.findViewById(R.id.over_all_view).setVisibility(View.GONE);
            layout.findViewById(R.id.subject_wise).setVisibility(View.VISIBLE);
        }
        layout.findViewById(R.id.over_click).setOnClickListener(this);
        layout.findViewById(R.id.subject_click).setOnClickListener(this);

        ((TextView) layout.findViewById(R.id.over_click)).setBackground(getActivity().getDrawable(R.drawable.red_button_background));
        ((TextView) layout.findViewById(R.id.subject_click)).setBackground(getActivity().getDrawable(R.drawable.blue_button_background));
        prent_txt = layout.findViewById(R.id.percentage_cal);
        chart = layout.findViewById(R.id.chart1);
        mRecyclerView = layout.findViewById(R.id.content_scol);
        mMarksRecycler = layout.findViewById(R.id.sub_marks_content);
        rrsubjectRecy = layout.findViewById(R.id.sub_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mMarksRecycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        rrsubjectRecy.setLayoutManager(layoutManager);


        getSubjectWiseMarks();
        return layout;
    }

    private void getSubjectWiseMarks() {
        try {
            //class_id, examination_name, roll_no, school_id
            JSONObject object = new JSONObject();
            object.put("class_id", class_id);
            object.put("examination_name", examination_name);
            object.put("roll_no", roll_no);
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Loading...", 2, Paths.base + "overall_marks", object.toString(), 1);

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
                        layout.findViewById(R.id.marks_tv).setVisibility(View.VISIBLE);
                        layout.findViewById(R.id.percentage_tv).setVisibility(View.VISIBLE);
                        layout.findViewById(R.id.grade_tv).setVisibility(View.VISIBLE);
                        layout.findViewById(R.id.attance_tv).setVisibility(View.VISIBLE);
                        JSONArray array = ob.getJSONArray("marks_details");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                JSONObject obs = array.getJSONObject(i);
                                ((TextView) layout.findViewById(R.id.marks_tv)).setText(Html.fromHtml("Marks <br/>" + obs.optString("marks_obtained") + "/" + obs.optString("total_marks")));
                                ((TextView) layout.findViewById(R.id.percentage_tv)).setText(Html.fromHtml("Percentage <br/>" + obs.optString("percentage") + "%"));
                                ((TextView) layout.findViewById(R.id.grade_tv)).setText(Html.fromHtml("Grade<br/> " + obs.optString("grade")));
                                ((TextView) layout.findViewById(R.id.attance_tv)).setText(Html.fromHtml("Attendance <br/>Percentage <br/>" + obs.optString("attendance_percentage") + "%"));
                            }
                        }
                        getSubjectListGraph();
                    } else {
                        layout.findViewById(R.id.marks_tv).setVisibility(View.GONE);
                        layout.findViewById(R.id.percentage_tv).setVisibility(View.GONE);
                        layout.findViewById(R.id.grade_tv).setVisibility(View.GONE);
                        layout.findViewById(R.id.attance_tv).setVisibility(View.GONE);
                    }
                    break;
                case 3:
                    JSONObject ob1 = new JSONObject(results.toString());
                    if (ob1.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = ob1.getJSONArray("marks_details");
                        if (array.length() > 0) {
                            BARENTRY = new ArrayList<>();
                            BarEntryLabels = new ArrayList<>();
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

                            YAxis yAxis = chart.getAxisLeft();
                            YAxis yAxis1 = chart.getAxisRight();
                            yAxis.setAxisMinValue(0);
                            yAxis1.setAxisMinValue(0);
                            chart.setData(BARDATA);
                            chart.setDescription(examination_name + "-Subjects");
                            chart.animateY(3000);
                            getscholostcs();

                        }
                    }
                    break;
                case 4:
                    mSubjects = new ArrayList<>();
                    JSONObject ob2 = new JSONObject(results.toString());
                    if (ob2.optString("statuscode").equalsIgnoreCase("200")) {
                        layout.findViewById(R.id.no_sc).setVisibility(View.VISIBLE);
                        JSONArray array = ob2.getJSONArray("marks_details");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                Subjects subjects = new Subjects();
                                JSONObject obss = array.getJSONObject(i);
                                subjects.setSubject(obss.optString("subject"));
                                mSubjects.add(subjects);

                            }
                            adapter = new SubjectNonscolosticAdapter(getActivity(), mSubjects);
                            mRecyclerView.setAdapter(adapter);
                        }
                    } else {
                        layout.findViewById(R.id.no_sc).setVisibility(View.GONE);
                    }
                    break;
                case 5:
                    mMarks = new ArrayList<>();
                    JSONObject ob3 = new JSONObject(results.toString());
                    if (ob3.optString("statuscode").equalsIgnoreCase("200")) {
                        layout.findViewById(R.id.header).setVisibility(View.VISIBLE);
                        prent_txt.setVisibility(View.VISIBLE);
                        JSONArray array = ob3.getJSONArray("marks_details");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                MarksModel subjects = new MarksModel();
                                JSONObject obss = array.getJSONObject(i);
                                subjects.setSubject(obss.optString("subject"));
                                subjects.setGrade(obss.optString("grade"));
                                subjects.setPercengate(obss.optString("percentage"));
                                subjects.setMarks(obss.optString("total_marks"));
                                subjects.setMarks_obtained(obss.optString("marks_obtained"));
                                mMarks.add(subjects);

                            }
                            subWiseMarksAdapter = new SubWiseMarksAdapter(mMarks, getActivity());
                            mMarksRecycler.setAdapter(subWiseMarksAdapter);
                        }
                        getsubjects();
                    } else {
                        layout.findViewById(R.id.header).setVisibility(View.GONE);
                        prent_txt.setVisibility(View.GONE);
                        Toast.makeText(getActivity(), ob3.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 6:
                    mSub = new ArrayList<>();
                    JSONObject ob4 = new JSONObject(results.toString());
                    if (ob4.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = ob4.getJSONArray("marks_details");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                MarksModel subjects = new MarksModel();
                                JSONObject obss = array.getJSONObject(i);
                                subjects.setSubject(obss.optString("subject"));
                                subjects.setGrade(obss.optString("grade"));
                                subjects.setPercengate(obss.optString("percentage"));
                                subjects.setMarks(obss.optString("total_marks"));
                                subjects.setMarks_obtained(obss.optString("marks_obtained"));
                                mSub.add(subjects);

                            }
                            percentageAdapter = new SubWisePercentageAdapter(mSub, getActivity(), this);
                            rrsubjectRecy.setAdapter(percentageAdapter);
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
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
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
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.disableProgress();
            task.userRequest("", 3, Paths.base + "overall_subject_marks", object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.over_click:
                change_positon = 0;
                ((TextView) layout.findViewById(R.id.over_click)).setBackground(getActivity().getDrawable(R.drawable.red_button_background));
                ((TextView) layout.findViewById(R.id.subject_click)).setBackground(getActivity().getDrawable(R.drawable.blue_button_background));
                layout.findViewById(R.id.over_all_view).setVisibility(View.VISIBLE);
                layout.findViewById(R.id.subject_wise).setVisibility(View.GONE);
                chart.removeAllViews();
                chart.invalidate();
                getSubjectWiseMarks();
                break;
            case R.id.subject_click:
                change_positon = 1;
                ((TextView) layout.findViewById(R.id.over_click)).setBackground(getActivity().getDrawable(R.drawable.blue_button_background));
                ((TextView) layout.findViewById(R.id.subject_click)).setBackground(getActivity().getDrawable(R.drawable.red_button_background));
                layout.findViewById(R.id.over_all_view).setVisibility(View.GONE);
                layout.findViewById(R.id.subject_wise).setVisibility(View.VISIBLE);
                getsubject_wise();
                break;
        }

    }

    private void getsubject_wise() {

        try {
            //class_id, examination_name, roll_no, school_id
            JSONObject object = new JSONObject();
            object.put("class_id", class_id);
            object.put("examination_name", examination_name);
            object.put("roll_no", roll_no);
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Loading...", 5, Paths.base + "subject_wise_marks", object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getsubjects() {

        try {
            //class_id, examination_name, roll_no, school_id
            JSONObject object = new JSONObject();
            object.put("class_id", class_id);
            object.put("examination_name", examination_name);
            object.put("roll_no", roll_no);
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Loading...", 6, Paths.base + "subject_wise_marks", object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRowCTouch(List<MarksModel> matchesList, int position) {

        int percentage = Integer.parseInt(matchesList.get(position).getPercengate());
        // if (percentage==0)
        if (percentage < 60) {
            prent_txt.setText(Html.fromHtml("Your Child <br/>Is Weak In <br/>" + matchesList.get(position).getSubject()));
        } else if (percentage < 75) {
            prent_txt.setText(Html.fromHtml("More Hard Work <br/>Required In <br/>" + matchesList.get(position).getSubject()));
        } else if (percentage < 95) {
            prent_txt.setText(Html.fromHtml("Need Improvement <br/>To Reach Top Grade <br/>In " + matchesList.get(position).getSubject()));
        } else {
            prent_txt.setText(Html.fromHtml("Your Child<br/>Is Best At <br/>" + matchesList.get(position).getSubject()));
        }

    }
}