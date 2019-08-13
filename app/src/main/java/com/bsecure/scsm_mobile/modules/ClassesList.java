package com.bsecure.scsm_mobile.modules;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.fragments.AdminTransportListFragment;
import com.bsecure.scsm_mobile.fragments.ClassGalleryFragment;
import com.bsecure.scsm_mobile.fragments.ClassesListFragment;
import com.bsecure.scsm_mobile.fragments.SchoolGalleryFragment;
import com.bsecure.scsm_mobile.fragments.StudentGalleryFragment;
import com.bsecure.scsm_mobile.models.ClassModel;

import java.util.ArrayList;
import java.util.List;

public class ClassesList extends AppCompatActivity {

    ArrayList<ClassModel> classes;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_classes_list);

        Intent in = getIntent();

        classes = (ArrayList<ClassModel>)in.getSerializableExtra("classes");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Classes List");//Organization Head

        toolbar.setTitleTextColor(getResources().getColor(R.color.white));

        setSupportActionBar(toolbar);

        ActionBar actionBar = getSupportActionBar();

        actionBar.setDisplayHomeAsUpEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.viewpager);

        createViewPager(viewPager);

        tabLayout = (TabLayout) findViewById(R.id.tabs);

        tabLayout.setupWithViewPager(viewPager);

        createTabIcons();

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

    private void createTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

        tabOne.setText("CLASSES");

        tabOne.setTextSize(13);
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_students, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        /*TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);

        tabTwo.setText("TRANSPORT");

        tabTwo.setTextSize(13);
        //tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_teachers, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);
*/
    }


    private void createViewPager(ViewPager viewPager) {

        ClassesList.ViewPagerAdapter adapter = new ClassesList.ViewPagerAdapter(getSupportFragmentManager());

        adapter.addFrag(new ClassesListFragment(), "CLASSES");

        //adapter.addFrag(new AdminTransportListFragment(), "TRANSPORT");
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
}
