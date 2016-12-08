package com.kachidoki.oxgenmusic.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.BaseActivity;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.AdapterPlaylist;
import com.kachidoki.oxgenmusic.model.MusicDBHelper;
import com.kachidoki.oxgenmusic.model.bean.ApiResult;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.model.bean.SongBean;
import com.kachidoki.oxgenmusic.network.NetWork;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mayiwei on 16/11/29.
 */
public class RankActivity extends BaseActivity {
    List<Song> songList = new ArrayList<>();
    @BindView(R.id.recyclerView_playlist)
    RecyclerView recyclerView;
    @BindView(R.id.list_img)
    ImageView listImg;
    @BindView(R.id.list_count)
    TextView listCount;
    @BindView(R.id.list_countTime)
    TextView listCountTime;
    @BindView(R.id.list_playall)
    ImageView listPlayall;

    AdapterPlaylist adapter = new AdapterPlaylist(RankActivity.this);

    Observer<List<Song>> observer = new Observer<List<Song>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            Toast.makeText(RankActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
        }

        @Override
        public void onNext(List<Song> songs) {
            adapter.setData(songs);
            countAllTime(songs);
            songList = songs;
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);
        ButterKnife.bind(this);

        setToolbar(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        setListImg(getIntent().getStringExtra("topid"));
        getRankMusic();
    }

    private void getRankMusic() {
        unsubscribe();
        subscription = NetWork.getMusicApi()
                .getMusicList(Constants.showapi_appid, Constants.showapi_sign, getIntent().getStringExtra("topid"))
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

    private void countAllTime(List<Song> songs){
        if (songs!=null){
            int countTime = 0;
            for (int i=0;i<songs.size();i++){
                countTime = countTime+songs.get(i).seconds;
            }
            if (countTime<3600){
                listCountTime.setText(countTime/60+"分");
            }else {
                listCountTime.setText(countTime/3600+"时"+(countTime-(countTime/3600)*3600)/60+"分");
            }
            listCount.setText(songs.size()+"首");
        }
    }

    private void setListImg(String topid){
        switch (topid){
            case "16":listImg.setImageResource(R.drawable.rank_16);
                break;
            case "17":listImg.setImageResource(R.drawable.rank_17);
                break;
            case "18":listImg.setImageResource(R.drawable.rank_18);
                break;
            case "19":listImg.setImageResource(R.drawable.rank_19_3);
                break;
            case "23":listImg.setImageResource(R.drawable.rank_23_);
                break;
            case "3":listImg.setImageResource(R.drawable.rank_3_1);
                break;
            case "5":listImg.setImageResource(R.drawable.rank_5_2);
                break;
            case "6":listImg.setImageResource(R.drawable.rank_6_2);
                break;
            case "26":listImg.setImageResource(R.drawable.rank_26);
                break;
            default:listImg.setImageResource(R.drawable.cd_nomal);
                break;
        }
    }

    @OnClick(R.id.list_playall)
    void playAll(){
        if (SPUtils.get(this,Constants.nowQueue_sp,"noQueue").equals(Constants.myList)){
            SPUtils.put(this,Constants.nowQueue_sp,Constants.hotList);
        }
        if (songList!=null){
            MusicManager.getMusicManager().setQueue(songList,0,true);
            MusicDBHelper.getMusicDBHelper().deleteQueueSong(MusicManager.hotList);
            MusicDBHelper.getMusicDBHelper().saveListSong(songList,MusicManager.hotList);
        }

    }

}
