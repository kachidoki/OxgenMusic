package com.kachidoki.oxgenmusic.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.App;
import com.kachidoki.oxgenmusic.app.BaseActivity;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.widget.CDview;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mayiwei on 16/11/30.
 */
public class PlayActivity extends BaseActivity {
    @BindView(R.id.play_more)
    ImageView playMore;
    @BindView(R.id.play_previous)
    ImageView playPrevious;
    @BindView(R.id.play_play)
    ImageView playPlay;
    @BindView(R.id.play_next)
    ImageView playNext;
    @BindView(R.id.play_mode)
    ImageView playMode;
    @BindView(R.id.play_cdView)
    CDview cDview;
    @BindView(R.id.play_toMylist)
    CardView playToMylist;

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
        setContentView(R.layout.activity_play);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        setToolbar(true);
        cDview.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.cd_nomal));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MusicManager.getMusicManager().getIsPlaying()) {
            playPlay.setImageResource(R.drawable.icon_play_pause);
        } else {
            playPlay.setImageResource(R.drawable.icon_play_play);
        }

        if (MusicManager.getMusicManager().getNowSong() != null) {
            loadCDBitmap();
        }

        if (MusicManager.getMusicManager().getIsPlaying()) {
            cDview.start();
        } else {
            cDview.pause();
        }


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @OnClick({R.id.play_more, R.id.play_previous, R.id.play_play, R.id.play_next, R.id.play_mode})
    void sendCommand(View view) {
        switch (view.getId()) {
            case R.id.play_more:
                break;
            case R.id.play_previous:
                App.playEvent.setAction(PlayEvent.Action.PREVIOES);
                EventBus.getDefault().post(App.playEvent);
                loadCDBitmap();
                break;
            case R.id.play_play:
                if (MusicManager.getMusicManager().getIsPlaying()) {
                    App.playEvent.setAction(PlayEvent.Action.PAUSE);
                    cDview.pause();
                    playPlay.setImageResource(R.drawable.icon_play_play);
                } else {
                    App.playEvent.setAction(PlayEvent.Action.PLAY);
                    cDview.start();
                    playPlay.setImageResource(R.drawable.icon_play_pause);
                }
                EventBus.getDefault().post(App.playEvent);
                break;
            case R.id.play_next:
                App.playEvent.setAction(PlayEvent.Action.NEXT);
                EventBus.getDefault().post(App.playEvent);
                loadCDBitmap();
                break;
            case R.id.play_mode:
                break;
        }
    }

    @OnClick(R.id.play_toMylist)
    void toMyList(){
        startActivity(new Intent(this,MyPlaylistActivity.class));
    }


    @Subscribe
    public void onEvent(PlayEvent playEvent) {
        switch (playEvent.getAction()) {
            case CHANGE:
                if (MusicManager.getMusicManager().getNowSong() != null) {
                    loadCDBitmap();
                }
                if (MusicManager.getMusicManager().getIsPlaying()) {
                    cDview.start();
                } else {
                    cDview.pause();
                }
                break;

        }
    }


    private void loadCDBitmap() {
        Glide.with(getApplicationContext())
                .load(MusicManager.getMusicManager().getNowSong().albumpic_big)
                .asBitmap()
                .into(target);
    }
}
