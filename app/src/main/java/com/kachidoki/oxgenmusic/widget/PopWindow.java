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
import com.kachidoki.oxgenmusic.model.bean.SongBean;
import com.kachidoki.oxgenmusic.model.event.PlayEvent;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.utils.SPUtils;

import org.greenrobot.eventbus.EventBus;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mayiwei on 16/11/30.
 */
public class PopWindow extends PopupWindow {

    private Context context;
    private View view;
    private Song song;
    @BindView(R.id.pop_addlist)
    LinearLayout add;
    @BindView(R.id.pop_playthis)
    LinearLayout playthis;
    @BindView(R.id.pop_cancel)
    LinearLayout cancel;


    public PopWindow(Context mContext, Song mSong) {
        context = mContext;
        song = mSong;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.pop_list, null);
        ButterKnife.bind(this,view);
        // 设置外部可点击
        this.setOutsideTouchable(true);

        // mMenuView添加OnTouchListener监听判断获取触屏位置如果在选择框外面则销毁弹出框
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

         /* 设置弹出窗口特征 */
        // 设置视图
        this.setContentView(this.view);
        // 设置弹出窗体的宽和高
        this.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
        this.setWidth(RelativeLayout.LayoutParams.MATCH_PARENT);

        // 设置弹出窗体可点击
        this.setFocusable(true);

        // 设置弹出窗体的背景
        this.setBackgroundDrawable(new ColorDrawable(0xff282828));

        // 设置弹出窗体显示时的动画，从底部向上弹出
        this.setAnimationStyle(R.style.pop_anim);
    }

    @OnClick({R.id.pop_playthis,R.id.pop_addlist,R.id.pop_cancel})
    void toTarget(View view){
        switch (view.getId()){
            case R.id.pop_addlist:
                if (!MusicManager.getMusicManager().checkIsAdd(song)){
                    Toast.makeText(context,"添加成功",Toast.LENGTH_SHORT).show();
                    MusicManager.getMusicManager().addQueue(song);
                    MusicDBHelper.getMusicDBHelper().saveSong(song,MusicManager.myList);
                }else {
                    Toast.makeText(context,"已在播放列表",Toast.LENGTH_SHORT).show();
                }
                dismiss();
                break;
            case R.id.pop_playthis:
                Toast.makeText(context,"播放歌曲",Toast.LENGTH_SHORT).show();
                if(!MusicManager.getMusicManager().playAndCheck(song)){
                    MusicManager.getMusicManager().addQueuePlay(song);
                    MusicDBHelper.getMusicDBHelper().saveSong(song,MusicManager.myList);
                    App.playEvent.setAction(PlayEvent.Action.PLAYNOW);
                    EventBus.getDefault().post(App.playEvent);
                }
                dismiss();
                break;
            case R.id.pop_cancel:
                dismiss();
                break;
        }
    }

}
