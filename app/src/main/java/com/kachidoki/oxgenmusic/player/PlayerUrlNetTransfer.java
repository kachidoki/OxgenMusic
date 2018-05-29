package com.kachidoki.oxgenmusic.player;

import android.util.Log;

import com.kachidoki.oxgenmusic.model.bean.NewApiGetMediaUrlResult;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.network.NetWork;

import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * 播放url失效时，调用后台接口重新获得有效链接
 * Created by Kachidoki on 2018/5/9.
 */

public class PlayerUrlNetTransfer {

    private OnTransferFinsh caller;

    public void setCaller(OnTransferFinsh caller) {
        this.caller = caller;
    }

    public interface OnTransferFinsh{
        void succes(String url);
        void fail(Throwable e);
    }

    public void getTransferUrl(final Song song){

        Log.e("PlayerUrlNetTransfer","call transfer songmid = "+song.songmid);

        NetWork.getNewMusicApi()
                .getMediaUrl(song.songmid)
                .map(new Func1<NewApiGetMediaUrlResult, String>() {
                    @Override
                    public String call(NewApiGetMediaUrlResult result) {
                        return result.res_body.playurl;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable throwable) {
                        caller.fail(throwable);
                    }

                    @Override
                    public void onNext(String url) {
                        Log.e("PlayerUrlNetTransfer","transfer finsh newUrl = "+ url);

                        // 重写内存url
                        song.url = url;

                        caller.succes(url);
                    }
                });
    }

}
