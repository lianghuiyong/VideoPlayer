package com.sxzx.videoplayer;

import android.content.Context;
import android.media.AudioManager;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.danikula.videocache.HttpProxyCacheServer;
import com.socks.library.KLog;
import com.sxzx.videoplayer.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by lianghuiyong on 2017/7/26.
 */

public class VideoPlayerView extends BaseVideoView implements IVideoPlayer, View.OnClickListener {


    private final String TAG = VideoPlayerView.class.getSimpleName();

    // 初始高度
    private int mInitHeight;
    // 屏幕宽/高度
    private int mWidthPixels;

    public VideoPlayerView(Context context) {
        super(context);
    }

    public VideoPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(final Context context) {
        super.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.base_layout_videoplayer, this);
        rootLayout = (FrameLayout) view.findViewById(R.id.base_video_root_layout);
        controlLayout = (FrameLayout) view.findViewById(R.id.base_video_control_layout);
        videoView = (IjkVideoView) view.findViewById(R.id.base_video_view);
        loading = (ProgressBar) view.findViewById(R.id.base_video_loading);
        videoPlay = (ImageView) view.findViewById(R.id.base_video_play);
        videoTime = (TextView) view.findViewById(R.id.base_video_time);
        seekBar = (AppCompatSeekBar) view.findViewById(R.id.seekBar);

