package com.example.androidvloger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

public class MainActivity extends AppCompatActivity {

    VideoView vv;
    
    TextView tvTest;
    
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //vv = findViewById(R.id.vv1);
        //MediaController controller = new MediaController(this);
        //controller.setMediaPlayer(vv);5
        //vv.setMediaController(controller);

        tvTest = findViewById(R.id.tv_test);
    }

    @Override
    protected void onStart() {
        super.onStart();
        //initPlayer();
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopPlayer();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == 1111) {
            if (resultCode == RESULT_OK) {
                userId = data.getExtras().getString("id");
                tvTest.setText(userId);
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (userId == null) {
            Intent intent = new Intent(getBaseContext(), SignupActivity.class);
            startActivityForResult(intent, 1111);
        }
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