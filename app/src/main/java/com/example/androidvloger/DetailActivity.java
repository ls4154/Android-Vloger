package com.example.androidvloger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

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
        
        imgThumbnail = findViewById(R.id.imgThumbnail);
        imgHeart = findViewById(R.id.imgHeart);
        tvTitle = findViewById(R.id.tvTitle);
        tvTop = findViewById(R.id.tvTop);
        tvHeartNum = findViewById(R.id.tvHeartNum);
        tvUsername = findViewById(R.id.tvUsername);
        tvUploadtime = findViewById(R.id.tvUploadtime);
        tvDesc = findViewById(R.id.tvDesc);
        recyclerView =  findViewById(R.id.recyclerView);
        etComment = findViewById(R.id.etComment);
        constraintLayout = findViewById(R.id.constraintLayout);

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
        
        String path = "http://" + IP_ADDR + "/thumb" + videoId + ".jpg";
        Picasso.get().load(path).into(imgThumbnail);
        
        // 댓글 가져오기
        GetData getData = new GetData();
        getData.execute("http://" + IP_ADDR + "/get_comments.php", videoId);
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

    class GetData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String postParams = "id=" + strings[1];
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setReadTimeout(5000);
                huc.setConnectTimeout(5000);
                huc.setRequestMethod("POST");
                huc.connect();

                OutputStream os = huc.getOutputStream();
                os.write(postParams.getBytes("UTF-8"));
                os.flush();
                os.close();

                int responseCode = huc.getResponseCode();
                Log.d("response", "code:" + responseCode);

                InputStream is;
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    is = huc.getInputStream();
                } else {
                    is = huc.getErrorStream();
                }

                InputStreamReader isr = new InputStreamReader(is, "UTF-8");
                BufferedReader br = new BufferedReader(isr);

                StringBuilder sb = new StringBuilder();
                String line = null;
                while ((line = br.readLine()) != null) {
                    sb.append(line);
                }
                br.close();
                return sb.toString();
            } catch (Exception e) {
                return "Error "+ e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("JSON", s);
            try {
                JSONObject jo = new JSONObject(s);
                JSONArray ja = jo.getJSONArray("comments");

                for (int i = ja.length()-1; i >= 0; i--) {
                    String[] t = new String[3];
                    t[0] = ja.getJSONObject(i).getString("id"); // 댓글쓴 사람 id
                    t[1] = ja.getJSONObject(i).getString("name"); // 댓글쓴 사람 이름
                    t[2] = ja.getJSONObject(i).getString("content"); // 댓글 내용
                }
            } catch (Exception e) {
                Log.d("JSON Parser", "Error");
            }
        }
    } // Asynctask
    
}
