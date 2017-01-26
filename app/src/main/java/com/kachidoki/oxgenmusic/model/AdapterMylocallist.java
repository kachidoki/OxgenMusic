package com.kachidoki.oxgenmusic.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.player.PlayerService;
import com.kachidoki.oxgenmusic.utils.SPUtils;
import com.kachidoki.oxgenmusic.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayiwei on 17/1/24.
 */
public class AdapterMylocallist extends RecyclerView.Adapter{
    List<Song> songLists;
    public Context context;
    public int playingSong = -1;
    private String callname = "mylocal";

    public AdapterMylocallist(Context context){
        this.context = context;
    }




    public void setItemPlaying(int i){
        int oldSongPlaying = playingSong;
        playingSong = i;
        this.notifyItemChanged(i);
        if (oldSongPlaying!=-1) this.notifyItemChanged(oldSongPlaying);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_list, parent, false);
        return new PlayListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PlayListViewHolder playListViewHolder = (PlayListViewHolder) holder;
        playListViewHolder.setData(songLists.get(position),position,songLists,playingSong,callname);
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
        @BindView(R.id.more_list)
        LinearLayout more;
        @BindView(R.id.number_list)
        TextView number;
        @BindView(R.id.playLayout_list)
        LinearLayout layout;
        @BindView(R.id.isplay_list)
        ImageView isplaying;

        public PlayListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void setData(final Song song, final int i, final List<Song> songs, int songPlaying, final String callname){
            singername.setText(song.singername);
            songname.setText(song.songname);
            number.setText((i+1)+"");
            more.setVisibility(View.GONE);
            setIsPlaying(i==songPlaying && SPUtils.get(itemView.getContext(), Constants.hotListname_sp,"noCall").equals(callname));


            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SPUtils.get(itemView.getContext(),Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)&&SPUtils.get(itemView.getContext(),Constants.hotListname_sp,"noname").equals(callname)){
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
                        SPUtils.put(itemView.getContext(),Constants.hotListname_sp,callname);
                    }
                }
            });


        }
        public void setIsPlaying(boolean isPlaying){
            isplaying.setVisibility(isPlaying?View.VISIBLE:View.GONE);
            number.setVisibility(isPlaying?View.GONE:View.VISIBLE);
        }


    }

}
