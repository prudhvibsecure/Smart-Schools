package com.bsecure.scsm_mobile.fragments;

import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
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
import com.bsecure.scsm_mobile.adapters.StudentsListAdapter;
import com.bsecure.scsm_mobile.adapters.TransportListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.ContentValues;
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
import java.util.concurrent.CancellationException;

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
    private String tras_id = null, tp_name, phone_number,school_id;
    StringBuilder builder = new StringBuilder();

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
        getServiceTtansport();
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
        m_spinner.setPrompt("Select Student");
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

                    final String s = jsonobject.optString("student_id");
                    builder.append("," +s);

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
            if (phone_number.length() == 0) {
                Toast.makeText(getActivity(), "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone_number.length() < 10) {
                Toast.makeText(getActivity(), "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                return;
            }
            StudentModel selectedCity = (StudentModel) m_spinner.getSelectedItem();

            if (selectedCity == null) {
                Toast.makeText(getActivity(), " Please Select Student Name", Toast.LENGTH_SHORT).show();
                return;
            }
            student_id = selectedCity.getStudent_id();
            if (student_id == null) {
                Toast.makeText(getActivity(), " Please Select Student Name", Toast.LENGTH_SHORT).show();
                return;
            }
            JSONObject object = new JSONObject();
            object.put("transport_id", tras_id);
            object.put("transport_name", tp_name);
            object.put("phone_number", phone_number);
            object.put("student_id", SharedValues.getValue(getActivity(),"student_id"));
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            object.put("domain", ContentValues.DOMAIN);
            //object.put("created_by", "0");

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
                    transportModel.setStudent_id(jsonobject.optString("student_id"));
                    transportModel.setTransport_id(jsonobject.optString("transport_id"));
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
            object.put("domain", ContentValues.DOMAIN);
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
            tras_id=matchesList.get(position).getTransport_id();
            school_id=matchesList.get(position).getSchool_id();
            String status = matchesList.get(position).getStatus();
            if(status.equalsIgnoreCase("0")) {
                if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) ==
                        PackageManager.PERMISSION_GRANTED) {
                    passData();
                } else {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                                102);
                    }
                }
            }
            else
            {
                Toast.makeText(schoolMain, "Transport is Inactive", Toast.LENGTH_SHORT).show();
            }

            // getEventShowTrasport(matchesList.get(position).getTransport_id(), matchesList.get(position).getStudent_id(), matchesList.get(position).getSchool_id());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void passData() {
        Intent maps = new Intent(getActivity(), TrasportMaps.class);
        maps.putExtra("transport_id", tras_id);
        //  maps.putExtra("student_id",matchesList.get(position).getStudent_id());//check its comming null
        maps.putExtra("school_id", school_id);
        maps.putExtra("p_con", "0");
        startActivity(maps);
    }

