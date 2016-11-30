package com.kachidoki.oxgenmusic.model.event;


import com.kachidoki.oxgenmusic.model.bean.Song;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mayiwei on 16/11/12.
 */
public class PlayEvent {

    public enum Action {
        PLAY, PAUSE, NEXT, PREVIOES
    }

    private Action mAction;
    private Song mSong;


    public Song getSong() {
        return mSong;
    }

    public void setSong(Song song) {
        mSong = song;
    }

    public Action getAction() {
        return mAction;
    }

    public void setAction(Action action) {
        mAction = action;
    }



}