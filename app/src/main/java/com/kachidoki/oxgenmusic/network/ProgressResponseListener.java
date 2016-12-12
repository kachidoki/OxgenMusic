package com.kachidoki.oxgenmusic.network;

/**
 * Created by mayiwei on 16/11/23.
 */
public interface ProgressResponseListener {

    void onResponseProgress(long bytesRead, long contentLength, boolean done);

}
