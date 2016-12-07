package com.kachidoki.oxgenmusic.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.utils.Utils;
import com.kachidoki.oxgenmusic.widget.PopWindow;
import com.kachidoki.oxgenmusic.widget.PopWindowMylist;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayiwei on 16/12/7.
 */
public class AdapterMylist extends RecyclerView.Adapter {
    List<Song> songLists;
    public Context context;

    private PopWindowMylist.OnChange onChange = new PopWindowMylist.OnChange() {
        @Override
        public void Callback(int i) {
            songLists.remove(i);
            notifyDataSetChanged();
        }
    };

    public AdapterMylist(Context context){
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_list, parent, false);
        return new PlayListViewHolder(view,onChange);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        PlayListViewHolder playListViewHolder = (PlayListViewHolder) holder;
        playListViewHolder.setData(songLists.get(position),position);
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
        PopWindowMylist.OnChange callback;
        @BindView(R.id.songername_list)
        TextView singername;
        @BindView(R.id.songname_list)
        TextView songname;
        @BindView(R.id.more_list)
        LinearLayout more;
        @BindView(R.id.number_list)
        TextView number;

        public PlayListViewHolder(View itemView, PopWindowMylist.OnChange onChange) {
            super(itemView);
            ButterKnife.bind(this,itemView);
            callback = onChange;
        }
        public void setData(final Song song,int i){
            singername.setText(song.singername);
            songname.setText(song.songname);
            number.setText((i+1)+"");

            final PopWindowMylist popWindow = new PopWindowMylist(itemView.getContext(),song,i,callback);
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popWindow.showAtLocation(itemView, Gravity.BOTTOM, 0, Utils.getNavigationBarHeight());
                }
            });
        }


    }





}