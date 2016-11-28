package com.kachidoki.oxgenmusic.app;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.kachidoki.oxgenmusic.R;

import butterknife.BindView;

/**
 * Created by mayiwei on 16/11/28.
 */
public class BaseActivity extends AppCompatActivity {
    @BindView(R.id.toolbar) Toolbar toolbar;
    public Toolbar getToolbar() {
        return toolbar;
    }
    protected void setToolbar(boolean returnable){
        if(toolbar!=null){
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(returnable);
        }
    }
}
