package com.bsecure.scsm_mobile.modules;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CalendarView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.models.GalleryModel;

import org.json.JSONArray;

import java.util.ArrayList;

import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.MarkStyle;
import sun.bob.mcalendarview.listeners.OnDateClickListener;
import sun.bob.mcalendarview.vo.DateData;

public class Absentdate_view extends AppCompatActivity {

   MCalendarView mCalendarView;
   String year,month;
   ArrayList<String> dates;
   int mnth=1;
   CalendarView cv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_absentdate_view);

        mCalendarView  =  findViewById(R.id.calendar);
        Intent i=getIntent();
        Bundle bd=i.getExtras();
        if(bd != null) {
            year = bd.getString("year");
            month = bd.getString("month");
            dates = (ArrayList<String>) bd.getSerializable("dates");

        }
        cv = new CalendarView(this);

       /* ArrayList<DateData> dates=new ArrayList<>();
        dates.add(new DateData(2018,04,26));
        dates.add(new DateData(2018,04,27));

        for(int i=0;i<dates.size();i++) {*/
       if (month.equalsIgnoreCase("Jan")){
           mnth=1;
       }
        else if (month.equalsIgnoreCase("Feb")){
            mnth=2;

        } else if (month.equalsIgnoreCase("Mar")){
            mnth=3;

        } else if (month.equalsIgnoreCase("Apr")){
            mnth=4;

        } else if (month.equalsIgnoreCase("May")){
            mnth=5;

        } else if (month.equalsIgnoreCase("Jun")){
            mnth=6;

        } else if (month.equalsIgnoreCase("Jul")){
            mnth=7;

        } else if (month.equalsIgnoreCase("Aug")){
            mnth=8;

        } else if (month.equalsIgnoreCase("Sep")){
            mnth=9;

        } else if (month.equalsIgnoreCase("Oct")){
            mnth=10;

        } else if (month.equalsIgnoreCase("Nov")){
            mnth=11;

        } else if (month.equalsIgnoreCase("Dec")){
            mnth=12;

        }
       for (int j=0;j<dates.size();j++)
       {
           try {
               mCalendarView.markDate(new DateData(Integer.parseInt(year),mnth,Integer.parseInt(dates.get(j))).setMarkStyle(new MarkStyle(MarkStyle.BACKGROUND, Color.RED)));

           }catch (Exception e)
           {
               e.printStackTrace();
           }
       }

       //mark multiple dates with this code.

        //dates.get(i).getYear(),dates.get(i).getMonth(),dates.get(i).getDay()

        //Log.d("marked dates:-",""+mCalendarView.getMarkedDates());//get all marked dates.
        mCalendarView.setOnDateClickListener(new OnDateClickListener() {
            @Override
            public void onDateClick(View view, DateData date) {
                Toast.makeText(Absentdate_view.this, "absent", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
