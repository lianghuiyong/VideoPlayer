package com.better.videoplayer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.sxzx.videoplayer.VideoPlayerView;

public class MainActivity extends AppCompatActivity {
    private String url1 = "http://1252602955.vod2.myqcloud.com/e56d0644vodgzp1252602955/772a18949031868222899077374/f0.mp4";


    VideoPlayerView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        videoView = (VideoPlayerView) findViewById(R.id.video_view);

        videoView.setPath(url1)
                .play();
    }

}
