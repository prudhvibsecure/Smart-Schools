package com.bsecure.scsm_mobile.graphs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.graphs.graphadapters.SubWiseMarksAdapter;
import com.bsecure.scsm_mobile.graphs.graphadapters.SubWisePercentageAdapter;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.MarksModel;
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

public class ToppersFragment extends Fragment implements HttpHandler, View.OnClickListener, SubWisePercentageAdapter.ContactAdapterListener {
    String class_id, roll_no, examination_name = "";
    private Bundle bundle;
    private View layout;
    private int change_positon = 0;
    List<MarksModel> mExams, mSub;
    BarChart chart,chart1;
    ArrayList<BarEntry> BARENTRY,BARENTRY1,BARENTRY2;
    ArrayList<String> BarEntryLabels,BarEntryLabels1;
    BarDataSet Bardataset,Bardataset1,Bardataset2;
    BarData BARDATA,BARDATA1,BARDATA2;
    private SubWisePercentageAdapter percentageAdapter;
    RecyclerView mRecyclerView;

    public ToppersFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        class_id = bundle.getString("class_id");
        roll_no = bundle.getString("roll_no");
        examination_name = bundle.getString("examination_name");

        layout = inflater.inflate(R.layout.topper_main, container, false);

        if (change_positon == 0) {
            layout.findViewById(R.id.over_all_view).setVisibility(View.VISIBLE);
            layout.findViewById(R.id.subject_wise).setVisibility(View.GONE);

        } else {
            layout.findViewById(R.id.over_all_view).setVisibility(View.GONE);
            layout.findViewById(R.id.subject_wise).setVisibility(View.VISIBLE);
        }
        layout.findViewById(R.id.over_click).setOnClickListener(this);
        layout.findViewById(R.id.subject_click).setOnClickListener(this);
        mRecyclerView = layout.findViewById(R.id.tooper_row_rec);
        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false);
        mRecyclerView.setLayoutManager(layoutManager);
        ((TextView) layout.findViewById(R.id.over_click)).setBackground(getActivity().getDrawable(R.drawable.red_button_background));
        ((TextView) layout.findViewById(R.id.subject_click)).setBackground(getActivity().getDrawable(R.drawable.blue_button_background));
        chart = layout.findViewById(R.id.chart13);
        chart1 = layout.findViewById(R.id.chart131);
        getTopperOverall();
        return layout;
    }

    private void getTopperOverall() {
        try {
            //class_id, examination_name, roll_no, school_id
            JSONObject object = new JSONObject();
            object.put("class_id", class_id);
            object.put("roll_no", roll_no);
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Loading...", 2, Paths.base + "topper_overall_marks", object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }

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
                getTopperOverall();
                break;
            case R.id.subject_click:
                change_positon = 1;
                ((TextView) layout.findViewById(R.id.over_click)).setBackground(getActivity().getDrawable(R.drawable.blue_button_background));
                ((TextView) layout.findViewById(R.id.subject_click)).setBackground(getActivity().getDrawable(R.drawable.red_button_background));
                layout.findViewById(R.id.over_all_view).setVisibility(View.GONE);
                layout.findViewById(R.id.subject_wise).setVisibility(View.VISIBLE);
                getsubjects();

                break;
        }
    }


    @Override
    public void onResponse(Object results, int requestType) {

        try {

            switch (requestType) {
                case 2:
                    BARENTRY = new ArrayList<>();
                    BarEntryLabels = new ArrayList<>();
                    JSONObject ob3 = new JSONObject(results.toString());
                    if (ob3.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = ob3.getJSONArray("marks_details");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {

                                JSONObject obss = array.getJSONObject(i);
                                float ob_m = obss.getInt("marks_obtained");
                                float ob_topavg=obss.getInt("topper_avg");
                                BARENTRY.add(new BarEntry(ob_m, i));
                                BARENTRY2.add(new BarEntry(ob_topavg, i));
                                BarEntryLabels.add(obss.optString("examination_name"));

                            }
                            Bardataset = new BarDataSet(BARENTRY, "Marks");
                            Bardataset.setColor(Color.RED);
                            Bardataset2 = new BarDataSet(BARENTRY2, "Marks");
                            Bardataset2.setColor(Color.GREEN);

                            //Bardataset.setColors(ColorTemplate.COLORFUL_COLORS);
                            Bardataset.setBarSpacePercent(70f);
                            Bardataset2.setBarSpacePercent(70f);
                            BARDATA = new BarData(BarEntryLabels, Bardataset);
                            BARDATA2 = new BarData(BarEntryLabels, Bardataset2);
                            YAxis yAxis = chart.getAxisLeft();
                            YAxis yAxis1 = chart.getAxisRight();
                            yAxis.setAxisMinValue(0);
                            yAxis1.setAxisMinValue(0);
                            chart.setData(BARDATA);
                            chart.setData(BARDATA2);
                            chart.setDescription("");
                            chart.animateY(2000);
                        }
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
                            mRecyclerView.setAdapter(percentageAdapter);
                        }
                    }

                    break;
                case 3:
                    mExams = new ArrayList<>();
                    BARENTRY1 = new ArrayList<>();
                    BarEntryLabels1 = new ArrayList<>();
                    JSONObject ob5 = new JSONObject(results.toString());
                    if (ob5.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = ob5.getJSONArray("marks_details");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                MarksModel subjects = new MarksModel();
                                JSONObject obss = array.getJSONObject(i);
                                subjects.setSubject(obss.optString("examination_name"));
                                subjects.setGrade(obss.optString("grade"));
                                subjects.setPercengate(obss.optString("percentage"));
                                subjects.setMarks(obss.optString("total_marks"));
                                subjects.setMarks_obtained(obss.optString("marks_obtained"));
                                float ob_m = obss.getInt("marks_obtained");
                                BARENTRY1.add(new BarEntry(ob_m, i));
                                BarEntryLabels1.add(obss.optString("examination_name"));
                                mExams.add(subjects);

                            }
                            Bardataset1 = new BarDataSet(BARENTRY1, "Marks");
                            Bardataset1.setColors(ColorTemplate.COLORFUL_COLORS);
                            Bardataset1.setBarSpacePercent(70f);
                            BARDATA1 = new BarData(BarEntryLabels1, Bardataset1);
                            YAxis yAxis = chart1.getAxisLeft();
                            YAxis yAxis1 = chart1.getAxisRight();
                            yAxis.setAxisMinValue(0);
                            yAxis1.setAxisMinValue(0);
                            chart1.setData(BARDATA1);
                            chart1.setDescription("");
                            chart1.animateY(1000);
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
        getTopper_wise(matchesList.get(position).getSubject());
    }

    private void getTopper_wise(String sub) {
        try {
            //class_id, examination_name, roll_no, school_id
            JSONObject object = new JSONObject();
            object.put("class_id", class_id);
            object.put("roll_no", roll_no);
            object.put("subject", sub);
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Loading...", 3, Paths.base + "topper_subject_wise_marks", object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
