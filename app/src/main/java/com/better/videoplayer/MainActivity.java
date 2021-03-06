package com.better.videoplayer;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;

import com.sxzx.videoplayer.IVideoPlayer;
import com.sxzx.videoplayer.VideoPlayerView;

public class MainActivity extends AppCompatActivity {
    private String url1 = "http://1252602955.vod2.myqcloud.com/e56d0644vodgzp1252602955/772a18949031868222899077374/f0.mp4";
    //private String url1 = "http://www.w3school.com.cn/example/html5/mov_bbb.mp4";

    VideoPlayerView video;

    // 屏幕UI可见性
    private int mScreenUiVisibility;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        video = (VideoPlayerView) findViewById(R.id.video_view);

        video.setPath(url1).play();
        // 视频播放时开启屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        video.addPlayerViewOnClickListener(new IVideoPlayer.OnPlayerViewOnClickListener() {
            @Override
            public void onBack() {
                onBackPressed();
            }

            @Override
            public void onFullScreen() {
                toFullScreen();
            }

            @Override
            public float onBrightness(float percent) {
                return setBrightness(percent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        //判断是否横屏
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //设置屏幕为横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 全屏切换，点击全屏按钮
     */
    private void toFullScreen() {
        //判断是否横屏
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            //设置屏幕为横屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            //设置屏幕为竖屏
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

        // 沉浸式只能在SDK19以上实现
        if (Build.VERSION.SDK_INT >= 14) {
            if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
                // 获取关联 Activity 的 DecorView
                View decorView = getWindow().getDecorView();
                // 保存旧的配置
                mScreenUiVisibility = decorView.getSystemUiVisibility();
                // 沉浸式使用这些Flag
                decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE |
                        View.SYSTEM_UI_FLAG_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN |
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION |
                        View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                );
                getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                video.changeHeight(true);
                onFullSpace();

            } else if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
                View decorView = getWindow().getDecorView();
                // 还原
                decorView.setSystemUiVisibility(mScreenUiVisibility);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

                video.changeHeight(false);

                onLandSpace();
            }
        }
    }

    //竖屏
    protected void onLandSpace(){
        findViewById(R.id.root_layout).setVisibility(View.VISIBLE);
    }

    //全屏
    protected void onFullSpace(){
        findViewById(R.id.root_layout).setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        video.pause();

        //关闭屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onResume() {
        super.onResume();
        video.play();

        // 视频播放时开启屏幕常亮
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        video.stop();

        //关闭屏幕常亮
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private float setBrightness(float percent){
        WindowManager.LayoutParams lp = getWindow().getAttributes();

        lp.screenBrightness = lp.screenBrightness + percent;
        if (lp.screenBrightness > 1) {
            lp.screenBrightness = 1;
        } else if (lp.screenBrightness < 0.1) {
            lp.screenBrightness = (float) 0.1;
        }
        getWindow().setAttributes(lp);
        return lp.screenBrightness;
    }
}
