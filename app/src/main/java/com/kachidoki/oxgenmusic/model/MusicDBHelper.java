package com.kachidoki.oxgenmusic.model;

import com.activeandroid.query.Delete;
import com.activeandroid.query.Select;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.model.bean.SongBean;
import com.kachidoki.oxgenmusic.model.bean.SongQueue;

import java.util.ArrayList;
import java.util.List;


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
}
