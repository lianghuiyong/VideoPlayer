package com.sxzx.videoplayer;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sxzx.videoplayer.media.IjkVideoView;

import java.util.Locale;

/**
 * Created by lianghuiyong on 2017/7/27.
 */

public abstract class BaseVideoView extends LinearLayout {

    //handler 每隔一秒同步进度
    protected static final int SYNC_PROGRESS = 0;

    protected View view;
    protected Context context;

    /**
     * 控制顶部页面
     */
    protected FrameLayout rootLayout;

    /**
     * 控制主页面
     */
    protected FrameLayout controlLayout;

    /**
     * 播放控制按钮
     */
    protected ImageView videoPlay;

    /**
     * 是否是直播
     */
    protected boolean isLive = false;

    /**
     * 播放进度
     */
    protected int progress = 0;

    protected TextView videoTime;

    /**
     * 播放地址
     */
    protected String path;

    protected IjkVideoView videoView;
    protected ProgressBar loading;
    protected AppCompatSeekBar seekBar;

    /**
     * 按键监听回调
     */
    protected IVideoPlayer.OnPlayerViewOnClickListener listener;

    public BaseVideoView(Context context) {
        super(context);
        init(context);
    }

    public BaseVideoView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    /**
     * 消息处理
     */
    @SuppressWarnings("HandlerLeak")
    protected Handler mHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SYNC_PROGRESS:
                    syncProgress();
                    sendEmptyMessageDelayed(SYNC_PROGRESS, 1000);
            }
        }
    };

    protected void startSync() {
        mHandler.removeMessages(SYNC_PROGRESS);
        mHandler.sendEmptyMessageDelayed(SYNC_PROGRESS, 1000);
    }

    protected void stopSync() {
        mHandler.removeMessages(SYNC_PROGRESS);
    }

    /**
     * 时长格式化显示
     */
    protected String generateTime(long duration) {
        long total_seconds = duration / 1000;
        long hours = total_seconds / 3600;
        long minutes = (total_seconds % 3600) / 60;
        long seconds = total_seconds % 60;
        if (duration <= 0) {
            return "00:00";
        }
        if (hours >= 100) {
            return String.format(Locale.US, "%d:%02d:%02d", hours, minutes, seconds);
        } else if (hours > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format(Locale.US, "%02d:%02d", minutes, seconds);
        }
    }

    /**
     * 获取当前播放位置
     */
    public int getCurrentPosition() {
        int currentPosition;
        if (!isLive) {
            currentPosition = videoView.getCurrentPosition();
        } else {
            /**直播*/
            currentPosition = -1;
        }
        return currentPosition;
    }

    protected abstract void syncProgress();

    protected abstract void init(Context context);
}
