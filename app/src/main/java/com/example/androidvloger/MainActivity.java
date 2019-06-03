package com.example.androidvloger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.widget.Adapter;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    VideoView vv;
    
    TextView tvTest;
    
    String userId;

    RecyclerView recyclerView;
    TimelineAdapter adapter;

    final static int SIGNUP_RC = 1111; // sign up request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

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
        /*
        if (userId == null) { // 로그인 되어 있지 않으면
            Intent intent = new Intent(getBaseContext(), SignupActivity.class);
            startActivityForResult(intent, 1111);
        }*/
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

    void refreshTimeline(){
        // TODO
        // new TimelineAdapter에 들어가는 데이터 데베에서 받아온걸로 바꿔야함
        adapter = new TimelineAdapter(new ArrayList<TimelineAdapter.ThumbItem>());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }
}