package com.example.androidvloger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

public class DetailActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    ImageView imgThumbnail, imgHeart;
    TextView tvTitle, tvTop, tvHeartNum, tvUsername, tvUploadtime, tvDesc;
    RecyclerView recyclerView;
    EditText etComment;
    Button btnComment;
    
    ConstraintLayout constraintLayout;
    final String IP_ADDR = "13.124.45.74";
    String videoId;
    String userId;
    ArrayList<String[]> commentlist;
    SwipeRefreshLayout swipeRefreshLayout;
    DetailAdapter adapter;

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
        btnComment = findViewById(R.id.buttonComment);

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        
        // 처음에 테스트용 텍스트 다 날리기
        tvTitle.setText("");
        tvDesc.setText("");
        tvUploadtime.setText("");
        tvUsername.setText("");
        
        // get extras
        Intent intent = getIntent();
        videoId = intent.getExtras().getString("id");
        userId = intent.getStringExtra("id");
        String path = "http://" + IP_ADDR + "/thumb" + videoId + ".jpg";
        Picasso.get().load(path).into(imgThumbnail);

        // 댓글 가져오기
        refresh();
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
    
    void onClickComment(View view) {
        if (etComment.getText().equals("")) {
            Toast.makeText(getApplicationContext(), "Type comment first", Toast.LENGTH_SHORT).show();
            return;
        }
        
        btnComment.setEnabled(false);
        SendData task = new SendData();
        task.execute("http://" + IP_ADDR + "/add_comment.php", userId, videoId, etComment.getText().toString());
    }

    void onclickGotoUserpage(View view){
        Intent intent = new Intent(getBaseContext(), UserpageActivity.class);
        intent.putExtra("id", userId);
        String pageId = (String)view.getTag();
        intent.putExtra("pageid", pageId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // 동영상 정보 및 댓글 가져오기 
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

                commentlist = new ArrayList<>();
                for (int i = ja.length()-1; i >= 0; i--) {
                    String[] t = new String[3];
                    t[0] = ja.getJSONObject(i).getString("id"); // 댓글쓴 사람 id
                    t[1] = ja.getJSONObject(i).getString("name"); // 댓글쓴 사람 이름
                    t[2] = ja.getJSONObject(i).getString("content"); // 댓글 내용
                    commentlist.add(t);
                }
                
                //정보 가져오고 텍스트 셋
                tvTitle.setText(jo.getString("title"));
                tvUsername.setTag(jo.getString("id"));
                tvDesc.setText(jo.getString("desc"));
                tvUploadtime.setText(jo.getString("date"));
                tvUsername.setText(jo.getString("name"));
                
            } catch (Exception e) {
                Log.d("JSON Parser", "Error");
            }
            refreshUI();
        }
    } // Asynctask

    // 댓글 새로 가져오기
    void refresh(){
        GetData getData = new GetData();
        //getData.execute("http://" + IP_ADDR + "/get_comments.php", videoId);
        getData.execute("http://" + IP_ADDR + "/get_detail.php", videoId);
    }

    // 댓글 어댑터 리로드
    void refreshUI(){
        adapter = new DetailAdapter(commentlist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void onRefresh() {
        recyclerView.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
                Snackbar.make(recyclerView,"Refresh Success",Snackbar.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
            }
        },500);
    }

    // 댓글 전송 Task
    class SendData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String postParams = "iduser=" + strings[1] + "&idvideos=" + strings[2] +
                    "&content=" + strings[3];

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
                //Log.d("response", "code:" + responseCode);

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
                return "Error: "+ e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if (s.substring(0, 5).equalsIgnoreCase("Error")) {
                Toast.makeText(getApplicationContext(), "Error occured! Try agiain.", Toast.LENGTH_LONG).show();
            } else {
                // 댓글 추가됐으니 리프레시
                etComment.setText("");
                refresh();
            }
            btnComment.setEnabled(true);
        }
    }
}
