package com.ryankeith.haemophiliac_helper;

// This activity uses TabHost and ViewPager to show 4 fragments in one page.
/*
References:
 Tabhost + ViewPager + Sliding menu in android Part 1
 Think Twice Code Once
https://www.youtube.com/watch?v=GxEi_I3tv2k
* */

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.TabHost;

import java.util.ArrayList;
import java.util.List;

import fragments.Fragment1;
import fragments.Fragment2;
import fragments.Fragment3;
import fragments.Fragment4;

public class MainActivity extends AppCompatActivity implements ViewPager.OnPageChangeListener, TabHost.OnTabChangeListener {

    //Properties
    ViewPager viewPager;
    TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViewPager();
        initTabHost();
        isStoragePermissionGranted();
    }

    public  boolean isStoragePermissionGranted() {
        if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    == PackageManager.PERMISSION_GRANTED) {
                System.out.print("Permission is granted");
                return true;
            } else {
                System.out.print("Permission is revoked");
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else { //permission is automatically granted on sdk<23 upon installation
            System.out.print("Permission is granted");
            return true;
        }
    }

    private void initViewPager() {
        viewPager = (ViewPager) findViewById(R.id.view_pager);
        List<Fragment> listFragments = new ArrayList<Fragment>();
        listFragments.add(new Fragment1());
        listFragments.add(new Fragment2());
        listFragments.add(new Fragment3());
        listFragments.add(new Fragment4());
        MyFragmentPagerAdapter myFragmentPagerAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager(), listFragments);
        viewPager.setAdapter(myFragmentPagerAdapter);
        viewPager.setOnPageChangeListener(this);
    }

    private void initTabHost() {
        tabHost = (TabHost) findViewById(android.R.id.tabhost);
        tabHost.setup();
        String[] tabNames = {"Infusion", "Bleeding", "Knowledge", "Other"};
        for (int i = 0; i < tabNames.length; i++) {
            TabHost.TabSpec tabSpec;
            tabSpec = tabHost.newTabSpec(tabNames[i]);
            tabSpec.setIndicator(tabNames[i]);
            tabSpec.setContent(new FakeContent(getApplicationContext()));
            tabHost.addTab(tabSpec);
        }
        tabHost.setOnTabChangedListener(this);
    }

    //viewPager Listener
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(int position) {
        tabHost.setCurrentTab(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    //tabHost Listener
    @Override
    public void onTabChanged(String s) {
        int selectedItem = tabHost.getCurrentTab();
        viewPager.setCurrentItem(selectedItem);

        HorizontalScrollView hScrollView = (HorizontalScrollView) findViewById(R.id.h_scroll_view);
        View tabView = tabHost.getCurrentTabView();
        int scrollPos = tabView.getLeft() - (hScrollView.getWidth() - tabView.getWidth()) / 2;
        hScrollView.smoothScrollTo(scrollPos, 0);
    }

    public class FakeContent implements TabHost.TabContentFactory {
        Context context;

        public FakeContent(Context mcontext) {
            context = mcontext;
        }

        @Override
        public View createTabContent(String s) {
            View fakeView = new View(context);
            fakeView.setMinimumHeight(0);
            fakeView.setMinimumWidth(0);
            return fakeView;
        }
    }
}
