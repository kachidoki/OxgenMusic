package com.kachidoki.oxgenmusic.player;

import android.util.Log;

import com.kachidoki.oxgenmusic.model.AccountModel;
import com.kachidoki.oxgenmusic.model.bean.NewApiGetTopResult;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.network.NetWork;

import java.util.HashMap;
import java.util.Map;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Kachidoki on 2018/5/21.
 */

public class PlayerHistoryReporter {

    public void report(Song song){
//        Log.e("PlayerHistoryReporter","report");
        Map<String,String> historyMap = new HashMap<>();
        if (AccountModel.getAccountModel().isLogin()) {
            historyMap.put("uid",AccountModel.getAccountModel().getAccount().getObjectId());
        } else {
            return;
        }
        historyMap.put("songid", String.valueOf(song.songid));
        historyMap.put("songname", song.songname);
        historyMap.put("songmid", song.songmid);
        historyMap.put("singerid", String.valueOf(song.singerid));
        historyMap.put("singername", song.singername);
        historyMap.put("albumid", String.valueOf(song.albumid));
        historyMap.put("albummid", String.valueOf(song.albummid));
        historyMap.put("tmstamp", String.valueOf(System.currentTimeMillis()));

        NetWork.getNewMusicApi()
                .reportHistory(historyMap)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<NewApiGetTopResult>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        Log.e("PlayerHistoryReporter","onError "+throwable.getMessage());
                    }

                    @Override
                    public void onNext(NewApiGetTopResult newApiGetTopResult) {
                        if (newApiGetTopResult.res_code == 200){
                            Log.i("PlayerHistoryReporter","save history");
                        }else if (newApiGetTopResult.res_code == 401){
                            Log.i("PlayerHistoryReporter","miss args");
                        }
                    }
                });

    }
}
