package com.sxzx.videoplayer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
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

    public VideoPlayerView(Context context) {
        super(context);
    }

    public VideoPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void init(Context context) {
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

        rootLayout.setOnClickListener(this);
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

    private void setProgress(int progress) {
        this.progress = progress;
    }

    public void addPlayerViewOnClickListener(OnPlayerViewOnClickListener listener) {
        this.listener = listener;
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
}
