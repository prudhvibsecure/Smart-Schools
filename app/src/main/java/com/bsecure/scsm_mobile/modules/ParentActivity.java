package com.bsecure.scsm_mobile.modules;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.bsecure.scsm_mobile.Login_Phone;
import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.common.CustomViewPager;
import com.bsecure.scsm_mobile.database.DB_Tables;
import com.bsecure.scsm_mobile.fragments.MoreFragment;
import com.bsecure.scsm_mobile.fragments.StudentsFragment;
import com.bsecure.scsm_mobile.fragments.TransportListFragment;
import com.bsecure.scsm_mobile.fragments.TutorsListFragment;
import com.bsecure.scsm_mobile.utils.SharedValues;

import java.util.ArrayList;
import java.util.List;


public class ParentActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private CustomViewPager viewPager;
    private IntentFilter filter,l_mfilter;
    private DB_Tables db_tables;
    String[] stu_ids;
    IntentFilter mfilter, tfilter, afilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_vv);

        mfilter = new IntentFilter("com.notification.show");
        registerReceiver(mmBroadcastReceiver, mfilter);

        filter = new IntentFilter("com.scs.app.SESSION");
        l_mfilter = new IntentFilter("com.parenttutor.refresh");
        registerReceiver(mBroadcastReceiver, filter);

        tfilter = new IntentFilter("com.trans.refresh");
        registerReceiver(tBroadcastReceiver, tfilter);
//
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        viewPager = findViewById(R.id.viewpager);
        viewPager.setPagingEnabled(false);
        createViewPager(viewPager);
       /* viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });*/
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        createTabIcons();
    }

    /*@Override
    protected void onResume() {
        registerReceiver(mBroadcastReceiver_ref, l_mfilter);

        super.onResume();
    }
*/
    private void createTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Students");
        tabOne.setTextSize(14);
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_students, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Tutors");
        tabTwo.setTextSize(14);
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_teachers, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Transport");
        tabThree.setTextSize(14);
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_transport, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

        TextView tabFour = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabFour.setText("More");
        tabFour.setTextSize(14);
        tabFour.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.more, 0, 0);
        tabLayout.getTabAt(3).setCustomView(tabFour);
    }

    private void createViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new StudentsFragment(), "Students");
        adapter.addFrag(new TutorsListFragment(), "Tutors");
        adapter.addFrag(new TransportListFragment(), "Transport");
        adapter.addFrag(new MoreFragment(), "More");
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void addFrag(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            unregisterReceiver(mBroadcastReceiver);
           // unregisterReceiver(mBroadcastReceiver_ref);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {
                SharedValues.saveValue(getApplicationContext(), "member_id", "");
                getApplicationContext().deleteDatabase("schools_data_mob.db");
                Intent sc = new Intent(getApplicationContext(), Login_Phone.class);
                startActivity(sc);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    private final BroadcastReceiver tBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            try {

                Intent sc = new Intent(getApplicationContext(), ParentActivity.class);
                startActivity(sc);
                finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };


//    private final BroadcastReceiver mBroadcastReceiver_ref = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            try {
//
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
//    };

    private BroadcastReceiver mmBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            Intent in = getIntent();
            String message = in.getStringExtra("message");
            final Dialog dialog = new Dialog(ParentActivity.this);
            dialog.setContentView(R.layout.dialogue_message);
            dialog.setTitle("Alert!");
            Button yes = dialog.findViewById(R.id.yes);
            Button no = dialog.findViewById(R.id.no);
            Button later = findViewById(R.id.later);

            TextView tv = dialog.findViewById(R.id.data);
            tv.setText(message);
            yes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            no.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            later.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

            dialog.show();
        }
    };

}
