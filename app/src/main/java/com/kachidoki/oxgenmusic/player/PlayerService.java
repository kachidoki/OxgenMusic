package com.kachidoki.oxgenmusic.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.NotificationTarget;
import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.activity.PlayActivity;
import com.kachidoki.oxgenmusic.app.App;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by mayiwei on 16/11/29.
 */
public class PlayerService extends Service {
    public static final int CommandCreate =0;
    public static final int CommandPlay =1;
    public static final int CommandNext =2;
    public static final int CommandPrevious = 3;
    public static final int CommandClose =4;
    public static final int CommandPlayNow = 5;
    private NotificationTarget notificationTarget;
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        MusicManager.getMusicManager().setCallBack(new MusicManager.CallBack() {
            @Override
            public void OnChange() {
                sendPlayerNotification();
                App.playEvent.setAction(PlayEvent.Action.CHANGE);
                EventBus.getDefault().post(App.playEvent);
            }

        });
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent==null){
            return super.onStartCommand(intent, flags, startId);
        }
        int command = intent.getIntExtra("command",0);
        setMediaPlayer(command);
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);
        SPUtils.put(PlayerService.this,Constants.nowIndex_sp,MusicManager.getMusicManager().getIndex());
        MusicManager.getMusicManager().setIsfirst(true);
    }


    private void sendPlayerNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_music);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setShowWhen(false);


        //bigView
        RemoteViews bigRemoteViews = new RemoteViews(getPackageName(),R.layout.notification_big_content);
        if (MusicManager.getMusicManager().getNowSong()!=null){
            bigRemoteViews.setTextViewText(R.id.nof_songname,MusicManager.getMusicManager().getNowSong().songname);
            bigRemoteViews.setTextViewText(R.id.nof_singer,MusicManager.getMusicManager().getNowSong().singername);
        }


        if(MusicManager.getMusicManager().getIsPlaying()){
            bigRemoteViews.setImageViewResource(R.id.nof_playpasue,R.drawable.icon_pause);
        }else{
            bigRemoteViews.setImageViewResource(R.id.nof_playpasue,R.drawable.icon_play_gray);
        }

        Intent Intent1 = new Intent(this,PlayerService.class);
        Intent1.putExtra("command",CommandPlay);
        PendingIntent PIntent1 =  PendingIntent.getService(this,5,Intent1,0);
        bigRemoteViews.setOnClickPendingIntent(R.id.nof_playpasue,PIntent1);

        Intent Intent2 = new Intent(this,PlayerService.class);
        Intent2.putExtra("command",CommandNext);
        PendingIntent PIntent2 =  PendingIntent.getService(this,6,Intent2,0);
        bigRemoteViews.setOnClickPendingIntent(R.id.nof_next,PIntent2);

        Intent Intent3 = new Intent(this,PlayerService.class);
        Intent3.putExtra("command",CommandPrevious);
        PendingIntent PIntent3 =  PendingIntent.getService(this,7,Intent3,0);
        bigRemoteViews.setOnClickPendingIntent(R.id.nof_pre,PIntent3);

        Intent Intent4 = new Intent(this,PlayerService.class);
        Intent4.putExtra("command",CommandClose);
        PendingIntent PIntent4 =  PendingIntent.getService(this,8,Intent4,0);
        bigRemoteViews.setOnClickPendingIntent(R.id.nof_stop,PIntent4);

        Intent intentToPlay = new Intent(this, PlayActivity.class);
        PendingIntent pIntentToPlay = PendingIntent.getActivity(this,9,intentToPlay,0);
        bigRemoteViews.setOnClickPendingIntent(R.id.nof_toPlay,pIntentToPlay);
        bigRemoteViews.setOnClickPendingIntent(R.id.nof_img,pIntentToPlay);

        Intent notificationIntent = new Intent(this,PlayActivity.class);
        PendingIntent contentIntent = PendingIntent.getService(this,0,notificationIntent,0);


//        builder.setContent(bigRemoteViews);
        builder.setCustomBigContentView(bigRemoteViews);
        Notification notification = builder.build();
        notificationTarget = new NotificationTarget(getApplicationContext(),bigRemoteViews,R.id.nof_img,notification,Constants.PlayerNotification);
        notification.contentIntent = contentIntent;
        if (MusicManager.getMusicManager().getNowSong()!=null){
            Glide.with(getApplicationContext())
                    .load(MusicManager.getMusicManager().getNowSong().albumpic_big)
                    .asBitmap()
                    .into(notificationTarget);
        }

        startForeground(Constants.PlayerNotification,notification);
    }

    private void setMediaPlayer(int command) {
        switch (command){
            case CommandCreate:
                sendPlayerNotification();
                break;
            case CommandNext:
                Log.e("Test","setMediaPlayer : CommandNext");
                if (MusicManager.getMusicManager().getmQueue()!=null){
                    MusicManager.getMusicManager().next();
                }
                sendPlayerNotification();
                break;
            case CommandPlay:
                Log.e("Test","setMediaPlayer : CommandPlay");
                if (MusicManager.getMusicManager().getIsPlaying()){
                    MusicManager.getMusicManager().pause();
                }else {
                    MusicManager.getMusicManager().start();
                }
                sendPlayerNotification();
                break;
            case CommandPrevious:
                Log.e("Test","setMediaPlayer : CommandPrevious");
                if (MusicManager.getMusicManager().getmQueue()!=null){
                    MusicManager.getMusicManager().previous();
                }
                sendPlayerNotification();
                break;
            case CommandClose:
                Log.e("Test","setMediaPlayer : CommandClose");
                if (MusicManager.getMusicManager().getNowSong()!=null){
                    if (MusicManager.getMusicManager().getIsReady()){
                        MusicManager.getMusicManager().pause();
                    }
                }
                stopForeground(true);
                stopSelf();
                break;
            case CommandPlayNow:
                if (MusicManager.getMusicManager().getNowSong()!=null){
                    MusicManager.getMusicManager().playNow();
                }
                sendPlayerNotification();
                break;
        }
    }



}
