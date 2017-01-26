package com.kachidoki.oxgenmusic.model.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

/**
 * Created by mayiwei on 17/1/23.
 */
@Table(name = "SongDownloads")
public class SongDown extends Model {
    @Column(name = "songname")
    public String songname;
    @Column(name = "seconds")
    public int seconds;
    @Column(name = "singerid")
    public int singerid;
    @Column(name = "albumpic")
    public String albumpic;
    @Column(name = "url")
    public String url;
    @Column(name = "singername")
    public String singername;
    @Column(name = "albumid")
    public int albumid;
    @Column(name = "songid")
    public int songid;

    public SongDown(){
        super();
    }
    public SongDown(String songname,int seconds,int singerid,String albumpic,String url,String singername,int albumid,int songid){
        super();
        this.songname = songname;
        this.seconds = seconds;
        this.singerid = singerid;
        this.albumpic = albumpic;
        this.url = url;
        this.singername = singername;
        this.albumid = albumid;
        this.songid = songid;
    }
}
