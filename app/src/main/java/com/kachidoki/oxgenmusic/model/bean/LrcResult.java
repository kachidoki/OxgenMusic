package com.kachidoki.oxgenmusic.model.bean;

/**
 * Created by mayiwei on 16/12/28.
 */
public class LrcResult {
    public int showapi_res_code;
    public String showapi_res_error;
    public LrcBody showapi_res_body;
    public class LrcBody {
        public int ret_code;
        public String lyric;
        public String lyric_txt;
    }
}
