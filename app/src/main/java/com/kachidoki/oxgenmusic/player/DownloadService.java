package com.kachidoki.oxgenmusic.player;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.bean.ProgressBean;
import com.kachidoki.oxgenmusic.model.bean.SongDown;
import com.kachidoki.oxgenmusic.network.NetWork;
import com.kachidoki.oxgenmusic.network.ProgressResponseListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import rx.Observer;
import rx.schedulers.Schedulers;

/**
 * Created by mayiwei on 16/12/12.
 */
public class DownloadService extends Service{
    public static int CommandDownload=1;
    private final int DOWNLOAD_PROGRESS=2;

    private NotificationManager manger;
    private Map<String,SongDown> downMap = new HashMap<>();

    private Handler downloadHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            ProgressBean progressBean = (ProgressBean)msg.obj;
            int id = downMap.get(progressBean.songName).songid;
            sendNotification(progressBean.songName,(int)progressBean.contentLength,(int)progressBean.bytesRead,id);
            if (progressBean.done){
                Log.i("downloadHandler","long = "+progressBean.contentLength+" read = "+progressBean.bytesRead);
                manger.cancel(id);
                sendDownNotification(progressBean.songName,downMap.get(progressBean.songName).songid);
            }
        }
    };


    ProgressResponseListener progressResponseListener = new ProgressResponseListener() {
        private ProgressBean progressBean = new ProgressBean();

        @Override
        public void onResponseProgress(long bytesRead, long contentLength, boolean done,String songname) {
            progressBean.bytesRead=bytesRead;
            progressBean.contentLength=contentLength;
            progressBean.done = done;
            progressBean.songName = songname;
            Message message = downloadHandler.obtainMessage(DOWNLOAD_PROGRESS,progressBean);
            downloadHandler.sendMessage(message);
        }
    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        manger = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent==null){
            return super.onStartCommand(intent, flags, startId);
        }

        int command = intent.getIntExtra("command",0);
        if (command==CommandDownload){
            SongDown songDown = new SongDown(intent.getStringExtra("songname"),
                    intent.getIntExtra("seconds",0),
                    intent.getIntExtra("singerid",0),
                    intent.getStringExtra("albumpic"),
                    getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath()+File.separator+intent.getStringExtra("songname")+".mp3",
                    intent.getStringExtra("singername"),
                    intent.getIntExtra("albumid",0),
                    intent.getIntExtra("songid",0));
            downMap.put(songDown.songname,songDown);
            Log.i("DownloadService",getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath()+File.separator+intent.getStringExtra("songname")+".mp3");
            Log.i("DownloadService","开始下载 "+downMap.get(songDown.songname).songid);
            downloadMusic(songDown.songname,intent.getStringExtra("url"));
        }

        return super.onStartCommand(intent, flags, startId);

    }


    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    private void sendNotification(String songName, int max, int progressNow,int i){
        NotificationCompat.Builder nofDown = new NotificationCompat.Builder(this);
        nofDown.setSmallIcon(R.drawable.drawer_local);
        nofDown.setAutoCancel(false);
        nofDown.setOngoing(true);
        nofDown.setShowWhen(false);
        nofDown.setContentTitle(songName);
        nofDown.setProgress(max,progressNow,false);
        nofDown.setContentInfo(progressNow*100/max+"%");
        Notification notification = nofDown.build();
        manger.notify(i,notification);
    }

    private void sendDownNotification(String songName,int i){
        NotificationCompat.Builder nofDownOk = new NotificationCompat.Builder(this);
        nofDownOk.setSmallIcon(R.drawable.drawer_local);
        nofDownOk.setLargeIcon(BitmapFactory.decodeResource(getResources(),R.drawable.icon_download));
        nofDownOk.setTicker(songName+"下载完成");
        nofDownOk.setAutoCancel(true);
        nofDownOk.setOngoing(false);
        nofDownOk.setShowWhen(true);
        nofDownOk.setContentTitle(songName+" 下载完成");
        Notification notification = nofDownOk.build();
        manger.notify(i,notification);
    }


    private void downloadMusic(final String name,String url){
        NetWork.getDownloadApi(progressResponseListener,name)
                .download(url)
                .subscribeOn(Schedulers.io())
                .subscribe(new Observer<ResponseBody>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e("FileDownload","onError "+e.getMessage());
                    }

                    @Override
                    public void onNext(ResponseBody responseBody) {
                        if(writeResponseBodyToDisk(responseBody,name)){
                            Log.e("FileDownload","the file is down");
                            downMap.get(name).save();
//                            downMap.remove(name);
                        }else {
                            Log.e("FileDownload","the filesdown is fail");
                        }

                    }
                });
    }

    private boolean writeResponseBodyToDisk(ResponseBody body,String name) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(getExternalFilesDir(Environment.DIRECTORY_MUSIC) ,name+".m4a");
            InputStream inputStream = null;
            OutputStream outputStream = null;
            try {
                byte[] fileReader = new byte[2048];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);
                while (true) {
                    int read = inputStream.read(fileReader);
                    if (read == -1) {
                        break;
                    }
                    outputStream.write(fileReader, 0, read);
                }
                outputStream.flush();
                return true;
            } catch (IOException e) {
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            return false;
        }
    }


}
