package com.kachidoki.oxgenmusic.model.bean;

import java.util.List;

/**
 * Created by mayiwei on 16/12/13.
 */
public class SearchResult {
    public int showapi_res_code;
    public String showapi_res_error;
    public SearchBody showapi_res_body;
    public class SearchBody{
        public int ret_code;
        public SearchPageBean pagebean;
    }
    public class SearchPageBean{
        public String w;
        public int allPages;
        public int ret_code;
        public List<SearchContent> contentlist;
        public int currentPage;
        public String notice;
        public int allNum;
        public int maxResult;
    }
    public class SearchContent{
        public String m4a;
        public String mmedia_mid4a;
        public int songid;
        public int singerid;
        public String albumname;
        public String downUrl;
        public String singername;
        public String songname;
        public String strMediaMid;
        public String albummid;
        public String songmid;
        public String albumpic_big;
        public String albumpic_small;
        public int albumid;
    }
}
