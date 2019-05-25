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
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.ParentStudentsListAdapter;
import com.bsecure.scsm_mobile.adapters.TimeTableListAdapter;
import com.bsecure.scsm_mobile.adapters.TrasnsListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.Periods;
import com.bsecure.scsm_mobile.models.Subjects;
import com.bsecure.scsm_mobile.models.Transport;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimeTableView extends AppCompatActivity implements HttpHandler ,TimeTableListAdapter.ContactAdapterListener{
    ArrayList<Periods> periodsArrayList;
    private TimeTableListAdapter adapter;
    private RecyclerView mRecyclerView;
    String class_id, school_id;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Time Table");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.content_list);

        Intent data = getIntent();
        school_id = data.getStringExtra("student_id");
        class_id = data.getStringExtra("class_id");
        getTimeTable();
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
    private void getTimeTable() {
        try {
            try {
                JSONObject object = new JSONObject();
                object.put("school_id", school_id);
                object.put("class_id", class_id);
                HTTPNewPost task = new HTTPNewPost(this, this);
                task.userRequest("Processing...", 101, Paths.view_periods, object.toString(), 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Object results, int requestType) {
        try {
            switch (requestType) {
                case 101:
                    periodsArrayList = new ArrayList<>();
                    JSONObject jsonObject = new JSONObject(results.toString());
                    if (jsonObject.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = jsonObject.getJSONArray("period_details");
                        if (array.length() > 0) {
                            for (int y = 0; y < array.length(); y++) {
                                Periods periods = new Periods();
                                periods.setDay(jsonObject.optString("day"));
                                periods.setPeriod_num(jsonObject.optString("period_num"));
                                periods.setPeriod_name(jsonObject.optString("period_name"));
                                periods.setFrom_time(jsonObject.optString("from_time"));
                                periods.setTo_time(jsonObject.optString("to_time"));
                                periodsArrayList.add(periods);
                            }
                            adapter = new TimeTableListAdapter(periodsArrayList, this, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
                        }

                    } else {
                        Toast.makeText(this, jsonObject.optString("statusdescription"), Toast.LENGTH_SHORT).show();
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
    public void onRowClicked(List<Periods> matchesList, int position) {

    }
}
