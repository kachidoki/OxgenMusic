package com.kachidoki.oxgenmusic.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.BaseActivity;

import butterknife.ButterKnife;

/**
 * Created by mayiwei on 16/12/16.
 */
public class AboutActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);
        setToolbar(true);
    }
}
