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

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.AdminClassListAdapter;
import com.bsecure.scsm_mobile.callbacks.ClickListener;
import com.bsecure.scsm_mobile.models.ClassModel;
import com.bsecure.scsm_mobile.modules.TeachersList;
import com.bsecure.scsm_mobile.utils.SharedValues;

import java.util.ArrayList;

public class ClassesListFragment extends Fragment {

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

        classlist = (ArrayList<ClassModel>)getActivity().getIntent().getSerializableExtra("classes");

        getList();

        return view_layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getList() {

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        getActivity().invalidateOptionsMenu();
    }



}
