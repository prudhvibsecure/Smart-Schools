package com.bsecure.scsm_mobile.modules;

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
import android.widget.TextView;

import com.bsecure.scsm_mobile.Login_Phone;
import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.fragments.StudentsFragment;
import com.bsecure.scsm_mobile.fragments.TransportListFragment;
import com.bsecure.scsm_mobile.fragments.TutorsListFragment;
import com.bsecure.scsm_mobile.utils.SharedValues;

import java.util.ArrayList;
import java.util.List;


public class ParentActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private IntentFilter filter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.parent_vv);
        filter = new IntentFilter("com.scs.app.SESSION");
        registerReceiver(mBroadcastReceiver, filter);
//        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
//        setSupportActionBar(toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        createViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        createTabIcons();
    }

    private void createTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("Students");
        tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_students, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Tutors");
        tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_teachers, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Transport");
        tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_transport, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);
    }

    private void createViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new StudentsFragment(), "Students");
        adapter.addFrag(new TutorsListFragment(), "Tutors");
        adapter.addFrag(new TransportListFragment(), "Transport");
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
}
