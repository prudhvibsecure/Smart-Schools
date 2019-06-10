package com.bsecure.scsm_mobile.modules;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import com.bsecure.scsm_mobile.R;

public class PeriodsTeacherView extends AppCompatActivity {

    String school_id, class_id, day;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_periods_teacher_view);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Calender");//Organization Head
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        Intent data = getIntent();
        if(data!=null)
        {
            school_id = data.getStringExtra("school_id");
            class_id = data.getStringExtra("class_id");
            day = "1";
        }
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.loadUrl("https://gogosmartschool.com/scs/teacher/periodtimetable?school_id="+school_id+"&day=1&class_id="+class_id);
        webView.getSettings().setJavaScriptEnabled(true);

    }
}
