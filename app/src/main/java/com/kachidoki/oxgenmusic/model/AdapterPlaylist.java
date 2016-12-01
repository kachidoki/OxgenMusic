package com.kachidoki.oxgenmusic.model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.App;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.player.MusicManager;
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
        @BindView(R.id.songername_list)
        TextView singername;
        @BindView(R.id.songname_list)
        TextView songname;
        @BindView(R.id.more_list)
        ImageView more;
        @BindView(R.id.number_list)
        TextView number;

        public PlayListViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);
        }
        public void setData(final Song song,int i){
            singername.setText(song.singername);
            songname.setText(song.songname);
            number.setText((i+1)+"");

            final PopWindow popWindow = new PopWindow(itemView.getContext(), new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    switch (view.getId()){
                        case R.id.pop_addlist:
                            if (!MusicManager.getMusicManager().checkIsAdd(song)){
                                Toast.makeText(itemView.getContext(),"添加成功",Toast.LENGTH_SHORT).show();
                                MusicManager.getMusicManager().addQueue(song);
                            }else {
                                Toast.makeText(itemView.getContext(),"已在播放列表",Toast.LENGTH_SHORT).show();
                            }

                            break;
                        case R.id.pop_playthis:
                            Toast.makeText(itemView.getContext(),"播放歌曲",Toast.LENGTH_SHORT).show();
                            if(!MusicManager.getMusicManager().playAndCheck(song)){
                                MusicManager.getMusicManager().addQueuePlay(song);
                                App.playEvent.setAction(PlayEvent.Action.PLAYNOW);
                                EventBus.getDefault().post(App.playEvent);
                            }
                            break;


                    }
                }
            });
            more.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    popWindow.showAtLocation(itemView,Gravity.CENTER_VERTICAL, 0, 0);
                }
            });
        }


    }





}
