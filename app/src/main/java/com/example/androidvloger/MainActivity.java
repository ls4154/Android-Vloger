package com.example.androidvloger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    VideoView vv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        vv = findViewById(R.id.vv1);

        MediaController controller = new MediaController(this);
        //controller.setMediaPlayer(vv);5
        vv.setMediaController(controller);

    }

    @Override
    protected void onStart() {
        super.onStart();
        initPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopPlayer();
    }

    public void initPlayer() {
        String path = "https://s3.ap-northeast-2.amazonaws.com/ls41540/2019-05-15+12.21.20.mp4";
        //path="http://videocdn.bodybuilding.com/video/mp4/62000/62792m.mp4";
        vv.setVideoPath(path);
        vv.start();
    }

    public void stopPlayer() {
        vv.stopPlayback();
    }
}