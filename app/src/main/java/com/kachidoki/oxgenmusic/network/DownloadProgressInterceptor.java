package com.kachidoki.oxgenmusic.network;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Response;

/**
 * Created by mayiwei on 16/11/23.
 */
public class DownloadProgressInterceptor implements Interceptor {

    private ProgressResponseListener listener;
    private String songName;

    public DownloadProgressInterceptor(String songName,ProgressResponseListener listener) {
        this.listener = listener;
        this.songName = songName;
    }

    @Override
    public Response intercept(Chain chain) throws IOException {
        //拦截
        Response originalResponse = chain.proceed(chain.request());

        //包装响应体并返回
        return originalResponse.newBuilder()
                .body(new ProgressResponseBody(originalResponse.body(), listener,songName))
                .build();
    }
}
