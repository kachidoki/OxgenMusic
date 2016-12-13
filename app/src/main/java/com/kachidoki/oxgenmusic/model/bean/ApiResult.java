package com.kachidoki.oxgenmusic.model.bean;



/**
 * Created by mayiwei on 16/11/29.
 */
public class ApiResult {
    public int showapi_res_code;
    public String showapi_res_error;
    public ApiBody showapi_res_body;
    public class ApiBody {
        public int ret_code;
        public PageBean pagebean;
    }
}
