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
import com.bsecure.scsm_mobile.adapters.GalleryListAdapter;
import com.bsecure.scsm_mobile.adapters.StudentsListAdapter;
import com.bsecure.scsm_mobile.adapters.TransportListAdapter_New;
import com.bsecure.scsm_mobile.callbacks.ClickListener;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.GalleryModel;
import com.bsecure.scsm_mobile.models.StudentModel;
import com.bsecure.scsm_mobile.models.TransportModel;
import com.bsecure.scsm_mobile.modules.Gallery;
import com.bsecure.scsm_mobile.modules.ParentActivity;
import com.bsecure.scsm_mobile.modules.ViewGallery;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperCallback_Tut_stu;
import com.bsecure.scsm_mobile.recyclertouch.ItemTouchHelperExtension;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class StudentGalleryFragment extends Fragment implements HttpHandler {

    private Gallery schoolMain;
    View view_layout;
    private RecyclerView mRecyclerView;
    private CoordinatorLayout coordinatorLayout;
    ArrayList<GalleryModel> galleryList;
    private GalleryListAdapter adapter;
    private String teach_Id, t_status, student_id, school_id, class_id;
    private Dialog member_dialog;
    TextView nodata;
    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        this.schoolMain = (Gallery) context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view_layout = inflater.inflate(R.layout.gallery_fragment, null);

        nodata = view_layout.findViewById(R.id.nodata);
        mRecyclerView = view_layout.findViewById(R.id.list);
        student_id = getActivity().getIntent().getStringExtra("student_id");
        class_id = getActivity().getIntent().getStringExtra("class_id");
        getList();
        return view_layout;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void getList() {
        try {

            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(getActivity(), "school_id"));
            object.put("student_id", student_id);
            object.put("class_id", class_id);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(getActivity(), this);
            task.userRequest("Loading...", 12, Paths.view_photo_gallery_title, object.toString(), 1);

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
    public void onResponse(Object results, int requestType) {
        try {
            switch (requestType) {
                case 12:

                    try {
                        galleryList = new ArrayList<>();
                        JSONObject object = new JSONObject(results.toString());
                        JSONArray array = object.getJSONArray("photo_gallery_title_details");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                GalleryModel galleryModel = new GalleryModel();
                                JSONObject jsonobject = array.getJSONObject(i);
                                galleryModel.setGid(jsonobject.optString("photo_gallery_title_id"));
                                galleryModel.setEname(jsonobject.optString("title"));
                                galleryList.add(galleryModel);
                            }

                            adapter = new GalleryListAdapter(getActivity(), galleryList, new ClickListener() {
                                @Override
                                public void OnRowClicked(int position, View view) {
                                    Intent in = new Intent(getActivity(), ViewGallery.class);
                                    in.putExtra("student_id", student_id);
                                    in.putExtra("class_id", class_id);
                                    in.putExtra("gid", galleryList.get(position).getGid());
                                    startActivity(in);
                                }
                            });
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
                            mRecyclerView.setLayoutManager(linearLayoutManager);
                            mRecyclerView.setAdapter(adapter);
                        } else {
                            Toast.makeText(getActivity(), object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
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

}
