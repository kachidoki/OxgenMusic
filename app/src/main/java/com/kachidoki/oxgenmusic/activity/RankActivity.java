package com.kachidoki.oxgenmusic.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.BaseActivity;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.AdapterPlaylist;
import com.kachidoki.oxgenmusic.model.bean.ApiResult;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.network.NetWork;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mayiwei on 16/11/29.
 */
public class RankActivity extends BaseActivity {
    @BindView(R.id.recyclerView_playlist)
    RecyclerView recyclerView;

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

        getRankMusic();
    }

    private void getRankMusic(){
        unsubscribe();
        subscription = NetWork.getMusicApi()
                .getMusicList(Constants.showapi_appid,Constants.showapi_sign,"5")
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

}
