package com.kachidoki.oxgenmusic.network;

import android.util.Log;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.CallAdapter;
import retrofit2.Converter;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by mayiwei on 16/11/17.
 */
public class NetWork {
    private static Download download;
    private static MusicApi musicApi;
    private static OkHttpClient okHttpClient ;
    private static final int DEFAULT_TIMEOUT = 15;
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();


    public static MusicApi getMusicApi(){
        okHttpClient = new OkHttpClient();
        if (musicApi==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl("http://route.showapi.com/")
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            musicApi = retrofit.create(MusicApi.class);
        }
        return musicApi;
    }
    public static Download getDownloadApi(ProgressResponseListener listener){
        okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new DownloadProgressInterceptor(listener))
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        if (download==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://ws.stream.qqmusic.qq.com/")
                    .client(okHttpClient)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            download = retrofit.create(Download.class);
        }
        return download;
    }
}