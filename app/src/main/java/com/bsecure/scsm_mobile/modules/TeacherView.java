package com.bsecure.scsm_mobile.modules;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bsecure.scsm_mobile.AttendanceView;
import com.bsecure.scsm_mobile.Login_Phone;
import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.ClassListAdapter;
import com.bsecure.scsm_mobile.adapters.OrgListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.chat.ChatSingle;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.controls.ColorGenerator;
import com.bsecure.scsm_mobile.controls.TextDrawable;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.ClassModel;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback_Staff;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TeacherView extends AppCompatActivity implements HttpHandler, ClassListAdapter.ContactAdapterListener {
    private DB_Tables db_tables;
    ArrayList<ClassModel> classModelArrayList;
    private ClassListAdapter adapter;
    private RecyclerView mRecyclerView;
    public ItemTouchHelperExtension mItemTouchHelper;
    public ItemTouchHelperExtension.Callback mCallback;
    private TextDrawable.IBuilder builder = null;
    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private Dialog m_dialog;
    private IntentFilter filter, re_filter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);
        filter = new IntentFilter("com.scs.app.SESSION");
        filter.setPriority(1);

        re_filter = new IntentFilter("com.teacher.add");
        registerReceiver(mBroadcastReceiver_ref, re_filter);

        db_tables = new DB_Tables(this);
        db_tables.openDB();

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Class List");//Organization Head
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.content_list);
        mCallback = new ItemTouchHelperCallback();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        getTeachers();
    }

    private void getTeachers() {

        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("id", SharedValues.getValue(this, "id"));
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.get_teacher, object.toString(), 1);
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
                        JSONArray jsonarray2 = object.getJSONArray("teacher_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                db_tables.addClassList(jsonobject.optString("teacher_id"), jsonobject.optString("teacher_name"), jsonobject.optString("phone_number"), jsonobject.optString("class_teacher"), jsonobject.optString("class_id"), jsonobject.optString("status"));
                            }
                        }
                        JSONArray jsonArray3 = object.getJSONArray("teacher_classes_details");
                        if (jsonArray3.length() > 0) {
                            for (int i = 0; i < jsonArray3.length(); i++) {
                                JSONObject jsonobject = jsonArray3.getJSONObject(i);
                                db_tables.addTeacherClassList(jsonobject.optString("teacher_classes_id"), jsonobject.optString("class_id"), jsonobject.optString("section"), jsonobject.optString("class_name"), jsonobject.optString("subjects"));
                            }
                        }
                        getListTeachers();
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getListTeachers() {

        try {
            String msg_list = db_tables.getclsList();
            classModelArrayList = new ArrayList<>();
            JSONObject obj = new JSONObject(msg_list);
            JSONArray jsonarray2 = obj.getJSONArray("teacher_classes_details");
            // staff_details[staff_id, name, designation, phone_number, school_id]
            if (jsonarray2.length() > 0) {
                for (int i = 0; i < jsonarray2.length(); i++) {
                    ClassModel classModel = new ClassModel();

                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
                    classModel.setTeacher_classes_id(jsonobject.optString("teacher_classes_id"));
                    classModel.setSectionName(jsonobject.optString("section"));
                    classModel.setClsName(jsonobject.optString("class_name"));
                    classModel.setClass_id(jsonobject.optString("class_id"));
                    classModel.setSubjects(jsonobject.optString("subjects"));
                    classModelArrayList.add(classModel);

                }


                adapter = new ClassListAdapter(classModelArrayList, this, this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.teacher_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.pr_teacher:
                showProfile();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProfile() {
        builder = TextDrawable.builder().beginConfig().toUpperCase().textColor(Color.WHITE).endConfig().round();
        String name = db_tables.getProfileData(SharedValues.getValue(this, "ph_number"));
        int color = generator.getColor(name);
        TextDrawable ic1 = builder.build(name.substring(0, 1).trim(), color);
        m_dialog = new Dialog(this, R.style.MyAlertDialogStyle);
        m_dialog.setContentView(R.layout.techer_profile);
        m_dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        m_dialog.setCancelable(true);
        m_dialog.setCanceledOnTouchOutside(true);
        ((TextView) m_dialog.findViewById(R.id.user_profile_name)).setText(name);
        ((TextView) m_dialog.findViewById(R.id.email)).setText(SharedValues.getValue(this, "ph_number"));
        ((TextView) m_dialog.findViewById(R.id.user_profile_short_bio)).setText("Class Teacher");
        ((ImageView) m_dialog.findViewById(R.id.user_profile_photo)).setImageDrawable(ic1);
        m_dialog.getWindow().setGravity(Gravity.BOTTOM);
        m_dialog.show();
    }

    @Override
    public void onMessageRowClicked(List<ClassModel> matchesList, int position) {

        Intent chat = new Intent(getApplicationContext(), ChatSingle.class);
        chat.putExtra("class_id", matchesList.get(position).getClass_id());
        chat.putExtra("section", matchesList.get(position).getSectionName());
        chat.putExtra("class_name", matchesList.get(position).getClsName());
        chat.putExtra("teacher_id", SharedValues.getValue(this, "id"));
        startActivity(chat);
    }

    @Override
    public void swipeToSyllabus(int position, List<ClassModel> matchesList) {
        adapter.notifyDataSetChanged();
        getListTeachers();

        Intent chat = new Intent(getApplicationContext(), Marks.class);
        chat.putExtra("class_id", matchesList.get(position).getClass_id());
        chat.putExtra("section", matchesList.get(position).getSectionName());
        chat.putExtra("class_name", matchesList.get(position).getClsName());
        chat.putExtra("teacher_id", SharedValues.getValue(this, "id"));
        chat.putExtra("value", "0");
        startActivity(chat);
    }

    @Override
    public void swipeToMarks(int position, List<ClassModel> matchesList) {
        adapter.notifyDataSetChanged();
        getListTeachers();
        Intent chat = new Intent(getApplicationContext(), Marks.class);
        chat.putExtra("class_id", matchesList.get(position).getClass_id());
        chat.putExtra("section", matchesList.get(position).getSectionName());
        chat.putExtra("class_name", matchesList.get(position).getClsName());
        chat.putExtra("teacher_id", SharedValues.getValue(this, "id"));
        chat.putExtra("value", "1");
        startActivity(chat);
    }

    @Override
    public void swipeToAttendence(int position, List<ClassModel> classModelList) {
        adapter.notifyDataSetChanged();
        getListTeachers();
        Intent chat = new Intent(getApplicationContext(), AttendanceView.class);
        chat.putExtra("class_id", classModelList.get(position).getClass_id());
        chat.putExtra("section", classModelList.get(position).getSectionName());
        chat.putExtra("class_name", classModelList.get(position).getClsName());
        chat.putExtra("teacher_id", SharedValues.getValue(this, "id"));
        startActivity(chat);

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mBroadcastReceiver);
            unregisterReceiver(mBroadcastReceiver_ref);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    adapter.clear();
                    getListTeachers();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };
    private final BroadcastReceiver mBroadcastReceiver_ref = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                if (adapter != null) {
                    adapter.notifyDataSetChanged();
                    adapter.clear();
                    getListTeachers();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

}
