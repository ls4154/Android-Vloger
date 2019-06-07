package com.example.androidvloger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class VideoActivity extends AppCompatActivity {
    final String IP_ADDR = "13.124.45.74";
    String videoId;
    VideoView vv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        
        vv = findViewById(R.id.videoView);

        Intent intent = getIntent();
        videoId = intent.getStringExtra("id");

        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(vv);
        vv.setMediaController(mediaController);

        getSupportActionBar().hide();
    }

    @Override
    protected void onStart() {
        super.onStart();
        initPlayer();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlayer();
    }

    public void initPlayer() {
        String path = "http://" + IP_ADDR + "/vid" + videoId + ".mp4";
        vv.setVideoPath(path);
        vv.start();
    }

    public void stopPlayer() {
        vv.stopPlayback();
    }
}
