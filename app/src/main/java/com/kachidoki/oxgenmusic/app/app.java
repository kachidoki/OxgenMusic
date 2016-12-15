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

import cn.bmob.v3.Bmob;

/**
 * Created by mayiwei on 16/11/30.
 */
public class App extends Application {

    public static PlayEvent playEvent= new PlayEvent();


    @Override
    public void onCreate() {
        super.onCreate();
        Utils.initialize(this);
        ActiveAndroid.initialize(this);
        Bmob.initialize(this,Constants.BmobApi);
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
    }





}
