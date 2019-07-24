package com.bsecure.scsm_mobile;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.ClassModel;
import com.bsecure.scsm_mobile.modules.ClassesList;
import com.bsecure.scsm_mobile.utils.SharedValues;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class AdminLogin extends AppCompatActivity implements HttpHandler {

    ArrayList<ClassModel> classes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);

        toolbar.setTitle("Admin Login");//Organization Head

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        ((Button)findViewById(R.id.login)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                validateLogin();
            }
        });
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

    private void validateLogin() {

        String username =  ((EditText)findViewById(R.id.username)).getText().toString();

        String password =  ((EditText)findViewById(R.id.password)).getText().toString();

        if(username.length() == 0 || username.equals(""))
        {
            Toast.makeText(this, "Username is Empty", Toast.LENGTH_SHORT).show();

            return;
        }

        if(password.length() == 0 || password.equals(""))
        {
            Toast.makeText(this, "password is Empty", Toast.LENGTH_SHORT).show();

            return;
        }

        try {

            JSONObject obj = new JSONObject();

            obj.put("username", username);

            obj.put("password", password);

            obj.put("domain", ContentValues.DOMAIN);

            HTTPNewPost task = new HTTPNewPost(this, this);

            task.userRequest("Processing...", 1, Paths.staff_login, obj.toString(), 1);

        } catch (JSONException e) {
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

                        JSONObject object1 = object.getJSONArray("staff_details").getJSONObject(0);

                        SharedValues.saveValue(this,"school_id", object1.optString("school_id"));

                        SharedValues.saveValue(this,"staff_id", object1.optString("staff_id"));

                        SharedValues.saveValue(this,"name", object1.optString("name"));

                        SharedValues.saveValue(this,"designation", object1.optString("designation"));

                        JSONArray array = object.getJSONArray("class_list");

                        classes = new ArrayList<>();

                        for(int i = 0; i<array.length(); i++)
                        {
                            JSONObject obj = array.getJSONObject(i);

                            ClassModel model = new ClassModel();

                            model.setClass_id(obj.optString("class_id"));

                            model.setClsName(obj.optString("class_name"));

                            model.setSectionName(obj.optString("section"));

                            model.setSubjects(obj.optString("subjects"));

                            classes.add(model);
                        }

                        Intent in = new Intent(AdminLogin.this, ClassesList.class);

                        in.putExtra("classes", (Serializable) classes);

                        startActivity(in);
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
