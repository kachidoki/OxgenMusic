package com.kachidoki.oxgenmusic.model.bean;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;

import java.util.List;

/**
 * Created by mayiwei on 16/12/5.
 */
@Table(name = "SongQueue")
public class SongQueue extends Model {
    @Column(name = "name")
    public String name;

    public List<SongBean> songs(){
        return getMany(SongBean.class,"queue");
    }
    public SongQueue(){
        super();
    }
    public SongQueue(String mName){
        super();
        this.name = mName;
    }


}
