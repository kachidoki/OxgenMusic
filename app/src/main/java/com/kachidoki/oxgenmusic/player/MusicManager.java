package com.kachidoki.oxgenmusic.player;

import android.media.MediaPlayer;

import com.kachidoki.oxgenmusic.model.bean.Song;

import java.io.IOException;
import java.security.PublicKey;
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
    private Boolean isfirst;



    public MusicManager(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

        mQueue = new ArrayList<>();
        mQueueIndex = 0;
        playMode = PlayMode.NOMAL;
        isReady = false;
        isfirst = true;
    }


    /**
     * 播放队列管理
     * @param queue
     * @param index
     */
    public void setQueue(List<Song> queue,int index){
        mQueue = queue;
        mQueueIndex = index;
        play(getNowPlaying());
    }

    public void addQueue(Song song){
        mQueue.add(song);
    }

    public void addQueue(List<Song> songs){
        mQueue.addAll(songs);
    }


    /**
     * MusicManager调用给外部的接口
     */

    public Boolean getIsReady(){
        return isReady;
    }

    public void start(){
        if (getNowPlaying()!=null){
          if (isfirst==true){
              play(getNowPlaying());
          }else {
              mediaPlayer.start();
          }
        }
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

    public void stop(){
        mediaPlayer.stop();
    }

    public boolean getIsPlaying(){
        if (getNowPlaying()!=null){
            return mediaPlayer.isPlaying();
        }
        return false;
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

    public void release() {
        mediaPlayer.release();
        mediaPlayer = null;
    }



    /**
     * 内部使用的播放函数
     * @param song
     */
    private void play(Song song){
        isfirst = false;
        try {
            mediaPlayer.reset();
            mediaPlayer.setDataSource(song.downUrl);
            mediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 内部歌曲变换算法
     * @return
     */
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


    /**
     * 指针变换算法
     * @return
     */
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



}
