package com.bsecure.scsm_mobile.fragments;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.UserGuide;
import com.bsecure.scsm_mobile.adapters.StudentsListAdapter;
import com.bsecure.scsm_mobile.adapters.TransportListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.CalenderModel;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.models.TransportModel;
import com.bsecure.scsm_mobile.modules.Calender;
import com.bsecure.scsm_mobile.modules.ParentActivity;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback_Trns;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MoreFragment extends Fragment {

    private ParentActivity schoolMain;
    View view_layout;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout coordinatorLayout;
    List<String>listdata;
    ArrayAdapter<String> adapter;
    ListView list;

    private ArrayList<String> st_ids = new ArrayList<>();
    private IntentFilter filter;
    public MoreFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.schoolMain = (ParentActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view_layout = inflater.inflate(R.layout.content_main_view, null);

        view_layout.findViewById(R.id.toolset).setVisibility(View.GONE);
        mRecyclerView = view_layout.findViewById(R.id.content_list);
        mRecyclerView.setVisibility(View.GONE);
        list = view_layout.findViewById(R.id.list);
        list.setVisibility(View.VISIBLE);

        listdata = new ArrayList<>();
        //listdata.add("Attendance");
        listdata.add("User Guide");

        adapter = new ArrayAdapter<>(getActivity(), R.layout.list_item, R.id.name, listdata);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                {
                    startActivity(new Intent(getActivity(), UserGuide.class));
                }
            }
        });
        return view_layout;
    }

}
