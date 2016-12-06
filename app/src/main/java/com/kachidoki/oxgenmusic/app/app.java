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
        startService(new Intent(this, PlayerService.class));
        initQueueDB();
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        ActiveAndroid.dispose();
        stopService(new Intent(this, PlayerService.class));
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

}
