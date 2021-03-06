package com.kachidoki.oxgenmusic.player;

import android.media.MediaPlayer;
import android.util.Log;

import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.model.bean.SongQueue;

import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Created by mayiwei on 16/11/29.
 */
public class MusicManager implements MediaPlayer.OnCompletionListener,MediaPlayer.OnPreparedListener {

    public static SongQueue myList;
    public static SongQueue hotList;
    public enum PlayMode {
       LOOP, RANDOM, REPEAT
    }

    private static MusicManager musicManager = new MusicManager();

    public static MusicManager getMusicManager(){
        return musicManager;
    }

    private PlayerUrlNetTransfer mTransfer;
    private PlayerHistoryReporter mReporter;

    private MediaPlayer mediaPlayer;
    private List<Song> mQueue;
    private int mQueueIndex;
    private PlayMode playMode;
    private Boolean isReady;
    private Boolean isfirst;
    private CallBack callBack;



    public MusicManager(){
        mediaPlayer = new MediaPlayer();
        mTransfer = new PlayerUrlNetTransfer();
        mReporter = new PlayerHistoryReporter();
        mTransfer.setCaller(new PlayerUrlNetTransfer.OnTransferFinsh() {
            @Override
            public void succes(String url) {
                play(getNowSong());
            }

            @Override
            public void fail(Throwable e) {
                Log.e("MusicManager","mTransfer fail "+e.getMessage());
            }
        });
        mediaPlayer.setOnPreparedListener(this);
        mediaPlayer.setOnCompletionListener(this);
        mediaPlayer.reset();

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
     */
    public void justSetIndex(int index){
        mQueueIndex = index;
    }

    public void setIndex(int index){
        mQueueIndex = index;
        play(getNowPlaying());
    }

    public int getIndex(){
        return mQueueIndex;
    }

    public void setQueue(List<Song> queue,int index,boolean play){
        mQueue = queue;
        mQueueIndex = index;
        if (play){
            play(getNowPlaying());
        }

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

    public void swapSong(int from,int to){
        Collections.swap(mQueue, from, to);
    }

    public void deleteSong(int index,boolean isPlaying,boolean isMylist){
        Log.d("Test","before deleteSong "+" size = "+mQueue.size());
        for (int i =0;i<mQueue.size();i++){
            Log.d("Test","before deleteSong"+"i = "+i+" songname = "+mQueue.get(i).songname);
        }
        boolean deleteIsPlaying = index==mQueueIndex;
        if (isMylist){
            mQueue.remove(index);
            if (!(mQueue.size()<1)){
                if (!(mQueueIndex+1<mQueue.size())){
                    mQueueIndex = mQueueIndex-1<0?0:mQueueIndex-1;
                    if (isPlaying&&deleteIsPlaying) play(getNowPlaying());
                }else {
                    if (isPlaying&&deleteIsPlaying) play(getNowPlaying());
                }
            }else {
                mediaPlayer.stop();
                callBack.OnChange();
            }
            Log.d("Test","==================================");
            Log.d("Test","after deleteSong "+" size = "+mQueue.size());
            for (int i =0;i<mQueue.size();i++){
                Log.e("Test","after deleteSong"+"i = "+i+" songname = "+mQueue.get(i).songname);
            }
        }

    }

    public List<Song> getmQueue(){
        return mQueue;
    }

    public boolean checkIsAdd(Song song){
        for (int i=0;i<mQueue.size();i++){
            if (song.songname.equals(mQueue.get(i).songname)) return true;
        }
        return false;
    }

    public boolean playAndCheck(Song song){
        for (int i=0;i<mQueue.size();i++){
            if (song.songname.equals(mQueue.get(i).songname)){
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

    public void setIsfirst(boolean isFirst){
        this.isfirst  = isFirst;
    }

    public boolean getIsReady(){
        return isReady;
    }

    public void start(){
        if (getNowPlaying()!=null){
          if (isfirst){
              play(getNowPlaying());
          }else {
              try {
                  mediaPlayer.start();
              } catch (IllegalStateException e){
                  Log.e("MusicManager",e.getMessage());
              }
          }
        }
        callBack.OnChange();
    }

    public void pause(){
        mediaPlayer.pause();
        callBack.OnChange();
    }

    public void playNow(){
        play(getNowPlaying());
    }

    public void next(){
        isReady = false;
        play(getNextSong());
    }

    public void previous(){
        isReady  = false;
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

    public void seekTo(int position){
        mediaPlayer.seekTo(position);
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
        if (song!=null){
            isfirst = false;
            try {
                mediaPlayer.reset();
                mediaPlayer.setDataSource(song.url);
                mediaPlayer.prepare();
                mReporter.report(song);
            } catch (IOException e) {
                Log.e("MusicManager",e.getMessage());
                mTransfer.getTransferUrl(song);
                e.printStackTrace();
            }
        }
        callBack.OnChange();
    }


    /**
     * 内部歌曲变换算法
     * @return
     */
    private Song getNowPlaying(){
        if (mQueue.isEmpty()){
            return null;
        }
        try {
            Song now = mQueue.get(mQueueIndex);
            return now;
        } catch (IndexOutOfBoundsException e) {
            Log.e("MusicPlayer","must in the wrong queue error: "+e.getMessage());
        }
        return null;
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
        try {
            mediaPlayer.start();
            callBack.OnChange();
        } catch (IllegalStateException e){
            Log.e("MusicManager",e.getMessage());
        }


    }


    /**
     * 指针变换算法
     * @return
     */
    private int getPreviousIndex() {
        if (!((mQueueIndex-1)<0)){
            mQueueIndex = (mQueueIndex - 1) % mQueue.size();
        }else {
            mQueueIndex=0;
        }
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
