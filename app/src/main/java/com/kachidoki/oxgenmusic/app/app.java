package com.kachidoki.oxgenmusic.app;

import android.app.Application;
import android.content.Intent;

import com.kachidoki.oxgenmusic.model.event.PlayEvent;
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
        playEvent = new PlayEvent();
        startService(new Intent(this, PlayerService.class));
    }
}
