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
import android.view.MenuItem;
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
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallbackMessage;
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
    String student_id, class_id;

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

        Intent in = getIntent();
        student_id = in.getStringExtra("student_id");
        class_id = in.getStringExtra("class_id");

        mRecyclerView = findViewById(R.id.content_list);

        db_tables = new DB_Tables(this);
        db_tables.openDB();

        mCallback = new ItemTouchHelperCallbackMessage();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        getMessages();

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
            object.put("student_id", student_id);
            object.put("class_id", class_id);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.get_approval_messages, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageRowClicked(List<ApprovalModel> matchesList, int position) {

    }

    @Override
    public void swipeToSyllabus(int position, List<ApprovalModel> classModelList) {

        try {
            JSONObject object = new JSONObject();
            object.put("student_id", student_id);
            object.put("message_id", classModelList.get(position).getMessage_id());
            object.put("status", 1);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 2, Paths.student_approval_message, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void swipeToDelete(int position, List<ApprovalModel> classModelList) {

        try {
            JSONObject object = new JSONObject();
            object.put("student_id", student_id);
            object.put("message_id", classModelList.get(position).getMessage_id());
            object.put("status", 2);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 2, Paths.student_approval_message, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Object results, int requestType) {

        try {
            switch (requestType) {
                case 1:
                    messages = new ArrayList<>();
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        //findViewById(R.id.no_data).setVisibility(View.GONE);
                        JSONArray jsonarray2 = object.getJSONArray("approval_msgs_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                ApprovalModel message = new ApprovalModel();
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                message.setMessage(jsonobject.optString("message"));
                                message.setMessage_id(jsonobject.optString("message_id"));
                                message.setMessage_date(jsonobject.optString("message_date"));
                                messages.add(message);
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

                case 2:
                    JSONObject object1 = new JSONObject(results.toString());
                    if(object1.optString("statuscode").equalsIgnoreCase("200"))
                    {
                        Toast.makeText(this, "Status Updated Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        Toast.makeText(this, "Failed To Update Status.Try Again", Toast.LENGTH_SHORT).show();
                    }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }
}