        if (view.isInEditMode()) {
            return;
        }

        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Throwable e) {
            Log.e(TAG, "load IjkMediaPlayer error", e);
        }

        final GestureDetector gestureDetector = new GestureDetector(context, new PlayerGestureListener());
        rootLayout.setOnClickListener(this);
        rootLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (gestureDetector.onTouchEvent(motionEvent))
                    return true;

                // 处理手势结束
                switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
                    case MotionEvent.ACTION_UP:
                        KLog.e("MotionEvent.ACTION_UP");
                        KLog.e(mMoveProgress);
                        if (mMoveProgress != 0) {
                            seekTo(mMoveProgress * seekBar.getMax());
                            mMoveProgress = 0;
                            return true;
                        }
                        break;
                }
                return false;
            }
        });

        videoPlay.setOnClickListener(this);
        view.findViewById(R.id.base_video_back).setOnClickListener(this);
        view.findViewById(R.id.base_video_full_screen).setOnClickListener(this);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            private long mTargetPosition;

            @Override
            public void onStartTrackingTouch(SeekBar bar) {
                stopSync();
            }

            @Override
            public void onProgressChanged(SeekBar bar, int progress, boolean fromUser) {
                if (!fromUser) {
                    return;
                }

                long duration = videoView.getDuration();
                // 计算目标位置
                mTargetPosition = (duration * progress) / seekBar.getMax();
                videoTime.setText(generateTime(mTargetPosition) + "/" + generateTime(duration));
            }

            @Override
            public void onStopTrackingTouch(SeekBar bar) {
                seekTo((int) mTargetPosition);
            }
        });

        setInfoListener();
    }

    protected void setInfoListener() {
        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int i1) {
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        KLog.e("onInfo = ", "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                        break;

                    //视频准备渲染
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        KLog.e("onInfo = ", "MEDIA_INFO_VIDEO_RENDERING_START:");
                        setVisibility(VISIBLE);
                        hideLoading();
                        videoPlay.setImageResource(R.drawable.ic_pause);
                        break;

                    //开始缓冲
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_START:
                        KLog.e("onInfo = ", "MEDIA_INFO_BUFFERING_START:");
                        break;

                    //缓冲结束
                    case IMediaPlayer.MEDIA_INFO_BUFFERING_END:
                        KLog.e("onInfo = ", "MEDIA_INFO_BUFFERING_END:");
                        break;

                    case IMediaPlayer.MEDIA_INFO_NETWORK_BANDWIDTH:
                        KLog.e("onInfo = ", "MEDIA_INFO_NETWORK_BANDWIDTH: ");
                        break;
                    case IMediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
                        KLog.e("onInfo = ", "MEDIA_INFO_BAD_INTERLEAVING:");
                        break;

                    //表示不可拖动
                    case IMediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
                        KLog.e("onInfo = ", "MEDIA_INFO_NOT_SEEKABLE:");
                        break;

                    case IMediaPlayer.MEDIA_INFO_METADATA_UPDATE:
                        KLog.e("onInfo = ", "MEDIA_INFO_METADATA_UPDATE:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_UNSUPPORTED_SUBTITLE:
                        KLog.e("onInfo = ", "MEDIA_INFO_UNSUPPORTED_SUBTITLE:");
                        break;
                    case IMediaPlayer.MEDIA_INFO_SUBTITLE_TIMED_OUT:
                        KLog.e("onInfo = ", "MEDIA_INFO_SUBTITLE_TIMED_OUT:");
                        break;

                    //视频方向旋转
                    case IMediaPlayer.MEDIA_INFO_VIDEO_ROTATION_CHANGED:
                        KLog.e("onInfo = ", "MEDIA_INFO_VIDEO_ROTATION_CHANGED: ");
                        break;

                    case IMediaPlayer.MEDIA_INFO_AUDIO_RENDERING_START:
                        KLog.e("onInfo = ", "MEDIA_INFO_AUDIO_RENDERING_START:");
                        break;
                }
                return false;
            }
        });
    }


    @Override
    public void pause() {
        videoView.pause();
        videoPlay.setImageResource(R.drawable.ic_play);
        showRootLayout(true);
        stopSync();
    }

    @Override
    public void stop() {
        videoView.stopPlayback();
        videoView.stopBackgroundPlay();
        IjkMediaPlayer.native_profileEnd();
        showRootLayout(true);
        stopSync();
    }

    @Override
    public void play() {
        videoView.start();
        videoPlay.setImageResource(R.drawable.ic_pause);
        showRootLayout(false);
        startSync();
    }

    /**
     * 跳转
     *
     * @param position 位置
     */
    public void seekTo(int position) {
        videoView.seekTo(position);
        videoView.start();
        startSync();
    }

    /**
     * 每一秒更新进度
     */
    @Override
    protected void syncProgress() {
        long position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();

        if (seekBar != null) {
            if (duration > 0) {
                long pos = seekBar.getMax() * position / duration;
                seekBar.setProgress((int) pos);
            }

            int percent = videoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent * 10);
        }

        if (videoTime != null) {
            videoTime.setText(generateTime(position) + "/" + generateTime(duration));
        }
    }

    protected int setProgress(long progress) {
        int position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();

        if (progress > duration) {
            progress = duration;
        } else if (progress <= 0) {
            progress = 0;
        }

        long pos = seekBar.getMax() * progress / duration;
        seekBar.setProgress((int) pos);


        // 对比当前位置来显示快进或后退
        videoTime.setText(generateTime(progress) + "/" + generateTime(duration));

        //快进快退时间
        int deltaTime = (int) ((progress - position) / 1000);

        KLog.e((deltaTime > 0 ? "快进" : "快退") + deltaTime + "秒");

        return (int) pos;
    }

    private void showRootLayout(boolean isShow) {
        if (controlLayout != null) {
            controlLayout.setVisibility(isShow ? VISIBLE : GONE);
        }
    }

    @Override
    public IVideoPlayer setPath(String path) {
        this.path = path;
        HttpProxyCacheServer cacheServer = VideoCacheUtils.getInstance().getProxy(context);
        String url = cacheServer.getProxyUrl(path);
        videoView.setVideoPath(url);
        return this;
    }

    @Override
    public IVideoPlayer isLive(boolean isLive) {
        this.isLive = isLive;
        return this;
    }

    private void showLoading() {
        if (loading != null) {
            loading.setVisibility(VISIBLE);
        }
    }

    private void hideLoading() {
        if (loading != null) {
            loading.setVisibility(GONE);
        }
    }

    public void addPlayerViewOnClickListener(OnPlayerViewOnClickListener listener) {
        this.listener = listener;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mInitHeight == 0) {
            mInitHeight = getHeight();
            mWidthPixels = getResources().getDisplayMetrics().widthPixels;
        }
    }

    /**
     * 改变视频布局高度
     *
     * @param isFullscreen
     */
    public void changeHeight(boolean isFullscreen) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        if (isFullscreen) {
            // 高度扩展为横向全屏
            layoutParams.height = mWidthPixels;
        } else {
            // 还原高度
            layoutParams.height = mInitHeight;
        }
        setLayoutParams(layoutParams);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.base_video_root_layout) {
            showRootLayout(controlLayout.getVisibility() != VISIBLE);
        } else if (i == R.id.base_video_play) {
            if (videoView != null && videoView.isPlaying()) {
                pause();
            } else {
                play();
            }
        } else if (i == R.id.base_video_back) {
            if (listener != null) {
                listener.back();
            }
        } else if (i == R.id.base_video_full_screen) {
            if (listener != null) {
                listener.fullScreen();
            }
        }
    }

    /**
     * 播放器的手势监听
     */
    public class PlayerGestureListener extends GestureDetector.SimpleOnGestureListener {

        // 是否是按下的标识，默认为其他动作，true为按下标识，false为其他动作
        private boolean isDownTouch;
        // 是否声音控制,默认为亮度控制，true为声音控制，false为亮度控制
        private boolean isVolume;
        // 是否横向滑动，默认为纵向滑动，true为横向滑动，false为纵向滑动
        private boolean isLandscape;

        @Override
        public boolean onDown(MotionEvent e) {
            isDownTouch = true;
            return false;
        }

        @Override
        public void onShowPress(MotionEvent e) {

        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (listener != null) {
                listener.fullScreen();
            }
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            float mOldX = e1.getX(), mOldY = e1.getY();
            float deltaY = mOldY - e2.getY();
            float deltaX = mOldX - e2.getX();
            if (isDownTouch) {
                // 判断左右或上下滑动
                isLandscape = Math.abs(distanceX) >= Math.abs(distanceY);
                // 判断是声音或亮度控制
                isVolume = mOldX > getResources().getDisplayMetrics().widthPixels * 0.5f;
                isDownTouch = false;
            }

            if (isLandscape) {
                //快进或者快退
                mMoveProgress = setProgressSlide(-deltaX / videoView.getWidth());
            } else {
                float percent = deltaY / videoView.getHeight();
                if (isVolume) {
                    setVolumeSlide(percent);
                } else {
                    setBrightnessSlide(percent);
                }
            }

            return false;
        }

        @Override
        public void onLongPress(MotionEvent e) {

        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            return false;
        }
    }

    ;

    //快进快退进度更新
    protected int setProgressSlide(float percent) {
        stopSync();

        int position = videoView.getCurrentPosition();
        long duration = videoView.getDuration();
        // 单次拖拽最大时间差为100秒或播放时长的1/2
        long deltaMax = Math.min(100 * 1000, duration / 2);
        // 计算滑动时间
        long delta = (long) (deltaMax * percent);
        // 目标位置
        long mTargetPosition = delta + position;

        showRootLayout(true);
        return setProgress(mTargetPosition);
    }

    //更新音量值
    protected void setVolumeSlide(float percent) {

        KLog.e("setVolumeSlide = " + percent);
/*        if (mCurVolume == INVALID_VALUE) {
            mCurVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if (mCurVolume < 0) {
                mCurVolume = 0;
            }
        }
        int index = (int) (percent * mMaxVolume) + mCurVolume;
        if (index > mMaxVolume) {
            index = mMaxVolume;
        } else if (index < 0) {
            index = 0;
        }
        // 变更声音
        mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, index, 0);*/
    }

    //设置亮度值
    protected void setBrightnessSlide(float percent) {
        KLog.e("setBrightnessSlide = " + percent);
    }
}
