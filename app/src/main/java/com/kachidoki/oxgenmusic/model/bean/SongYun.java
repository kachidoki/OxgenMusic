package com.kachidoki.oxgenmusic.model.bean;

import android.support.annotation.NonNull;

import cn.bmob.v3.BmobObject;

/**
 * Created by mayiwei on 16/12/11.
 */
public class SongYun extends BmobObject {
    public String songname;
    public int seconds;
    public int singerid;
    public String albumpic_big;
    public String url;
    public String singername;
    public int albumid;
    public String queue;
    @NonNull
    public String userId;
    public int songid;
    public SongYun(){}
    public SongYun(String songName,int second,int singerId,String albumpic,String Url,String singerName,int albumId,int songid,String que,String userid){
        this.songname = songName;
        this.seconds = second;
        this.singerid = singerId;
        this.albumpic_big = albumpic;
        this.url = Url;
        this.singername = singerName;
        this.albumid = albumId;
        this.queue = que;
        this.userId = userid;
        this.songid = songid;
    }
}
