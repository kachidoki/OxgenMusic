package com.kachidoki.oxgenmusic.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.BaseActivity;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.player.DownloadService;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.player.PlayerService;
import com.kachidoki.oxgenmusic.widget.CDview;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.kachidoki.oxgenmusic.player.MusicManager.PlayMode.*;

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
    @BindView(R.id.play_seekBar)
    SeekBar playSeekBar;
    @BindView(R.id.play_nowTime)
    TextView playNowTime;
    @BindView(R.id.play_allTime)
    TextView playAllTime;
    @BindView(R.id.play_backGround)
    LinearLayout backGround;
    @BindView(R.id.play_backImag)
    ImageView backImg;

    private SimpleDateFormat time = new SimpleDateFormat("m:ss");
    private Handler handler = new Handler();
    public Runnable updataProgress = new Runnable() {
        @Override
        public void run() {
            if (MusicManager.getMusicManager().getNowSong()!=null){
                if (MusicManager.getMusicManager().getIsReady()){
                    playAllTime.setText(time.format(MusicManager.getMusicManager().getDuration()));
                    playNowTime.setText(time.format(MusicManager.getMusicManager().getCurrentPosition()));
                    playSeekBar.setProgress(MusicManager.getMusicManager().getCurrentPosition());
                    playSeekBar.setMax(MusicManager.getMusicManager().getDuration());
                    playSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            if (b) MusicManager.getMusicManager().seekTo(seekBar.getProgress());
                        }
                        @Override
                        public void onStartTrackingTouch(SeekBar seekBar) {
                        }
                        @Override
                        public void onStopTrackingTouch(SeekBar seekBar) {
                        }
                    });
                }
            }else {
                playAllTime.setText("");
                playNowTime.setText("");
            }
            handler.postDelayed(updataProgress,500);
        }
    };

    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            cDview.setImage(resource);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            super.onLoadFailed(e, errorDrawable);
            cDview.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.cd_nomal_png));
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

        switch (MusicManager.getMusicManager().getPlayMode()){
            case LOOP:playMode.setImageResource(R.drawable.icon_play_loop);
                break;
            case REPEAT:playMode.setImageResource(R.drawable.icon_play_one);
                break;
            case RANDOM:playMode.setImageResource(R.drawable.icon_play_random);
                break;
        }

        if (MusicManager.getMusicManager().getNowSong()!=null){
            if (MusicManager.getMusicManager().getIsReady()){
                playAllTime.setText(time.format(MusicManager.getMusicManager().getDuration()));
                playNowTime.setText(time.format(MusicManager.getMusicManager().getCurrentPosition()));
                playSeekBar.setMax(MusicManager.getMusicManager().getDuration());
                playSeekBar.setProgress(MusicManager.getMusicManager().getCurrentPosition());
            }
        }
        setBackGround();
        handler.post(updataProgress);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        handler.removeCallbacks(updataProgress);
    }

    @OnClick({R.id.play_more, R.id.play_previous, R.id.play_play, R.id.play_next, R.id.play_mode})
    void sendCommand(View view) {
        switch (view.getId()) {
            case R.id.play_more:
                break;
            case R.id.play_previous:
                Intent previous = new Intent(this, PlayerService.class);
                previous.putExtra("command", PlayerService.CommandPrevious);
                startService(previous);
                loadCDBitmap();
                break;
            case R.id.play_play:
                Intent play = new Intent(this, PlayerService.class);
                play.putExtra("command", PlayerService.CommandPlay);
                startService(play);
                break;
            case R.id.play_next:
                Intent next = new Intent(this, PlayerService.class);
                next.putExtra("command", PlayerService.CommandNext);
                startService(next);
                loadCDBitmap();
                break;
            case R.id.play_mode:
                break;
        }
    }

    @OnClick(R.id.play_more)
    void download(){
        Intent intent = new Intent(PlayActivity.this, DownloadService.class);
        intent.putExtra("command",DownloadService.CommandDownload);
        intent.putExtra("songname", MusicManager.getMusicManager().getNowSong().songname);
        intent.putExtra("url",MusicManager.getMusicManager().getNowSong().url);
        startService(intent);
    }

    @OnClick(R.id.play_toMylist)
    void toMyList() {
        startActivity(new Intent(this, MyPlaylistActivity.class));
    }

    @OnClick(R.id.play_mode)
    void changeMode(){
        switch (MusicManager.getMusicManager().getPlayMode()){
            case LOOP:
                playMode.setImageResource(R.drawable.icon_play_one);
                MusicManager.getMusicManager().setPlayMode(REPEAT);
                break;
            case REPEAT:
                playMode.setImageResource(R.drawable.icon_play_random);
                MusicManager.getMusicManager().setPlayMode(RANDOM);
                break;
            case RANDOM:
                playMode.setImageResource(R.drawable.icon_play_loop);
                MusicManager.getMusicManager().setPlayMode(LOOP);
                break;
        }
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
                if (!MusicManager.getMusicManager().getIsPlaying()) {
                    playPlay.setImageResource(R.drawable.icon_play_play);
                } else {
                    playPlay.setImageResource(R.drawable.icon_play_pause);
                }
                setBackGround();
                break;
        }
    }


    private void loadCDBitmap() {
        Glide.with(getApplicationContext())
                .load(MusicManager.getMusicManager().getNowSong().albumpic_big)
                .asBitmap()
                .into(target);
    }

    private void setBackGround(){
        if (MusicManager.getMusicManager().getNowSong()!=null){
            backGround.getBackground().setAlpha(235);
            getToolbar().getBackground().setAlpha(235);
            Glide.with(PlayActivity.this).load(MusicManager.getMusicManager().getNowSong().albumpic_big).into(backImg);
            getToolbar().setTitle(MusicManager.getMusicManager().getNowSong().songname);
            getToolbar().setSubtitle(MusicManager.getMusicManager().getNowSong().singername);
        }else {
            backGround.getBackground().setAlpha(255);
            getToolbar().setTitle("正在播放");
            getToolbar().setSubtitle("");
        }

    }
}
