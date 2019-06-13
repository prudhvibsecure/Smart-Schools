package com.bsecure.scsm_mobile;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

public class VerifyOtp extends AppCompatActivity implements HttpHandler {

    String member_id, school_id, phone, id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_otp);

         Intent in = getIntent();
         if(in!= null)
         {
             member_id = in.getStringExtra("member_id");
             school_id = in.getStringExtra("school_id");
             phone = in.getStringExtra("phone");
             id = in.getStringExtra("id");
         }

        findViewById(R.id.done_v).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifyOTP();
            }
        });

        findViewById(R.id.resend).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendOTP();
            }
        });
    }

    private void resendOTP() {
    }

    private void verifyOTP() {

        if (isNetworkConnected()) {

            String mob_number = ((EditText) findViewById(R.id.mob_number)).getText().toString().trim();
            if (mob_number.length() == 0) {
                getError("OTP Cannot be Blank");
                return;
            }
            if (mob_number.length() < 4) {
                getError("OTP Must be 4 Digits");
                return;
            }
            try {
                JSONObject object = new JSONObject();
                object.put("phone_number", phone);
                object.put("member_id", member_id);
                object.put("domain", ContentValues.DOMAIN);
                object.put("otp", mob_number);
                HTTPNewPost task = new HTTPNewPost(this, this);
                task.userRequest("Processing...", 1, Paths.verify_otp, object.toString(), 1);
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
                        SharedValues.saveValue(this, "member_id", member_id);
                        SharedValues.saveValue(this, "school_id", school_id);
                        SharedValues.saveValue(this, "id", id);
                        SharedValues.saveValue(this, "ph_number", phone);
                        //Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                        String id = member_id;
                        if (id.equalsIgnoreCase("1")) {
                            //Teacher
                            startPages(TeacherView.class);
                        } else if (id.equalsIgnoreCase("2")) {
                            // Parent
                            startPages(ParentActivity.class);
                        } else if (id.equalsIgnoreCase("3")) {
                            // Staff
                            startPages(StaffView.class);
                        } else if (id.equalsIgnoreCase("4")) {
                            // Tutor
                            startPages(TutorsView.class);
                        } else if (id.equalsIgnoreCase("5")) {
                            startPages(TransportView.class);
                            // Transport
                        } else {
                            startPages(RoutesList.class);
                        }
                        return;
                    }
                    else {
                        getError(object.optString("statusdescription"));
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

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
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

    private void startPages(Class<?> cls) {

        Intent st = new Intent(getApplicationContext(), cls);
        st.putExtra("number", SharedValues.getValue(this, "ph_number"));
        startActivity(st);
        finish();
    }
}
