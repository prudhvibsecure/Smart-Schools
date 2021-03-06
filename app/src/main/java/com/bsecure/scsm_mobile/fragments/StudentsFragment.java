package com.bsecure.scsm_mobile.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.StudentsViewAttendence;
import com.bsecure.scsm_mobile.View_Exam_List;
import com.bsecure.scsm_mobile.adapters.ExamsListAdapter;
import com.bsecure.scsm_mobile.adapters.ParentStudentsListAdapter;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.chat.ViewChatSingle;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.graphs.GrapsMain;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.Exams;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.modules.ApprovalMessages;
import com.bsecure.scsm_mobile.modules.Calender;
import com.bsecure.scsm_mobile.modules.Gallery;
import com.bsecure.scsm_mobile.modules.ParentActivity;
import com.bsecure.scsm_mobile.modules.StudentPerformance;
import com.bsecure.scsm_mobile.modules.TimeTableView;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback_Parent;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentsFragment extends Fragment implements HttpHandler, ParentStudentsListAdapter.ContactAdapterListener, ExamsListAdapter.ContactAdapterListener {

    private DB_Tables db_tables;
    private List<StudentModel> studentModelList;
    private ParentStudentsListAdapter adapter;
    private RecyclerView mRecyclerView;
    public ItemTouchHelperExtension mItemTouchHelper;
    public ItemTouchHelperExtension.Callback mCallback;
    private View layout;
    private Dialog mDialog;
    private List<Exams> examsList = null;
    List<StudentModel> classModelList;
    String id, name, roll_no, class_id, exam_name;
    private ParentActivity schoolMain;
    String stu_id, sch_id;
    String[]stu_ids;
    String [] sids;
    ArrayList<String>school_ids;
    IntentFilter filter,approve_filter;
    SharedPreferences sp;

    public StudentsFragment() {
        // Required empty public constructor
    }
    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.schoolMain = (ParentActivity) context;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        layout = inflater.inflate(R.layout.content_main_view, container, false);
        filter = new IntentFilter("com.parent.refresh");
        approve_filter = new IntentFilter("com.scs.app.dashboard");
        getActivity().registerReceiver(mBroadcastReceiver, filter);
        getActivity().registerReceiver(mBroadcastReceiver2, approve_filter);
        school_ids = new ArrayList<>();
        db_tables = new DB_Tables(getActivity());
        db_tables.openDB();
        Toolbar toolbar = (Toolbar) layout.findViewById(R.id.toolset);
        toolbar.setVisibility(View.GONE);

        sp = getActivity().getSharedPreferences("apm", Context.MODE_PRIVATE);
        if(sp.getString("seen","").equals("1"))
        {
           getApproval(sp.getString("sid",""),sp.getString("mid",""),sp.getString("msg",""));
        }
//        toolbar.setTitle("Students List");
//        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
//        setSupportActionBar(toolbar);
        mRecyclerView = layout.findViewById(R.id.content_list);
        mCallback = new ItemTouchHelperCallback_Parent();
        mItemTouchHelper = new ItemTouchHelperExtension(mCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        stu_id = SharedValues.getValue(getActivity(), "id");
        sch_id = SharedValues.getValue(getActivity(), "school_id");

        /*stu_ids = stu_id.split("'");
        sids = sch_id.split(",");
        for(int i = 0; i< sids.length; i++)
        {
            if(!school_ids.contains(sids[i]))
            {
                school_ids.add(sids[i]);
                getStudents(sids[i]);
            }
        }
*/
        getStudents(SharedValues.getValue(getActivity(), "school_id"));

        return layout;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String student_id = intent.getStringExtra("student_id");
            db_tables.parentDeleteStudent(student_id);
            getStudentsList();
        }
    };
    private BroadcastReceiver mBroadcastReceiver2 = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String student_id = intent.getStringExtra("student_id");
            String message_id = intent.getStringExtra("message_id");
            String message = intent.getStringExtra("message");
            getApproval(student_id,message_id, message);

        }
    };

    private void getApproval(final String student_id, final String message_id, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        //builder.setMessage(message);

        builder.setMessage(Html.fromHtml("<font color='#000000'>"+message+"</font>"));

        builder.setCancelable(false);

        builder.setTitle("Message!");

        builder.setPositiveButton("ACCEPT", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int id) {

                sendStatus(message_id, student_id,1);

                dialog.cancel();
            }
        }).setNegativeButton("DECLINE", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        sendStatus(message_id, student_id,2);

                        dialog.cancel();

                    }
        }).setNeutralButton("PENDING", new DialogInterface.OnClickListener() {
                 @Override
                public void onClick(DialogInterface dialog, int which) {

                     sendStatus(message_id, student_id,0);

                     dialog.cancel();
                }
         }).show();

    }


    private void getStudents(String schoolid) {

        try {
            JSONObject object = new JSONObject();
            object.put("phone_number", SharedValues.getValue(getActivity(), "ph_number"));
            object.put("school_id", schoolid);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Processing...", 1, Paths.get_parent_students, object.toString(), 1);
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
                        JSONArray jsonarray2 = object.getJSONArray("student_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                db_tables.addstudents(jsonobject.optString("student_id"), jsonobject.optString("roll_no"), jsonobject.optString("student_name"), jsonobject.optString("status"), jsonobject.optString("class_id"), jsonobject.optString("section"), jsonobject.optString("class_name"));
                            }
                        }
                        getStudentsList();
                    }
                    break;
                case 101:
                    examsList = new ArrayList<>();
                    JSONObject object1 = new JSONObject(results.toString());
                    if (object1.optString("statuscode").equalsIgnoreCase("200")) {
                        JSONArray jsonarray2 = object1.getJSONArray("examination_details");
                        if (jsonarray2.length() > 0) {
                            for (int i = 0; i < jsonarray2.length(); i++) {
                                JSONObject jsonobject = jsonarray2.getJSONObject(i);
                                Exams exams = new Exams();
                                exams.setExam_name(jsonobject.optString("exam_name"));
                                exams.setExaminations_id(jsonobject.optString("examinations_id"));
                                examsList.add(exams);
                            }

                            getDialog(examsList);
//
                        }
                    }
                    break;
                case 102:
                    JSONObject object12 = new JSONObject(results.toString());
                    if (object12.optString("statuscode").equalsIgnoreCase("200")) {
                        Intent next = new Intent(getActivity(), GrapsMain.class);
                        next.putExtra("student_id", id);
                        next.putExtra("name", name);
                        next.putExtra("roll_no", roll_no);
                        next.putExtra("class_id", class_id);
                        next.putExtra("examination_name", exam_name);
                        startActivity(next);
                    } else {
                        Toast.makeText(getActivity(), object12.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                    }
                    break;

                case 100:

                    JSONObject obj = new JSONObject(results.toString());
                    if(obj.optString("statuscode").equalsIgnoreCase("200"))
                    {
                        Toast.makeText(schoolMain, "Status Sent Successfully", Toast.LENGTH_SHORT).show();
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("seen", "0");
                        editor.apply();
                    }
                    else
                    {
                        Toast.makeText(schoolMain, "Failed To Send.Try Again", Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void getStudentsList() {

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
                    studentModel.setClass_name(jsonobject.optString("class_name"));
                    studentModel.setStudent_id(jsonobject.optString("student_id"));
                    studentModel.setSection(jsonobject.optString("section"));
                    studentModelList.add(studentModel);

                }

                adapter = new ParentStudentsListAdapter(studentModelList, getActivity(), this);
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
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
    public void onMessageRowClicked(List<StudentModel> matchesList, int position) {
        Intent next = new Intent(getActivity(), ViewChatSingle.class);
        next.putExtra("student_id", matchesList.get(position).getStudent_id());
        next.putExtra("roll_no", matchesList.get(position).getRoll_no());
        next.putExtra("class_id", matchesList.get(position).getClass_id());
        next.putExtra("name", matchesList.get(position).getStudent_name());
        startActivity(next);
    }

    @Override
    public void swipeToSyllabus(int position, List<StudentModel> classModelList) {
        adapter.notifyDataSetChanged();
        getStudentsList();
        Intent next = new Intent(getActivity(), View_Exam_List.class);
        next.putExtra("student_id", classModelList.get(position).getStudent_id());
        next.putExtra("roll_no", classModelList.get(position).getRoll_no());
        next.putExtra("class_id", classModelList.get(position).getClass_id());
        next.putExtra("check", "1");
        startActivity(next);
    }

    @Override
    public void swipeToMarks(int position, List<StudentModel> classModelList) {
        adapter.notifyDataSetChanged();
        getStudentsList();
        Intent next = new Intent(getActivity(), View_Exam_List.class);
        next.putExtra("student_id", classModelList.get(position).getStudent_id());
        next.putExtra("roll_no", classModelList.get(position).getRoll_no());
        next.putExtra("class_id", classModelList.get(position).getClass_id());
        next.putExtra("check", "0");
        startActivity(next);
    }

    @Override
    public void swipeToAttendence(int position, List<StudentModel> classModelList) {
        adapter.notifyDataSetChanged();
        getStudentsList();
        Intent next = new Intent(getActivity(), StudentsViewAttendence.class);
        next.putExtra("student_id", classModelList.get(position).getStudent_id());
        next.putExtra("roll_no", classModelList.get(position).getRoll_no());
        next.putExtra("class_id", classModelList.get(position).getClass_id());
        startActivity(next);

    }

    @Override
    public void swipeToprofomance(int position, List<StudentModel> classModelList) {
        //this.classModelList = classModelList;
        adapter.notifyDataSetChanged();
        getStudentsList();
        id = classModelList.get(position).getStudent_id();
        name = classModelList.get(position).getStudent_name();
        roll_no = classModelList.get(position).getRoll_no();
        class_id = classModelList.get(position).getClass_id();
        //chekGraphs();
        getExams();

    }


    @Override
    public void onMessageTimeTable(int position, List<StudentModel> classModelList) {
        Intent next = new Intent(getActivity(), TimeTableView.class);
        next.putExtra("student_id", classModelList.get(position).getStudent_id());
        next.putExtra("roll_no", classModelList.get(position).getRoll_no());
        next.putExtra("class_id", classModelList.get(position).getClass_id());
        startActivity(next);
    }

    @Override
    public void swipeToMore(final int position, final List<StudentModel> classModelList, View view_list_repo_action_more) {
        PopupMenu popup = new PopupMenu(getActivity(), view_list_repo_action_more);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.parent_menu_more, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                //do your things in each of the following cases
                switch (menuItem.getItemId()) {
                    case R.id.reply:
                        adapter.notifyDataSetChanged();
                        getStudentsList();
                        id = classModelList.get(position).getStudent_id();
                        name = classModelList.get(position).getStudent_name();
                        roll_no = classModelList.get(position).getRoll_no();
                        class_id = classModelList.get(position).getClass_id();
                        //chekGraphs();

                        getExams();
                       /* exam_name = "F.A%20-%201";
                        String school_id = "33";
                        class_id ="170";
                        String roll_no = "2";*/
                        /*Intent in = new Intent(getActivity(), StudentPerformance.class);
                        in.putExtra("class_id", class_id);
                        in.putExtra("exam_name", exam_name);
                        in.putExtra("roll_no", roll_no);
                        in.putExtra("school_id", school_id);
                        startActivity(in);
*/
                        //getExams();
                        return true;
                    case R.id.replayall:
                        adapter.notifyDataSetChanged();
                        getStudentsList();
                        Intent next = new Intent(getActivity(), TimeTableView.class);
                        next.putExtra("student_id", classModelList.get(position).getStudent_id());
                        next.putExtra("roll_no", classModelList.get(position).getRoll_no());
                        next.putExtra("class_id", classModelList.get(position).getClass_id());
                        startActivity(next);
                        return true;

                    case R.id.calendar:
                        adapter.notifyDataSetChanged();
                        getStudentsList();
                        Intent cal = new Intent(getActivity(), Calender.class);
                        cal.putExtra("student_id", classModelList.get(position).getStudent_id());
                        cal.putExtra("roll_no", classModelList.get(position).getRoll_no());
                        cal.putExtra("class_id", classModelList.get(position).getClass_id());
                        startActivity(cal);
                        return true;

                    case R.id.gallery:
                        Intent in = new Intent(getActivity(), Gallery.class);
                        in.putExtra("student_id", classModelList.get(position).getStudent_id());
                        in.putExtra("class_id", classModelList.get(position).getClass_id());
                        startActivity(in);
                        return true;

                    case R.id.apmsg:
                        Intent am = new Intent(getActivity(), ApprovalMessages.class);
                        am.putExtra("student_id", classModelList.get(position).getStudent_id());
                        am.putExtra("class_id", classModelList.get(position).getClass_id());
                        startActivity(am);
                        return true;

                    default:
                        return false;
                }
            }
        });
        popup.show();
    }

    private void chekGraphs(String ex_name) {
        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            object.put("class_id", class_id);
            object.put("examination_name", ex_name);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Processing...", 102, Paths.check_prformance, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getExams() {
        try {
            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            object.put("class_id", class_id);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Processing...", 101, Paths.get_examinations, object.toString(), 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void getDialog(List<Exams> examsList) {
        mDialog = new Dialog(getActivity());
        mDialog.setContentView(R.layout.content_main_view);
        Toolbar toolbar = mDialog.findViewById(R.id.toolset);
        toolbar.setTitle("Exams");
        toolbar.setTitleTextColor(Color.WHITE);
        mDialog.show();
        RecyclerView recyclerView = mDialog.findViewById(R.id.content_list);
        ExamsListAdapter adapter = new ExamsListAdapter(examsList, getActivity(), this);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onExamRowClicked(List<Exams> matchesList, int position) {
        mDialog.dismiss();
        exam_name = matchesList.get(position).getExam_name();
       // chekGraphs(matchesList.get(position).getExam_name());
        String school_id = SharedValues.getValue(getActivity(), "school_id");
        Intent in = new Intent(getActivity(), StudentPerformance.class);
        in.putExtra("class_id", class_id);
        in.putExtra("exam_name", exam_name);
        in.putExtra("roll_no", roll_no);
        in.putExtra("school_id", school_id);
        startActivity(in);
    }

    @Override
    public void onDestroy() {

        try{
            getActivity().unregisterReceiver(mBroadcastReceiver);
            getActivity().unregisterReceiver(mBroadcastReceiver2);

        }catch (Exception e){
            e.printStackTrace();
        }
        super.onDestroy();
    }

    public void sendStatus(String message_id, String student_id, int status)
    {
        try {

            JSONObject object = new JSONObject();

            object.put("message_id", message_id);

            object.put("student_id", student_id);

            object.put("domain", ContentValues.DOMAIN);

            object.put("status", status);

            HTTPNewPost task = new HTTPNewPost(getActivity(), this);

            task.userRequest("Processing...", 100, Paths.student_approval_message, object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
