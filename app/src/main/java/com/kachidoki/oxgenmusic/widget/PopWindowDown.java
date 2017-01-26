package com.kachidoki.oxgenmusic.widget;

import android.content.Context;
import android.content.Intent;
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
import com.kachidoki.oxgenmusic.config.Constants;
import com.kachidoki.oxgenmusic.model.MusicDBHelper;
import com.kachidoki.oxgenmusic.model.bean.Song;
import com.kachidoki.oxgenmusic.player.DownloadService;
import com.kachidoki.oxgenmusic.player.MusicManager;
import com.kachidoki.oxgenmusic.player.PlayerService;
import com.kachidoki.oxgenmusic.utils.SPUtils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by mayiwei on 17/1/25.
 */
public class PopWindowDown extends PopupWindow {
    private Context context;
    private View view;
    private Song song;
    private List<Song> songList;
    private int queueIndex;
    private String callname;
    @BindView(R.id.popDown_addlist)
    LinearLayout add;
    @BindView(R.id.popDown_playthis)
    LinearLayout playthis;
    @BindView(R.id.popDown_cancel)
    LinearLayout cancel;
    @BindView(R.id.popDown_delete)
    LinearLayout delete;

    private OnChange onChange;

    public interface OnChange{
        void Callback(int i);
    }

    public PopWindowDown(Context mContext, Song mSong, List<Song> songs,int index,String callname,OnChange callback) {
        this.context = mContext;
        this.song = mSong;
        this.songList = songs;
        this.queueIndex = index;
        this.callname = callname;
        this.onChange = callback;
        this.view = LayoutInflater.from(mContext).inflate(R.layout.pop_mydown, null);
        ButterKnife.bind(this, view);
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

    @OnClick({R.id.popDown_playthis, R.id.popDown_addlist, R.id.popDown_cancel,R.id.popDown_delete})
    void toTarget(View view) {
        switch (view.getId()) {
            case R.id.popDown_addlist:
                if (SPUtils.get(context, Constants.nowQueue_sp,"noQueue").equals(Constants.myList)){
                    if (!MusicManager.getMusicManager().checkIsAdd(song)) {
                        Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                        MusicManager.getMusicManager().addQueue(song);
                        MusicDBHelper.getMusicDBHelper().saveSong(song, MusicManager.myList);
                    } else {
                        Toast.makeText(context, "已在我的歌单", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    if (!MusicDBHelper.getMusicDBHelper().checkIsAdd(song.songname,MusicManager.myList)){
                        Toast.makeText(context, "添加成功", Toast.LENGTH_SHORT).show();
                        MusicDBHelper.getMusicDBHelper().saveSong(song, MusicManager.myList);
                    }else {
                        Toast.makeText(context, "已在我的歌单", Toast.LENGTH_SHORT).show();
                    }
                }
                dismiss();
                break;
            case R.id.popDown_playthis:
                Toast.makeText(context, "播放歌曲", Toast.LENGTH_SHORT).show();
                if (SPUtils.get(context,Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)
                        &&SPUtils.get(context,Constants.hotListname_sp,"noname").equals(callname)){
                    //设置index即可
                    Log.e("Test","设置index即可");
                    MusicManager.getMusicManager().setIndex(queueIndex);
                }else {
                    //重置队列
                    SPUtils.put(context,Constants.hotListname_sp,callname);
                    MusicDBHelper.getMusicDBHelper().deleteQueueSong(MusicManager.hotList);
                    MusicDBHelper.getMusicDBHelper().saveListSong(songList,MusicManager.hotList);
                    MusicManager.getMusicManager().setQueue(songList,queueIndex,false);
                    Intent PlayNow = new Intent(context, PlayerService.class);
                    PlayNow.putExtra("command", PlayerService.CommandPlayNow);
                    context.startService(PlayNow);
                }
                if (SPUtils.get(context,Constants.nowQueue_sp,"noQueue").equals(Constants.myList)){
                    SPUtils.put(context,Constants.nowQueue_sp,Constants.hotList);
                    SPUtils.put(context,Constants.hotListname_sp,callname);
                }
                dismiss();
                break;
            case R.id.popDown_delete:
//                MusicManager.getMusicManager().deleteSong(queueIndex,MusicManager.getMusicManager().getIsPlaying(),
//                        SPUtils.get(context, Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)
//                        &&SPUtils.get(context, Constants.hotListname_sp,"nocall").equals(callname));
                MusicDBHelper.getMusicDBHelper().deleteDownSong(song);
                onChange.Callback(queueIndex);
                if (MusicManager.getMusicManager().getIsfirst()
                        &&SPUtils.get(context,Constants.nowQueue_sp,"noQueue").equals(Constants.hotList)
                        &&SPUtils.get(context, Constants.hotListname_sp,"nocall").equals(callname)){
                    SPUtils.put(context,Constants.nowIndex_sp,MusicManager.getMusicManager().getIndex());
                }
                dismiss();
                break;
            case R.id.popDown_cancel:
                dismiss();
                break;

        }
    }
}
