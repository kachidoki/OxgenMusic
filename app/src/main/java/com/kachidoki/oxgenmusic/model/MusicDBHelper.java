package com.kachidoki.oxgenmusic.model;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.activeandroid.ActiveAndroid;
import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.activeandroid.query.Update;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.model.bean.SongBean;
import com.kachidoki.oxgenmusic.model.bean.SongQueue;
import com.kachidoki.oxgenmusic.model.bean.SongYun;
import com.kachidoki.oxgenmusic.player.MusicManager;

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
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;


/**
 * Created by mayiwei on 16/12/6.
 */
public class MusicDBHelper {

    private static MusicDBHelper musicDBHelper = new MusicDBHelper();
    public static MusicDBHelper getMusicDBHelper(){
        return musicDBHelper;
    }

    public void saveSong(Song song,SongQueue songQueue){
        new SongBean(song.songname,song.seconds,song.singerid,song.albumpic_big,song.url,song.singername,song.albumid,song.songid,songQueue).save();
    }

    public void saveQueue(SongQueue songQueue){
        songQueue.save();
    }

    public void saveListSong(List<Song> songs,SongQueue songQueue){
        ActiveAndroid.beginTransaction();
        try {
            for (int i = 0; i < songs.size(); i++) {
                new SongBean(songs.get(i).songname,songs.get(i).seconds,songs.get(i).singerid,songs.get(i).albumpic_big,songs.get(i).url,songs.get(i).singername,songs.get(i).albumid,songs.get(i).songid,songQueue).save();
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
                new SongBean(songYun.songname,songYun.seconds,songYun.singerid,songYun.albumpic_big,songYun.url,songYun.singername,songYun.albumid,songYun.songid,songQueue).save();
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
                .where("queue = ?", MusicManager.myList.getId())
                .execute();
    }

    public void swapSongs(Song fromSong,Song toSong){
        long fromId = new Select().from(SongBean.class).where("songname = ?",fromSong.songname).where("singername = ?",fromSong.singername).where("queue = ?", MusicManager.myList.getId()).execute().get(0).getId();
        long toId = new Select().from(SongBean.class).where("songname = ?",toSong.songname).where("singername = ?",toSong.singername).where("queue = ?", MusicManager.myList.getId()).execute().get(0).getId();
        new Update(SongBean.class)
                .set("songname=?," + "seconds=?,"+ "singerid=?,"+ "albumpic=?,"+ "url=?,"+ "singername=?,"+ "albumid=?,"+"songid=?",toSong.songname,toSong.seconds,toSong.singerid,toSong.albumpic_big,toSong.url,toSong.singername,toSong.albumid,toSong.songid)
                .where("Id = ?",fromId).execute();
        new Update(SongBean.class)
                .set("songname=?," + "seconds=?,"+ "singerid=?,"+ "albumpic=?,"+ "url=?,"+ "singername=?,"+ "albumid=?,"+"songid=?",fromSong.songname,fromSong.seconds,fromSong.singerid,fromSong.albumpic_big,fromSong.url,fromSong.singername,fromSong.albumid,fromSong.songid)
                .where("Id = ?",toId).execute();
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

    public boolean checkIsAdd(String songname,SongQueue songQueue){
        List<SongBean> list = SelectQueue(songQueue.name).songs();
        for (SongBean songBean:list){
            if (songname.equals(songBean.songname)) return true;
        }
        return false;
    }


    public List<Song> fastConvertQueue(int index,SongQueue songQueue){
        List<SongBean> songBeanList = songQueue.songs();
        if (songBeanList!=null){
            int half = 10;
            boolean isless = index - half<0;
            boolean ismore = index + half>songBeanList.size()-1;
            List<Song> songs = new ArrayList<>();
            for (int i = isless?0:index-half;i<(ismore?songBeanList.size():index+half);i++){
                Song song = new Song();
                song.singername = songQueue.songs().get(i).singername;
                song.seconds = songQueue.songs().get(i).seconds;
                song.singerid = songQueue.songs().get(i).singerid;
                song.songname = songQueue.songs().get(i).songname;
                song.albumid = songQueue.songs().get(i).albumid;
                song.albumpic_big = songQueue.songs().get(i).albumpic;
                song.songid = songQueue.songs().get(i).songid;
                song.url = songQueue.songs().get(i).url;
                songs.add(song);
            }
            return songs;
        }
        return null;
    }

    public Observable<List<Song>> RxFastConvertQueue(final int index, final SongQueue songQueue){
        final boolean isSmall = songQueue.songs().size()<40;
        return Observable.create(new Observable.OnSubscribe<List<Song>>() {
            @Override
            public void call(Subscriber<? super List<Song>> subscriber) {
                if (isSmall){
                    subscriber.onNext(ConvertQueue(songQueue));
                    subscriber.onCompleted();
                }else {
                    subscriber.onNext(fastConvertQueue(index,songQueue));
                    subscriber.onCompleted();
                }
            }
        });

    }



    public List<Song> ConvertQueue(SongQueue songQueue){
        if (songQueue.songs()!=null){
            List<Song> songs = new ArrayList<Song>();
            for (int i=0;i<songQueue.songs().size();i++){
                Song song = new Song();
                song.singername = songQueue.songs().get(i).singername;
                song.seconds = songQueue.songs().get(i).seconds;
                song.singerid = songQueue.songs().get(i).singerid;
                song.songname = songQueue.songs().get(i).songname;
                song.albumid = songQueue.songs().get(i).albumid;
                song.albumpic_big = songQueue.songs().get(i).albumpic;
                song.songid = songQueue.songs().get(i).songid;
                song.url = songQueue.songs().get(i).url;
                songs.add(song);
            }
            return songs;
        }
        return null;
    }

    public Observable<List<Song>> RxConvertQueue(final SongQueue songQueue){
        return Observable.create(new Observable.OnSubscribe<List<Song>>() {
            @Override
            public void call(Subscriber<? super List<Song>> subscriber) {
                subscriber.onNext(ConvertQueue(songQueue));
                subscriber.onCompleted();
            }
        });
    }


    //云

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
            song.songid = queue.songs().get(i).songid;
            songs.add(song);
        }
        return songs;
    }


//    public void saveToYun(final Context context, List<BmobObject> songYuns,String userId){
//        if (songYuns!=null){
//            new BmobBatch().insertBatch(songYuns).doBatch(new QueryListListener<BatchResult>() {
//                @Override
//                public void done(List<BatchResult> list, BmobException e) {
//                    if(e==null){
//                        for(int i=0;i<list.size();i++){
//                            BatchResult result = list.get(i);
//                            BmobException ex =result.getError();
//                            if(ex==null){
//                                Log.e("BMOBsava","第"+i+"个数据批量添加成功："+result.getCreatedAt()+","+result.getObjectId()+","+result.getUpdatedAt());
//                            }else{
//                                Log.e("BMOBsava","第"+i+"个数据批量添加失败："+ex.getMessage()+","+ex.getErrorCode());
//                            }
//                            Toast.makeText(context,"同步成功",Toast.LENGTH_SHORT).show();
//                        }
//                    }else{
//                        Log.i("BMOBsava","失败："+e.getMessage()+","+e.getErrorCode());
//                    }
//                }
//            });
//        }
//    }

//    public void deleteFromYun(List<BmobObject> songYuns){
//        if (songYuns!=null){
//            new BmobBatch().deleteBatch(songYuns).doBatch(new QueryListListener<BatchResult>() {
//                @Override
//                public void done(List<BatchResult> list, BmobException e) {
//                    if(e==null){
//                        for(int i=0;i<list.size();i++){
//                            BatchResult result = list.get(i);
//                            BmobException ex =result.getError();
//                            if(ex==null){
//                                Log.e("BMOBdelete","第"+i+"个数据批量删除成功");
//                            }else{
//                                Log.e("BMOBdelete","第"+i+"个数据批量删除失败："+ex.getMessage()+","+ex.getErrorCode());
//                            }
//                        }
//                    }else{
//                        Log.i("BMOBdelete","失败："+e.getMessage()+","+e.getErrorCode());
//                    }
//                }
//            });
//        }
//    }

//    public void findAndDeleteFromYun(String userId){
//        BmobQuery<SongYun> query = new BmobQuery<>();
//        query.addWhereEqualTo("userId",userId);
//        query.findObjects(new FindListener<SongYun>() {
//            @Override
//            public void done(List<SongYun> list, BmobException e) {
//                if (e==null){
//                    if (list!=null){
//                        List<BmobObject> deleteList = new ArrayList<>();
//                        for (SongYun songYun:list){
//                            deleteList.add(songYun);
//                        }
//                        deleteFromYun(deleteList);
//                    }
//                }else {
//                    Log.e("bmob","失败："+e.getMessage()+","+e.getErrorCode());
//
//                }
//            }
//        });
//    }

//    public void findAndSaveFromYun(String userId, final SongQueue queue){
//        BmobQuery<SongYun> query = new BmobQuery<>();
//        query.addWhereEqualTo("userId",userId);
//        query.findObjects(new FindListener<SongYun>() {
//            @Override
//            public void done(List<SongYun> list, BmobException e) {
//                if (e==null){
//                    saveListSongYun(list,queue);
//                }else {
//                    Log.e("bmob","失败："+e.getMessage()+","+e.getErrorCode());
//                }
//            }
//        });
//    }


//    public void syncFromYun(Context context,SongQueue queue,String userid){
//        if (userid!=null&&!userid.equals("")){
//            if (queue!=null&&queue.songs().size()!=0){
//                findAndDeleteFromYun(userid);
//                saveToYun(context,ConvertQueueToYun(queue,userid),userid);
//            }else {
//                findAndSaveFromYun(userid,queue);
//                Toast.makeText(context,"已从云端导入",Toast.LENGTH_SHORT).show();
//            }
//        }else {
//            Toast.makeText(context,"无效的用户信息",Toast.LENGTH_SHORT).show();
//        }
//
//    }


    public void syncYun(Context context,SongQueue queue,String userid){
        if (userid!=null&&!userid.equals("")){
            if (queue!=null&&queue.songs().size()!=0){
                QueryAndDeleteFromYun(context,userid,queue);
            }else {
                QueryAndSaveFromYun(context,userid,queue);
            }
        }else {
            Toast.makeText(context,"无效的用户信息",Toast.LENGTH_SHORT).show();
        }
    }


    public Observable<List<SongYun>> QueryYun(String userId){
        BmobQuery<SongYun> query = new BmobQuery<>();
        query.addWhereEqualTo("userId",userId);
        return query.findObjectsObservable(SongYun.class);
    }

    public void QueryAndSaveFromYun(final Context context, String userId, final SongQueue queue){
        QueryYun(userId).subscribe(new Subscriber<List<SongYun>>() {
            @Override
            public void onCompleted() {
                Toast.makeText(context,"已从云端导入",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("bmob","失败："+throwable.getMessage());
            }

            @Override
            public void onNext(List<SongYun> songYuns) {
                saveListSongYun(songYuns,queue);
            }
        });
    }


    public void QueryAndDeleteFromYun(final Context context,final String userId, final SongQueue queue){
        QueryYun(userId).concatMap(new Func1<List<SongYun>,Observable<List<BatchResult>>>(){
            @Override
            public Observable<List<BatchResult>> call(List<SongYun> songYuns) {
                List<BmobObject> deleteList = new ArrayList<>();
                for (SongYun songYun:songYuns){
                    deleteList.add(songYun);
                }
                return new BmobBatch().deleteBatch(deleteList).doBatchObservable();
            }
        }).onErrorReturn(new Func1<Throwable, List<BatchResult>>() {
            @Override
            public List<BatchResult> call(Throwable throwable) {
                return null;
            }
        })
        .doOnNext(new Action1<List<BatchResult>>() {
            @Override
            public void call(List<BatchResult> batchResults) {
                if (batchResults!=null){
                    for (int i=0;i<batchResults.size();i++){
                        BatchResult result = batchResults.get(i);
                        BmobException ex =result.getError();
                        if(ex!=null){
                            Log.e("Bomb","第"+i+"个数据批量删除失败："+ex.getMessage()+","+ex.getErrorCode());
                        }
                    }
                }
            }
        }).concatMap(new Func1<List<BatchResult>, Observable<List<BatchResult>>>() {
            @Override
            public Observable<List<BatchResult>> call(List<BatchResult> batchResults) {
                return new BmobBatch().insertBatch(ConvertQueueToYun(queue,userId)).doBatchObservable();
            }
        }).doOnNext(new Action1<List<BatchResult>>() {
            @Override
            public void call(List<BatchResult> batchResults) {
                for (int i=0;i<batchResults.size();i++){
                    BatchResult result = batchResults.get(i);
                    BmobException ex =result.getError();
                    if(ex!=null){
                        Log.e("Bomb","第"+i+"个数据批量上传失败："+ex.getMessage()+","+ex.getErrorCode());
                    }
                }
            }
        }).subscribe(new Subscriber<List<BatchResult>>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable throwable) {
                Log.e("Test",throwable.getMessage());
                Toast.makeText(context,"上传数据失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNext(List<BatchResult> batchResults) {
                Toast.makeText(context,"已上传数据到云端",Toast.LENGTH_SHORT).show();
            }
        });
    }




}
