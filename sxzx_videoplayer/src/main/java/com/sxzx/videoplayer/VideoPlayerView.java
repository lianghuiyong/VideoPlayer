package com.sxzx.videoplayer;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.github.rubensousa.previewseekbar.PreviewSeekBar;
import com.socks.library.KLog;
import com.sxzx.videoplayer.media.IjkVideoView;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by lianghuiyong on 2017/7/26.
 */

public class VideoPlayerView extends BaseVideoView implements IVideoPlayer {
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
        seekBar = (PreviewSeekBar) view.findViewById(R.id.seekBar);

        if (view.isInEditMode()) {
            return;
        }

        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Throwable e) {
            Log.e(TAG, "load IjkMediaPlayer error", e);
        }

        rootLayout.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (controlLayout.getVisibility() == VISIBLE) {
                    hideRootLayout();
                } else {
                    showRootLayout();
                }
            }
        });


        videoPlay.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (videoView.isPlaying()) {
                    if (isLive) {
                        videoView.stopPlayback();
                    } else {
                        pause();
                    }
                    videoPlay.setImageResource(R.drawable.ic_play);
                } else {
                    videoPlay.setImageResource(R.drawable.ic_pause);
                    play();
                }
            }
        });

        view.findViewById(R.id.base_video_back).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.back();
                }
            }
        });

        view.findViewById(R.id.base_video_full_screen).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.fullScreen();
                }
            }
        });

        videoView.setOnInfoListener(new IMediaPlayer.OnInfoListener() {
            @Override
            public boolean onInfo(IMediaPlayer iMediaPlayer, int what, int i1) {

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
                switch (what) {
                    case IMediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
                        KLog.e("onInfo = ", "MEDIA_INFO_VIDEO_TRACK_LAGGING:");
                        break;

                    //视频准备渲染
                    case IMediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        KLog.e("onInfo = ", "MEDIA_INFO_VIDEO_RENDERING_START:");

                        videoPlay.setImageResource(R.drawable.ic_pause);
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
        videoView.pause();
        setProgress(videoView.getCurrentPosition());
        showRootLayout();
        stopSync();
    }

    @Override
    public void stop() {
        videoView.stopPlayback();
        showRootLayout();
        stopSync();
    }

    @Override
    public void play() {
        videoView.setVideoPath(path);
        if (!isLive) {
            videoView.seekTo(progress);
        }
        videoView.start();
        showLoading();
        hideRootLayout();
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
                long pos = 1000L * position / duration;
                seekBar.setProgress((int) pos);
            }
            int percent = videoView.getBufferPercentage();
            seekBar.setSecondaryProgress(percent * 10);
        }
        if (videoTime != null) {
            videoTime.setText(generateTime(position));
        }
    }

    private void hideRootLayout() {
        if (controlLayout != null) {
            controlLayout.setVisibility(GONE);
        }
    }

    private void showRootLayout() {
        if (controlLayout != null) {
            controlLayout.setVisibility(VISIBLE);
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
}
