package com.bsecure.scsm_mobile.modules;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.CalenderAdapter;
import com.bsecure.scsm_mobile.adapters.TutorAssignStudentsListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.CalenderModel;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Calender extends AppCompatActivity implements HttpHandler {

    RecyclerView calender;
    CalenderAdapter adapter;
    List<CalenderModel>calenderList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calender);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Calender");//Organization Head
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        calender = findViewById(R.id.calender);
        getCalender();
    }

    private void getCalender() {

        try {

            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));

            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Loading...", 1, Paths.base + "view_calendar", object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onResponse(Object results, int requestType) {

        try {
            switch (requestType) {
                case 1:
                    calenderList = new ArrayList<>();
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200"))
                    {
                        JSONArray jsonarray2 = object.getJSONArray("calendar_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {

                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                CalenderModel calenderModel = new CalenderModel();
                                calenderModel.setOccassion(jsonobject.optString("occassion"));
                                calenderModel.setFromdate(jsonobject.optString("calendar_from_date"));
                                calenderModel.setTodate(jsonobject.optString("calendar_to_date"));
                               //calenderModel.setTodate(jsonobject.optString("student_id"));
                                calenderList.add(calenderModel);
                            }
                            adapter = new CalenderAdapter(this, calenderList);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            calender.setLayoutManager(linearLayoutManager);
                            calender.setAdapter(adapter);
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
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
