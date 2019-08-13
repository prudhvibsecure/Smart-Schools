package com.bsecure.scsm_mobile.modules;

import android.content.Intent;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

import com.bsecure.scsm_mobile.AdminLogin;
import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.TeachersListAdapter;
import com.bsecure.scsm_mobile.callbacks.ClickListener;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.ClassModel;
import com.bsecure.scsm_mobile.models.TeacherModel;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class TeachersList extends AppCompatActivity implements HttpHandler {

    String school_id ="", class_id="", class_name ="";

    ArrayList<TeacherModel>teachers;

    private RecyclerView mRecyclerView;

    private CoordinatorLayout coordinatorLayout;

    private TeachersListAdapter adapter;

    private DB_Tables db_tables;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teachers_list);

        db_tables = new DB_Tables(this);
        Intent in  = getIntent();

        Bundle bd = in.getExtras();
        if(bd != null)
        {
            school_id = bd.getString("school_id");
            class_id = bd.getString("class_id");
            class_name = bd.getString("class_name");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Classes List");//Organization Head

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        mRecyclerView = findViewById(R.id.list);

        getTeachers();

    }

    private void getTeachers() {

        try {

            JSONObject object = new JSONObject();

            object.put("school_id", school_id);

            object.put("class_id", class_id);

            object.put("domain", ContentValues.DOMAIN);

            HTTPNewPost task = new HTTPNewPost(this, this);

            task.userRequest("Processing...", 1, Paths.get_teachers, object.toString(), 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }
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

    @Override
    public void onResponse(Object results, int requestType) {

        try {

            switch (requestType) {

                case 1:

                    JSONObject object = new JSONObject(results.toString());

                    if (object.optString("statuscode").equalsIgnoreCase("200")) {

                        teachers = new ArrayList<>();

                       JSONArray array = object.getJSONArray("teachers_list");

                        for(int i = 0; i<array.length(); i++)
                        {
                            JSONObject obj = array.getJSONObject(i);

                            TeacherModel model = new TeacherModel();

                            model.setClass_teacher(obj.optString("class_teacher"));

                            model.setPhone_number(obj.optString("phone_number"));

                            model.setTeacher_id(obj.optString("teacher_id"));

                            model.setTeacher_name(obj.optString("teacher_name"));

                            model.setSubjects(obj.optString("subjects"));

                            teachers.add(model);
                        }

                        adapter = new TeachersListAdapter(this, teachers, new ClickListener() {
                            @Override
                            public void OnRowClicked(int position, View view) {

                                db_tables.deleteClasses();
                                SharedValues.saveValue(TeachersList.this,"id", teachers.get(position).getTeacher_id());
                                startActivity(new Intent(TeachersList.this,TeacherView.class));

                            }
                        });

                        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);

                        mRecyclerView.setLayoutManager(linearLayoutManager);

                        mRecyclerView.setAdapter(adapter);

                    }
                    else
                    {



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
}
