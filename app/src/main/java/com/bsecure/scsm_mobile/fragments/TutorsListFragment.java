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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.StudentsListAdapter;
import com.bsecure.scsm_mobile.adapters.TransportListAdapter_New;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.models.TransportModel;
import com.bsecure.scsm_mobile.modules.ParentActivity;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback_Trns;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback_Tut_stu;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TutorsListFragment extends Fragment implements TransportListAdapter_New.ContactAdapterListener, HttpHandler, StudentsListAdapter.ContactAdapterListener {

    private ParentActivity schoolMain;
    View view_layout;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout coordinatorLayout;
    public ItemTouchHelperExtension mItemTouchHelper;
    public ItemTouchHelperExtension.Callback mCallback;
    private DB_Tables db_tables;
    ArrayList<TransportModel> teacherModelArrayList;
    private TransportListAdapter_New adapter;
    private String teach_Id, t_status, student_id;
    private Dialog member_dialog;
    private Spinner m_spinner;
    private List<StudentModel> studentModelList;
    private List<StudentModel> selectList;
    String tras_id = null, tp_name, phone_number;
    StudentsListAdapter studentsAdapter;
    int id = 0;
    String id_st = "";
    String[] stu_ids;

    RecyclerView students;
    private ArrayList<String> st_ids;
    private ArrayList<String> st_names = new ArrayList<>();
    private IntentFilter filter;
    String match_ids[];
    List<String> stids;
    TextView tv_nodata;

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.schoolMain = (ParentActivity) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view_layout = inflater.inflate(R.layout.content_main_view, null);

        filter = new IntentFilter("com.parenttutor.refresh");
        getActivity().registerReceiver(mBroadcastReceiver, filter);
        db_tables = new DB_Tables(getActivity());
        db_tables.openDB();
        tv_nodata = view_layout.findViewById(R.id.no_data);
        view_layout.findViewById(R.id.toolset).setVisibility(View.GONE);
        view_layout.findViewById(R.id.fab).setVisibility(View.VISIBLE);
        view_layout.findViewById(R.id.fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAddTutors();
            }
        });
        mRecyclerView = view_layout.findViewById(R.id.content_list);
        teachersList();
        mCallback = new ItemTouchHelperCallback_Tut_stu();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);
        return view_layout;
    }

    @Override
    public void onDestroy() {
        try {
            getActivity().unregisterReceiver(mBroadcastReceiver);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onDestroy();
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String student_id = intent.getStringExtra("student_id");
            String tutor_id = intent.getStringExtra("tutor_id");

            String sids = db_tables.getTutorStudents(tutor_id);
            if(sids.length() != 0) {
                sids = sids.substring(1, sids.length() - 1);
                stu_ids = sids.split(",");
                ArrayList<String> mm_ids = new ArrayList<>();
                StringBuilder builder = new StringBuilder();
                for (String id : stu_ids) {
                    mm_ids.add(id);
                    if (student_id.equalsIgnoreCase(id)) {
//                    builder.append("," +id);
                        mm_ids.remove(id);
                    }
                }
                for (String f_ids : mm_ids) {
                    builder.append("," + f_ids);
                }
                if (mm_ids.size() > 0) {
                    String fids = builder.toString();
                    fids = fids.substring(1);

                    db_tables.updateTutorStudents(tutor_id, "[" + fids + "]");
                } else {
                    db_tables.deleteTutor(tutor_id);
                }

                if (adapter != null) {
                    adapter.clear();
                    adapter.notifyDataSetChanged();
                    teachersList();
                }
            }
        }
    };

    private void getAddTutors() {
        member_dialog = new Dialog(getActivity(), R.style.MyAlertDialogStyle);
        member_dialog.setContentView(R.layout.add_tutor);

        member_dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        member_dialog.setCancelable(true);
        member_dialog.setCanceledOnTouchOutside(true);
        ((EditText) member_dialog.findViewById(R.id.ad_t_ts)).setHint("Tutor Name");
        m_spinner = (Spinner) member_dialog.findViewById(R.id.st_spinner);
        m_spinner.setVisibility(View.GONE);
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
        member_dialog.findViewById(R.id.add_cls).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //st_ids= new ArrayList<>();
                id = 1;
                addTP();
            }
        });
        member_dialog.getWindow().setGravity(Gravity.BOTTOM);
        member_dialog.show();
    }

    private void getStudentsList(Spinner m_spinner) {
        try {
            st_ids = new ArrayList<>();
            String msg_list = db_tables.getstudentsList();
            studentModelList = new ArrayList<>();
            selectList = new ArrayList<>();
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
                    students = (RecyclerView) member_dialog.findViewById(R.id.students);

                }
                studentsAdapter = new StudentsListAdapter(studentModelList, getActivity(), this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                students.setLayoutManager(mLayoutManager);
                students.setAdapter(studentsAdapter);
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
            // tras_id = String.valueOf(System.currentTimeMillis());
            tp_name = ((EditText) member_dialog.findViewById(R.id.ad_t_ts)).getText().toString().trim();
            if (tp_name.length() == 0) {
                Toast.makeText(getActivity(), "Please Enter Tutor Name", Toast.LENGTH_SHORT).show();
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
            if (st_ids.size() == 0) {
                Toast.makeText(getActivity(), "Please Assign Atleast One Student", Toast.LENGTH_SHORT).show();
                return;
            }
            StringBuilder builder = new StringBuilder();
            for (String s : st_ids) {
                builder.append("," + s.trim());
            }
            student_id = builder.substring(1);
            JSONObject object = new JSONObject();
            //  object.put("tutor_id", tras_id);
            object.put("tutor_name", tp_name.trim());
            object.put("phone_number", phone_number);
            object.put("student_id", student_id.trim());
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Please Wait...", 4, Paths.add_tutor, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    private void updateaddTP(String tr_id) {
//        try {
//            //ss String tras_id = String.valueOf(System.currentTimeMillis());
//            String tp_name = ((EditText) member_dialog.findViewById(R.id.ad_t_ts)).getText().toString();
//            if (tp_name.length() == 0) {
//                Toast.makeText(getActivity(), "Please Enter Tutor Name", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            String phone_number = ((EditText) member_dialog.findViewById(R.id.ad_t_ts_n)).getText().toString();
//            if (phone_number.length() == 0) {
//                Toast.makeText(getActivity(), "Please Enter Mobile Number", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (phone_number.length() < 10) {
//                Toast.makeText(getActivity(), "Please Enter Valid Mobile Number", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            if (st_ids.size() == 0) {
//                Toast.makeText(getActivity(), "Please Assign Atleast One Student", Toast.LENGTH_SHORT).show();
//                return;
//            }
//            StringBuilder builder = new StringBuilder();
//            for (String s : st_ids) {
//                builder.append("," + s);
//            }
//            student_id = builder.substring(1);
//            JSONObject object = new JSONObject();
//            object.put("tutor_id", tr_id);
//            object.put("tutor_name", tp_name);
//            object.put("phone_number", phone_number);
//            object.put("student_id", student_id);
//            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
//            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
//            db_tables.updateTutorsList(tr_id, tp_name, SharedValues.getValue(getActivity(), "school_id"), phone_number, st_ids.toString());
//            task.userRequest("Please Wait...", 5, Paths.edit_tutor, object.toString(), 1);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }

    private void teachersList() {
        try {
            String msg_list = db_tables.getTutorsList();

            teacherModelArrayList = new ArrayList<>();
            JSONObject obj = new JSONObject(msg_list);
            JSONArray jsonarray2 = obj.getJSONArray("tutor_body");

            if (jsonarray2.length() > 0) {
                for (int i = 0; i < jsonarray2.length(); i++) {
                    TransportModel transportModel = new TransportModel();

                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
                    transportModel.setPhone_number(jsonobject.optString("phone_number"));
                    transportModel.setStatus(jsonobject.optString("tutor_status"));
                    transportModel.setTransport_name(jsonobject.optString("tutor_name"));
                    transportModel.setStudent_id(jsonobject.optString("student_id"));
                    transportModel.setSchool_id(jsonobject.optString("school_id"));
                    transportModel.setTransport_id(jsonobject.optString("tutor_id"));
                    teacherModelArrayList.add(transportModel);

                }


                adapter = new TransportListAdapter_New(teacherModelArrayList, getActivity(), this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);
                adapter.setItemTouchHelperExtension(mItemTouchHelper);
            } else {
                getServiceTutorls();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getServiceTutorls() {
        try {

            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            object.put("student_id", SharedValues.getValue(getActivity(), "id"));
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Loading...", 12, Paths.base + "view_tutors_v1", object.toString(), 1);

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
        edittutorsForm(matchesList, position);
        tras_id = matchesList.get(position).getTransport_id();

    }

    private void edittutorsForm(final List<TransportModel> matchesList, final int position) {

        member_dialog = new Dialog(getActivity(), R.style.MyAlertDialogStyle);
        member_dialog.setContentView(R.layout.add_tutor);
        member_dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        member_dialog.setCancelable(true);
        member_dialog.setCanceledOnTouchOutside(true);
        member_dialog.getWindow().setGravity(Gravity.BOTTOM);
        ((EditText) member_dialog.findViewById(R.id.ad_t_ts)).setHint("Tutor Name");
        ((EditText) member_dialog.findViewById(R.id.ad_t_ts)).setText(matchesList.get(position).getTransport_name());
        ((EditText) member_dialog.findViewById(R.id.ad_t_ts_n)).setText(matchesList.get(position).getPhone_number());
        ((EditText) member_dialog.findViewById(R.id.ad_t_ts_n)).setEnabled(false);
        String student_ids = matchesList.get(position).getStudent_id();
        //   tras_id = matchesList.get(position).getTransport_id();
        student_ids = student_ids.substring(1, student_ids.length() - 1);
        if (!TextUtils.isEmpty(student_ids)) {
            match_ids = student_ids.split(",");
        }
        m_spinner = (Spinner) member_dialog.findViewById(R.id.st_spinner);
        m_spinner.setVisibility(View.GONE);
        try {
            st_ids = new ArrayList<>();
            String msg_list = db_tables.getstudentsList();
            studentModelList = new ArrayList<>();
            selectList = new ArrayList<>();
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
                    for (String ids : match_ids) {
                        if (ids.trim().equalsIgnoreCase(jsonobject.optString("student_id"))) {
                            studentModel.setSelected(true);
                            st_ids.add(ids.trim());
                        }
                    }
                    studentModelList.add(studentModel);
                }
                students = (RecyclerView) member_dialog.findViewById(R.id.students);
                studentsAdapter = new StudentsListAdapter(studentModelList, getActivity(), this);
                RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity());
                students.setLayoutManager(mLayoutManager);
                students.setAdapter(studentsAdapter);
                ArrayAdapter<StudentModel> dataAdapter = new ArrayAdapter<StudentModel>(getContext(),
                        android.R.layout.simple_spinner_item, studentModelList);
                dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                this.m_spinner.setAdapter(dataAdapter);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
//        getStudentsList(m_spinner);
//        m_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int mpos, long l) {
//                StudentModel studentModel = null;
//                studentModel = (StudentModel) m_spinner.getSelectedItem();
//                student_id = studentModel.getStudent_id();
//                if (student_ids.equalsIgnoreCase(student_id)) {
//                    m_spinner.setSelection(mpos);
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
        member_dialog.findViewById(R.id.update_ex).setVisibility(View.VISIBLE);
        member_dialog.findViewById(R.id.add_cls).setVisibility(View.GONE);
        member_dialog.findViewById(R.id.update_ex).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                id = 2;
                addTP();
                //updateaddTP(matchesList.get(position).getTransport_id());
            }
        });
        member_dialog.getWindow().setGravity(Gravity.BOTTOM);
        member_dialog.show();
    }


    @Override
    public void swipeToDelete(int position, List<TransportModel> classModelList) {
        String s_id = SharedValues.getValue(getActivity(), "school_id");
        String sids = classModelList.get(position).getStudent_id();
        teach_Id = classModelList.get(position).getTransport_id();
        try {
            JSONObject object = new JSONObject();
            object.put("tutor_id", teach_Id);
            object.put("school_id", s_id);
            String stu_ids = sids.substring(1, sids.length() - 1);
            object.put("student_id", stu_ids);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Please Wait...", 1, Paths.delete_tutor, object.toString(), 1);
            adapter.removeItem(position);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void swipeToStatus(int position, List<TransportModel> classModelList, String status) {
        String s_id = SharedValues.getValue(getActivity(), "school_id");
        teach_Id = classModelList.get(position).getTransport_id();
        t_status = status;
        try {
            JSONObject object = new JSONObject();
            object.put("status", status);
            object.put("tutor_id", teach_Id);
            object.put("school_id", s_id);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Please Wait...", 2, Paths.set_tutor_status, object.toString(), 1);
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
                        //schoolMain.onKeyDown(4, null);
                        adapter.notifyDataSetChanged();

                        db_tables.deleteTutor(teach_Id);
                        Toast.makeText(schoolMain, "Tutor Deleted Successfully", Toast.LENGTH_SHORT).show();
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
                        db_tables.update_tutors_status(teach_Id, t_status);
                        if (t_status.equals("0")) {
                            Toast.makeText(getActivity(), "Tutor Activated Successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "Tutor Inactivated Successfully", Toast.LENGTH_SHORT).show();
                        }
                        teachersList();
                    } else {

                    }
                    break;
                case 4:
                    JSONObject object11 = new JSONObject(results.toString());
                    if (object11.optString("statuscode").equalsIgnoreCase("200")) {
                        member_dialog.dismiss();
                        String tut_Id = db_tables.getTutorId(object11.optString("tutor_id"));
                        if (TextUtils.isEmpty(tut_Id)) {
                            Toast.makeText(schoolMain, "Tutor Added Successfully", Toast.LENGTH_SHORT).show();
                            db_tables.addTutorsList(tp_name, SharedValues.getValue(getActivity(), "school_id"), phone_number, "[" + student_id + "]", object11.optString("tutor_id"), "0");
                            tv_nodata.setVisibility(View.GONE);
                        } else {
                            Toast.makeText(schoolMain, "Tutor Updated Successfully", Toast.LENGTH_SHORT).show();
                            db_tables.updateTutorsList(object11.optString("tutor_id"), tp_name, SharedValues.getValue(getActivity(), "school_id"), phone_number, "[" + student_id + "]");
                            tv_nodata.setVisibility(View.GONE);
                        }

//                        if(id == 1)
//                        {
//                            Toast.makeText(schoolMain, "Tutor Added Successfully", Toast.LENGTH_SHORT).show();
//                        }
//                        else
//                        {
//                            Toast.makeText(schoolMain, "Tutor Updated Successfully", Toast.LENGTH_SHORT).show();
//                        }

                        teachersList();
                    } else {

                        member_dialog.dismiss();
                        Toast.makeText(getActivity(), object11.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 12:
                    JSONObject object2 = new JSONObject(results.toString());
                    if (object2.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray array = object2.getJSONArray("tutor_details");
                        if (array.length() > 0) {
                            for (int p = 0; p < array.length(); p++) {
                                JSONObject ob = array.getJSONObject(p);
                                db_tables.addTutorsList(ob.optString("tutor_name"), SharedValues.getValue(getActivity(), "school_id"), ob.optString("phone_number"), "[" + ob.optString("student_id") + "]", ob.optString("tutor_id"), ob.optString("status"));
                                tv_nodata.setVisibility(View.GONE);
                            }
                            teachersList();
                        }
                        else
                        {
                            tv_nodata.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.GONE);
                        }
                    }
                    else
                    {
                        tv_nodata.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                    break;
                case 5:
                    JSONObject object12 = new JSONObject(results.toString());
                    if (object12.optString("statuscode").equalsIgnoreCase("200")) {
                        member_dialog.dismiss();
                        // db_tables.addTutorsList(tp_name, SharedValues.getValue(getActivity(), "school_id"), phone_number, st_ids.toString(), tras_id, "0");
                        Toast.makeText(getActivity(), "Tutor Details Updated Successfully", Toast.LENGTH_SHORT).show();
                        teachersList();
                    } else {

                    }
                    break;
                case 121:
                    JSONObject object121 = new JSONObject(results.toString());
                    if (object121.optString("statuscode").equalsIgnoreCase("200")) {
                        //db_tables.updateTutorsList();
                        //member_dialog.dismiss();
                        if (st_ids.size() > 0) {
                            StringBuilder builder = new StringBuilder();
                            for (String s : st_ids) {
                                builder.append("," + s.trim());
                            }
                            student_id = builder.substring(1);
                            db_tables.updateTutorsList(tras_id, tp_name, SharedValues.getValue(getActivity(), "school_id"), phone_number, "[" + student_id.trim() + "]");
                        } else {
                            db_tables.deleteTutor(tras_id);
                            member_dialog.dismiss();
                            //refresh list
                            adapter.clear();
                            teachersList();
                        }
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
    public void onRowClicked(List<StudentModel> matchesList, boolean value, CheckBox chk_name, int position) {
        String id = studentModelList.get(position).getStudent_id();
        if (chk_name.isChecked()) {
            st_ids.add(id);
            // st_names.add(id);
            chk_name.setBackground(getResources().getDrawable(R.mipmap.ic_check));
        } else {
            st_ids.remove(id);
            deleteStudentTutor(id);

            // st_names.remove(id);
            chk_name.setBackground(getResources().getDrawable(R.mipmap.delete));
        }
    }

    @Override
    public void onRowDelete(List<StudentModel> matchesList, boolean value, CheckBox chk_name, int position, LinearLayout view_list_main_content) {
        id_st = studentModelList.get(position).getStudent_id();
        view_list_main_content.setVisibility(View.VISIBLE);
        chk_name.setBackground(getResources().getDrawable(R.mipmap.delete));

        for (String st_id : st_ids) {
            if (id_st.equalsIgnoreCase(st_id.trim())) {
                st_ids.remove(id_st);
                deleteStudentTutor(id_st);

            }
        }

    }

    private void deleteStudentTutor(String id) {
        try {

            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            object.put("student_id", id);
            object.put("tutor_id", tras_id);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.disableProgress();
            task.userRequest("Loading...", 121, Paths.base + "delete_tutor_student_v1", object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
