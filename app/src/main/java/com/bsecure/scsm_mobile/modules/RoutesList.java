package com.bsecure.scsm_mobile.modules;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Rational;
import android.view.View;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.RouteListAdapter;
import com.bsecure.scsm_mobile.adapters.TrasnsListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.Transport;
import com.bsecure.scsm_mobile.models.TransportModel;
import com.bsecure.scsm_mobile.mpasv.GoogleView;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class RoutesList extends AppCompatActivity implements HttpHandler, RouteListAdapter.ContactAdapterListener {
    ArrayList<TransportModel> transportArrayList;
    private RouteListAdapter adapter;
    private RecyclerView mRecyclerView;
    PictureInPictureParams.Builder pictureInPictureParamsBuilder = new PictureInPictureParams.Builder();
    Toolbar toolbar;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);

        toolbar= (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Transport");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.content_list);
        transportArrayList = new ArrayList<>();
        getTurotos();
    }

    private void getTurotos() {

        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.get_transports, object.toString(), 1);
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

                            for (int i = 0; i < jsonarray2.length(); i++) {
                                TransportModel transport = new TransportModel();
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                transport.setTransport_id(jsonobject.optString("transport_id"));
                                transport.setPhone_number(jsonobject.optString("phone_number"));
                                transport.setTransport_name(jsonobject.optString("transport_name"));
                                transportArrayList.add(transport);

                            }
                            adapter = new RouteListAdapter(transportArrayList, this, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
                        }


                    }
                    break;
                case 2:
                    JSONObject object1 = new JSONObject(results.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        Toast.makeText(this, object1.optString("statusdescription"), Toast.LENGTH_SHORT).show();
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
    public void onMessageRow(List<TransportModel> matchesList, int position) {
//        Intent nxt = new Intent(this, GoogleView.class);
//        nxt.putExtra("t_id", matchesList.get(position).getTransport_id());
//        nxt.putExtra("sc_id", matchesList.get(position).getSchool_id());
//        nxt.putExtra("st_id", matchesList.get(position).getStudent_id());
        // startActivity(nxt);
    }

    @Override
    public void onClickStart(List<TransportModel> classModelList, int position) {
        try {
            JSONObject object = new JSONObject();
            object.put("transport_id", classModelList.get(position).getTransport_id());
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 2, Paths.start_transport, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClickStop(List<TransportModel> classModelList, int position) {
        try {
            JSONObject object = new JSONObject();
            object.put("transport_id", classModelList.get(position).getTransport_id());
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 2, Paths.stop_transport, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        startPictureInPictureFeature();
        super.onBackPressed();
    }

    private void startPictureInPictureFeature() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            Rational aspectRatio = new Rational(mRecyclerView.getWidth(), mRecyclerView.getHeight());
            pictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
            enterPictureInPictureMode(pictureInPictureParamsBuilder.build());
        }
    }

    @Override
    public void onUserLeaveHint() {
        if (!isInPictureInPictureMode()) {
            Rational aspectRatio = new Rational(mRecyclerView.getWidth(), mRecyclerView.getHeight());
            pictureInPictureParamsBuilder.setAspectRatio(aspectRatio).build();
            enterPictureInPictureMode(pictureInPictureParamsBuilder.build());
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