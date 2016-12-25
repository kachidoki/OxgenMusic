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
import android.widget.Toast;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.App;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.player.PlayerService;
import com.kachidoki.oxgenmusic.utils.SPUtils;
import com.kachidoki.oxgenmusic.utils.Utils;
import com.kachidoki.oxgenmusic.widget.PopWindow;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayiwei on 16/11/29.
 */
public class AdapterPlaylist extends RecyclerView.Adapter {
    List<Song> songLists;
    public Context context;
    public String callname;
    public int playingSong = -1;

    public AdapterPlaylist(Context context,String callname){
        this.context = context;
        this.callname = callname;
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
        @BindView(R.id.songername_list)
        TextView singername;
        @BindView(R.id.songname_list)
        TextView songname;
        @BindView(R.id.more_list)
        LinearLayout more;
        @BindView(R.id.number_list)
        TextView number;
        @BindView(R.id.layout_list)
        LinearLayout layout;
        @BindView(R.id.isplay_list)
        ImageView isplaying;

        public PlayListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void setData(final Song song, final int i, final List<Song> songs, final String callname, int songPlaying){
            singername.setText(song.singername);
            songname.setText(song.songname);
            number.setText((i+1)+"");
            setIsPlaying(i==songPlaying && SPUtils.get(itemView.getContext(), Constants.hotListname_sp,"noCall").equals(callname));

            final PopWindow popWindow = new PopWindow(itemView.getContext(),song,songs,i,callname);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popWindow.showAtLocation(itemView,Gravity.BOTTOM, 0, Utils.checkDeviceHasNavigationBar()?Utils.getNavigationBarHeight():0);
                }
            });
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.e("Test","nowQueue"+SPUtils.get(itemView.getContext(),Constants.nowQueue_sp,"noQueue"));
                    Log.e("Test","nowCallname"+SPUtils.get(itemView.getContext(),Constants.hotListname_sp,"noQueue"));
                    if (SPUtils.get(itemView.getContext(),Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)&&SPUtils.get(itemView.getContext(),Constants.hotListname_sp,"noname").equals(callname)){
                        //设置index即可
                        Log.e("Test","设置index即可");
                        MusicManager.getMusicManager().setIndex(i);
                        if (callname.equals("search")){
                            //也要重置
                            MusicDBHelper.getMusicDBHelper().deleteQueueSong(MusicManager.hotList);
                            MusicDBHelper.getMusicDBHelper().saveListSong(songs,MusicManager.hotList);
                            MusicManager.getMusicManager().setQueue(songs,i,true);
                        }
                    }else {
                        //重置队列
                        Log.e("Test","重置队列");
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
                    Log.e("Test","nowQueue"+SPUtils.get(itemView.getContext(),Constants.nowQueue_sp,"noQueue"));
                    Log.e("Test","nowCallname"+SPUtils.get(itemView.getContext(),Constants.hotListname_sp,"noQueue"));
                }
            });


        }
        public void setIsPlaying(boolean isPlaying){
            isplaying.setVisibility(isPlaying?View.VISIBLE:View.GONE);
            number.setVisibility(isPlaying?View.GONE:View.VISIBLE);
        }


    }





}
