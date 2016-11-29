package com.kachidoki.oxgenmusic.player;

import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.config.Constants;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by mayiwei on 16/11/29.
 */
public class PlayerService extends Service {
    public static final int CommandPlay =1;
    public static final int CommandNext =2;
    public static final int CommandPrevious = 3;
    public static final int CommandClose =4;
    private NotificationManager notificationManager;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        EventBus.getDefault().register(this);
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent==null){
            return super.onStartCommand(intent, flags, startId);
        }
        int command = intent.getIntExtra("command",0);
        sendPlayerNotification(command);
        setMediaPlayer(command);
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(Constants.PlayerNotification);
    }

    private void sendPlayerNotification(int command) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("Notification");
        builder.setContentText("自定义通知栏示例");
        builder.setSmallIcon(R.mipmap.ic_launcher);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setShowWhen(false);


    }

    private void setMediaPlayer(int command) {
    }
}
