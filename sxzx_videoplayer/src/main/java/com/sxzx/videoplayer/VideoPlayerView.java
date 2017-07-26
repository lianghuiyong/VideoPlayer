package com.sxzx.videoplayer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.socks.library.KLog;
import com.sxzx.videoplayer.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by lianghuiyong on 2017/7/26.
 */

public class VideoPlayerView extends LinearLayout implements IVideoPlayer {
    private final String TAG = VideoPlayerView.class.getSimpleName();

    private View view;
    private Context context;

    /**
     * 控制主页面
     */
    private FrameLayout rootLayout;

    /**
     * 是否是直播
     */
    private boolean isLive = false;

    /**
     * 是否可以全屏，默认全屏
     */
    private boolean canFullscreen = true;

    /**
     * 播放进度
     */
    private int progress = 0;

    /**
     * 播放地址
     */
    private String path;

    private IjkVideoView videoView;
    private ProgressBar loading;

    public VideoPlayerView(Context context) {
        super(context);
        init(context);
    }

    public VideoPlayerView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public VideoPlayerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.context = context;
        view = LayoutInflater.from(context).inflate(R.layout.base_layout_videoplayer, this);

        rootLayout = (FrameLayout) view.findViewById(R.id.base_video_root_layout);
        videoView = (IjkVideoView) view.findViewById(R.id.base_video_view);
        loading = (ProgressBar) view.findViewById(R.id.base_video_loading);


        if (view.isInEditMode()) {
            return;
        }

        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Throwable e) {
            Log.e(TAG, "loadLibraries error", e);
        }


        videoView.setOnPreparedListener(new IMediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(IMediaPlayer iMediaPlayer) {
                KLog.e("onPrepared");
            }
        });

        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int i, int i1) {

                /**
                 int MEDIA_INFO_UNKNOWN = 1;//未知信息
                 int MEDIA_INFO_STARTED_AS_NEXT = 2;//播放下一条
                 int MEDIA_INFO_VIDEO_RENDERING_START = 3;//视频开始整备中
                 int MEDIA_INFO_VIDEO_TRACK_LAGGING = 700;//视频日志跟踪
                 int MEDIA_INFO_BUFFERING_START = 701;//开始缓冲中
                 int MEDIA_INFO_BUFFERING_END = 702;//缓冲结束
                 int MEDIA_INFO_NETWORK_BANDWIDTH = 703;//网络带宽，网速方面
                 int MEDIA_INFO_BAD_INTERLEAVING = 800;//
                 int MEDIA_INFO_NOT_SEEKABLE = 801;//不可设置播放位置，直播方面
                 int MEDIA_INFO_METADATA_UPDATE = 802;//
                 int MEDIA_INFO_TIMED_TEXT_ERROR = 900;
                 int MEDIA_INFO_UNSUPPORTED_SUBTITLE = 901;//不支持字幕
                 int MEDIA_INFO_SUBTITLE_TIMED_OUT = 902;//字幕超时

                 int MEDIA_INFO_VIDEO_INTERRUPT= -10000;//数据连接中断
                 int MEDIA_INFO_VIDEO_ROTATION_CHANGED = 10001;//视频方向改变
                 int MEDIA_INFO_AUDIO_RENDERING_START = 10002;//音频开始整备中

                 int MEDIA_ERROR_UNKNOWN = 1;//未知错误
                 int MEDIA_ERROR_SERVER_DIED = 100;//服务挂掉
                 int MEDIA_ERROR_NOT_VALID_FOR_PROGRESSIVE_PLAYBACK = 200;//数据错误没有有效的回收
                 int MEDIA_ERROR_IO = -1004;//IO错误
                 int MEDIA_ERROR_MALFORMED = -1007;
                 int MEDIA_ERROR_UNSUPPORTED = -1010;//数据不支持
                 int MEDIA_ERROR_TIMED_OUT = -110;//数据超时
                 * */
                switch (i) {
                    case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        KLog.e("onInfo = ", "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                        break;

                    //视频准备渲染
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        KLog.e("onInfo = ", "MEDIA_INFO_VIDEO_RENDERING_START:");
                        hideLoading();
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

    }

    @Override
    public void play() {
        videoView.setVideoPath(path);
        videoView.seekTo(progress);
        videoView.start();
        showLoading();
    }

    private void hide() {
        if (rootLayout != null) {
            rootLayout.setVisibility(GONE);
        }
    }

    private void show() {
        if (rootLayout != null) {
            rootLayout.setVisibility(VISIBLE);
        }
    }

    @Override
    public IVideoPlayer setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public IVideoPlayer isLive(boolean isLive) {
        this.isLive = isLive;
        return this;
    }

    @Override
    public IVideoPlayer canFullScreen(boolean canFullScreen) {
        this.canFullscreen = canFullScreen;
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
}
