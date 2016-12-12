package com.kachidoki.oxgenmusic.network;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by mayiwei on 16/12/12.
 */
public interface Download {
    @Streaming
    @GET("http://ws.stream.qqmusic.qq.com/102954019.m4a?fromtag=46")
    Observable<ResponseBody> download();
}
