package com.bsecure.scsm_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;

import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.modules.ClassesList;
import com.bsecure.scsm_mobile.modules.ParentActivity;
import com.bsecure.scsm_mobile.modules.ParentView;
import com.bsecure.scsm_mobile.modules.RoutesList;
import com.bsecure.scsm_mobile.modules.StaffView;
import com.bsecure.scsm_mobile.modules.TeacherView;
import com.bsecure.scsm_mobile.modules.TransportView;
import com.bsecure.scsm_mobile.modules.TutorsView;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONException;
import org.json.JSONObject;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final String member_id = SharedValues.getValue(this, "member_id");
        final String staff_id = SharedValues.getValue(this,"staff_id");

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(staff_id != null || !TextUtils.isEmpty(staff_id))
                    {
                        startPages(ClassesList.class);
                        return;
                    }
                    if (member_id != null)
                    {

                    /*if(staff_id != null || !TextUtils.isEmpty(staff_id))
                    {
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

                        Intent in = new Intent(SplashActivity.this, ClassesList.class);

                    }*/

                    if (member_id.equalsIgnoreCase("1")) {
                        //Teacher
                        startPages(TeacherView.class);
                    } else if (member_id.equalsIgnoreCase("2")) {
                        startPages(ParentActivity.class);
                        // Parent
                    } else if (member_id.equalsIgnoreCase("3")) {
                        // Staff
                        startPages(StaffView.class);
                    } else if (member_id.equalsIgnoreCase("4")) {
                        // Tutor
                        startPages(TutorsView.class);
                    } else if (member_id.equalsIgnoreCase("5")) {
                        // Transport
                        startPages(TransportView.class);
                    } else if (member_id.equalsIgnoreCase("6")) {
                        startPages(RoutesList.class);
                    }/*else if(staff_id != null)
                    {
                        startPages(ClassesList.class);
                    }*/
                    else{
                        startPages(Login_Phone.class);
                    }

                    } else {
            /*if(staff_id != null)
            {
                startPages(ClassesList.class);
            }
            else {*/
                        startPages(Login_Phone.class);
                        //}
                    }

                }
            }, 3000);

    }

    private void startPages(Class<?> cls) {

        Intent st = new Intent(getApplicationContext(), cls);
        startActivity(st);
        finish();
    }
}
