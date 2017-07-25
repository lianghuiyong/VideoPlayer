package com.sxzx.videoplayer;

import android.util.Log;

import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created by lianghuiyong on 2017/7/25.
 */

public class VideoPlayerManager {

    public static void start(){
        try {
            IjkMediaPlayer.loadLibrariesOnce(null);
            IjkMediaPlayer.native_profileBegin("libijkplayer.so");
        } catch (Throwable e) {
            Log.e("GiraffePlayer", "loadLibraries error", e);
        }
    }
}
