package com.kachidoki.oxgenmusic.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.BaseActivity;
import com.kachidoki.oxgenmusic.model.AdapterPlaylist;
import com.kachidoki.oxgenmusic.player.MusicManager;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayiwei on 16/12/1.
 */
public class MyPlaylistActivity extends BaseActivity {
    @BindView(R.id.recyclerView_playlist)
    RecyclerView recyclerView;

    AdapterPlaylist adapter = new AdapterPlaylist(MyPlaylistActivity.this);
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_playlist);

        ButterKnife.bind(this);

        setToolbar(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        getMylist();
    }

    private void getMylist(){
        adapter.setData(MusicManager.getMusicManager().getmQueue());
    }
}
