package com.kachidoki.oxgenmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.MusicDBHelper;
import com.kachidoki.oxgenmusic.model.bean.SongQueue;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.utils.SPUtils;

/**
 * Created by mayiwei on 16/12/15.
 */
public class LaunchActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //隐藏状态栏
        //定义全屏参数
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //获得当前窗体对象
        Window window=LaunchActivity.this.getWindow();
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_launch);
        if (MusicManager.getMusicManager().getIsfirst()){
            initSP();
            initQueueDB();
            skipActivity(1500);
        }else {
            skipActivity(1000);
        }

    }



    /**
     * 延迟多少秒进入主界面
     * @param sencond 秒
     */
    private void skipActivity(int sencond) {
        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent intent = new Intent(LaunchActivity.this, MainActivity.class);
                startActivity(intent);
                LaunchActivity.this.finish();
            }
        }, sencond);
    }


    private void initSP(){
        Log.e("Launch","initSP");
        if (!SPUtils.get(getApplicationContext(),Constants.isInitSp_sp,"false").equals("true")){
            SPUtils.put(getApplicationContext(),Constants.isInitSp_sp,"true");
            Log.e("Test","initSP isInitSp_sp = false");
            if (!SPUtils.contains(getApplicationContext(),Constants.nowQueue_sp)){
                SPUtils.put(getApplicationContext(),Constants.nowQueue_sp,Constants.myList);
            }
            if (!SPUtils.contains(getApplicationContext(),Constants.nowIndex_sp)){
                SPUtils.put(getApplicationContext(),Constants.nowIndex_sp,0);
            }
            if (!SPUtils.contains(getApplicationContext(),Constants.nowTime_sp)){
                SPUtils.put(getApplicationContext(),Constants.nowTime_sp,0);
            }
            if (!SPUtils.contains(getApplicationContext(),Constants.hotListname_sp)){
                SPUtils.put(getApplicationContext(),Constants.hotListname_sp,"noname");
            }
        }
    }

    private void initQueueDB(){
        Log.e("Launch","initQueueDB ");
        if (MusicDBHelper.getMusicDBHelper().SelectQueue(Constants.myList)==null){
            Log.e("Launch","Constants.myList==null ");
            MusicDBHelper.getMusicDBHelper().saveQueue(new SongQueue(Constants.myList));
            MusicManager.myList = MusicDBHelper.getMusicDBHelper().SelectQueue(Constants.myList);
        }else {
            MusicManager.myList = MusicDBHelper.getMusicDBHelper().SelectQueue(Constants.myList);
        }
        if (MusicDBHelper.getMusicDBHelper().SelectQueue(Constants.hotList)==null){
            Log.e("Launch","Constants.hotList==null ");
            MusicDBHelper.getMusicDBHelper().saveQueue(new SongQueue(Constants.hotList));
            MusicManager.hotList = MusicDBHelper.getMusicDBHelper().SelectQueue(Constants.hotList);
        }else {
            MusicManager.hotList = MusicDBHelper.getMusicDBHelper().SelectQueue(Constants.hotList);
        }
        Log.e("Launch","initQueueDB down");
    }


}
