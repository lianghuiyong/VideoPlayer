package com.sxzx.videoplayer;

/**
 * Created by lianghuiyong on 2017/7/26.
 */

public interface IVideoPlayer {
    void pause();

    void play();

    //设置播放路径
    IVideoPlayer setPath(String path);

    //是否是直播
    IVideoPlayer isLive(boolean isLive);

    IVideoPlayer canFullScreen(boolean canFullScreen);
}
