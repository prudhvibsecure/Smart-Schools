package com.bsecure.scsm_mobile.modules;

import android.annotation.TargetApi;
import android.app.PictureInPictureParams;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Rational;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.TrasnsListAdapter;
import com.bsecure.scsm_mobile.adapters.TutorsListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.chat.ViewChatSingle;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.Transport;
import com.bsecure.scsm_mobile.models.TutorsModel;
import com.bsecure.scsm_mobile.mpasv.GMaps;
import com.bsecure.scsm_mobile.mpasv.GPSTracker;
import com.bsecure.scsm_mobile.mpasv.GoogleView;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransportView extends AppCompatActivity implements HttpHandler, TrasnsListAdapter.ContactAdapterListener, View.OnClickListener {
    private DB_Tables db_tables;
    ArrayList<Transport> transportArrayList;
    private TrasnsListAdapter adapter;
    private RecyclerView mRecyclerView;
    private IntentFilter filter;
    private TextView tv_name;
    private Button start_btn, stop_btn;
    Handler habs;
    Runnable myRunnable;
    Toolbar toolbar;
    LinearLayout vv;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.transport_nn);

        db_tables = new DB_Tables(this);
        db_tables.openDB();
        toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Transport");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        tv_name = findViewById(R.id.name);
        start_btn = findViewById(R.id.start_btn);
        start_btn.setOnClickListener(this);
        stop_btn = findViewById(R.id.stop_btn);
        stop_btn.setOnClickListener(this);
        vv = findViewById(R.id.vv);
        // mRecyclerView = findViewById(R.id.content_list);
