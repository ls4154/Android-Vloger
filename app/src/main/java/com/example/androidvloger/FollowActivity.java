package com.example.androidvloger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

public class FollowActivity extends AppCompatActivity {
    int type; // 0 : page id 가 팔로우 하는 사람 목록, 1 : page id를 팔로우 하는 사람 목록
    String userId;
    String pageId, pagename;
    TextView tvPageinfo;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_follow);
        tvPageinfo = findViewById(R.id.tvPageinfo);
        recyclerView = findViewById(R.id.recyclerView);

        Intent intent = getIntent();
        type = intent.getIntExtra("type", 0);
        userId = intent.getStringExtra("id"); // login user id
        pageId = intent.getStringExtra("pageid"); // page user id
        pagename = intent.getStringExtra("pagename");

        String info = "";
        if(type == 0) info = pagename + "'s followings";
        else if(type == 1) info = pagename + "'s followers";
        tvPageinfo.setText(info);
    }
}