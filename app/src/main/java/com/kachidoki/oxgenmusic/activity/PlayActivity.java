package com.kachidoki.oxgenmusic.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.activity.fragment.CdViewFragment;
import com.kachidoki.oxgenmusic.activity.fragment.LrcViewFragment;
import com.kachidoki.oxgenmusic.app.BaseActivity;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.MusicDBHelper;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.model.bean.SongDown;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.player.DownloadService;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.player.PlayerService;
import com.kachidoki.oxgenmusic.utils.SPUtils;


import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

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
    @BindView(R.id.play_viewpager)
    ViewPager viewPager;
    @BindView(R.id.play_container)
    LinearLayout container;


    private List<ImageView> DotList = new ArrayList<>();//导航图集合
    private int mCurrentIndex = 0;//当前小圆点的位置
    private List<Fragment> fragments = new ArrayList<>();

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
//                    lrcView.changeCurrent(MusicManager.getMusicManager().getCurrentPosition());

                    playSeekBar.setMax(MusicManager.getMusicManager().getDuration());
                    playSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                        @Override
                        public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                            if (b) {
                                MusicManager.getMusicManager().seekTo(seekBar.getProgress());
//                                lrcView.onDrag(seekBar.getProgress());
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



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        EventBus.getDefault().register(this);
        ButterKnife.bind(this);
        setToolbar(true);

        initViewPager();

    }


    private void initViewPager(){
        fragments.add(new CdViewFragment());
        fragments.add(new LrcViewFragment());
        for (int i = 0; i < fragments.size(); i++) {
            ImageView imageView = new ImageView(this);
            DotList.add(imageView);
            ImageView dot = new ImageView(this);
            if (i == mCurrentIndex) {
                dot.setImageResource(R.drawable.page_now);
            } else {
                dot.setImageResource(R.drawable.page);
            }
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            if (i > 0) {
                params.leftMargin = 2;
            }
            dot.setLayoutParams(params);
            container.addView(dot);
        }
        viewPager.setAdapter(new MyViewPagerAdapter(getSupportFragmentManager()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                mCurrentIndex = position;
                for (int i = 0; i < container.getChildCount(); i++) {
                    ImageView imageView = (ImageView) container.getChildAt(i);
                    if (i == position) {
                        imageView.setImageResource(R.drawable.page_now);
                    } else {
                        imageView.setImageResource(R.drawable.page);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MusicManager.getMusicManager().getIsPlaying()) {
            playPlay.setImageResource(R.drawable.icon_play_pause);
        } else {
            playPlay.setImageResource(R.drawable.icon_play_play);
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

    @OnClick({R.id.play_previous, R.id.play_play, R.id.play_next})
    void sendCommand(View view) {
        switch (view.getId()) {
            case R.id.play_previous:
                Intent previous = new Intent(this, PlayerService.class);
                previous.putExtra("command", PlayerService.CommandPrevious);
                startService(previous);
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
                break;
        }
    }

    @OnClick(R.id.play_more)
    void download(){
        Log.e("Test","nowQueue ="+SPUtils.get(PlayActivity.this, Constants.nowQueue_sp,"noQueue"));
        Log.e("Test","hotListname_sp ="+SPUtils.get(PlayActivity.this, Constants.hotListname_sp,"nocall"));
        if (SPUtils.get(PlayActivity.this, Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)
                &&(SPUtils.get(PlayActivity.this, Constants.hotListname_sp,"nocall").equals("mylocal")
                ||SPUtils.get(PlayActivity.this, Constants.hotListname_sp,"nocall").equals("mydown"))){
            Toast.makeText(PlayActivity.this,"这首是本地歌曲",Toast.LENGTH_SHORT).show();
        }else {
            MusicDBHelper.getMusicDBHelper().RxGetDownsongs()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Subscriber<List<Song>>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable throwable) {

                        }

                        @Override
                        public void onNext(List<Song> songs) {
                            if (MusicDBHelper.getMusicDBHelper().checkIsDown(MusicManager.getMusicManager().getNowSong().songname,songs)){
                                Toast.makeText(PlayActivity.this, "已经下载", Toast.LENGTH_SHORT).show();
                            }else {
                                Intent intent = new Intent(PlayActivity.this, DownloadService.class);
                                intent.putExtra("command",DownloadService.CommandDownload);
                                intent.putExtra("songname", MusicManager.getMusicManager().getNowSong().songname);
                                intent.putExtra("seconds",MusicManager.getMusicManager().getNowSong().seconds);
                                intent.putExtra("singerid",MusicManager.getMusicManager().getNowSong().singerid);
                                intent.putExtra("albumpic",MusicManager.getMusicManager().getNowSong().albumpic_big);
                                intent.putExtra("singername",MusicManager.getMusicManager().getNowSong().singername);
                                intent.putExtra("albumid",MusicManager.getMusicManager().getNowSong().albumid);
                                intent.putExtra("songid",MusicManager.getMusicManager().getNowSong().songid);
                                intent.putExtra("url",MusicManager.getMusicManager().getNowSong().url);
                                startService(intent);
                            }
                        }
                    });

        }

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
                if (!MusicManager.getMusicManager().getIsPlaying()) {
                    playPlay.setImageResource(R.drawable.icon_play_play);
                } else {
                    playPlay.setImageResource(R.drawable.icon_play_pause);
                }
                setBackGround();
                break;
        }
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


    public class MyViewPagerAdapter extends FragmentPagerAdapter {
        public MyViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int i) {
            return fragments.get(i);
        }
        @Override
        public int getCount() {
            return fragments.size();
        }
    }
}
