package com.bsecure.scsm_mobile.modules;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.OrgListAdapter;
import com.bsecure.scsm_mobile.adapters.TutorsListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.TutorsModel;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback_Staff;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StaffView extends AppCompatActivity implements HttpHandler ,OrgListAdapter.ContactAdapterListener{
    private DB_Tables db_tables;
    ArrayList<OrganizationModel> organizationModelArrayList;
    private OrgListAdapter adapter;
    private RecyclerView mRecyclerView;
    public ItemTouchHelperExtension mItemTouchHelper;
    public ItemTouchHelperExtension.Callback mCallback;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);

        db_tables = new DB_Tables(this);
        db_tables.openDB();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Staff");//Organization Head
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        mRecyclerView = findViewById(R.id.content_list);
        mCallback = new ItemTouchHelperCallback_Staff();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        getStaff();
    }

    private void getStaff() {

        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("staff_id", SharedValues.getValue(this, "id"));
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Processing...", 1, Paths.get_staff, object.toString(), 1);
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
                        JSONArray jsonarray2 = object.getJSONArray("staff_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                db_tables.addStaff(jsonobject.optString("staff_id"), jsonobject.optString("name"), jsonobject.optString("phone_number"), jsonobject.optString("designation"), jsonobject.optString("school_id"), "0");
                            }
                            getListStaff();
                        }
                    }
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getListStaff() {

        try {
            String msg_list = db_tables.getStaffeList();
            organizationModelArrayList = new ArrayList<>();
            JSONObject obj = new JSONObject(msg_list);
            JSONArray jsonarray2 = obj.getJSONArray("staff_body");
           // staff_details[staff_id, name, designation, phone_number, school_id]
            if (jsonarray2.length() > 0) {
                for (int i = 0; i < jsonarray2.length(); i++) {
                    OrganizationModel organizationModel = new OrganizationModel();

                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
                    organizationModel.setStaff_id(jsonobject.optString("staff_id"));
                    organizationModel.setPhone_number(jsonobject.optString("phone_number"));
                    organizationModel.setSchool_id(jsonobject.optString("school_id"));
                    organizationModel.setDesignation(jsonobject.optString("designation"));
                    organizationModel.setName(jsonobject.optString("name"));
                    organizationModel.setStatus(jsonobject.optString("status"));
                    organizationModelArrayList.add(organizationModel);

                }


                adapter = new OrgListAdapter(organizationModelArrayList, this, this);
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
    public void onMessageRowClicked(List<OrganizationModel> matchesList, int position) {

    }

    @Override
    public void swipeToDelete(int position, List<OrganizationModel> classModelList) {

    }

    @Override
    public void swipeToStatus(int position, List<OrganizationModel> classModelList, String s) {

    }
}
