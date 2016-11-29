package com.kachidoki.oxgenmusic.player;

import android.media.MediaPlayer;

import com.kachidoki.oxgenmusic.model.bean.Song;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by mayiwei on 16/11/29.
 */
public class MusicManager implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener {

    private enum PlayMode {
        NOMAL,LOOP, RANDOM, REPEAT
    }

    private static MusicManager musicManager = new MusicManager();

    public static MusicManager getMusicManager(){
        return musicManager;
    }

    private MediaPlayer mediaPlayer;
    private List<Song> mQueue;
    private int mQueueIndex;
    private PlayMode playMode;
    private Boolean isReady;

    private Boolean getIsReady(){
        return isReady;
    }

    public MusicManager(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

        mQueue = new ArrayList<>();
        mQueueIndex = 0;
        playMode = PlayMode.NOMAL;
        isReady = false;
    }

    public void setmQueue(List<Song> queue,int index){
        mQueue = queue;
        mQueueIndex = index;
        play(getNowPlaying());
    }

    private void play(Song song){
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.downUrl);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        mediaPlayer.start();
    }

    public void pause(){
        mediaPlayer.pause();
    }

    public void resume(){
        mediaPlayer.start();
    }

    public void next(){
        if (mQueueIndex>0&&mQueueIndex<(mQueue.size()-1)){
            play(getNextSong());
        }else {
            mediaPlayer.reset();
        }
    }

    public void previous(){
        if (mQueueIndex>0&&mQueueIndex<(mQueue.size()-1)){
            play(getPreviousSong());
        }else {
            mediaPlayer.reset();
        }

    }

    private Song getNowPlaying(){
        if (mQueue.isEmpty()){
            return null;
        }
        return mQueue.get(mQueueIndex);
    }

    private Song getNextSong(){
        if (mQueue.isEmpty()){
            return null;
        }
        switch (playMode){
            case NOMAL:
                return mQueue.get(getNextIndex());
            case LOOP:
                return mQueue.get(getNextIndex());
            case RANDOM:
                return mQueue.get(getRandomIndex());
            case REPEAT:
                return mQueue.get(mQueueIndex);
        }
        return null;
    }

    private Song getPreviousSong(){
        if (mQueue.isEmpty()){
            return null;
        }
        switch (playMode){
            case NOMAL:
                return mQueue.get(getPreviousIndex());
            case LOOP:
                return mQueue.get(getPreviousIndex());
            case RANDOM:
                return mQueue.get(getRandomIndex());
            case REPEAT:
                return mQueue.get(mQueueIndex);
        }
        return null;
    }

    public int getCurrentPosition() {
        if (getNowPlaying() != null) {
            return mediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (getNowPlaying() != null) {
            return mediaPlayer.getDuration();
        }
        return 0;
    }

    public PlayMode getPlayMode() {
        return playMode;
    }

    public void setPlayMode(PlayMode playMode) {
        this.playMode = playMode;
    }


    @Override
    public void onCompletion(MediaPlayer mediaPlayer) {
        isReady = false;
        next();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        isReady = true;
        mediaPlayer.start();
    }


    private int getPreviousIndex() {
        if (playMode==PlayMode.NOMAL){
            if ((mQueueIndex-1)<0){
                mQueueIndex = -1;
                return mQueueIndex;
            }else {
                mQueueIndex = mQueueIndex - 1;
                return mQueueIndex;
            }
        }else {
            mQueueIndex = (mQueueIndex - 1) % mQueue.size();
            return mQueueIndex;
        }

    }

    private int getRandomIndex() {
        mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
        return mQueueIndex;
    }


    private int getNextIndex() {
        if (playMode==PlayMode.NOMAL){
            if ((mQueueIndex+1)>(mQueue.size()-1)){
                mQueueIndex = -1;
                return mQueueIndex;
            }else {
                mQueueIndex = mQueueIndex + 1;
                return mQueueIndex;
            }
        }else {
            mQueueIndex = (mQueueIndex + 1) % mQueue.size();
            return mQueueIndex;
        }


    }

    private void release() {
        mediaPlayer.release();
        mediaPlayer = null;
    }

}
