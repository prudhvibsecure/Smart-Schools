package com.bsecure.scsm_mobile.graphs;

import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bsecure.scsm_mobile.R;

import java.util.ArrayList;
import java.util.List;

public class ComapetativeFragment extends Fragment {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private View layout;
    String class_id, roll_no, examination_name = "";
    private boolean isViewShown = false;
    private Bundle bundle;

    public ComapetativeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        bundle = getArguments();
        class_id = bundle.getString("class_id");
        roll_no = bundle.getString("roll_no");
        examination_name = bundle.getString("examination_name");

        layout = inflater.inflate(R.layout.view_main_gphs, container, false);
        layout.findViewById(R.id.toolbar).setVisibility(View.GONE);

        viewPager = (ViewPager) layout.findViewById(R.id.viewpager);
        setupViewPager(viewPager);

        tabLayout = (TabLayout) layout.findViewById(R.id.tabs);
        tabLayout.setBackgroundColor(getActivity().getResources().getColor(R.color.orange));
        tabLayout.setupWithViewPager(viewPager);
        return layout;
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getChildFragmentManager());
        adapter.addFragment(new PrevoiousFragment(), "Previous", bundle);
        adapter.addFragment(new ToppersFragment(), "Toppers", bundle);
        viewPager.setAdapter(adapter);
    }

    public class ViewPagerAdapter extends FragmentPagerAdapter {
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
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (getView() != null) {
            isViewShown = true;

        } else {
            isViewShown = false;
        }
    }
}