//        filter = new IntentFilter("com.scm.gps");
//        registerReceiver(mB, filter);
        getTurotos();
    }

    private void getTurotos() {

        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("transport_id", SharedValues.getValue(this, "id"));
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.get_transport, object.toString(), 1);
        } catch (Exception e) {
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
                        JSONArray jsonarray2 = object.getJSONArray("transport_details");
                        if (jsonarray2.length() > 0) {
                            //for (int i = 0; i < jsonarray2.length(); i++) {
                            JSONObject jsonobject = jsonarray2.getJSONObject(0);
                            db_tables.addTransport(jsonobject.optString("transport_id"), jsonobject.optString("transport_name"), jsonobject.optString("phone_number"), jsonobject.optString("school_id"), "0", jsonobject.optString("student_id"));
                            // }

                            getListTutors();
                        }
                    }
                    break;
                case 2:
                    JSONObject objects = new JSONObject(results.toString());
                    if (objects.optString("statuscode").equalsIgnoreCase("200")) {
                        getRoute();

                    }
                    break;
                case 3:
                    if (myRunnable != null) {
                        habs.removeCallbacks(myRunnable);
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void getRoute() {
        habs = new Handler();
        myRunnable = new Runnable() {
            @Override
            public void run() {
                syncCall();
                habs.postDelayed(myRunnable, 30000);

            }
        };
        habs.postDelayed(myRunnable, 3000);
    }

    private void syncCall() {
        try {
            GPSTracker gpsTracker = new GPSTracker(this);
            if (gpsTracker.getIsGPSTrackingEnabled()) {
                JSONObject object = new JSONObject();
                object.put("transport_id", SharedValues.getValue(this, "id"));
                object.put("school_id", SharedValues.getValue(this, "school_id"));
                object.put("lat", gpsTracker.getLatitude());
                object.put("lang", gpsTracker.getLongitude());
                object.put("domain", ContentValues.DOMAIN);
                HTTPNewPost task = new HTTPNewPost(this, this);
                task.disableProgress();
                task.userRequest("Processing...", 4, Paths.send_coordinates, object.toString(), 1);
            } else {
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gpsTracker.showSettingsAlert();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {

        //unregisterReceiver(mB);
        super.onDestroy();
    }

//    BroadcastReceiver mB = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            if (intent.getAction().equalsIgnoreCase("com.scm.gps")) {
//                String trans_id = intent.getStringExtra("trans_id");
//                String student_id = intent.getStringExtra("student_id");
//                String school_id = intent.getStringExtra("school_id");
//                sendNotify(trans_id, student_id, school_id);
//            }
//        }
//    };

    private void sendNotify(String trans_id, String student_id, String school_id) {
        GPSTracker gpsTracker = new GPSTracker(TransportView.this);
        if (gpsTracker.getIsGPSTrackingEnabled()) {
            try {
                //transport_id,student_id,school_id,lat,lang
                JSONObject object = new JSONObject();
                object.put("transport_id", trans_id);
                object.put("student_id", student_id);
                object.put("school_id", school_id);
                object.put("lat", String.valueOf(gpsTracker.getLatitude()));
                object.put("lang", String.valueOf(gpsTracker.getLongitude()));
                object.put("domain", ContentValues.DOMAIN);
                HTTPNewPost task = new HTTPNewPost(getApplicationContext(), this);
                task.disableProgress();
                task.userRequest("Please Wait...", 10, Paths.get_coordinates, object.toString(), 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }

    private void getListTutors() {

        try {
            String msg_list = db_tables.getTransList();
            transportArrayList = new ArrayList<>();
            JSONObject obj = new JSONObject(msg_list);
            JSONArray jsonarray2 = obj.getJSONArray("trans_body");

            if (jsonarray2.length() > 0) {
                // for (int i = 0; i < jsonarray2.length(); i++) {
                Transport transport = new Transport();
                JSONObject jsonobject = jsonarray2.getJSONObject(0);
                transport.setTransport_id(jsonobject.optString("transport_id"));
                transport.setPhone_number(jsonobject.optString("phone_number"));
                transport.setSchool_id(jsonobject.optString("school_id"));
                transport.setTransport_name(jsonobject.optString("transport_name"));
                transport.setStudent_id(jsonobject.optString("student_id"));
                // transportArrayList.add(transport);
                tv_name.setText(jsonobject.optString("transport_name"));
                // }


//                adapter = new TrasnsListAdapter(transportArrayList, this, this);
//                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
//                mRecyclerView.setLayoutManager(linearLayoutManager);
//                mRecyclerView.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }

    @Override
    public void onMessageRowClicked(List<Transport> matchesList, int position) {

//        Intent nxt = new Intent(this, GoogleView.class);
//        nxt.putExtra("t_id", matchesList.get(position).getTransport_id());
//        nxt.putExtra("sc_id", matchesList.get(position).getSchool_id());
//       // nxt.putExtra("st_id", matchesList.get(position).getStudent_id());
//        startActivity(nxt);

    }

    @Override
    public void swipeToEdit(int position, List<Transport> classModelList) {

    }

    @Override
    public void swipeToDelete(int position, List<Transport> classModelList) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_btn:
                start_btn.setVisibility(View.GONE);
                stop_btn.setVisibility(View.VISIBLE);
                startUp(1);
                break;
            case R.id.stop_btn:
                start_btn.setVisibility(View.VISIBLE);
                stop_btn.setVisibility(View.GONE);
                startUp(2);
                break;
        }

    }

    private void startUp(int req) {

        try {
            JSONObject object = new JSONObject();
            object.put("transport_id", SharedValues.getValue(this, "id"));
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.disableProgress();
            if (req == 1) {
                task.userRequest("Processing...", 2, Paths.start_transport, object.toString(), 1);
            } else {
                task.userRequest("Processing...", 3, Paths.stop_transport, object.toString(), 1);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {

        if (myRunnable != null) {
            habs.removeCallbacks(myRunnable);
        }
        super.onBackPressed();
    }

    private void startPictureInPictureFeature() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            PictureInPictureParams.Builder pictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
            Rational aspectRatio = new Rational(vv.getWidth(), vv.getHeight());
            pictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
            enterPictureInPictureMode(pictureInPictureParamsBuilder.build());
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    public void onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            if (!isInPictureInPictureMode()) {
                Rational aspectRatio = new Rational(vv.getWidth(), vv.getHeight());
                PictureInPictureParams.Builder pictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
                pictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
                enterPictureInPictureMode(pictureInPictureParamsBuilder.build());
            }
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode,
                                              Configuration newConfig) {
        if (isInPictureInPictureMode) {

            toolbar.setVisibility(View.GONE);
        } else {

            toolbar.setVisibility(View.VISIBLE);
        }
    }


}
