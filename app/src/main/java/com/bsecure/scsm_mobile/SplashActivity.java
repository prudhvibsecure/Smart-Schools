package com.bsecure.scsm_mobile;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bsecure.scsm_mobile.modules.ClassesList;
import com.bsecure.scsm_mobile.modules.ParentActivity;
import com.bsecure.scsm_mobile.modules.ParentView;
import com.bsecure.scsm_mobile.modules.RoutesList;
import com.bsecure.scsm_mobile.modules.StaffView;
import com.bsecure.scsm_mobile.modules.TeacherView;
import com.bsecure.scsm_mobile.modules.TransportView;
import com.bsecure.scsm_mobile.modules.TutorsView;
import com.bsecure.scsm_mobile.utils.SharedValues;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final String member_id = SharedValues.getValue(this, "member_id");
        final String staff_id = SharedValues.getValue(this,"staff_id");
        if (member_id != null) {
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
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

                }
            }, 3000);
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

    private void startPages(Class<?> cls) {

        Intent st = new Intent(getApplicationContext(), cls);
        startActivity(st);
        finish();
    }
}
