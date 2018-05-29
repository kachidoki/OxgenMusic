package com.kachidoki.oxgenmusic.network;

import com.kachidoki.oxgenmusic.model.bean.NewApiGetMediaUrlResult;
import com.kachidoki.oxgenmusic.model.bean.NewApiGetTopResult;

import java.util.Map;

import retrofit2.http.FieldMap;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Kachidoki on 2018/5/9.
 */

public interface NewMusicApi {

    @GET("getTopList")
    Observable<NewApiGetTopResult> getMusicList(@Query("topid") String topid);

    @GET("getSearchList")
    Observable<NewApiGetTopResult> getSearchList(@Query("keyword") String keyword,@Query("limit") int limit,@Query("page") int page);

    @GET("getRecommendList")
    Observable<NewApiGetTopResult> getRecommendList(@Query("uid") String uid);

    @GET("getMediaUrl")
    Observable<NewApiGetMediaUrlResult> getMediaUrl(@Query("songid") String songid);

    @FormUrlEncoded
    @POST("/reportHistory")
    Observable<NewApiGetTopResult> reportHistory(@FieldMap Map<String, String> history);

}
