package com.kachidoki.oxgenmusic.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.model.bean.Song;
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
        searchListViewHolder.setData(songList.get(position),position,songList);
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
        public SearchListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

        public void setData(final Song song,int i,List<Song> songs){
            songernameSearch.setText(song.singername);
            songnameSearch.setText(song.songname);
            Glide.with(itemView.getContext()).load(song.albumpic_big).into(imgSearch);
            final PopWindow popWindow = new PopWindow(itemView.getContext(),song,songs,i,"search");
            moreSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popWindow.showAtLocation(itemView, Gravity.BOTTOM, 0, Utils.checkDeviceHasNavigationBar()?Utils.getNavigationBarHeight():0);
                }
            });
        }


    }

}