//    private void getEventShowTrasport(String transport_id, String student_id, String school_id) {
////        try {
////            JSONObject object = new JSONObject();
////            object.put("transport_id", transport_id);
////            object.put("student_id", student_id);
////            object.put("school_id", school_id);
////            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
////            task.disableProgress();
////            task.userRequest("Please Wait...", 10, Paths.view_transport_location, object.toString(), 1);
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
////    }

    @Override
    public void swipeToDelete(int position, List<TransportModel> classModelList) {
        if (classModelList.get(position).getCreated_by().equalsIgnoreCase("1")) {
            Toast toast = Toast.makeText(getActivity(),"Cannot Delete School Transport", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            String s_id = SharedValues.getValue(getActivity(), "school_id");
            String stu_id = classModelList.get(position).getStudent_id();
            teach_Id = classModelList.get(position).getTransport_id();
            try {
                JSONObject object = new JSONObject();
                object.put("transport_id", teach_Id);
                object.put("school_id", s_id);
                object.put("student_id", stu_id);
                object.put("domain", ContentValues.DOMAIN);
                HTTPNewPost task = new HTTPNewPost(getActivity(), this);
                task.userRequest("Please Wait...", 1, Paths.delete_transport, object.toString(), 1);
                //adapter.removeItem(position);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    public void swipeToStatus(int position, List<TransportModel> classModelList, String status) {
        if (classModelList.get(position).getCreated_by().equalsIgnoreCase("1")) {
            Toast toast = Toast.makeText(getActivity(),"Cannot Inactivate Transport", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        }
        else
        {
            String s_id = SharedValues.getValue(getActivity(), "school_id");
            teach_Id = classModelList.get(position).getTransport_id();
            t_status = status;
            try {
                JSONObject object = new JSONObject();
                object.put("status", status);
                object.put("transport_id", teach_Id);
                object.put("school_id", s_id);
                object.put("domain", ContentValues.DOMAIN);
                HTTPNewPost task = new HTTPNewPost(getActivity(), this);
                task.userRequest("Please Wait...", 2, Paths.set_transport_status, object.toString(), 1);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
                    else {
                        Toast.makeText(getActivity(), "No Data Found", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 1:
                    JSONObject object = new JSONObject(results.toString());
                    if (object.optString("statuscode").equalsIgnoreCase("200")) {
                        //schoolMain.onKeyDown(4, null);
                        adapter.notifyDataSetChanged();
                        adapter.clear();
                        db_tables.deleteTransport(teach_Id);
                        Toast.makeText(getActivity(), object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(schoolMain, "Status Updated Successfully", Toast.LENGTH_SHORT).show();

                        teachersList();
                    } else {
                        Toast.makeText(schoolMain, object1.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 4:
                    JSONObject object11 = new JSONObject(results.toString());
                    if (object11.optString("statuscode").equalsIgnoreCase("200")) {
                        member_dialog.dismiss();
                        db_tables.addTransportList(tp_name, SharedValues.getValue(getActivity(), "school_id"), phone_number, tras_id, "0", "0");
                        Toast.makeText(getActivity(), "Transport Added Successfully", Toast.LENGTH_SHORT).show();
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
                case 7:
                    JSONObject object111 = new JSONObject(results.toString());
                    if (object111.optString("statuscode").equalsIgnoreCase("200")) {
                        member_dialog.dismiss();
                        //db_tables.updateTransportList(tras_id, tp_name, phone_number, SharedValues.getValue(getActivity(), "school_id"));
                        db_tables.updateTransportList(object111.optString("transport_id"), object111.optString("transport_name"), object111.optString("phone_number"), SharedValues.getValue(getActivity(), "school_id"));
                        Toast.makeText(getActivity(), "Transport Updated Successfully", Toast.LENGTH_SHORT).show();
                        teachersList();
                    } else {
                        member_dialog.dismiss();
                        Toast.makeText(getActivity(), object111.optString("statusdescription"), Toast.LENGTH_SHORT).show();
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
    public void swipeToUpdate(final int position, final List<TransportModel> matchesList) {
        if (matchesList.get(position).getCreated_by().equalsIgnoreCase("1")) {
            Toast toast =  Toast.makeText(getActivity(),"Cannot Edit School Transport", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.CENTER, 0, 0);
            toast.show();
        } else {
            tras_id = matchesList.get(position).getTransport_id();
            member_dialog = new Dialog(getActivity(), R.style.MyAlertDialogStyle);
            member_dialog.setContentView(R.layout.add_transport);
            member_dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
            member_dialog.setCancelable(true);
            member_dialog.setCanceledOnTouchOutside(true);
            member_dialog.getWindow().setGravity(Gravity.BOTTOM);
            ((EditText) member_dialog.findViewById(R.id.ad_t_ts)).setHint("Transport Name");
            ((EditText) member_dialog.findViewById(R.id.ad_t_ts)).setText(matchesList.get(position).getTransport_name());
            ((EditText) member_dialog.findViewById(R.id.ad_t_ts_n)).setText(matchesList.get(position).getPhone_number());
            ((EditText) member_dialog.findViewById(R.id.ad_t_ts_n)).setEnabled(false);
            final String student_ids = matchesList.get(position).getStudent_id();
            m_spinner = (Spinner) member_dialog.findViewById(R.id.st_spinner);
            m_spinner.setEnabled(false);
            m_spinner.setVisibility(View.GONE);
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
            getStudentsList(m_spinner);
            m_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> adapterView, View view, int mpos, long l) {
                    StudentModel studentModel = null;
                    studentModel = (StudentModel) m_spinner.getSelectedItem();
                    student_id = studentModel.getStudent_id();
                    if (student_ids.equalsIgnoreCase(student_id)) {
                        m_spinner.setSelection(mpos);
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> adapterView) {

                }
            });
            member_dialog.findViewById(R.id.update_ex).setVisibility(View.VISIBLE);
            member_dialog.findViewById(R.id.add_cls).setVisibility(View.GONE);
            member_dialog.findViewById(R.id.update_ex).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateaddTP(matchesList.get(position).getTransport_id());
                }
            });
            member_dialog.getWindow().setGravity(Gravity.BOTTOM);
            member_dialog.show();
        }
    }

    private void updateaddTP(String tr_id) {
        try {
            String tp_name = ((EditText) member_dialog.findViewById(R.id.ad_t_ts)).getText().toString();
            if (tp_name.length() == 0) {
                Toast.makeText(getActivity(), "Please Enter Tutor Name", Toast.LENGTH_SHORT).show();
                return;
            }
            String phone_number = ((EditText) member_dialog.findViewById(R.id.ad_t_ts_n)).getText().toString();
            if (phone_number.length() == 0) {
                Toast.makeText(getActivity(), "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
                return;
            }
            if (phone_number.length() < 10) {
                Toast.makeText(getActivity(), "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
                return;
            }
//            if (st_ids.size() == 0) {
//                Toast.makeText(getActivity(), "Please Assign Atleast One Student", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            StringBuilder builder = new StringBuilder();
//            for (String s : st_ids) {
//                builder.append("," + s);
//            }
            //   student_id = builder.substring(1);
            JSONObject object = new JSONObject();
            object.put("transport_id", tr_id);
            object.put("transport_name", tp_name);
            object.put("phone_number", phone_number);
            object.put("student_id", builder.substring(1));
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Please Wait...", 7, Paths.edit_transport, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 102
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
        passData();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

    }
}

