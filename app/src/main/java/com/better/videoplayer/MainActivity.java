package com.better.videoplayer;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.sxzx.videoplayer.VideoPlayerManager;
import com.sxzx.videoplayer.media.IjkVideoView;

public class MainActivity extends AppCompatActivity {
    private String url1 = "http://1252602955.vod2.myqcloud.com/e56d0644vodgzp1252602955/772a18949031868222899077374/f0.mp4";


    IjkVideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (IjkVideoView) findViewById(R.id.video_view);

        VideoPlayerManager.start();

        videoView.setVideoPath(url1);
        videoView.start();
    }

}
