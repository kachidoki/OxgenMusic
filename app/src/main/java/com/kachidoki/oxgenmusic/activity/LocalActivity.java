package com.kachidoki.oxgenmusic.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.activity.fragment.MyDownloadFragment;
import com.kachidoki.oxgenmusic.activity.fragment.MyLocalMusicFragment;
import com.kachidoki.oxgenmusic.app.BaseActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayiwei on 17/1/23.
 */
public class LocalActivity extends BaseActivity {


    @BindView(R.id.local_mydown)
    TextView localMydown;
    @BindView(R.id.local_localmusic)
    TextView localLocalmusic;
    @BindView(R.id.local_slider)
    View localSlider;
    @BindView(R.id.local_viewpager)
    ViewPager viewPager;

    private MyDownloadFragment myDownloadFragment;
    private MyLocalMusicFragment myLocalMusicFragment;
    private List<Fragment> fragments;
    private int currIndex = 0;// 当前页卡编号

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local);
        ButterKnife.bind(this);
        setToolbar(true);
        InitSlider();
        InitViewPager();

    }

    private void InitSlider(){
        localSlider.post(new Runnable() {
            @Override
            public void run() {
                localSlider.setLayoutParams(new FrameLayout.LayoutParams(localSlider.getWidth() / 2, localSlider.getHeight(), Gravity.BOTTOM));
            }
        });
    }

    private void InitViewPager(){
        localMydown.setOnClickListener(new MyOnClickListener(0));
        localLocalmusic.setOnClickListener(new MyOnClickListener(1));
        fragments = new ArrayList<Fragment>();
        myDownloadFragment = new MyDownloadFragment();
        myLocalMusicFragment = new MyLocalMusicFragment();
        fragments.add(myDownloadFragment);
        fragments.add(myLocalMusicFragment);
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(0);
        viewPager.setOnPageChangeListener(new MyOnPagerChangeListener());
    }

    private class MyOnClickListener implements View.OnClickListener {
        private int index=0;
        public MyOnClickListener(int i){
            index=i;
        }
        public void onClick(View v) {
            viewPager.setCurrentItem(index);
        }

    }

    public class MyViewPagerAdapter extends FragmentPagerAdapter {

        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }

        @Override
        public int getCount() {
            return fragments.size();
        }
    }

    class MyOnPagerChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int i, float v, int i1) {
            int w = localSlider.getWidth();
            float x = w*i+w*v;
            localSlider.setX(x);
        }

        @Override
        public void onPageSelected(int i) {

        }

        @Override
        public void onPageScrollStateChanged(int i) {

        }
    }


}
