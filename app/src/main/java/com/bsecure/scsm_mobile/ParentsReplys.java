package com.bsecure.scsm_mobile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.bsecure.scsm_mobile.adapters.ParentMessageListAdapter;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.models.MessageObject;
import com.bsecure.scsm_mobile.utils.ContactUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ParentsReplys extends AppCompatActivity implements ParentMessageListAdapter.ChatAdapterListener {

    private RecyclerView mRecyclerView;
    private String class_id;
    private ParentMessageListAdapter adapter;
    private DB_Tables db_tables;
    ArrayList<MessageObject> messageList;
    LinearLayoutManager linearLayoutManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_main_view);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolset);
        toolbar.setTitle("Parent Reply");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        db_tables = new DB_Tables(this);
        db_tables.openDB();
        Intent getData = getIntent();
        if (getData != null) {
            class_id = getData.getStringExtra("class_id");
        }
        mRecyclerView = findViewById(R.id.content_list);
        mRecyclerView.setBackground(getResources().getDrawable(R.mipmap.ic_chat_bg));
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                int position = getCurrentItem();
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (position > 0) {
                        findViewById(R.id.headerview_ll).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.date_header)).setText(ContactUtils.getTimeAgolatest(Long.parseLong(messageList.get(position).getMessage_date())));
                    } else {
                        findViewById(R.id.headerview_ll).setVisibility(View.GONE);
                    }
                } else if (newState == RecyclerView.SCROLL_INDICATOR_BOTTOM) {
                    if (position > 0) {
                        findViewById(R.id.headerview_ll).setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.date_header)).setText(ContactUtils.getTimeAgolatest(Long.parseLong(messageList.get(position).getMessage_date())));
                    } else {
                        findViewById(R.id.headerview_ll).setVisibility(View.GONE);
                    }
                }
            }
        });
        initViews();
    }

    private int getCurrentItem() {
        return ((LinearLayoutManager) mRecyclerView.getLayoutManager())
                .findFirstVisibleItemPosition();
    }

    private void initViews() {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {

                getChatMessages();
            }
        });
    }

    private void getChatMessages() {

        try {
            String msg_list = db_tables.getchatList_replys(class_id);
            messageList = new ArrayList<>();
            JSONObject obj = new JSONObject(msg_list);
            JSONArray jsonarray2 = obj.getJSONArray("message_body");

            if (jsonarray2.length() > 0) {
                for (int i = 0; i < jsonarray2.length(); i++) {
                    MessageObject messageObject = new MessageObject();

                    JSONObject jsonobject = jsonarray2.getJSONObject(i);
                    messageObject.setMessage_date(jsonobject.optString("message_date"));
                    messageObject.setMessage(jsonobject.optString("message"));
                    messageObject.setSender_member_id(jsonobject.optString("class_id"));
                    messageObject.setSender_name(jsonobject.optString("sender_name"));
                    messageObject.setUser_me(jsonobject.optString("user_me"));
                    messageObject.setMessage_id(jsonobject.optString("message_id"));
                    messageList.add(messageObject);

                }


                adapter = new ParentMessageListAdapter(messageList, this, this);
                linearLayoutManager = new LinearLayoutManager(this);
                //linearLayoutManager.setStackFromEnd(true);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                // mRecyclerView.setHasStableIds(true);
                linearLayoutManager.scrollToPosition(messageList.size() - 1);
                mRecyclerView.setAdapter(adapter);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageRowClicked(int position, String contactName, String contact_name, String pic_url, String contact_phone) {

    }

    @Override
    public void onMessageView(List<MessageObject> matchesList, int position) {

    }

    @Override
    public void onRowLongClicked(List<MessageObject> matchesList, int position, boolean value) {

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ParentsReplys.this.finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
