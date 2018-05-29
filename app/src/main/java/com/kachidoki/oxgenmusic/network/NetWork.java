package com.kachidoki.oxgenmusic.network;

import android.util.Log;

import com.kachidoki.oxgenmusic.config.Constants;

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
    private static NewMusicApi newMusicApi;
    private static final int DEFAULT_TIMEOUT = 15;
    private static Converter.Factory gsonConverterFactory = GsonConverterFactory.create();
    private static CallAdapter.Factory rxJavaCallAdapterFactory = RxJavaCallAdapterFactory.create();


    public static MusicApi getMusicApi(){
        OkHttpClient okHttpClient = new OkHttpClient();
        if (musicApi==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(Constants.baseUrl)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            musicApi = retrofit.create(MusicApi.class);
        }
        return musicApi;
    }

    public static NewMusicApi getNewMusicApi(){
        OkHttpClient okHttpClient = new OkHttpClient();
        if (newMusicApi==null){
            Retrofit retrofit = new Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(Constants.newBaseUrl)
                    .addConverterFactory(gsonConverterFactory)
                    .addCallAdapterFactory(rxJavaCallAdapterFactory)
                    .build();
            newMusicApi = retrofit.create(NewMusicApi.class);
        }
        return newMusicApi;
    }

    public static Download getDownloadApi(ProgressResponseListener listener,String songname){
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new DownloadProgressInterceptor(songname,listener))
                .retryOnConnectionFailure(true)
                .connectTimeout(DEFAULT_TIMEOUT, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(Constants.DownbaseUrl)
                .client(okHttpClient)
                .addConverterFactory(gsonConverterFactory)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .build();
        download = retrofit.create(Download.class);
        return download;
    }



}