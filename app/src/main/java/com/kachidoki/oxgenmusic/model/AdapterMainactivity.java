package com.kachidoki.oxgenmusic.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.activity.RankActivity;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.player.PlayerService;
import com.kachidoki.oxgenmusic.utils.SPUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayiwei on 16/11/28.
 */
public class AdapterMainactivity extends RecyclerView.Adapter {
    List<Song> songLists;
    public Context context;
    public String callname;
    public int playingSong = -1;

    public AdapterMainactivity(Context context,String callname) {
        this.context = context;
        this.callname = callname;
    }

    public void setCallname(String name) {
        this.callname = callname;
    }

    public void initPlayingSong(){
        playingSong = -1;
        notifyDataSetChanged();
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_hot, parent, false);
        return new MusicCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MusicCardViewHolder musicCardViewHolder = (MusicCardViewHolder) holder;
        musicCardViewHolder.setData(songLists.get(position),position,songLists,callname,playingSong);
    }

    @Override
    public int getItemCount() {
        return songLists==null?0:songLists.size();
    }

    public void setData(List<Song> songLists){
        this.songLists = songLists;
        notifyDataSetChanged();
    }

    public void setItemPlaying(int i){
        int oldSongPlaying = playingSong;
        playingSong = i;
        this.notifyItemChanged(i);
        if (oldSongPlaying!=-1) this.notifyItemChanged(oldSongPlaying);

    }

    static class MusicCardViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.image_card_amb)
        ImageView img;
        @BindView(R.id.name_card_amb)
        TextView name;
        @BindView(R.id.isplaying_card_amb)
        LinearLayout isplaying;

        public MusicCardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

        public void setData(final Song song,final int i, final List<Song> songs, final String callname, int songPlaying){
            Glide.with(itemView.getContext()).load(song.albumpic_big).into(img);
            name.setText(song.songname);
            name.getBackground().setAlpha(230);
            setIsPlaying(i==songPlaying && SPUtils.get(itemView.getContext(), Constants.hotListname_sp,"noCall").equals(callname));

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SPUtils.get(itemView.getContext(),Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)&&SPUtils.get(itemView.getContext(),Constants.hotListname_sp,"noname").equals(callname)){
                        //设置index即可
                        MusicManager.getMusicManager().setIndex(i);
                        if (callname.equals("search")){
                            //也要重置
                            MusicDBHelper.getMusicDBHelper().deleteQueueSong(MusicManager.hotList);
                            MusicDBHelper.getMusicDBHelper().saveListSong(songs,MusicManager.hotList);
                            MusicManager.getMusicManager().setQueue(songs,i,true);
                        }
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

        public void setIsPlaying(boolean isPlaying){
            isplaying.setVisibility(isPlaying?View.VISIBLE:View.GONE);
        }
    }

}
