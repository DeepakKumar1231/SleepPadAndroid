package com.szip.smartdream.Controller.Fragment.report;


import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.szip.smartdream.Adapter.MyPagerAdapter;
import com.szip.smartdream.Controller.Fragment.BaseFragment;
import com.szip.smartdream.R;
import com.szip.smartdream.View.NoScrollViewPager;

import java.util.ArrayList;


/**
 * Created by Administrator on 2019/1/23.
 */

public class ReportFragment extends BaseFragment {

    ReportDayFragment reportDayFrag = ReportDayFragment.newInstance("ReportDay");
    private String[] tabs = new String[]{"day", "week", "month"};
    private TabLayout mTab;
    private NoScrollViewPager mPager;
    private FragmentManager fm;
    private FragmentTransaction transaction;
    private MyPagerAdapter myPagerAdapter;
    private ArrayList<Fragment> fragments = new ArrayList<>();


    private TextView day, month, year;
    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.day:
                    day.setBackgroundColor(requireContext().getResources().getColor(R.color.purple));
                    day.setTextColor(requireContext().getResources().getColor(R.color.white));
                    month.setBackgroundColor(requireContext().getResources().getColor(R.color.white));
                    month.setTextColor(requireContext().getResources().getColor(R.color.black));
                    year.setBackgroundColor(requireContext().getResources().getColor(R.color.white));
                    year.setTextColor(requireContext().getResources().getColor(R.color.black));
                    mPager.setCurrentItem(0);
                    break;

                case R.id.month:
                    day.setBackgroundColor(requireContext().getResources().getColor(R.color.white));
                    day.setTextColor(requireContext().getResources().getColor(R.color.black));
                    month.setBackgroundColor(requireContext().getResources().getColor(R.color.purple));
                    month.setTextColor(requireContext().getResources().getColor(R.color.white));
                    year.setBackgroundColor(requireContext().getResources().getColor(R.color.white));
                    year.setTextColor(requireContext().getResources().getColor(R.color.black));
                    mPager.setCurrentItem(1);
                    break;

                case R.id.year:
                    day.setBackgroundColor(requireContext().getResources().getColor(R.color.white));
                    day.setTextColor(requireContext().getResources().getColor(R.color.black));
                    month.setBackgroundColor(requireContext().getResources().getColor(R.color.white));
                    month.setTextColor(requireContext().getResources().getColor(R.color.black));
                    year.setBackgroundColor(requireContext().getResources().getColor(R.color.purple));
                    year.setTextColor(requireContext().getResources().getColor(R.color.white));
                    mPager.setCurrentItem(2);
                    break;

            }
        }
    };

    /**
     * 返回一个fragment实例，Activity中调用
     */
    public static ReportFragment newInstance(String param) {
        Bundle bundle = new Bundle();
        bundle.putString("param", param);
        ReportFragment fragment = new ReportFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_report;
    }

    @Override
    protected void afterOnCreated(Bundle savedInstanceState) {
        if (mPager == null && mTab == null) {
            initView();
            initPager();
        }
    }

    /**
     * 初始化界面
     **/
    private void initView() {


        fm = requireFragmentManager();
        transaction = fm.beginTransaction();


        day = getView().findViewById(R.id.day);
        month = getView().findViewById(R.id.month);
        year = getView().findViewById(R.id.year);

        mTab = getView().findViewById(R.id.reportTl);
        mPager = getView().findViewById(R.id.reportVp);

        day.setOnClickListener(onClickListener);
        month.setOnClickListener(onClickListener);
        year.setOnClickListener(onClickListener);

        day.performClick();
    }

    /**
     * 初始化滑动页面
     */
    private void initPager() {
        // 创建一个集合,装填Fragment
        ReportDayFragment dayFragment = ReportDayFragment.newInstance("szip");
        ReportWeekFragment weekFragment = ReportWeekFragment.newInstance("szip");
        ReportMonthFragment monthFragment = ReportMonthFragment.newInstance("szip");

        // 装填
        fragments.add(dayFragment);
        fragments.add(weekFragment);
        fragments.add(monthFragment);
        // 创建ViewPager适配器
        myPagerAdapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());
        myPagerAdapter.setFragmentArrayList(fragments);
        // 给ViewPager设置适配器
        mPager.setAdapter(myPagerAdapter);

        // 使用 TabLayout 和 ViewPager 相关联
        mTab.setupWithViewPager(mPager);

        // TabLayout 指示器 (记得自己手动创建3个Fragment,注意是 app包下的Fragment 还是 V4包下的 Fragment)
        for (int i = 0; i < myPagerAdapter.getCount(); i++) {
            TabLayout.Tab tab = mTab.getTabAt(i);//获得每一个tab
            tab.setCustomView(R.layout.main_top_layout);//给每一个tab设置view
            if (i == 0) {
                // 设置第一个tab的TextView是被选择的样式
                tab.getCustomView().findViewById(R.id.main_tv).setSelected(true);//第一个tab被选中
            }
            TextView textView = (TextView) tab.getCustomView().findViewById(R.id.main_tv);
            textView.setText(tabs[i]);//设置tab上的文字
        }
        mTab.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.i("SANJAY","ONE "+tab.getPosition());

                tab.getCustomView().findViewById(R.id.main_tv).setSelected(true);
                mPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                Log.i("SANJAY","ONE "+tab.getPosition());
                tab.getCustomView().findViewById(R.id.main_tv).setSelected(false);
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                Log.i("SANJAY","ONE "+tab.getPosition());
            }
        });
    }
}
