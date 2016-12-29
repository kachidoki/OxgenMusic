package com.kachidoki.oxgenmusic.activity.fragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.SimpleTarget;
import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.widget.CDview;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by mayiwei on 16/12/28.
 */
public class CdViewFragment extends Fragment {

    @BindView(R.id.play_cdView)
    CDview cDview;


    private SimpleTarget target = new SimpleTarget<Bitmap>() {
        @Override
        public void onResourceReady(Bitmap resource, GlideAnimation<? super Bitmap> glideAnimation) {
            cDview.setImage(resource);
        }

        @Override
        public void onLoadFailed(Exception e, Drawable errorDrawable) {
            super.onLoadFailed(e, errorDrawable);
            cDview.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.cd_nomal_png));
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_play_cd_view,container,false);
        ButterKnife.bind(this, view);
        EventBus.getDefault().register(this);
        cDview.setImage(BitmapFactory.decodeResource(getResources(), R.drawable.cd_nomal));

        if (MusicManager.getMusicManager().getNowSong() != null) {
            loadCDBitmap();
        }

        if (MusicManager.getMusicManager().getIsPlaying()) {
            cDview.start();
        } else {
            cDview.pause();
        }

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);

    }


    @Subscribe
    public void onEvent(PlayEvent playEvent) {
        switch (playEvent.getAction()) {
            case CHANGE:
                if (MusicManager.getMusicManager().getNowSong() != null) {
                    loadCDBitmap();
                }
                if (MusicManager.getMusicManager().getIsPlaying()) {
                    cDview.start();
                } else {
                    cDview.pause();
                }
                break;
        }
    }

    private void loadCDBitmap() {
        Glide.with(this)
                .load(MusicManager.getMusicManager().getNowSong().albumpic_big)
                .asBitmap()
                .into(target);
    }



}
