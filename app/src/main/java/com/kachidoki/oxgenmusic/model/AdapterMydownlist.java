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

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.player.PlayerService;
import com.kachidoki.oxgenmusic.utils.SPUtils;
import com.kachidoki.oxgenmusic.utils.Utils;
import com.kachidoki.oxgenmusic.widget.PopWindow;
import com.kachidoki.oxgenmusic.widget.PopWindowDown;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayiwei on 17/1/23.
 */
public class AdapterMydownlist extends RecyclerView.Adapter {
    List<Song> songLists;
    public Context context;
    public int playingSong = -1;
    private String callname = "mydown";

    public AdapterMydownlist(Context context){
        this.context = context;
    }


    private PopWindowDown.OnChange onChange = new PopWindowDown.OnChange() {
        @Override
        public void Callback(int i) {
            songLists.remove(i);
            notifyDataSetChanged();
        }
    };

    public void setItemPlaying(int i){
        int oldSongPlaying = playingSong;
        playingSong = i;
        this.notifyItemChanged(i);
        if (oldSongPlaying!=-1) this.notifyItemChanged(oldSongPlaying);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_list, parent, false);
        return new PlayListViewHolder(view,onChange);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PlayListViewHolder playListViewHolder = (PlayListViewHolder) holder;
        playListViewHolder.setData(songLists.get(position),position,songLists,callname,playingSong);
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
        PopWindowDown.OnChange callback;
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

        public PlayListViewHolder(View itemView,PopWindowDown.OnChange callback) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            this.callback = callback;
        }
        public void setData(final Song song, final int i, final List<Song> songs,  final String callname,int songPlaying){
            singername.setText(song.singername);
            songname.setText(song.songname);
            number.setText((i+1)+"");
            setIsPlaying(i==songPlaying && SPUtils.get(itemView.getContext(), Constants.hotListname_sp,"noCall").equals(callname));

            final PopWindowDown popWindow = new PopWindowDown(itemView.getContext(),song,songs,i,callname,callback);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popWindow.showAtLocation(itemView,Gravity.BOTTOM, 0, Utils.checkDeviceHasNavigationBar()?Utils.getNavigationBarHeight():0);
                }
            });


            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SPUtils.get(itemView.getContext(),Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)
                            &&SPUtils.get(itemView.getContext(),Constants.hotListname_sp,"noname").equals(callname)){
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
                    Log.e("Test","nowQueue_sp "+SPUtils.get(itemView.getContext(),Constants.nowQueue_sp,"noQueue"));
                    Log.e("Test","hotListname_sp "+SPUtils.get(itemView.getContext(),Constants.hotListname_sp,"noQueue"));
                }
            });


        }
        public void setIsPlaying(boolean isPlaying){
            isplaying.setVisibility(isPlaying?View.VISIBLE:View.GONE);
            number.setVisibility(isPlaying?View.GONE:View.VISIBLE);
        }


    }





}
