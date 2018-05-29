package com.kachidoki.oxgenmusic.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.BaseActivity;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.AccountModel;
import com.kachidoki.oxgenmusic.model.AdapterMainactivity;
import com.kachidoki.oxgenmusic.model.MusicDBHelper;
import com.kachidoki.oxgenmusic.model.bean.ApiResult;
import com.kachidoki.oxgenmusic.model.bean.NewApiGetTopResult;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.network.NetWork;
import com.kachidoki.oxgenmusic.network.NewMusicApi;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.player.PlayerService;
import com.kachidoki.oxgenmusic.utils.SPUtils;
import com.kachidoki.oxgenmusic.widget.CDview;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.internal.util.SubscriptionList;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

public class MainActivity extends BaseActivity {
    @BindView(R.id.recyclerView_main)
    RecyclerView recyclerView;
    @BindView(R.id.main_cd)
    LinearLayout mainCd;
    @BindView(R.id.main_cdView)
    CDview cDview;
    @BindView(R.id.main_more)
    TextView more;
    @BindView(R.id.main_hot_title)
    TextView hotTitle;
    @BindView(R.id.rank1)
    ImageView rank1;
    @BindView(R.id.rank2)
    ImageView rank2;
    @BindView(R.id.rank3)
    ImageView rank3;
    @BindView(R.id.rank4)
    ImageView rank4;
    @BindView(R.id.rank5)
    ImageView rank5;
    @BindView(R.id.rank6)
    ImageView rank6;
    @BindView(R.id.rank7)
    ImageView rank7;
    @BindView(R.id.rank8)
    ImageView rank8;

    IProfile profile;
    Drawer drawer;
    AccountHeader accountHeader;

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

    AdapterMainactivity adapter = new AdapterMainactivity(MainActivity.this,"26");

    Observer<List<Song>> hotObserver = new Observer<List<Song>>() {
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
            adapter.setCallname("26");
            if (SPUtils.get(MainActivity.this,Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)
                    &&SPUtils.get(MainActivity.this,Constants.hotListname_sp,"nocall").equals("26")
                    &&MusicManager.getMusicManager().getIsPlaying())
                adapter.setItemPlaying(MusicManager.getMusicManager().getIndex());
        }
    };


    Observer<List<Song>> recommendObserver = new Observer<List<Song>>() {
        @Override
        public void onCompleted() {
        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(List<Song> songs) {
            adapter.setData(songs);
            adapter.setCallname("-1");
            if (SPUtils.get(MainActivity.this,Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)
                    &&SPUtils.get(MainActivity.this,Constants.hotListname_sp,"nocall").equals("-1")
                    &&MusicManager.getMusicManager().getIsPlaying()) {
                adapter.setItemPlaying(MusicManager.getMusicManager().getIndex());
            }

            if (songs.size() == 0) {
                getHotSong();
            }
        }
    };


    SubscriptionList subscriptionList = new SubscriptionList();
    Observer<List<Song>> MusicObserver = new Subscriber<List<Song>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable throwable) {

        }

        @Override
        public void onNext(List<Song> songs) {
            //压缩过的队列
            int half = 10;
            int index = (int) SPUtils.get(MainActivity.this,Constants.nowIndex_sp,0);
            boolean isless = index - half<0;
            if (songs.size()<=half*2){
                SPUtils.put(getApplicationContext(),Constants.nowQueue_sp,Constants.cacheList);
                SPUtils.put(getApplicationContext(),Constants.hotListname_sp,Constants.cacheList);
                MusicManager.getMusicManager().setQueue(songs,isless?index:half, false);
            }else {
                MusicManager.getMusicManager().setQueue(songs, index, false);
            }
            loadCDBitmap();
            startService(new Intent(MainActivity.this,PlayerService.class));
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);

        setToolbar(true);

        if(MusicManager.getMusicManager().getIsfirst()){
            initQueue();
        }
        initDrawer(savedInstanceState);
        setProfile();

