package com.bsecure.scsm_mobile.modules;

import android.content.Intent;
import android.content.IntentFilter;
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
import android.view.View;
import android.widget.TextView;

import com.bsecure.scsm_mobile.R;
import com.bsecure.scsm_mobile.fragments.ClassGalleryFragment;
import com.bsecure.scsm_mobile.fragments.MoreFragment;
import com.bsecure.scsm_mobile.fragments.SchoolGalleryFragment;
import com.bsecure.scsm_mobile.fragments.StudentGalleryFragment;
import com.bsecure.scsm_mobile.fragments.StudentsFragment;
import com.bsecure.scsm_mobile.fragments.TransportListFragment;
import com.bsecure.scsm_mobile.fragments.TutorsListFragment;

import java.util.ArrayList;
import java.util.List;

public class Gallery extends AppCompatActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private IntentFilter filter,l_mfilter;
    String school_id, class_id, student_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);


        Intent data = getIntent();
        if(data!= null)
        {
            class_id = data.getStringExtra("class_id");
            student_id = data.getStringExtra("student_id");
        }

        Bundle bundle = new Bundle();
        bundle.putString("student_id", student_id);
        bundle.putString("class_id", class_id);
        SchoolGalleryFragment myObj = new SchoolGalleryFragment();
        myObj.setArguments(bundle);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setTitle("Image Gallery");//Organization Head
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        createViewPager(viewPager);
       /* viewPager.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return true;
            }
        });
*/
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        createTabIcons();
    }

    private void createTabIcons() {

        TextView tabOne = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabOne.setText("School Gallery");
        tabOne.setTextSize(13);
        //tabOne.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_students, 0, 0);
        tabLayout.getTabAt(0).setCustomView(tabOne);

        TextView tabTwo = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabTwo.setText("Class Gallery");
        tabTwo.setTextSize(13);
        //tabTwo.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_teachers, 0, 0);
        tabLayout.getTabAt(1).setCustomView(tabTwo);

        TextView tabThree = (TextView) LayoutInflater.from(this).inflate(R.layout.custom_tab, null);
        tabThree.setText("Student Gallery");
        tabThree.setTextSize(13);
        //tabThree.setCompoundDrawablesWithIntrinsicBounds(0, R.mipmap.ic_transport, 0, 0);
        tabLayout.getTabAt(2).setCustomView(tabThree);

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

    private void createViewPager(ViewPager viewPager) {
        Gallery.ViewPagerAdapter adapter = new Gallery.ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new SchoolGalleryFragment(), "School Gallery");
        adapter.addFrag(new ClassGalleryFragment(), "Class Gallery");
        adapter.addFrag(new StudentGalleryFragment(), "Student Gallery");
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

    }
}
