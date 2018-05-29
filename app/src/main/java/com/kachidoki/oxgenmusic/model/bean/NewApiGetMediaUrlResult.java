package com.kachidoki.oxgenmusic.model.bean;

/**
 * Created by Kachidoki on 2018/5/9.
 */

public class NewApiGetMediaUrlResult {
    public int res_code;
    public String res_err;
    public Body res_body;

    public class Body {
        public String playurl;
    }


}
