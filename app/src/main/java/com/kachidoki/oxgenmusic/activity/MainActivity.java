package com.kachidoki.oxgenmusic.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.BaseActivity;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.AdapterMainactivity;
import com.kachidoki.oxgenmusic.model.bean.ApiResult;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.network.NetWork;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.widget.CDview;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseActivity {
    @BindView(R.id.recyclerView_main)
    RecyclerView recyclerView;
    @BindView(R.id.main_cd)
    LinearLayout mainCd;
    @BindView(R.id.main_cdView)
    CDview cDview;

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

    AdapterMainactivity adapter = new AdapterMainactivity(MainActivity.this);

    Observer<List<Song>> observer = new Observer<List<Song>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(MainActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(List<Song> songs) {
            adapter.setData(songs);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        setToolbar(true);
        initDrawer();

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        cDview.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.cd_nomal));
        getHotSong();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (MusicManager.getMusicManager().getNowSong()!=null){
            loadCDBitmap();
        }

        if (MusicManager.getMusicManager().getIsPlaying()){
            cDview.start();
        }else {
            cDview.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    private void getHotSong() {
        unsubscribe();
        subscription = NetWork.getMusicApi()
                .getMusicList(Constants.showapi_appid, Constants.showapi_sign, "26")
                .map(new Func1<ApiResult, List<Song>>() {
                    @Override
                    public List<Song> call(ApiResult apiResult) {
                        return apiResult.showapi_res_body.pagebean.songLists;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }

    private void loadCDBitmap(){
        Glide.with(getApplicationContext())
                .load(MusicManager.getMusicManager().getNowSong().albumpic_big )
                .asBitmap()
                .into( target );
    }

    @OnClick({R.id.rank1, R.id.rank2,R.id.rank3,R.id.rank4,R.id.rank5,R.id.rank6,R.id.rank7,R.id.rank8})
    void toRankActivity(View view) {
        switch (view.getId()) {
            case R.id.rank1:
                Intent intent1 = new Intent(MainActivity.this, RankActivity.class);
                intent1.putExtra("topid","5");
                startActivity(intent1);
                break;
            case R.id.rank2:
                Intent intent2 = new Intent(MainActivity.this, RankActivity.class);
                intent2.putExtra("topid","6");
                startActivity(intent2);
                break;
            case R.id.rank3:
                Intent intent3 = new Intent(MainActivity.this, RankActivity.class);
                intent3.putExtra("topid","23");
                startActivity(intent3);
                break;
            case R.id.rank4:
                Intent intent4 = new Intent(MainActivity.this, RankActivity.class);
                intent4.putExtra("topid","19");
                startActivity(intent4);
                break;
            case R.id.rank5:
                Intent intent5 = new Intent(MainActivity.this, RankActivity.class);
                intent5.putExtra("topid","17");
                startActivity(intent5);
                break;
            case R.id.rank6:
                Intent intent6 = new Intent(MainActivity.this, RankActivity.class);
                intent6.putExtra("topid","18");
                startActivity(intent6);
                break;
            case R.id.rank7:
                Intent intent7 = new Intent(MainActivity.this, RankActivity.class);
                intent7.putExtra("topid","3");
                startActivity(intent7);
                break;
            case R.id.rank8:
                Intent intent8 = new Intent(MainActivity.this, RankActivity.class);
                intent8.putExtra("topid","16");
                startActivity(intent8);
                break;
        }
    }

    @OnClick(R.id.main_cd)
    void toPlayActivity(){
        startActivity(new Intent(MainActivity.this, PlayActivity.class));
    }

    @Subscribe
    public void onEvent(PlayEvent playEvent){
        switch (playEvent.getAction()) {
            case CHANGE:
                if (MusicManager.getMusicManager().getNowSong()!=null){
                    loadCDBitmap();
                }
                break;
        }
    }

    private void initDrawer() {
        PrimaryDrawerItem item1 = new PrimaryDrawerItem().withName("主界面");
        SecondaryDrawerItem item2 = new SecondaryDrawerItem().withName("歌曲榜单");
        SecondaryDrawerItem item3 = new SecondaryDrawerItem().withName("播放列表");

        AccountHeader accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        new ProfileDrawerItem().withName("kachidoki").withEmail("mayiwei889@126.com").withIcon(R.mipmap.ic_launcher)
                )
                .withHeaderBackground(R.color.blackDark)
                .build();

        Drawer drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(getToolbar())
                .withDisplayBelowStatusBar(true)
                .withAccountHeader(accountHeader)
                .addDrawerItems(
                        item1,
                        item2,
                        item3
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        switch (position) {
                            case 1:
                                break;
                            case 2:
                                break;
                            case 3:
                                startActivity(new Intent(MainActivity.this, MyPlaylistActivity.class));
                                break;
                        }
                        return false;
                    }
                })
                .build();


    }


}
