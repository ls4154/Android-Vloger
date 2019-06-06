package com.example.androidvloger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

public class UserpageActivity extends AppCompatActivity {
    final String IP_ADDR = "13.124.45.74";
    String userId;

    RecyclerView recyclerView;
    UserpageAdapter adapter;
    TextView tvUsername, tvFollowingsNum, tvFollowersNum;
    Button buttonFollow;
    ArrayList<Pair<String, String>> thumblist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpage);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        tvUsername = (TextView)findViewById(R.id.tvUsername);
        tvFollowingsNum = (TextView)findViewById(R.id.tvFollowingsNum);
        tvFollowersNum = (TextView)findViewById(R.id.tvFollowersNum);

        Intent intent = getIntent();
        userId = intent.getExtras().getString("id");
        tvUsername.setText(userId);
        // 팔로잉수 팔로워수도 데베에서 읽어와서 처리하기

        refresh();
    }

    void refresh(){
        // TODO
        // 받아온 데이터로 리스트 만들기
        thumblist = new ArrayList<>();
        String _path = "http://" + IP_ADDR + "/thumb1.jpg";
        String _title = "good";
        thumblist.add(new Pair<>(_title, _path));
        adapter = new UserpageAdapter(thumblist);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    void onclickFollow(View view){

    }
}
