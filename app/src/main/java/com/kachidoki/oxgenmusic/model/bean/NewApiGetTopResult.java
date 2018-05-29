package com.kachidoki.oxgenmusic.model.bean;

import java.util.List;

/**
 * Created by Kachidoki on 2018/5/9.
 */

public class NewApiGetTopResult {
    public int res_code;
    public String res_err;
    public Body res_body;

    public class Body {
        public List<Song> songlist;
        public int cur_song_num;
        public String update_time;
    }


}
