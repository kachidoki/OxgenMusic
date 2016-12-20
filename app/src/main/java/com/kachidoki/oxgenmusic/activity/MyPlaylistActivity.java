package com.kachidoki.oxgenmusic.activity;

import android.annotation.TargetApi;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.Slide;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.BaseActivity;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.AdapterMylist;
import com.kachidoki.oxgenmusic.model.MusicDBHelper;
import com.kachidoki.oxgenmusic.model.bean.SongBean;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.player.PlayerService;
import com.kachidoki.oxgenmusic.utils.SPUtils;
import com.kachidoki.oxgenmusic.widget.CDview;
import com.kachidoki.oxgenmusic.widget.PopWindowMylist;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mayiwei on 16/12/1.
 */
public class MyPlaylistActivity extends BaseActivity {
    @BindView(R.id.mylist_cdview)
    CDview cDview;
    @BindView(R.id.mylist_count)
    TextView mylistCount;
    @BindView(R.id.mylist_countTime)
    TextView mylistCountTime;
    @BindView(R.id.mylist_playall)
    ImageView mylistPlayall;
    @BindView(R.id.recyclerView_mylist)
    RecyclerView recyclerViewMylist;
    @BindView(R.id.myList_backGround)
    LinearLayout backGround;
    @BindView(R.id.mylist_backImag)
    ImageView backImg;
    @BindView(R.id.mylist_fab)
    FloatingActionButton fab;

    AdapterMylist adapter = new AdapterMylist(MyPlaylistActivity.this, new PopWindowMylist.OnChange() {
        @Override
        public void Callback(int i) {
            countAllTime();
        }
    });

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            cDview.setImage(resource);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            super.onLoadFailed(e, errorDrawable);
            cDview.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.cd_nomal));
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mylist);
        setActivityAnimation();
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);

        setToolbar(true);

        recyclerViewMylist.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMylist.setAdapter(adapter);
        cDview.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.cd_nomal));

        getMylist();
        countAllTime();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MusicManager.getMusicManager().getNowSong()!=null){
            loadCDBitmap();
        }

        if (MusicManager.getMusicManager().getIsPlaying()){
            cDview.start();
            fab.setImageResource(R.mipmap.ic_pause_black_24dp);
        }else {
            cDview.pause();
            fab.setImageResource(R.mipmap.ic_play_arrow_black_24dp);
        }

        setBackGround();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(PlayEvent playEvent){
        switch (playEvent.getAction()) {
            case CHANGE:
                if (MusicManager.getMusicManager().getNowSong()!=null){
                    loadCDBitmap();
                }
                if (MusicManager.getMusicManager().getIsPlaying()){
                    cDview.start();
                    fab.setImageResource(R.mipmap.ic_pause_black_24dp);
                }else {
                    cDview.pause();
                    fab.setImageResource(R.mipmap.ic_play_arrow_black_24dp);
                }
                setBackGround();
                break;
        }
    }


    private void loadCDBitmap(){
        Glide.with(getApplicationContext())
                .load(MusicManager.getMusicManager().getNowSong().albumpic_big )
                .asBitmap()
                .into( target );
    }

    private void getMylist() {
        if (MusicDBHelper.getMusicDBHelper().ConvertQueue(MusicManager.myList)!=null){
            adapter.setData(MusicDBHelper.getMusicDBHelper().ConvertQueue(MusicManager.myList));
        }
    }

    private void countAllTime(){
        final List<SongBean> list = MusicManager.myList.songs();
        if (list!=null){
            int countTime = 0;
            for (int i=0;i<list.size();i++){
                countTime = countTime+list.get(i).seconds;
            }
            if (countTime<3600){
                mylistCountTime.setText(countTime/60+"分");
            }else {
                mylistCountTime.setText(countTime/3600+"时"+(countTime-(countTime/3600)*3600)/60+"分");
            }
            mylistCount.setText(list.size()+"首");
        }
    }

    @OnClick(R.id.mylist_playall)
    void myListplayAll(){
        MusicManager.getMusicManager().setQueue(MusicDBHelper.getMusicDBHelper().ConvertQueue(MusicDBHelper.getMusicDBHelper().SelectQueue(Constants.myList)),0,true);
        if (!SPUtils.get(this, Constants.nowQueue_sp,"noQueue").equals(Constants.myList)){
            SPUtils.put(this,Constants.nowQueue_sp,Constants.myList);
        }
    }

    @OnClick(R.id.mylist_cdview)
    void toPlayActivity(){
        startActivity(new Intent(this,PlayActivity.class));
    }


    @OnClick(R.id.mylist_fab)
    void play(){
        Intent play = new Intent(this, PlayerService.class);
        play.putExtra("command", PlayerService.CommandPlay);
        startService(play);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setActivityAnimation(){
        getWindow().setEnterTransition(new Slide(Gravity.RIGHT).setDuration(1000));
    }

    private void setBackGround(){
        if (MusicManager.getMusicManager().getNowSong()!=null){
            backGround.getBackground().setAlpha(230);
            getToolbar().getBackground().setAlpha(230);
            Glide.with(MyPlaylistActivity.this).load(MusicManager.getMusicManager().getNowSong().albumpic_big).into(backImg);
        }else {
            backGround.getBackground().setAlpha(255);
            getToolbar().getBackground().setAlpha(255);
        }

    }
}
