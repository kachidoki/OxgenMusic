package com.kachidoki.oxgenmusic.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.kachidoki.oxgenmusic.R;
import com.kachidoki.oxgenmusic.app.App;
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.MusicDBHelper;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mayiwei on 16/12/7.
 */
public class PopWindowMylist extends PopupWindow {

    private Context context;
    private View view;
    private Song song;
    private int index;
    private OnChange onChange;

    public interface OnChange{
        void Callback(int i);
    }


    public PopWindowMylist(Context mContext, Song mSong,int position,OnChange callback) {
        context = mContext;
        song = mSong;
        index = position;
        onChange = callback;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.pop_mylist, null);
        ButterKnife.bind(this,view);
        this.setOutsideTouchable(true);

        this.view.setOnTouchListener(new View.OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {

                int height = view.findViewById(R.id.pop_layout).getTop();

                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });

        this.setContentView(this.view);
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);
        this.setFocusable(true);
        this.setBackgroundDrawable(new ColorDrawable(0xff282828));
        this.setAnimationStyle(R.style.pop_anim);
    }

    @OnClick({R.id.popMy_playthis,R.id.popMy_delete,R.id.popMy_cancel})
    void toTarget(View view){
        switch (view.getId()){
            case R.id.popMy_playthis:
                if (SPUtils.get(context, Constants.nowQueue_sp,"noQueue").equals(Constants.myList)){
                    MusicManager.getMusicManager().setIndex(index);
                }else {
                    MusicManager.getMusicManager().setQueue(MusicDBHelper.getMusicDBHelper().ConvertQueue(MusicManager.myList),index,true);
                    SPUtils.put(context,Constants.nowQueue_sp,Constants.myList);
                }
                dismiss();
                break;
            case R.id.popMy_delete:
                MusicManager.getMusicManager().deleteSong(index,MusicManager.getMusicManager().getIsPlaying(),SPUtils.get(context, Constants.nowQueue_sp,"noQueue").equals(Constants.myList));
                MusicDBHelper.getMusicDBHelper().deleteSingleSong(song);
                onChange.Callback(index);
                dismiss();
                break;
            case R.id.popMy_cancel:
                dismiss();
                break;
        }
    }

}
