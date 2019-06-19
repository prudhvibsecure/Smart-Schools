package com.bsecure.scsm_mobile.modules;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TimeTableView extends AppCompatActivity implements HttpHandler, TimeTableListAdapter.ContactAdapterListener {
    ArrayList<Periods> periodsArrayList;
    private TimeTableListAdapter adapter;
    private RecyclerView mRecyclerView;
    String class_id, school_id;
    TextView monday, tuesday, wednesday, thursday, friday, saturday;
    LinearLayout header;
    String[] snames;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Period Time Table");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        monday = findViewById(R.id.monday);
        tuesday = findViewById(R.id.tuesday);
        wednesday = findViewById(R.id.wednesday);
        thursday = findViewById(R.id.thursday);
        friday = findViewById(R.id.friday);
        saturday = findViewById(R.id.saturday);
        header = findViewById(R.id.header);
        header.setVisibility(View.VISIBLE);

        monday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimeTable("1");
                monday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                monday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                tuesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                wednesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                thursday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                friday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                saturday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));

                tuesday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                wednesday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                thursday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                friday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                saturday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }
        });

        tuesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimeTable("2");

                tuesday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                tuesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                monday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                wednesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                thursday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                friday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                saturday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));

                monday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                wednesday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                thursday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                friday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                saturday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }
        });

        wednesday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimeTable("3");

                wednesday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                wednesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                monday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                tuesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                thursday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                friday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                saturday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));

                tuesday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                monday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                thursday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                friday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                saturday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }
        });

        thursday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimeTable("4");

                thursday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                thursday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                monday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                tuesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                wednesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                friday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                saturday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));

                tuesday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                wednesday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                monday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                friday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                saturday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }
        });

        friday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimeTable("5");

                friday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                friday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                monday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                tuesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                wednesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                thursday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                saturday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));

                tuesday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                wednesday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                thursday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                monday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                saturday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }
        });

        saturday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getTimeTable("6");

                saturday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                saturday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

                monday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                tuesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                wednesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                thursday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
                friday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));

                tuesday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                wednesday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                thursday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                friday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
                monday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.white));
            }
        });


        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        mRecyclerView = findViewById(R.id.content_list);

        Intent data = getIntent();
        school_id = SharedValues.getValue(this, "school_id");
        class_id = data.getStringExtra("class_id");
        monday.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
        monday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.white));

        tuesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        wednesday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        thursday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        friday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        saturday.setBackgroundColor(ContextCompat.getColor(getApplicationContext(), R.color.green));
        getTimeTable("1");
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

    private void getTimeTable(String day) {
        try {

                JSONObject object = new JSONObject();
                object.put("school_id", school_id);
                object.put("class_id", class_id);
                object.put("day", day);
                HTTPNewPost task = new HTTPNewPost(this, this);
                task.userRequest("Processing...", 101, Paths.view_periods, object.toString(), 1);
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

                        JSONArray narray = jsonObject.getJSONArray("period_time_table_details");
                        if (narray.length() > 0) {
                            for (int k = 0; k < narray.length(); k++) {
                                JSONObject nobj = narray.getJSONObject(k);
                                String names = nobj.getString("subject");
                                snames = names.split(",");
                            }
                        }
                        JSONArray array = jsonObject.getJSONArray("period_details");
                        if (array.length() > 0) {
                            for (int y = 0; y < array.length(); y++) {
                                Periods periods = new Periods();
                                JSONObject object = array.getJSONObject(y);
                                // periods.setDay(jsonObject.optString("day"));
                                periods.setPeriod_num(object.optString("period_name"));
                               // periods.setPeriod_name(object.optString("period_name"));
                                if (y < snames.length) {
                                    periods.setPeriod_name(snames[y]);
                                } else {
                                    periods.setPeriod_name("");
                                }
                                periods.setFrom_time(object.optString("from_time"));
                                periods.setTo_time(object.optString("to_time"));
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
