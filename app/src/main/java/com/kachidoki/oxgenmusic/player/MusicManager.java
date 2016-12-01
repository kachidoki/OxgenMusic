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
       LOOP, RANDOM, REPEAT
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
    private CallBack callBack;



    public MusicManager(){
        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);

        mQueue = new ArrayList<>();
        mQueueIndex = 0;
        playMode = PlayMode.LOOP;
        isReady = false;
        isfirst = true;
    }

    public interface CallBack{
        void OnChange();
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
    public void addQueuePlay(Song song){
        mQueue.add(song);
        mQueueIndex = mQueue.size()-1;
    }


    public void addQueue(Song song){
        mQueue.add(song);
    }

    public void addQueue(List<Song> songs){
        mQueue.addAll(songs);
    }

    public List<Song> getmQueue(){
        return mQueue;
    }

    public boolean checkIsAdd(Song song){
        for (int i=0;i<mQueue.size();i++){
            if (song.songname==mQueue.get(i).songname) return true;
        }
        return false;
    }

    public boolean playAndCheck(Song song){
        for (int i=0;i<mQueue.size();i++){
            if (song.songname==mQueue.get(i).songname){
                mQueueIndex = i;
                play(getNowPlaying());
                return true;
            }
        }
        return false;
    }

    /**
     * MusicManager调用给外部的接口
     */

    public void setCallBack(CallBack callBack){
        this.callBack = callBack;
    }

    public boolean getIsfirst(){
        return isfirst;
    }

    public boolean getIsReady(){
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

    public void playNow(){
        play(getNowPlaying());
    }

    public void next(){
        play(getNextSong());
    }

    public void previous(){
        play(getPreviousSong());
    }

    public void stop(){
        mediaPlayer.stop();
    }

    public Song getNowSong(){
        return getNowPlaying();
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
            mediaPlayer.setDataSource(song.url);
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
        callBack.OnChange();
    }

    @Override
    public void onPrepared(MediaPlayer mediaPlayer) {
        isReady = true;
        mediaPlayer.start();
        callBack.OnChange();
    }


    /**
     * 指针变换算法
     * @return
     */
    private int getPreviousIndex() {
        mQueueIndex = (mQueueIndex - 1) % mQueue.size();
        return mQueueIndex;
    }

    private int getRandomIndex() {
        mQueueIndex = new Random().nextInt(mQueue.size()) % mQueue.size();
        return mQueueIndex;
    }


    private int getNextIndex() {
        mQueueIndex = (mQueueIndex + 1) % mQueue.size();
        return mQueueIndex;
    }



}
