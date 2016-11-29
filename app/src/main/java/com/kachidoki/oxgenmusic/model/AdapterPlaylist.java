package com.kachidoki.oxgenmusic.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.model.bean.Song;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayiwei on 16/11/29.
 */
public class AdapterPlaylist extends RecyclerView.Adapter {
    List<Song> songLists;
    public Context context;

    public AdapterPlaylist(Context context){
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_list, parent, false);
        return new PlayListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PlayListViewHolder playListViewHolder = (PlayListViewHolder) holder;
        playListViewHolder.setData(songLists.get(position));
    }

    @Override
    public int getItemCount() {
        return songLists==null?0:songLists.size();
    }

    public void setData(List<Song> songLists){
        this.songLists = songLists;
        notifyDataSetChanged();
    }

    static class PlayListViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.songername_list)
        TextView singername;
        @BindView(R.id.songname_list)
        TextView songname;

        public PlayListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void setData(final Song song){
            singername.setText(song.singername);
            songname.setText(song.songname);
        }
    }

}
