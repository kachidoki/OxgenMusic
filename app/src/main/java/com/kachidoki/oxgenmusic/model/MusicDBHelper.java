package com.kachidoki.oxgenmusic.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.model.bean.SongBean;
import com.kachidoki.oxgenmusic.model.bean.SongQueue;
import com.kachidoki.oxgenmusic.model.bean.SongYun;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobBatch;
import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BatchResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListListener;
import cn.bmob.v3.listener.SaveListener;


/**
 * Created by mayiwei on 16/12/6.
 */
public class MusicDBHelper {

    private static MusicDBHelper musicDBHelper = new MusicDBHelper();
    public static MusicDBHelper getMusicDBHelper(){
        return musicDBHelper;
    }

    public void saveSong(Song song,SongQueue songQueue){
        new SongBean(song.songname,song.seconds,song.singerid,song.albumpic_big,song.url,song.singername,song.albumid,songQueue).save();
    }

    public void saveQueue(SongQueue songQueue){
        songQueue.save();
    }

    public void saveListSong(List<Song> songs,SongQueue songQueue){
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < songs.size(); i++) {
                new SongBean(songs.get(i).songname,songs.get(i).seconds,songs.get(i).singerid,songs.get(i).albumpic_big,songs.get(i).url,songs.get(i).singername,songs.get(i).albumid,songQueue).save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    public void saveListSongYun(List<SongYun> songs,SongQueue songQueue){
        ActiveAndroid.beginTransaction();
        try {
            for (SongYun songYun:songs){
                new SongBean(songYun.songname,songYun.seconds,songYun.singerid,songYun.albumpic_big,songYun.url,songYun.singername,songYun.albumid,songQueue).save();
            }
            ActiveAndroid.setTransactionSuccessful();
        }
        finally {
            ActiveAndroid.endTransaction();
        }
    }

    public void deleteSingleSong(Song song){
        new Delete()
                .from(SongBean.class)
                .where("songname = ?",song.songname)
                .where("singername = ?",song.singername)
                .execute();
    }

    public void deleteQueueSong(SongQueue queue){
        new Delete()
                .from(SongBean.class)
                .where("queue = ?",queue.getId())
                .execute();
    }



    public SongQueue SelectQueue(String Queuename){
        return new Select()
                .from(SongQueue.class)
                .where("name = ?",Queuename)
                .executeSingle();
    }

    public List<Song> ConvertQueue(SongQueue songQueue){
        List<Song> songs = new ArrayList<Song>();
        for (int i=0;i<songQueue.songs().size();i++){
            Song song = new Song();
            song.singername = songQueue.songs().get(i).singername;
            song.seconds = songQueue.songs().get(i).seconds;
            song.singerid = songQueue.songs().get(i).singerid;
            song.songname = songQueue.songs().get(i).songname;
            song.albumid = songQueue.songs().get(i).albumid;
            song.albumpic_big = songQueue.songs().get(i).albumpic;
            song.url = songQueue.songs().get(i).url;
            songs.add(song);
        }
        return songs;
    }

    public List<BmobObject> ConvertQueueToYun(SongQueue queue,String userid){
        List<BmobObject> songs = new ArrayList<>();
        for (int i=0;i<queue.songs().size();i++){
            SongYun song = new SongYun();
            song.singername = queue.songs().get(i).singername;
            song.seconds = queue.songs().get(i).seconds;
            song.singerid = queue.songs().get(i).singerid;
            song.songname = queue.songs().get(i).songname;
            song.albumid = queue.songs().get(i).albumid;
            song.albumpic_big = queue.songs().get(i).albumpic;
            song.url = queue.songs().get(i).url;
            song.userId = userid;
            songs.add(song);
        }
        return songs;
    }


    public void saveToYun(final Context context, List<BmobObject> songYuns,String userId){
        if (songYuns!=null){
            new BmobBatch().insertBatch(songYuns).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if(e==null){
                        for(int i=0;i<list.size();i++){
                            BatchResult result = list.get(i);
                            BmobException ex =result.getError();
                            if(ex==null){
                                Log.e("BMOBsava","第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
                            }else{
                                Log.e("BMOBsava","第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
                            }
                            Toast.makeText(context,"同步成功",Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Log.i("BMOBsava","失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }
    }

    public void deleteFromYun(List<BmobObject> songYuns){
        if (songYuns!=null){
            new BmobBatch().deleteBatch(songYuns).doBatch(new QueryListListener<BatchResult>() {
                @Override
                public void done(List<BatchResult> list, BmobException e) {
                    if(e==null){
                        for(int i=0;i<list.size();i++){
                            BatchResult result = list.get(i);
                            BmobException ex =result.getError();
                            if(ex==null){
                                Log.e("BMOBdelete","第"+i+"个数据批量删除成功");
                            }else{
                                Log.e("BMOBdelete","第"+i+"个数据批量删除失败："+ex.getMessage()+","+ex.getErrorCode());
                            }
                        }
                    }else{
                        Log.i("BMOBdelete","失败："+e.getMessage()+","+e.getErrorCode());
                    }
                }
            });
        }
    }

    public void findAndDeleteFromYun(String userId){
        BmobQuery<SongYun> query = new BmobQuery<>();
        query.addWhereEqualTo("userId",userId);
        query.findObjects(new FindListener<SongYun>() {
            @Override
            public void done(List<SongYun> list, BmobException e) {
                if (e==null){
                    if (list!=null){
                        List<BmobObject> deleteList = new ArrayList<>();
                        for (SongYun songYun:list){
                            deleteList.add(songYun);
                        }
                        deleteFromYun(deleteList);
                    }
                }else {
                    Log.e("bmob","失败："+e.getMessage()+","+e.getErrorCode());

                }
            }
        });
    }

    public void findAndSaveFromYun(String userId, final SongQueue queue){
        BmobQuery<SongYun> query = new BmobQuery<>();
        query.addWhereEqualTo("userId",userId);
        query.findObjects(new FindListener<SongYun>() {
            @Override
            public void done(List<SongYun> list, BmobException e) {
                if (e==null){
                    saveListSongYun(list,queue);
                }else {
                    Log.e("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                }
            }
        });
    }


    public void syncFromYun(Context context,SongQueue queue,String userid){
        if (queue.songs().size()!=0){
            findAndDeleteFromYun(userid);
            saveToYun(context,ConvertQueueToYun(queue,userid),userid);
        }else {
            findAndSaveFromYun(userid,queue);
            Toast.makeText(context,"已从云端导入",Toast.LENGTH_SHORT).show();
        }
    }




}
