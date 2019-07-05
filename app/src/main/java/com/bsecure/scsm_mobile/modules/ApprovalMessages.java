package com.bsecure.scsm_mobile.modules;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.ApprovalMessagesAdapter;
import com.bsecure.scsm_mobile.adapters.TutorAssignStudentsListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.ApprovalModel;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback_Tutors;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ApprovalMessages extends AppCompatActivity implements HttpHandler, ApprovalMessagesAdapter.ContactAdapterListener{

    private DB_Tables db_tables;
    ArrayList<ApprovalModel> messages;
    private ApprovalMessagesAdapter adapter;
    private RecyclerView mRecyclerView;
    public ItemTouchHelperExtension mItemTouchHelper;
    public ItemTouchHelperExtension.Callback mCallback;
    IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_approval_messages);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Approval Messages");//Organization Head
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ApprovalModel model = new ApprovalModel();
        model.setMessage("picnic 200");
        model.setStatus("0");
        messages = new ArrayList<>();
        mRecyclerView = findViewById(R.id.content_list);
        messages.add(model);
        adapter = new ApprovalMessagesAdapter(messages, this, this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(adapter);


        db_tables = new DB_Tables(this);
        db_tables.openDB();

        mCallback = new ItemTouchHelperCallback_Tutors();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        //getMessages();

    }

    @Override
    protected void onResume() {
        //registerReceiver(mBroadcastReceiver, filter);
        super.onResume();
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (adapter != null) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    getMessages();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private void getMessages() {

        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("tutor_id", SharedValues.getValue(this, "id"));
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.get_tutor_students, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageRowClicked(List<ApprovalModel> matchesList, int position) {

    }

    @Override
    public void swipeToSyllabus(int position, List<ApprovalModel> classModelList) {

    }

    @Override
    public void swipeToDelete(int position, List<ApprovalModel> classModelList) {

    }

    @Override
    public void onResponse(Object results, int requestType) {

        try {
            switch (requestType) {
                case 1:
                    messages = new ArrayList<>();
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        findViewById(R.id.no_data).setVisibility(View.GONE);
                        JSONArray jsonarray2 = object.getJSONArray("tutor_students_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                ApprovalModel studentModel = new ApprovalModel();
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                studentModel.setMessage(jsonobject.optString("message"));
                                studentModel.setStatus(jsonobject.optString("status"));
                                messages.add(studentModel);
                            }
                            adapter = new ApprovalMessagesAdapter(messages, this, this);
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
                        }
                        else
                        {
                            Toast.makeText(ApprovalMessages.this, "No Data Found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(ApprovalMessages.this, "No Data Found", Toast.LENGTH_SHORT).show();
                        findViewById(R.id.no_data).setVisibility(View.VISIBLE);
                        //adapter.notifyDataSetChanged();

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
