package com.bsecure.scsm_mobile.modules;

import android.app.ProgressDialog;
import android.content.Intent;
import android.media.Image;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.adapters.GalleryAdapter;
import com.bsecure.scsm_mobile.adapters.GalleryListAdapter;
import com.bsecure.scsm_mobile.callbacks.ClickListener;
import com.bsecure.scsm_mobile.callbacks.HttpHandler;
import com.bsecure.scsm_mobile.common.ContentValues;
import com.bsecure.scsm_mobile.common.Paths;
import com.bsecure.scsm_mobile.fragments.SlideshowDialogFragment;
import com.bsecure.scsm_mobile.https.HTTPNewPost;
import com.bsecure.scsm_mobile.models.GalleryModel;
import com.bsecure.scsm_mobile.utils.SharedValues;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ViewGallery extends AppCompatActivity implements HttpHandler {

    private String TAG = ViewGallery.class.getSimpleName();
    private ArrayList<GalleryModel> images;
    private ProgressDialog pDialog;
    private GalleryAdapter mAdapter;
    private RecyclerView recyclerView;
    String school_id, class_id, student_id, gid, extra;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_gallery);


        Intent data = getIntent();
        if(data!= null)
        {
            student_id = data.getStringExtra("student_id");
            class_id = data.getStringExtra("class_id");
            gid = data.getStringExtra("gid");
            extra = data.getStringExtra("extra");
        }
        if(extra.equalsIgnoreCase("1"))
        {
           student_id = "";
           class_id = "";
        }
        else if(extra.equalsIgnoreCase("2"))
        {
            student_id = "";
        }
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Images");//Organization Head
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        pDialog = new ProgressDialog(this);
        images = new ArrayList<>();
        mAdapter = new GalleryAdapter(getApplicationContext(), images);
        getImages();

         recyclerView.addOnItemTouchListener(new GalleryAdapter.RecyclerTouchListener(getApplicationContext(), recyclerView, new GalleryAdapter.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Bundle bundle = new Bundle();
                bundle.putSerializable("images", images);
                bundle.putInt("position", position);

                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                SlideshowDialogFragment newFragment = SlideshowDialogFragment.newInstance();
                newFragment.setArguments(bundle);
                newFragment.show(ft, "slideshow");
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void getImages() {
        try {

            JSONObject object = new JSONObject();
            object.put("school_id", SharedValues.getValue(this, "school_id"));
            object.put("student_id", student_id);
            object.put("class_id", class_id);
            object.put("photo_gallery_title_id", gid);
            object.put("domain", ContentValues.DOMAIN);
            HTTPNewPost task = new HTTPNewPost(this, this);
            task.userRequest("Loading...", 12, Paths.view_photo_gallery_mobile, object.toString(), 1);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResponse(Object results, int requestType) {

        try {
            switch (requestType) {
                case 12:

                    try {
                        images = new ArrayList<>();
                        JSONObject object = new JSONObject(results.toString());
                        JSONArray array = object.getJSONArray("photo_gallery_details");
                        if (array.length() > 0) {
                            for (int i = 0; i < array.length(); i++) {
                                GalleryModel galleryModel = new GalleryModel();
                                JSONObject jsonobject = array.getJSONObject(i);
                                galleryModel.setGid(jsonobject.optString("photo_gallery_id"));
                                galleryModel.setEname(jsonobject.optString("photos"));
                                images.add(galleryModel);
                            }

                            mAdapter = new GalleryAdapter(this, images);
                            RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getApplicationContext(), 2);
                            recyclerView.setLayoutManager(mLayoutManager);
                            recyclerView.setItemAnimator(new DefaultItemAnimator());
                            recyclerView.setAdapter(mAdapter);
                        } else {
                            Toast.makeText(this, object.optString("statusdescription"), Toast.LENGTH_SHORT).show();
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
