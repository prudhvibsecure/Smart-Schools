package com.bsecure.scsm_mobile.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.AdminLogin;
import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.AdminClassListAdapter;
import com.bsecure.scsm_mobile.callbacks.ClickListener;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.ClassModel;
import com.bsecure.scsm_mobile.modules.ClassesList;
import com.bsecure.scsm_mobile.modules.TeachersList;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;

public class ClassesListFragment extends Fragment implements HttpHandler {

    View view_layout;

    private RecyclerView mRecyclerView;

    private CoordinatorLayout coordinatorLayout;

    ArrayList<ClassModel> classlist;

    private AdminClassListAdapter adapter;

    TextView nodata;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        //this.schoolMain = (ClassesList) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view_layout = inflater.inflate(R.layout.class_list_fragment, null);

        nodata = view_layout.findViewById(R.id.nodata);

        mRecyclerView = view_layout.findViewById(R.id.list);

        //classlist = (ArrayList<ClassModel>)getActivity().getIntent().getSerializableExtra("classes");

        getList();

        return view_layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getList() {

        try {

            JSONObject obj = new JSONObject();

            obj.put("school_id", SharedValues.getValue(getActivity(),"school_id"));

            obj.put("domain", ContentValues.DOMAIN);

            HTTPNewPost task = new HTTPNewPost(getActivity(), this);

            task.userRequest("Processing...", 1, Paths.get_class_list, obj.toString(), 1);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
    }


    @Override
    public void onResponse(Object results, int requestType) {

        JSONObject object = null;
        try {
            object = new JSONObject(results.toString());

            if (object.optString("statuscode").equalsIgnoreCase("200")) {

                JSONArray array = object.getJSONArray("class_list");

                classlist = new ArrayList<>();

                for(int i = 0; i<array.length(); i++)
                {
                    JSONObject obj = array.getJSONObject(i);

                    ClassModel model = new ClassModel();

                    model.setClass_id(obj.optString("class_id"));

                    model.setClsName(obj.optString("class_name"));

                    model.setSectionName(obj.optString("section"));

                    model.setSubjects(obj.optString("subjects"));

                    classlist.add(model);
                }

                adapter = new AdminClassListAdapter(getActivity(),classlist, new ClickListener() {
                    @Override
                    public void OnRowClicked(int position, View view) {
                        Intent in  = new Intent(getActivity(), TeachersList.class);
                        in.putExtra("school_id", SharedValues.getValue(getActivity(),"school_id"));
                        in.putExtra("class_id", classlist.get(position).getClass_id());
                        in.putExtra("class_name", classlist.get(position).getClsName());
                        startActivity(in);
                    }
                });
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);

            }
            else
            {
                Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onFailure(String errorCode, int requestType) {

    }
}
