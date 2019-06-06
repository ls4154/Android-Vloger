package com.example.androidvloger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    VideoView vv;
    
    TextView tvTest;
    
    String userId;

    RecyclerView recyclerView;
    TimelineAdapter adapter;
    final String IP_ADDR = "13.124.45.74";

    final static int SIGNUP_RC = 1111; // sign up request code
    final static int LOGIN_RC = 1112; // sign up request code
    final static int SEARCH_RC = 1113; // sign up request code
    final static int HOME_RC = 1114; // sign up request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        //vv = findViewById(R.id.vv1);
        //MediaController controller = new MediaController(this);
        //controller.setMediaPlayer(vv);5
        //vv.setMediaController(controller);
        refresh();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //initPlayer();
        

        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        if (userId == null)
            startActivityForResult(intent, LOGIN_RC);
    }

    @Override
    protected void onStop() {
        super.onStop();
        //stopPlayer();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_main_search) {
            Intent intent = new Intent(getBaseContext(), UserlistActivity.class);
            startActivityForResult(intent, SEARCH_RC);
        }
        else if(id == R.id.action_main_home){
            Intent intent = new Intent(getBaseContext(), UserpageActivity.class);
            intent.putExtra("id", userId);
            startActivityForResult(intent, HOME_RC);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == SIGNUP_RC) {
            if (resultCode == RESULT_OK) {
                userId = data.getExtras().getString("id");
                getSupportActionBar().setTitle(userId);
            }
        } else if (requestCode == LOGIN_RC) {
            if (resultCode == RESULT_OK) {
                userId = data.getExtras().getString("id");
                getSupportActionBar().setTitle(userId);
                Toast.makeText(this, "Welcome " + userId, Toast.LENGTH_LONG).show();
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

    void refresh(){
        // TODO
        // new TimelineAdapter에 들어가는 데이터 데베에서 받아온걸로 바꿔야함
        ArrayList<ThumbItem> thumblist = new ArrayList<>();
        ThumbItem t = new ThumbItem();
        t.imgPath = "http://" + IP_ADDR + "/thumb1.jpg";
        t.title = "goooood";
        t.uploader = "muzi";
        t.videoId = 1;
        t.uploadTime = "2019-05-01 13:23";
        thumblist.add(t);
        /*
        t = new ThumbItem();
        t.imgPath = "http://" + IP_ADDR + "/thumb2.jpg";
        t.title = "goooood";
        t.uploader = "muzi";
        t.uploadTime = "2019-05-01 13:23";
        thumblist.add(t);
        */

        adapter = new TimelineAdapter(thumblist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void onclickGotoDetail(View view){
        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        intent.putExtra("id", userId);
        intent.putExtra("videoId", (Integer)view.getTag());
        startActivity(intent);
    }

    public class ThumbItem {
        String imgPath;
        String uploader;
        String title;
        String uploadTime;
        int videoId;
    }

}