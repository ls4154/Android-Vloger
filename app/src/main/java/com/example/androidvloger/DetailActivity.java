package com.example.androidvloger;

import android.content.Intent;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.util.Pair;
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
    ConstraintLayout constraintLayout;
    final String IP_ADDR = "13.124.45.74";
    String videoId;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        imgThumbnail = (ImageView)findViewById(R.id.imgThumbnail);
        imgHeart=(ImageView)findViewById(R.id.imgHeart);
        tvTitle = (TextView)findViewById(R.id.tvTitle);
        tvTop = (TextView)findViewById(R.id.tvTop);
        tvHeartNum=(TextView)findViewById(R.id.tvHeartNum);
        tvUsername=(TextView)findViewById(R.id.tvUsername);
        tvUploadtime=(TextView)findViewById(R.id.tvUploadtime);
        tvDesc=(TextView)findViewById(R.id.tvDesc);
        recyclerView=(RecyclerView)findViewById(R.id.recyclerView);
        etComment=(EditText)findViewById(R.id.etComment);
        constraintLayout=(ConstraintLayout)findViewById(R.id.constraintLayout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent intent = getIntent();
        String[] t = intent.getExtras().getStringArray("info");
        userId = intent.getStringExtra("id");

        videoId = t[0];
        tvTitle.setText(t[1]);
        tvUsername.setText(t[2]);
        tvUsername.setTag(t[2]);
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

    void onclickGotoUserpage(View view){
        Intent intent = new Intent(getBaseContext(), UserpageActivity.class);
        intent.putExtra("id", userId);
        String pageId = (String)view.getTag();
        String t = pageId;
        intent.putExtra("pageid", pageId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

}
