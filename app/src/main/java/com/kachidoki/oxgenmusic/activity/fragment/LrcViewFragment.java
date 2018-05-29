package com.kachidoki.oxgenmusic.activity.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.bean.LrcResult;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.network.NetWork;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.widget.LrcView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by mayiwei on 16/12/28.
 */
public class LrcViewFragment extends Fragment {

    @BindView(R.id.play_lrc)
    LrcView lrcView;

    private String NowmusicId = "";
    protected Subscription subscription;
    private Handler handler = new Handler();
    public Runnable updataProgress = new Runnable() {
        @Override
        public void run() {
            if (MusicManager.getMusicManager().getNowSong()!=null){
                if (MusicManager.getMusicManager().getIsReady()){
                    lrcView.changeCurrent(MusicManager.getMusicManager().getCurrentPosition());
                }
            }
            handler.postDelayed(updataProgress,500);
        }
    };


    Observer<LrcResult> observer = new Observer<LrcResult>() {
        @Override
        public void onCompleted() {

        }

        @Override
        public void onError(Throwable throwable) {
            Log.e("LrcViewFragment","Error = "+throwable.getMessage());
        }

        @Override
        public void onNext(LrcResult lrcResult) {
            Log.d("LrcViewFragment","res = "+lrcResult.showapi_res_body.lyric);
            lrcView.setLrcWord(lrcResult.showapi_res_body.lyric);

        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_play_lrc_view,container,false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);

        if (MusicManager.getMusicManager().getNowSong() != null) {
            if (!MusicManager.getMusicManager().getNowSong().songmid.equals(NowmusicId)){
                NowmusicId = MusicManager.getMusicManager().getNowSong().songmid;
                getLrcWord(NowmusicId);
            }
        }
        return view;

    }


    private void getLrcWord(String musicId) {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        Log.e("LrcViewFragment","getLrcWord musicId = "+musicId);
        subscription = NetWork.getMusicApi()
                .getLrcWord(Constants.showapi_appid,Constants.showapi_sign, NowmusicId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(observer);
    }


    @Subscribe
    public void onEvent(PlayEvent playEvent) {
        switch (playEvent.getAction()) {
            case CHANGE:
                if (MusicManager.getMusicManager().getNowSong() != null) {
                    if (!MusicManager.getMusicManager().getNowSong().songmid.equals(NowmusicId)){
                        NowmusicId = MusicManager.getMusicManager().getNowSong().songmid;
                        getLrcWord(NowmusicId);
                    }
                }
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(updataProgress);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        handler.removeCallbacks(updataProgress);
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