//        startService(new Intent(this, PlayerService.class));

        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
        cDview.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.cd_nomal_png));
        getMainSong();
        getRankImg();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (MusicManager.getMusicManager().getNowSong() != null) {
            loadCDBitmap();
        }

        if (MusicManager.getMusicManager().getIsPlaying()) {
            cDview.start();
        } else {
            cDview.pause();
        }

        if (SPUtils.get(MainActivity.this,Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)&&SPUtils.get(MainActivity.this,Constants.hotListname_sp,"nocall").equals("26")&&MusicManager.getMusicManager().getIsPlaying()){
            adapter.setItemPlaying(MusicManager.getMusicManager().getIndex());
        }else {
            adapter.initPlayingSong();
        }

        checkDrawer();
    }

    private void initQueue() {
        if (SPUtils.get(getApplicationContext(), Constants.nowQueue_sp, "noQueue").equals(Constants.myList)) {
            subscriptionList.add(MusicDBHelper.getMusicDBHelper().RxFastConvertQueue((Integer) SPUtils.get(getApplicationContext(),Constants.nowIndex_sp,0),MusicManager.myList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(MusicObserver));

        } else  {
            subscriptionList.add(MusicDBHelper.getMusicDBHelper().RxFastConvertQueue((Integer) SPUtils.get(getApplicationContext(),Constants.nowIndex_sp,0),MusicManager.hotList)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(MusicObserver));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        subscriptionList.unsubscribe();
    }

    private void getMainSong() {
        if (AccountModel.getAccountModel().isLogin()) {
            getRecommendSong();
        }else {
            getHotSong();
        }
    }

    private void getRecommendSong() {
        unsubscribe();
        subscription = NetWork.getNewMusicApi()
                .getRecommendList(AccountModel.getAccountModel().getAccount().getObjectId())
                .map(new Func1<NewApiGetTopResult, List<Song>>() {
                    @Override
                    public List<Song> call(NewApiGetTopResult apiResult) {
                        return apiResult.res_body.songlist;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(recommendObserver);
        hotTitle.setText("为你推荐");
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
                .subscribe(hotObserver);
        hotTitle.setText("今日热歌");
    }

    private void loadCDBitmap() {
        if (MusicManager.getMusicManager().getNowSong().albumpic_big!=null){
            Glide.with(MainActivity.this)
                    .load(MusicManager.getMusicManager().getNowSong().albumpic_big)
                    .asBitmap()
                    .into(target);
        }else {
            cDview.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.cd_nomal_png));
        }

    }



    @OnClick({R.id.rank1, R.id.rank2, R.id.rank3, R.id.rank4, R.id.rank5, R.id.rank6, R.id.rank7, R.id.rank8, R.id.main_more})
    void toRankActivity(View view) {
        switch (view.getId()) {
            case R.id.rank1:
                Intent intent1 = new Intent(MainActivity.this, RankActivity.class);
                intent1.putExtra("topid", "5");
                ActivityOptionsCompat activityOptionsCompat1 = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, rank1, "rankImg");
                startActivity(intent1, activityOptionsCompat1.toBundle());
                break;
            case R.id.rank2:
                Intent intent2 = new Intent(MainActivity.this, RankActivity.class);
                intent2.putExtra("topid", "6");
                ActivityOptionsCompat activityOptionsCompat2 = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, rank2, "rankImg");
                startActivity(intent2, activityOptionsCompat2.toBundle());
                break;
            case R.id.rank3:
                Intent intent3 = new Intent(MainActivity.this, RankActivity.class);
                intent3.putExtra("topid", "23");
                ActivityOptionsCompat activityOptionsCompat3 = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, rank3, "rankImg");
                startActivity(intent3, activityOptionsCompat3.toBundle());
                break;
            case R.id.rank4:
                Intent intent4 = new Intent(MainActivity.this, RankActivity.class);
                intent4.putExtra("topid", "19");
                ActivityOptionsCompat activityOptionsCompat4 = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, rank4, "rankImg");
                startActivity(intent4, activityOptionsCompat4.toBundle());
                break;
            case R.id.rank5:
                Intent intent5 = new Intent(MainActivity.this, RankActivity.class);
                intent5.putExtra("topid", "17");
                ActivityOptionsCompat activityOptionsCompat5 = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, rank5, "rankImg");
                startActivity(intent5, activityOptionsCompat5.toBundle());
                break;
            case R.id.rank6:
                Intent intent6 = new Intent(MainActivity.this, RankActivity.class);
                intent6.putExtra("topid", "18");
                ActivityOptionsCompat activityOptionsCompat6 = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, rank6, "rankImg");
                startActivity(intent6, activityOptionsCompat6.toBundle());
                break;
            case R.id.rank7:
                Intent intent7 = new Intent(MainActivity.this, RankActivity.class);
                intent7.putExtra("topid", "3");
                ActivityOptionsCompat activityOptionsCompat7 = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, rank7, "rankImg");
                startActivity(intent7, activityOptionsCompat7.toBundle());
                break;
            case R.id.rank8:
                Intent intent8 = new Intent(MainActivity.this, RankActivity.class);
                intent8.putExtra("topid", "16");
                ActivityOptionsCompat activityOptionsCompat8 = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, rank8, "rankImg");
                startActivity(intent8, activityOptionsCompat8.toBundle());
                break;
            case R.id.main_more:
                Intent intent9 = new Intent(MainActivity.this, RankActivity.class);
                if (AccountModel.getAccountModel().isLogin()) {
                    intent9.putExtra("topid", "-1");
                    intent9.putExtra("uid",AccountModel.getAccountModel().getAccount().getObjectId());
                }else {
                    intent9.putExtra("topid", "26");
                }
                startActivity(intent9);
        }
    }


    private void getRankImg(){
        subscriptionList.add(NetWork.getMusicApi()
                .getMusicList(Constants.showapi_appid, Constants.showapi_sign, "5")
                .map(new Func1<ApiResult, List<Song>>() {
                    @Override
                    public List<Song> call(ApiResult apiResult) {
                        return apiResult.showapi_res_body.pagebean.songLists;
                    }
                })
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
                        Glide.with(MainActivity.this)
                                .load(songs.get(0).albumpic_big)
                                .into(rank1);
                    }
                }));
        subscriptionList.add(NetWork.getMusicApi()
                .getMusicList(Constants.showapi_appid, Constants.showapi_sign, "6")
                .map(new Func1<ApiResult, List<Song>>() {
                    @Override
                    public List<Song> call(ApiResult apiResult) {
                        return apiResult.showapi_res_body.pagebean.songLists;
                    }
                })
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
                        Glide.with(MainActivity.this)
                                .load(songs.get(0).albumpic_big)
                                .into(rank2);
                    }
                }));
        subscriptionList.add(NetWork.getMusicApi()
                .getMusicList(Constants.showapi_appid, Constants.showapi_sign, "23")
                .map(new Func1<ApiResult, List<Song>>() {
                    @Override
                    public List<Song> call(ApiResult apiResult) {
                        return apiResult.showapi_res_body.pagebean.songLists;
                    }
                })
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
                        Glide.with(MainActivity.this)
                                .load(songs.get(0).albumpic_big)
                                .into(rank3);
                    }
                }));
        subscriptionList.add(NetWork.getMusicApi()
                .getMusicList(Constants.showapi_appid, Constants.showapi_sign, "19")
                .map(new Func1<ApiResult, List<Song>>() {
                    @Override
                    public List<Song> call(ApiResult apiResult) {
                        return apiResult.showapi_res_body.pagebean.songLists;
                    }
                })
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
                        Glide.with(MainActivity.this)
                                .load(songs.get(0).albumpic_big)
                                .into(rank4);
                    }
                }));
        subscriptionList.add(NetWork.getMusicApi()
                .getMusicList(Constants.showapi_appid, Constants.showapi_sign, "17")
                .map(new Func1<ApiResult, List<Song>>() {
                    @Override
                    public List<Song> call(ApiResult apiResult) {
                        return apiResult.showapi_res_body.pagebean.songLists;
                    }
                })
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
                        Glide.with(MainActivity.this)
                                .load(songs.get(0).albumpic_big)
                                .into(rank5);
                    }
                }));

        subscriptionList.add(NetWork.getMusicApi()
                .getMusicList(Constants.showapi_appid, Constants.showapi_sign, "18")
                .map(new Func1<ApiResult, List<Song>>() {
                    @Override
                    public List<Song> call(ApiResult apiResult) {
                        return apiResult.showapi_res_body.pagebean.songLists;
                    }
                })
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
                        Glide.with(MainActivity.this)
                                .load(songs.get(0).albumpic_big)
                                .into(rank6);
                    }
                }));

        subscriptionList.add(NetWork.getMusicApi()
                .getMusicList(Constants.showapi_appid, Constants.showapi_sign, "3")
                .map(new Func1<ApiResult, List<Song>>() {
                    @Override
                    public List<Song> call(ApiResult apiResult) {
                        return apiResult.showapi_res_body.pagebean.songLists;
                    }
                })
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
                        Glide.with(MainActivity.this)
                                .load(songs.get(0).albumpic_big)
                                .into(rank7);
                    }
                }));
        subscriptionList.add(NetWork.getMusicApi()
                .getMusicList(Constants.showapi_appid, Constants.showapi_sign, "16")
                .map(new Func1<ApiResult, List<Song>>() {
                    @Override
                    public List<Song> call(ApiResult apiResult) {
                        return apiResult.showapi_res_body.pagebean.songLists;
                    }
                })
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
                        Glide.with(MainActivity.this)
                                .load(songs.get(0).albumpic_big)
                                .into(rank8);
                    }
                }));

    }


    @OnClick(R.id.main_cd)
    void toPlayActivity() {
        startActivity(new Intent(MainActivity.this, PlayActivity.class));
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
                if (SPUtils.get(MainActivity.this,Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)&&SPUtils.get(MainActivity.this,Constants.hotListname_sp,"nocall").equals("26")&&MusicManager.getMusicManager().getIsPlaying())
                    adapter.setItemPlaying(MusicManager.getMusicManager().getIndex());
                break;
        }
    }

    private void initDrawer(Bundle savedInstanceState) {
        profile = new ProfileDrawerItem().withName("  请先登录").withIcon(R.drawable.drawer_nolog);
        accountHeader = new AccountHeaderBuilder()
                .withActivity(this)
                .addProfiles(
                        profile,
                        new ProfileSettingDrawerItem().withName("添加用户").withIcon(R.drawable.drawer_login).withIdentifier(100)
                )
                .withHeaderBackground(R.color.blackDark)
                .withOnAccountHeaderListener(new AccountHeader.OnAccountHeaderListener() {
                    @Override
                    public boolean onProfileChanged(View view, IProfile profile, boolean current) {
                        if (profile instanceof IDrawerItem && profile.getIdentifier() == 100) {
                            startActivityForResult(new Intent(MainActivity.this, LoginActivity.class), Constants.ResquestLogin);
                        }
                        return true;
                    }
                })
                .build();

        drawer = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(getToolbar())
                .withAccountHeader(accountHeader)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("主界面").withIcon(R.drawable.drawer_home).withIdentifier(1),
                        new PrimaryDrawerItem().withName("我的列表").withIcon(R.drawable.drawer_list).withIdentifier(2),
                        new PrimaryDrawerItem().withName("正在播放").withIcon(R.drawable.drawer_play).withIdentifier(3),
                        new PrimaryDrawerItem().withName("本地音乐").withIcon(R.drawable.drawer_local).withIdentifier(8),
                        new DividerDrawerItem(),
//                        new SecondaryDrawerItem().withName("设置").withIcon(R.drawable.drawer_setting).withIdentifier(4),
                        new SecondaryDrawerItem().withName("关于").withIcon(R.drawable.drawer_about).withIdentifier(5)
                )
                .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                    @Override
                    public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                        if (drawerItem != null) {
                            Intent intent = null;
                            if (drawerItem.getIdentifier() == 2) {
                                intent = new Intent(MainActivity.this, MyPlaylistActivity.class);
                            } else if (drawerItem.getIdentifier() == 3) {
                                intent = new Intent(MainActivity.this, PlayActivity.class);
                            } else if (drawerItem.getIdentifier() == 8) {
                                intent = new Intent(MainActivity.this, LocalActivity.class);
//                                Toast.makeText(MainActivity.this,"其他功能等待更新哦",Toast.LENGTH_SHORT).show();
                            } else if (drawerItem.getIdentifier() == 5) {
                                intent = new Intent(MainActivity.this, AboutActivity.class);
                            } else if (drawerItem.getIdentifier()== 6) {
                                Toast.makeText(MainActivity.this, "同步数据", Toast.LENGTH_SHORT).show();
                                MusicDBHelper.getMusicDBHelper().syncYun(MainActivity.this, MusicManager.myList, AccountModel.getAccountModel().getAccount().getObjectId());
                                new Handler().postDelayed(new Runnable(){
                                    public void run() {
                                        MusicManager.getMusicManager().setQueue(MusicDBHelper.getMusicDBHelper().ConvertQueue(MusicDBHelper.getMusicDBHelper().SelectQueue(Constants.myList)),0,false);
                                    }
                                }, 1000);
                            } else if (drawerItem.getIdentifier() == 7) {
                                AccountModel.getAccountModel().logout();
                                setProfile();
                                checkDrawer();
                            }
                            if (intent != null) {
                                startActivity(intent);
                            }

                        }
                        return false;
                    }
                })
                .build();

    }

    private void setProfile() {
        if (AccountModel.getAccountModel().isLogin()) {
            profile.withName(AccountModel.getAccountModel().getAccount().getUsername());
            accountHeader.updateProfile(profile);
        } else {
            profile.withName(" 请先登录");
            accountHeader.updateProfile(profile);
        }

    }

    private void checkDrawer() {
        if (drawer.getDrawerItems().size() != 8) {
            if (AccountModel.getAccountModel().isLogin()) {
                drawer.addItemAtPosition(new SecondaryDrawerItem().withName("数据同步").withIcon(R.drawable.drawer_sync).withIdentifier(6), 6);
                drawer.addItemAtPosition(new SecondaryDrawerItem().withName("退出登录").withIcon(R.drawable.drawer_logout).withIdentifier(7), 7);
            }
        } else if (drawer.getDrawerItems().size() == 8) {
            if (!AccountModel.getAccountModel().isLogin()) {
                drawer.removeItem(6);
                drawer.removeItem(7);
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer != null && drawer.isDrawerOpen()) {
            drawer.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.ResquestLogin && resultCode == Constants.LoginSuccess) {
            profile.withName(data.getStringExtra("name"));
            accountHeader.updateProfile(profile);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.menu_search:
                startActivity(new Intent(MainActivity.this,SearchActivity.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
