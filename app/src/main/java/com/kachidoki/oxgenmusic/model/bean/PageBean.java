package com.kachidoki.oxgenmusic.model.bean;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mayiwei on 16/11/17.
 */
public class PageBean {
    public @SerializedName("songlist") List<Song> songLists;
    public int total_song_num;
    public int ret_code;
    public String update_time;
    public int color;
    public int cur_song_num;
    public int comment_num;
    public int currentPage;
    public int song_begin;
    public int totalpage;
}
