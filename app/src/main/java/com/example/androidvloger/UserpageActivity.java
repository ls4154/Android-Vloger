package com.example.androidvloger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import java.util.ArrayList;

public class UserpageActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    UserpageAdapter adapter;
    Button buttonFollow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpage);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);


    }

    void refreshTimeline(){
//        adapter = new UserpageAdapter(new ArrayList<UserpageAdapter.ThumbItem>());
//        recyclerView.setLayoutManager(new LinearLayoutManager(this));
//        recyclerView.setAdapter(adapter);
    }
}
