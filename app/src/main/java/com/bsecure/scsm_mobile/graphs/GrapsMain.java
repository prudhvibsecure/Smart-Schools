package com.bsecure.scsm_mobile.graphs;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.bsecure.scsm_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class GrapsMain extends AppCompatActivity {
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    String class_id, roll_no, examination_name = "";
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_main_gphs);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Performance Analyzer");
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Intent data = getIntent();
        class_id = data.getStringExtra("class_id");
        roll_no = data.getStringExtra("roll_no");
        examination_name = data.getStringExtra("examination_name");

        bundle=new Bundle();
        bundle.putString("class_id",class_id);
        bundle.putString("roll_no",roll_no);
        bundle.putString("examination_name",examination_name);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new SelfFragment(), "Self Analysis",bundle);
        adapter.addFragment(new ComapetativeFragment(), "Comparative Analysis",bundle);
        viewPager.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case android.R.id.home:
                GrapsMain.this.finish();
            break;
        }
        return super.onOptionsItemSelected(item);
    }

    public  class ViewPagerAdapter extends FragmentPagerAdapter {
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

        public void addFragment(Fragment fragment, String title, Bundle bundle) {

            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
            fragment.setArguments(bundle);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}
