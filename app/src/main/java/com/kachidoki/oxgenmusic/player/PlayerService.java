package com.kachidoki.oxgenmusic.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
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
import com.kachidoki.oxgenmusic.widget.ShakeHelper;

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
    private NotificationTarget notificationNomalTarget;
    private ShakeHelper shakeHelper;
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
        initShake();
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
        if (SPUtils.get(PlayerService.this,Constants.nowQueue_sp,"noQueue").equals(Constants.cacheList)){
            int half = 10;
            int index = (int)SPUtils.get(PlayerService.this,Constants.nowIndex_sp,0);
            boolean isless = index - half<0;
            SPUtils.put(PlayerService.this,Constants.nowIndex_sp,isless?MusicManager.getMusicManager().getIndex():index+(MusicManager.getMusicManager().getIndex()-half));
        }else {
            SPUtils.put(PlayerService.this,Constants.nowIndex_sp,MusicManager.getMusicManager().getIndex());
        }
        MusicManager.getMusicManager().setIsfirst(true);
    }


    private void sendPlayerNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(R.drawable.ic_music);
        builder.setAutoCancel(false);
        builder.setOngoing(true);
        builder.setShowWhen(false);


        RemoteViews nomalRemoteViews = new RemoteViews(getPackageName(),R.layout.notification_content);
        //bigView
        RemoteViews bigRemoteViews = new RemoteViews(getPackageName(),R.layout.notification_big_content);
        if (MusicManager.getMusicManager().getNowSong()!=null){
            bigRemoteViews.setTextViewText(R.id.nof_songname,MusicManager.getMusicManager().getNowSong().songname);
            bigRemoteViews.setTextViewText(R.id.nof_singer,MusicManager.getMusicManager().getNowSong().singername);
            nomalRemoteViews.setTextViewText(R.id.nof_nomal_songname,MusicManager.getMusicManager().getNowSong().songname);
            nomalRemoteViews.setTextViewText(R.id.nof_nomal_singer,MusicManager.getMusicManager().getNowSong().singername);
            if (MusicManager.getMusicManager().getNowSong().albumpic_big==null){
                bigRemoteViews.setImageViewResource(R.id.nof_img,R.drawable.cd_nomal_png);
                nomalRemoteViews.setImageViewResource(R.id.nof_nomal_img,R.drawable.cd_nomal_png);
            }
        }


        if(MusicManager.getMusicManager().getIsPlaying()){
            bigRemoteViews.setImageViewResource(R.id.nof_playpasue,R.drawable.icon_pause);
            nomalRemoteViews.setImageViewResource(R.id.nof_nomal_playpasue,R.drawable.icon_play_pause);
        }else{
            bigRemoteViews.setImageViewResource(R.id.nof_playpasue,R.drawable.icon_play_gray);
            nomalRemoteViews.setImageViewResource(R.id.nof_nomal_playpasue,R.drawable.icon_play_play);
        }

        Intent Intent1 = new Intent(this,PlayerService.class);
        Intent1.putExtra("command",CommandPlay);
        PendingIntent PIntent1 =  PendingIntent.getService(this,5,Intent1,0);
        bigRemoteViews.setOnClickPendingIntent(R.id.nof_playpasue,PIntent1);
        nomalRemoteViews.setOnClickPendingIntent(R.id.nof_nomal_playpasue,PIntent1);

        Intent Intent2 = new Intent(this,PlayerService.class);
        Intent2.putExtra("command",CommandNext);
        PendingIntent PIntent2 =  PendingIntent.getService(this,6,Intent2,0);
        bigRemoteViews.setOnClickPendingIntent(R.id.nof_next,PIntent2);
        nomalRemoteViews.setOnClickPendingIntent(R.id.nof_nomal_next,PIntent2);

        Intent Intent3 = new Intent(this,PlayerService.class);
        Intent3.putExtra("command",CommandPrevious);
        PendingIntent PIntent3 =  PendingIntent.getService(this,7,Intent3,0);
        bigRemoteViews.setOnClickPendingIntent(R.id.nof_pre,PIntent3);

        Intent Intent4 = new Intent(this,PlayerService.class);
        Intent4.putExtra("command",CommandClose);
        PendingIntent PIntent4 =  PendingIntent.getService(this,8,Intent4,0);
        bigRemoteViews.setOnClickPendingIntent(R.id.nof_stop,PIntent4);
        nomalRemoteViews.setOnClickPendingIntent(R.id.nof_nomal_stop,PIntent4);

        Intent intentToPlay = new Intent(this, PlayActivity.class);
        PendingIntent pIntentToPlay = PendingIntent.getActivity(this,9,intentToPlay,0);
        bigRemoteViews.setOnClickPendingIntent(R.id.nof_toPlay,pIntentToPlay);
        bigRemoteViews.setOnClickPendingIntent(R.id.nof_img,pIntentToPlay);
        nomalRemoteViews.setOnClickPendingIntent(R.id.nof_nomal_img,pIntentToPlay);
        nomalRemoteViews.setOnClickPendingIntent(R.id.nof_toPlay,pIntentToPlay);

        Intent notificationIntent = new Intent(this,PlayActivity.class);
        PendingIntent contentIntent = PendingIntent.getService(this,0,notificationIntent,0);


        builder.setContent(nomalRemoteViews);
        builder.setCustomBigContentView(bigRemoteViews);
        Notification notification = builder.build();
        notificationTarget = new NotificationTarget(getApplicationContext(),bigRemoteViews,R.id.nof_img,notification,Constants.PlayerNotification);
        notificationNomalTarget = new NotificationTarget(getApplicationContext(),nomalRemoteViews,R.id.nof_nomal_img,notification,Constants.PlayerNotification);
        notification.contentIntent = contentIntent;
        if (MusicManager.getMusicManager().getNowSong()!=null){
            Glide.with(getApplicationContext())
                    .load(MusicManager.getMusicManager().getNowSong().albumpic_big)
                    .asBitmap()
                    .into(notificationTarget);
            Glide.with(getApplicationContext())
                    .load(MusicManager.getMusicManager().getNowSong().albumpic_big)
                    .asBitmap()
                    .into(notificationNomalTarget);
        }

        startForeground(Constants.PlayerNotification,notification);
    }

    private void setMediaPlayer(int command) {
        switch (command){
            case CommandCreate:
                sendPlayerNotification();
                break;
            case CommandNext:
                shakeHelper.start();
                if (MusicManager.getMusicManager().getmQueue()!=null){
                    MusicManager.getMusicManager().next();
                }
                sendPlayerNotification();
                break;
            case CommandPlay:
                shakeHelper.start();
                if (MusicManager.getMusicManager().getIsPlaying()){
                    MusicManager.getMusicManager().pause();
                }else {
                    MusicManager.getMusicManager().start();
                }
                sendPlayerNotification();
                break;
            case CommandPrevious:
                shakeHelper.start();
                if (MusicManager.getMusicManager().getmQueue()!=null){
                    MusicManager.getMusicManager().previous();
                }
                sendPlayerNotification();
                break;
            case CommandClose:
                if (MusicManager.getMusicManager().getNowSong()!=null){
                    if (MusicManager.getMusicManager().getIsReady()){
                        MusicManager.getMusicManager().pause();
                    }
                }
                shakeHelper.stop();
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

    private void initShake(){
        final Vibrator vibrator=(Vibrator) getBaseContext().getSystemService(Context.VIBRATOR_SERVICE);
        shakeHelper=new ShakeHelper(getBaseContext());
        shakeHelper.setOnShakeListener(new ShakeHelper.OnShakeListener() {

            @Override
            public void onShake() {
                // TODO Auto-generated method stub
                if(MusicManager.getMusicManager().getIsPlaying()){
                    shakeHelper.stop();
                    vibrator.vibrate(200);
                    MusicManager.getMusicManager().next();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            shakeHelper.start();
                        }
                    },1500);
                }
            }
        } );
    }



}
