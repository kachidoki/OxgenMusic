package com.kachidoki.oxgenmusic.network;


import com.kachidoki.oxgenmusic.model.bean.ApiResult;
import com.kachidoki.oxgenmusic.model.bean.SearchResult;

import retrofit2.http.GET;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by mayiwei on 16/11/17.
 */
public interface MusicApi {

    @GET("213-4")
    Observable<ApiResult> getMusicList(@Query("showapi_appid") String appid, @Query("showapi_sign") String appSign, @Query("topid") String topid);

    @GET("213-1")
    Observable<SearchResult> getSearchList(@Query("showapi_appid") String appid, @Query("showapi_sign") String appSign, @Query("keyword") String keyword,@Query("page") int page);
}
