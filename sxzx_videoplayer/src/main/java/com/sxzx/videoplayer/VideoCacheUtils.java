package com.sxzx.videoplayer;

import android.annotation.SuppressLint;
import android.content.Context;

import com.danikula.videocache.HttpProxyCacheServer;

/**
 * Created by lianghuiyong on 2017/7/27.
 */

public class VideoCacheUtils {
    private HttpProxyCacheServer proxy;

    @SuppressLint("StaticFieldLeak")
    private static VideoCacheUtils instance;

    private VideoCacheUtils() {
    }

    /**
     * 单一实例
     */
    public static VideoCacheUtils getInstance() {
        if (instance == null) {
            instance = new VideoCacheUtils();
        }
        return instance;
    }

    public HttpProxyCacheServer getProxy(Context context) {
        return instance.proxy == null ? (instance.proxy = instance.newProxy(context)) : instance.proxy;
    }

    private HttpProxyCacheServer newProxy(Context context) {
        return new HttpProxyCacheServer.Builder(context)
                .maxCacheSize(200 * 1024 * 1024)       // 400M for cache
                .build();
    }
}
