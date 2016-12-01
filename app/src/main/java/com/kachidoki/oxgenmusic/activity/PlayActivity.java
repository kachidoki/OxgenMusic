package com.kachidoki.oxgenmusic.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.App;
import com.kachidoki.oxgenmusic.app.BaseActivity;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.player.MusicManager;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mayiwei on 16/11/30.
 */
public class PlayActivity extends BaseActivity {
    @BindView(R.id.play_more)
    ImageView playMore;
    @BindView(R.id.play_previous)
    ImageView playPrevious;
    @BindView(R.id.play_play)
    ImageView playPlay;
    @BindView(R.id.play_next)
    ImageView playNext;
    @BindView(R.id.play_mode)
    ImageView playMode;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        ButterKnife.bind(this);
        setToolbar(true);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (MusicManager.getMusicManager().getIsPlaying()){
            playPlay.setImageResource(R.drawable.icon_play_pause);
        }else {
            playPlay.setImageResource(R.drawable.icon_play_play);
        }
    }


    @OnClick({R.id.play_more,R.id.play_previous,R.id.play_play,R.id.play_next,R.id.play_mode})
    void sendCommand(View view){
        switch (view.getId()){
            case R.id.play_more:
                break;
            case R.id.play_previous:
                App.playEvent.setAction(PlayEvent.Action.PREVIOES);
                EventBus.getDefault().post(App.playEvent);
                break;
            case R.id.play_play:
                if (MusicManager.getMusicManager().getIsPlaying()){
                    App.playEvent.setAction(PlayEvent.Action.PAUSE);
                    playPlay.setImageResource(R.drawable.icon_play_play);
                }else {
                    App.playEvent.setAction(PlayEvent.Action.PLAY);
                    playPlay.setImageResource(R.drawable.icon_play_pause);
                }
                EventBus.getDefault().post(App.playEvent);
                break;
            case R.id.play_next:
                App.playEvent.setAction(PlayEvent.Action.NEXT);
                EventBus.getDefault().post(App.playEvent);
                break;
            case R.id.play_mode:
                break;
        }
    }
}
