package com.kachidoki.oxgenmusic.app;

import android.app.Application;
import android.content.Intent;
import android.util.Log;

import com.activeandroid.ActiveAndroid;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.MusicDBHelper;
import com.kachidoki.oxgenmusic.model.bean.SongQueue;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.player.PlayerService;
import com.kachidoki.oxgenmusic.utils.SPUtils;
import com.kachidoki.oxgenmusic.utils.Utils;

/**
 * Created by mayiwei on 16/11/30.
 */
public class App extends Application {

    public static PlayEvent playEvent;


    @Override
    public void onCreate() {
        super.onCreate();
        Utils.initialize(this);
        ActiveAndroid.initialize(this);
        playEvent = new PlayEvent();
        initQueueDB();
        initSP();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }

    private void initQueueDB(){
        if (MusicDBHelper.getMusicDBHelper().SelectQueue(Constants.myList)==null){
            MusicDBHelper.getMusicDBHelper().saveQueue(new SongQueue(Constants.myList));
        }else {
            MusicManager.myList = MusicDBHelper.getMusicDBHelper().SelectQueue(Constants.myList);
        }
        if (MusicDBHelper.getMusicDBHelper().SelectQueue(Constants.hotList)==null){
            MusicDBHelper.getMusicDBHelper().saveQueue(new SongQueue(Constants.hotList));
        }else {
            MusicManager.hotList = MusicDBHelper.getMusicDBHelper().SelectQueue(Constants.hotList);
        }
    }

    private void initSP(){
        if (!SPUtils.contains(getApplicationContext(),Constants.nowQueue_sp)){
            SPUtils.put(getApplicationContext(),Constants.nowQueue_sp,Constants.myList);
        }
        if (!SPUtils.contains(getApplicationContext(),Constants.nowIndex_sp)){
            SPUtils.put(getApplicationContext(),Constants.nowIndex_sp,0);
        }
        if (!SPUtils.contains(getApplicationContext(),Constants.nowTime_sp)){
            SPUtils.put(getApplicationContext(),Constants.nowTime_sp,0);
        }
    }

}
