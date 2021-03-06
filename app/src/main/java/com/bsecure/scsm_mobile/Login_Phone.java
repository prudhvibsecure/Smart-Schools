package com.bsecure.scsm_mobile;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.firebasepaths.SharedPrefManager;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.modules.ParentActivity;
import com.bsecure.scsm_mobile.modules.RoutesList;
import com.bsecure.scsm_mobile.modules.StaffView;
import com.bsecure.scsm_mobile.modules.TeacherView;
import com.bsecure.scsm_mobile.modules.TransportView;
import com.bsecure.scsm_mobile.modules.TutorsView;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONObject;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class Login_Phone extends AppCompatActivity implements HttpHandler {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_phone);

        findViewById(R.id.tv_admin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login_Phone.this,AdminLogin.class));
            }
        });
        findViewById(R.id.done_v).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLogin();
            }
        });
    }

    private void getLogin() {

        if (isNetworkConnected()) {

            String mob_number = ((EditText) findViewById(R.id.mob_number)).getText().toString();
            if (mob_number.length() == 0) {
                getError("Enter Your Mobile Number");
                return;
            }
            if (mob_number.length() < 10) {
                getError("Please Enter 10 Digit Number");
                return;
            }
            try {
                JSONObject object = new JSONObject();
                object.put("phone_number", mob_number);
                object.put("regidand", SharedPrefManager.getInstance(this).getDeviceToken());
                object.put("domain", ContentValues.DOMAIN);
                HTTPNewPost task = new HTTPNewPost(this, this);
                task.userRequest("Processing...", 1, Paths.member_verify, object.toString(), 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "No Internet Connection", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onResponse(Object results, int requestType) {
        try {

            switch (requestType) {
                case 1:
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        Intent in = new Intent(Login_Phone.this, VerifyOtp.class);
                        in.putExtra("member_id",object.optString("member_id"));
                        in.putExtra("school_id", object.optString("school_id"));
                        in.putExtra("id", object.optString("id"));
                        in.putExtra("phone",object.optString("phone_number"));
                        in.putExtra("class_teacher", object.optString("class_teacher"));
                        startActivity(in);
                        finish();

                       /* if (object.optString("member_id").equalsIgnoreCase("1")) {
                            //Teacher
                            startPages(TeacherView.class);
                        } else if (object.optString("member_id").equalsIgnoreCase("2")) {

                            // Parent
                            startPages(ParentActivity.class);
                        } else if (object.optString("member_id").equalsIgnoreCase("3")) {
                            // Staff
                            startPages(StaffView.class);
                        } else if (object.optString("member_id").equalsIgnoreCase("4")) {
                            // Tutor
                            startPages(TutorsView.class);
                        } else if (object.optString("member_id").equalsIgnoreCase("5")) {
                            startPages(TransportView.class);
                            // Transport
                        } else {
                            startPages(RoutesList.class);
                        }*/
                    }
                    getError(object.optString("statusdescription"));
                    break;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /*private void startPages(Class<?> cls) {

        Intent st = new Intent(getApplicationContext(), cls);
        st.putExtra("number", ((EditText) findViewById(R.id.mob_number)).getText().toString());
        startActivity(st);
        finish();
    }*/

    @Override
    public void onFailure(String errorCode, int requestType) {

    }

    private void getError(String text) {

        new SweetAlertDialog(this, SweetAlertDialog.ERROR_TYPE)
                .setTitleText("Alert!")
                .setContentText(text)
                .setConfirmText("Ok")
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.dismiss();
                    }
                })
                .show();

    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }
}
