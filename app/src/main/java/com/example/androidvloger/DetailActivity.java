package com.example.androidvloger;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {
    ImageView imgThumbnail, imgHeart;
    TextView tvTitle, tvTop, tvHeartNum, tvUsername, tvUploadtime, tvDesc;
    RecyclerView recyclerView;
    EditText etComment;
    final String IP_ADDR = "13.124.45.74";
    String videoId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imgThumbnail = findViewById(R.id.imgThumbnail);
        imgHeart = findViewById(R.id.imgHeart);
        tvTitle = findViewById(R.id.tvTitle);
        tvTop = findViewById(R.id.tvTop);
        tvHeartNum = findViewById(R.id.tvHeartNum);
        tvUsername = findViewById(R.id.tvUsername);
        tvUploadtime = findViewById(R.id.tvUploadtime);
        tvDesc = findViewById(R.id.tvDesc);
        recyclerView = findViewById(R.id.recyclerView);
        etComment = findViewById(R.id.etComment);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        Bundle ttt = intent.getExtras();

        String[] t = intent.getExtras().getStringArray("info");

        videoId = t[0];
        tvTitle.setText(t[1]);
        tvUsername.setText(t[2]);
        tvDesc.setText(t[3]);
        tvUploadtime.setText(t[4]);
        String path = "http://"+IP_ADDR+"/thumb"+videoId+".jpg";
        Picasso.get().load(path).into(imgThumbnail);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    void onClickPlay(View view) {
        Intent intent = new Intent(getBaseContext(), VideoActivity.class);
        intent.putExtra("id", videoId);
        startActivity(intent);
    }

    void onclickHeart(View view){

    }
}
