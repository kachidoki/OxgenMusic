package com.kachidoki.oxgenmusic.network;

import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Created by mayiwei on 16/12/12.
 */
public interface Download {
    @Streaming
    @GET("")
    Observable<ResponseBody> download(@Url String url);
}
