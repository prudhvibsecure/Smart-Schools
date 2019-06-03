package com.bsecure.scsm_mobile.fragments;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.TransportListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.models.TransportModel;
import com.bsecure.scsm_mobile.modules.ParentActivity;
import com.bsecure.scsm_mobile.mpasv.TrasportMaps;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback_Trns;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TransportListFragment extends Fragment implements TransportListAdapter.ContactAdapterListener, HttpHandler {

    private ParentActivity schoolMain;
    View view_layout;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout coordinatorLayout;
    public ItemTouchHelperExtension mItemTouchHelper;
    public ItemTouchHelperExtension.Callback mCallback;
    private DB_Tables db_tables;
    ArrayList<TransportModel> teacherModelArrayList;
    private TransportListAdapter adapter;
    private String teach_Id, t_status, student_id;
    private Dialog member_dialog;
    private List<StudentModel> studentModelList;
    private Spinner m_spinner;
    private String tras_id = null, tp_name, phone_number;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.schoolMain = (ParentActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view_layout = inflater.inflate(R.layout.content_main_view, null);
        db_tables = new DB_Tables(getActivity());
        db_tables.openDB();
        view_layout.findViewById(R.id.toolset).setVisibility(View.GONE);
        view_layout.findViewById(R.id.fab).setVisibility(View.VISIBLE);
        view_layout.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddTransport();
            }
        });
        mRecyclerView = view_layout.findViewById(R.id.content_list);
        teachersList();
        mCallback = new ItemTouchHelperCallback_Trns();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        return view_layout;
    }

    private void getAddTransport() {
        member_dialog = new Dialog(getActivity(), R.style.MyAlertDialogStyle);
        member_dialog.setContentView(R.layout.add_transport);
        member_dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        member_dialog.setCancelable(true);
        member_dialog.setCanceledOnTouchOutside(true);
        member_dialog.findViewById(R.id.add_cls).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addTP();
            }
        });
        m_spinner = (Spinner) member_dialog.findViewById(R.id.st_spinner);
        getStudentsList(m_spinner);
        m_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                StudentModel studentModel = null;
                studentModel = (StudentModel) m_spinner.getSelectedItem();
                student_id = studentModel.getStudent_id();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        member_dialog.getWindow().setGravity(Gravity.BOTTOM);

        member_dialog.show();
    }

    private void getStudentsList(Spinner m_spinner) {

        try {
            String msg_list = db_tables.getstudentsList();
            studentModelList = new ArrayList<>();
            JSONObject obj = new JSONObject(msg_list);
            JSONArray jsonarray2 = obj.getJSONArray("student_details");
            if (jsonarray2.length() > 0) {
                for (int i = 0; i < jsonarray2.length(); i++) {
                    StudentModel studentModel = new StudentModel();

                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
                    studentModel.setStudent_name(jsonobject.optString("student_name"));
                    studentModel.setRoll_no(jsonobject.optString("roll_no"));
                    studentModel.setStatus(jsonobject.optString("status"));
                    studentModel.setClass_id(jsonobject.optString("class_id"));
                    studentModel.setStudent_id(jsonobject.optString("student_id"));
                    studentModelList.add(studentModel);

                }

                ArrayAdapter<StudentModel> dataAdapter = new ArrayAdapter<StudentModel>(getContext(),
                        android.R.layout.simple_spinner_item, studentModelList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                this.m_spinner.setAdapter(dataAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addTP() {
        try {
            // transport_id, transport_name, phone_number, student_id, school_id
            tras_id = String.valueOf(System.currentTimeMillis());
            tp_name = ((EditText) member_dialog.findViewById(R.id.ad_t_ts)).getText().toString().trim();
            if (tp_name.length() == 0) {
                Toast.makeText(getActivity(), "Please Enter Transport Name", Toast.LENGTH_SHORT).show();
                return;
            }
            phone_number = ((EditText) member_dialog.findViewById(R.id.ad_t_ts_n)).getText().toString().trim();
            if (phone_number.length() < 10) {
                Toast.makeText(getActivity(), "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject object = new JSONObject();
            object.put("transport_id", tras_id);
            object.put("transport_name", tp_name);
            object.put("phone_number", phone_number);
            object.put("student_id", student_id);
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Please Wait...", 4, Paths.add_transport, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void teachersList() {
        try {
            String msg_list = db_tables.getTransportList();

            teacherModelArrayList = new ArrayList<>();
            JSONObject obj = new JSONObject(msg_list);
            JSONArray jsonarray2 = obj.getJSONArray("transport_body");

            if (jsonarray2.length() > 0) {
                for (int i = 0; i < jsonarray2.length(); i++) {
                    TransportModel transportModel = new TransportModel();

                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
                    transportModel.setPhone_number(jsonobject.optString("phone_number"));
                    transportModel.setStatus(jsonobject.optString("status"));
                    transportModel.setTransport_name(jsonobject.optString("transport_name"));
                    transportModel.setName(jsonobject.optString("name"));
                    transportModel.setSchool_id(jsonobject.optString("school_id"));
                    transportModel.setCreated_by(jsonobject.optString("created_by"));
                    teacherModelArrayList.add(transportModel);

                }


                adapter = new TransportListAdapter(teacherModelArrayList, getActivity(), this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);
                adapter.setItemTouchHelperExtension(mItemTouchHelper);
            } else {
                getServiceTtansport();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getServiceTtansport() {
        try {

            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            object.put("student_id", SharedValues.getValue(getActivity(), "id"));

            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Loading...", 12, Paths.base + "view_transports", object.toString(), 1);

        } catch (Exception e) {
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
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        inflater.inflate(R.menu.add_students, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.st_add:
                //  schoolMain.addTransportForm();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMessageRow(List<TransportModel> matchesList, int position) {
        try {
            Intent maps = new Intent(getActivity(), TrasportMaps.class);
            startActivity(maps);
            getEventShowTrasport(matchesList.get(position).getTransport_id(), matchesList.get(position).getStudent_id(), matchesList.get(position).getSchool_id());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void getEventShowTrasport(String transport_id, String student_id, String school_id) {
        try {
            JSONObject object = new JSONObject();
            object.put("transport_id", transport_id);
            object.put("student_id", student_id);
            object.put("school_id", school_id);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.disableProgress();
            task.userRequest("Please Wait...", 10, Paths.view_transport_location, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void swipeToDelete(int position, List<TransportModel> classModelList) {
        if (classModelList.get(position).getCreated_by().equalsIgnoreCase("1")) {
            Toast.makeText(getActivity(), "Can't Change Status", Toast.LENGTH_SHORT).show();
        } else {
            String s_id = SharedValues.getValue(getActivity(), "school_id");
            teach_Id = classModelList.get(position).getTransport_id();
            try {
                JSONObject object = new JSONObject();
                object.put("transport_id", teach_Id);
                object.put("school_id", s_id);
                HTTPNewPost task = new HTTPNewPost(getActivity(), this);
                task.userRequest("Please Wait...", 1, Paths.delete_transport, object.toString(), 1);
                adapter.removeItem(position);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void swipeToStatus(int position, List<TransportModel> classModelList, String status) {
        // if (classModelList.get(position).getCreated_by().equalsIgnoreCase("1")) {
        String s_id = SharedValues.getValue(getActivity(), "school_id");
        teach_Id = classModelList.get(position).getTransport_id();
        t_status = status;
        try {
            JSONObject object = new JSONObject();
            object.put("status", status);
            object.put("transport_id", teach_Id);
            object.put("school_id", s_id);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Please Wait...", 2, Paths.set_transport_status, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
        // }
    }

    @Override
    public void onResponse(Object results, int requestType) {
        try {
            switch (requestType) {

                case 12:
                    JSONObject object2 = new JSONObject(results.toString());
                    if (object2.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = object2.getJSONArray("transport_details");
                        if (array.length() > 0) {
                            for (int p = 0; p < array.length(); p++) {
                                JSONObject ob = array.getJSONObject(p);
                                db_tables.addTransportList(ob.optString("transport_name"), SharedValues.getValue(getActivity(), "school_id"), ob.optString("phone_number"), ob.optString("transport_id"), ob.optString("status"), ob.optString("created_by"));
                            }
                            teachersList();
                        }
                    }
                    break;
                case 1:
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        //schoolMain.onKeyDown(4, null);
                        adapter.notifyDataSetChanged();

                        db_tables.deleteTransport(teach_Id);
                        teachersList();
                        Snackbar snackbar = Snackbar
                                .make(coordinatorLayout, "Removed Successfully", Snackbar.LENGTH_LONG);
                        snackbar.setActionTextColor(Color.YELLOW);
                        snackbar.show();
                    } else {

                    }
                    break;
                case 2:
                    JSONObject object1 = new JSONObject(results.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        adapter.notifyDataSetChanged();
                        db_tables.update_transport_status(teach_Id, t_status);
                        teachersList();
                    } else {

                    }
                    break;
                case 4:
                    JSONObject object11 = new JSONObject(results.toString());
                    if (object11.optString("statuscode").equalsIgnoreCase("200")) {
                        member_dialog.dismiss();
                        db_tables.addTransportList(tp_name, SharedValues.getValue(getActivity(), "school_id"), phone_number, tras_id, "0", "0");
                        teachersList();
                    } else {
                        member_dialog.dismiss();
                        Toast.makeText(getActivity(), object11.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 5:
                    JSONObject object13 = new JSONObject(results.toString());
                    if (object13.optString("statuscode").equalsIgnoreCase("200")) {
                        member_dialog.dismiss();
                        teachersList();
                    } else {

                    }
                    break;
                case 10:

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
