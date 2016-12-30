package com.kachidoki.oxgenmusic.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.player.PlayerService;
import com.kachidoki.oxgenmusic.utils.SPUtils;
import com.kachidoki.oxgenmusic.utils.Utils;
import com.kachidoki.oxgenmusic.widget.PopWindow;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayiwei on 16/12/13.
 */
public class AdapterSearch extends RecyclerView.Adapter {

    List<Song> songList;
    public Context context;
    public String callname;

    public AdapterSearch(Context context) {
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_search, parent, false);
        return new SearchListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        SearchListViewHolder searchListViewHolder = (SearchListViewHolder) holder;
        searchListViewHolder.setData(songList.get(position),position,songList,callname==null?"search":callname);
    }

    @Override
    public int getItemCount() {
        return songList==null?0:songList.size();
    }

    public void setData(List<Song> songLists){
        this.songList = songLists;
        notifyDataSetChanged();
    }

    static class SearchListViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.img_search)
        ImageView imgSearch;
        @BindView(R.id.songname_search)
        TextView songnameSearch;
        @BindView(R.id.songername_search)
        TextView songernameSearch;
        @BindView(R.id.more_search)
        LinearLayout moreSearch;
        @BindView(R.id.layout_search)
        LinearLayout layout;
        public SearchListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(final Song song, final int i, final List<Song> songs, final String callname){
            songernameSearch.setText(song.singername);
            songnameSearch.setText(song.songname);
            Glide.with(itemView.getContext()).load(song.albumpic_big).into(imgSearch);
            final PopWindow popWindow = new PopWindow(itemView.getContext(),song,songs,i,callname);
            moreSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popWindow.showAtLocation(itemView, Gravity.BOTTOM, 0, Utils.checkDeviceHasNavigationBar()?Utils.getNavigationBarHeight():0);
                }
            });
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SPUtils.get(itemView.getContext(), Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)&&SPUtils.get(itemView.getContext(),Constants.hotListname_sp,"noname").equals(callname)){
                        Log.e("Test","nowQueue_sp = "+SPUtils.get(itemView.getContext(),Constants.nowQueue_sp,"noQueue")+" hotListname_sp = "+SPUtils.get(itemView.getContext(),Constants.hotListname_sp,"noname"));
                        //设置index即可
                        MusicManager.getMusicManager().setIndex(i);
                    }else {
                        //重置队列
                        SPUtils.put(itemView.getContext(),Constants.hotListname_sp,callname);
                        MusicDBHelper.getMusicDBHelper().deleteQueueSong(MusicManager.hotList);
                        MusicDBHelper.getMusicDBHelper().saveListSong(songs,MusicManager.hotList);
                        MusicManager.getMusicManager().setQueue(songs,i,false);
                        Intent PlayNow = new Intent(itemView.getContext(), PlayerService.class);
                        PlayNow.putExtra("command", PlayerService.CommandPlayNow);
                        itemView.getContext().startService(PlayNow);
                    }
                    if (SPUtils.get(itemView.getContext(),Constants.nowQueue_sp,"noQueue").equals(Constants.myList)){
                        SPUtils.put(itemView.getContext(),Constants.nowQueue_sp,Constants.hotList);
                    }
                }
            });

        }


    }

}
