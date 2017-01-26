package com.kachidoki.oxgenmusic.activity.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.AdapterMylocallist;
import com.kachidoki.oxgenmusic.model.MusicDBHelper;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mayiwei on 17/1/21.
 */
public class MyLocalMusicFragment extends Fragment {


    @BindView(R.id.loadFreshing)
    LinearLayout loadFreshing;
    @BindView(R.id.loadNull)
    LinearLayout loadNull;
    @BindView(R.id.recyclerView_mylocal)
    RecyclerView recyclerView;


    Subscription subscription;
    AdapterMylocallist adapter;
    List<Song> songList = new ArrayList<>();

    Observer<List<Song>> observer = new Observer<List<Song>>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable e) {
            loadFreshing.setVisibility(View.GONE);
            loadNull.setVisibility(View.VISIBLE);
        }

        @Override
        public void onNext(List<Song> songs) {
            loadFreshing.setVisibility(View.GONE);
            adapter.setData(songs);
            songList = songs;
            if (SPUtils.get(getContext(), Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)
                    &&SPUtils.get(getContext(),Constants.hotListname_sp,"nocall").equals("mylocal")
                    && MusicManager.getMusicManager().getIsPlaying())
                adapter.setItemPlaying(MusicManager.getMusicManager().getIndex());
        }
    };


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_local_mylocalmusic_view, container, false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        if (checkPermissom()) getDownMusic();

        adapter = new AdapterMylocallist(getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe
    public void onEvent(PlayEvent playEvent) {
        switch (playEvent.getAction()) {
            case CHANGE:
                if (SPUtils.get(getContext(), Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)
                        &&SPUtils.get(getContext(),Constants.hotListname_sp,"nocall").equals("mylocal")
                        && MusicManager.getMusicManager().getIsPlaying())
                    adapter.setItemPlaying(MusicManager.getMusicManager().getIndex());
                break;
        }
    }


    public void reLoad(){
        getDownMusic();
    }

    private void getDownMusic() {
        unsubscribe();
        subscription = MusicDBHelper.getMusicDBHelper().RxGetLocalsongs(getActivity())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    private boolean checkPermissom(){
        return (ContextCompat.checkSelfPermission(getContext(),Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
    }




    @Override
    public void onDestroy() {
        super.onDestroy();
        unsubscribe();
    }

    protected void unsubscribe() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }


}
