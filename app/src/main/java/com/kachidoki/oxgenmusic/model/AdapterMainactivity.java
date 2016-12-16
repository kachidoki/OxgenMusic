package com.kachidoki.oxgenmusic.model;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.activity.RankActivity;
import com.kachidoki.oxgenmusic.model.bean.Song;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayiwei on 16/11/28.
 */
public class AdapterMainactivity extends RecyclerView.Adapter {
    List<Song> songLists;
    public Context context;

    public AdapterMainactivity(Context context){
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_recycler_hot, parent, false);
        return new MusicCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        MusicCardViewHolder musicCardViewHolder = (MusicCardViewHolder) holder;
        musicCardViewHolder.setData(songLists.get(position));
    }

    @Override
    public int getItemCount() {
        return songLists==null?0:songLists.size();
    }

    public void setData(List<Song> songLists){
        this.songLists = songLists;
        notifyDataSetChanged();
    }


    static class MusicCardViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.image_card_amb)
        ImageView img;
        @BindView(R.id.name_card_amb)
        TextView name;

        public MusicCardViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this,itemView);

        }

        public void setData(final Song song){
            Glide.with(itemView.getContext()).load(song.albumpic_big).into(img);
            name.setText(song.songname);
            name.getBackground().setAlpha(230);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), RankActivity.class);
                    intent.putExtra("topid", "26");
                    itemView.getContext().startActivity(intent);
                }
            });
        }
    }

}
