package com.kachidoki.oxgenmusic.model.event;



/**
 * Created by mayiwei on 16/11/12.
 */
public class PlayEvent {

    public enum Action {
        CHANGE,CHANGESONG
    }

    private Action mAction;


    public Action getAction() {
        return mAction;
    }

    public void setAction(Action action) {
        mAction = action;
    }



}