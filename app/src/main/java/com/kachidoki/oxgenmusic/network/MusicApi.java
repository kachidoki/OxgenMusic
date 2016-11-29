package com.kachidoki.oxgenmusic.network;


import com.kachidoki.oxgenmusic.model.bean.ApiResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by mayiwei on 16/11/17.
 */
public interface MusicApi {

    @GET("213-4")
    Observable<ApiResult> getMusicList(@Query("showapi_appid") String appid, @Query("showapi_sign") String appSign, @Query("topid") String topid);

}
