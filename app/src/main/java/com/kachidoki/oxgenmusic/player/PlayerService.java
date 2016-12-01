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
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Created by mayiwei on 16/11/29.
 */
public class PlayerService extends Service {
    public static final int CommandPlay =1;
    public static final int CommandNext =2;
    public static final int CommandPrevious = 3;
    public static final int CommandClose =4;
    private static boolean NotificationClose = false;
    private NotificationManager notificationManager;
    private NotificationTarget notificationTarget;
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
        MusicManager.getMusicManager().setCallBack(new MusicManager.CallBack() {
            @Override
            public void OnChange() {
                sendPlayerNotification(NotificationClose);
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
        sendPlayerNotification(NotificationClose);
        return super.onStartCommand(intent, flags, startId);
    }



    @Override
    public void onDestroy() {
        super.onDestroy();
        notificationManager.cancel(Constants.PlayerNotification);
        MusicManager.getMusicManager().release();
    }


    private void sendPlayerNotification(boolean isClose) {
        if (!isClose){
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setAutoCancel(false);
            builder.setOngoing(true);
            builder.setShowWhen(false);

            //bigView
            RemoteViews bigRemoteViews = new RemoteViews(getPackageName(),R.layout.notification_big_content);
            if (MusicManager.getMusicManager().getNowSong()!=null){
                bigRemoteViews.setTextViewText(R.id.nof_songname,MusicManager.getMusicManager().getNowSong().songname);
                bigRemoteViews.setTextViewText(R.id.nof_singer,MusicManager.getMusicManager().getNowSong().singername);
                bigRemoteViews.setImageViewUri(R.id.nof_img, Uri.parse(MusicManager.getMusicManager().getNowSong().albumpic_big));
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



            builder.setCustomContentView(bigRemoteViews);
            builder.setCustomBigContentView(bigRemoteViews);
            Notification notification = builder.build();
            notificationTarget = new NotificationTarget(getApplicationContext(),bigRemoteViews,R.id.nof_img,notification,Constants.PlayerNotification);
            if (MusicManager.getMusicManager().getNowSong()!=null){
                Glide.with(getApplicationContext())
                        .load(MusicManager.getMusicManager().getNowSong().albumpic_big)
                        .asBitmap()
                        .into(notificationTarget);
            }
            notificationManager.notify(Constants.PlayerNotification,notification);
        }
    }

    private void setMediaPlayer(int command) {
        switch (command){
            case CommandNext:
                if (MusicManager.getMusicManager().getmQueue()!=null){
                    MusicManager.getMusicManager().next();
                }
                break;
            case CommandPlay:
                if (MusicManager.getMusicManager().getIsPlaying()){
                    MusicManager.getMusicManager().pause();
                }else {
                    MusicManager.getMusicManager().start();
                }
                sendPlayerNotification(NotificationClose);
                break;
            case CommandPrevious:
                if (MusicManager.getMusicManager().getmQueue()!=null){
                    MusicManager.getMusicManager().previous();
                }
                break;
            case CommandClose:
                if (MusicManager.getMusicManager().getNowSong()!=null){
                    MusicManager.getMusicManager().stop();
                }
                notificationManager.cancel(Constants.PlayerNotification);
                NotificationClose = true;
                break;
        }
    }

    @Subscribe
    public void onEvent(PlayEvent playEvent){
        switch (playEvent.getAction()) {
            case PLAY:
                MusicManager.getMusicManager().start();
                sendPlayerNotification(NotificationClose);
                break;
            case NEXT:
                MusicManager.getMusicManager().next();
                sendPlayerNotification(NotificationClose);
                break;
            case PREVIOES:
                MusicManager.getMusicManager().previous();
                sendPlayerNotification(NotificationClose);
                break;
            case PAUSE:
                MusicManager.getMusicManager().pause();
                sendPlayerNotification(NotificationClose);
                break;
            case PLAYNOW:
                MusicManager.getMusicManager().playNow();
                sendPlayerNotification(NotificationClose);
                break;

        }
    }


}
