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
import com.kachidoki.oxgenmusic.widget.LrcView;

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
    @BindView(R.id.play_lrc)
    LrcView lrcView;


    String toBeReadText = "[00&#58;00&#46;92]海阔天空&#32;&#32;&#45;&#32;BEYOND&#10;[00&#58;02&#46;27]词：黄家驹&#10;[00&#58;03&#46;32]曲：黄家驹&#10;[00&#58;04&#46;30]&#10;[00&#58;19&#46;17]今天我&#32;寒夜里看雪飘过&#10;[00&#58;25&#46;75]怀着冷却了的心窝飘远方&#10;[00&#58;30&#46;77]&#10;[00&#58;31&#46;60]风雨里追赶&#32;雾里分不清影踪&#10;[00&#58;37&#46;82]天空海阔你与我&#32;可会变&#10;[00&#58;43&#46;27]&#10;[00&#58;44&#46;14]多少次迎着冷眼与嘲笑&#10;[00&#58;50&#46;55]从没有放弃过心中的理想&#10;[00&#58;56&#46;02]&#10;[00&#58;56&#46;67]一刹那恍惚&#32;若有所失的感觉&#10;[01&#58;02&#46;65]不知不觉已变淡&#32;心里爱&#10;[01&#58;08&#46;64]&#10;[01&#58;09&#46;66]原谅我这一生不羁放纵爱自由&#10;[01&#58;15&#46;56]&#10;[01&#58;16&#46;40]也会怕有一天会跌倒&#10;[01&#58;22&#46;72]背弃了理想谁人都可以&#10;[01&#58;27&#46;84]&#10;[01&#58;28&#46;51]哪会怕有一天只你共我&#10;[01&#58;33&#46;89]&#10;[01&#58;43&#46;41]今天我&#32;寒夜里看雪飘过&#10;[01&#58;49&#46;76]怀着冷却了的心窝飘远方&#10;[01&#58;54&#46;86]&#10;[01&#58;55&#46;60]风雨里追赶&#32;雾里分不清影踪&#10;[02&#58;01&#46;92]天空海阔你与我&#32;可会变&#10;[02&#58;06&#46;61]&#10;[02&#58;08&#46;70]原谅我这一生不羁放纵爱自由&#10;[02&#58;14&#46;86]&#10;[02&#58;15&#46;55]也会怕有一天会跌倒&#10;[02&#58;21&#46;30]&#10;[02&#58;21&#46;83]背弃了理想谁人都可以&#10;[02&#58;27&#46;17]&#10;[02&#58;28&#46;08]哪会怕有一天只你共我&#10;[02&#58;33&#46;08]&#10;[02&#58;38&#46;06]仍然自由自我&#10;[02&#58;40&#46;57]&#10;[02&#58;41&#46;42]永远高唱我歌&#10;[02&#58;44&#46;42]走遍千里&#32;原谅我这一生不羁放纵爱自由&#10;[02&#58;55&#46;20]&#10;[02&#58;56&#46;14]也会怕有一天会跌倒&#10;[03&#58;02&#46;26]背弃了理想&#32;谁人都可以&#10;[03&#58;07&#46;48]&#10;[03&#58;08&#46;67]哪会怕有一天只你共我&#10;[03&#58;13&#46;58]&#10;[03&#58;14&#46;51]原谅我这一生不羁放纵爱自由&#10;[03&#58;21&#46;27]也会怕有一天会跌倒&#10;[03&#58;26&#46;00]&#10;[03&#58;27&#46;38]背弃了理想谁人都可以&#10;[03&#58;31&#46;94]&#10;[03&#58;33&#46;61]哪会怕有一天只你共我";

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
                    ////////////////
                    lrcView.changeCurrent(MusicManager.getMusicManager().getCurrentPosition());

                    playSeekBar.setMax(MusicManager.getMusicManager().getDuration());
                    playSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            if (b) {
                                MusicManager.getMusicManager().seekTo(seekBar.getProgress());
                                lrcView.onDrag(seekBar.getProgress());
                            }

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
        lrcView.setLrcWord(toBeReadText);
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
                ////////////////////
                lrcView.changeCurrent(MusicManager.getMusicManager().getCurrentPosition());
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
