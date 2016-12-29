package com.kachidoki.oxgenmusic.model;

import android.content.Context;
import android.graphics.Color;
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
import com.kachidoki.oxgenmusic.utils.SPUtils;
import com.kachidoki.oxgenmusic.utils.Utils;
import com.kachidoki.oxgenmusic.widget.MyItemTouchHelperCallback;
import com.kachidoki.oxgenmusic.widget.PopWindowMylist;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayiwei on 16/12/7.
 */
public class AdapterMylist extends RecyclerView.Adapter implements MyItemTouchHelperCallback.ItemTouchHelperLister{
    List<Song> songLists;
    public Context context;
    public int playingSong = -1;



    private PopWindowMylist.OnChange countTime;

    public AdapterMylist(Context context, PopWindowMylist.OnChange onChange){
        this.context = context;
        this.countTime = onChange;
    }

    private PopWindowMylist.OnChange onChange = new PopWindowMylist.OnChange() {
        @Override
        public void Callback(int i) {
            songLists.remove(i);
            notifyDataSetChanged();
            countTime.Callback(0);
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
        playListViewHolder.setData(songLists.get(position),position,playingSong);
    }

    @Override
    public int getItemCount() {
        return songLists==null?0:songLists.size();
    }

    public void setData(List<Song> songLists){
        this.songLists = songLists;
        notifyDataSetChanged();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        if (SPUtils.get(context,Constants.nowQueue_sp,"noQueue").equals(Constants.myList)){
            MusicManager.getMusicManager().swapSong(fromPosition,toPosition);
            if (playingSong==fromPosition||playingSong==toPosition){
                MusicManager.getMusicManager().justSetIndex(playingSong==fromPosition?toPosition:fromPosition);
                setItemPlaying(playingSong==fromPosition?toPosition:fromPosition);
            }
        }
        MusicDBHelper.getMusicDBHelper().swapSongs(songLists.get(fromPosition),songLists.get(toPosition));
        Collections.swap(songLists, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
        this.notifyItemChanged(fromPosition);
        this.notifyItemChanged(toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        MusicManager.getMusicManager().deleteSong(position,MusicManager.getMusicManager().getIsPlaying(),SPUtils.get(context, Constants.nowQueue_sp,"noQueue").equals(Constants.myList));
        MusicDBHelper.getMusicDBHelper().deleteSingleSong(songLists.get(position));
        if (MusicManager.getMusicManager().getIsfirst()&&SPUtils.get(context,Constants.nowQueue_sp,"noQueue").equals(Constants.myList)){
            SPUtils.put(context,Constants.nowIndex_sp,MusicManager.getMusicManager().getIndex());
        }
        songLists.remove(position);
        notifyItemRemoved(position);
        countTime.Callback(0);
    }

    static class PlayListViewHolder extends RecyclerView.ViewHolder implements MyItemTouchHelperCallback.ItemTouchHelperViewHolderLister{
        PopWindowMylist.OnChange callback;
        @BindView(R.id.isplay_list)
        ImageView isplaying;
        @BindView(R.id.songername_list)
        TextView singername;
        @BindView(R.id.songname_list)
        TextView songname;
        @BindView(R.id.more_list)
        LinearLayout more;
        @BindView(R.id.number_list)
        TextView number;
        @BindView(R.id.playLayout_list)
        LinearLayout playlayout;

        public PlayListViewHolder(View itemView, PopWindowMylist.OnChange onChange) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            callback = onChange;
        }
        public void setData(final Song song, final int i, int songPlaying){
            singername.setText(song.singername);
            songname.setText(song.songname);
            number.setText((i+1)+"");
            setIsPlaying(i==songPlaying);

            final PopWindowMylist popWindow = new PopWindowMylist(itemView.getContext(),song,i,callback);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popWindow.showAtLocation(itemView, Gravity.BOTTOM, 0, Utils.checkDeviceHasNavigationBar()?Utils.getNavigationBarHeight():0);
                }
            });
            playlayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (SPUtils.get(itemView.getContext(), Constants.nowQueue_sp,"noQueue").equals(Constants.myList)){
                        MusicManager.getMusicManager().setIndex(i);
                    }else {
                        MusicManager.getMusicManager().setQueue(MusicDBHelper.getMusicDBHelper().ConvertQueue(MusicManager.myList),i,true);
                        SPUtils.put(itemView.getContext(),Constants.nowQueue_sp,Constants.myList);
                    }
                }
            });

        }

        public void setIsPlaying(boolean isPlaying){
            isplaying.setVisibility(isPlaying?View.VISIBLE:View.GONE);
            number.setVisibility(isPlaying?View.GONE:View.VISIBLE);
        }


        @Override
        public void onItemSelected() {
            itemView.setBackgroundResource(R.color.grayA);
        }

        @Override
        public void onItemClear() {
            itemView.setBackgroundColor(0);
        }
    }





}