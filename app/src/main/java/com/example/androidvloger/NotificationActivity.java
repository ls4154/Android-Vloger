package com.example.androidvloger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;

public class NotificationActivity extends AppCompatActivity {
    final String IP_ADDR = "13.124.45.74";
    String userId;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        
        // find views
        
        // get extras
        Intent intent = getIntent();
        userId = intent.getStringExtra("id");

        // 뒤로가기
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // 툴바 이름
        getSupportActionBar().setTitle("What's new?");
        
        // 알림 가져오고 UI 리셋
        refresh(); 
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
    
    void refresh() {
        GetData getData = new GetData();
        getData.execute("http://" + IP_ADDR + "/get_notification.php", userId);
    }
    
    void refreshUI() {
        
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
                
                ArrayList<NotificationItem> notiList = new ArrayList<>();
                
                // 내 영상에 댓글
                JSONArray ja = jo.getJSONArray("comments");
                for (int i = ja.length()-1; i >= 0; i--) {
                    notiList.add(new NotificationItem(
                        ja.getJSONObject(i).getString("id"), // 동영상 id
                        ja.getJSONObject(i).getString("date"), // 댓글쓴 날짜
                        ja.getJSONObject(i).getString("name"), // 댓글쓴 사람 이름
                        ja.getJSONObject(i).getString("content"), // 댓글 내용
                        0
                    ));
                }
                // 팔로워 영상
                ja = jo.getJSONArray("videos");
                for (int i = ja.length()-1; i >= 0; i--) {
                    notiList.add(new NotificationItem(
                        ja.getJSONObject(i).getString("id"), // 동영상 id
                        ja.getJSONObject(i).getString("date"), // 올린 날짜
                        ja.getJSONObject(i).getString("name"), // 업로더 이름 
                        ja.getJSONObject(i).getString("title"), // 제목
                        1
                    ));
                }
                // 내 영상에 좋아요
                ja = jo.getJSONArray("likes");
                for (int i = ja.length()-1; i >= 0; i--) {
                    notiList.add(new NotificationItem(
                        ja.getJSONObject(i).getString("id"), // 동영상 id
                        ja.getJSONObject(i).getString("date"), // 좋아요 날짜
                        ja.getJSONObject(i).getString("name"), // 좋아요 한 사람 이름 
                        ja.getJSONObject(i).getString("title"), // 제목
                        2
                    ));
                }
                
                Collections.sort(notiList);
                // 어댑터로 넘겨 주기
                
                Log.d("notilist", "size " + notiList.size());
                
            } catch (Exception e) {
                Log.d("JSON Parser", "Error");
            }
            refreshUI();
        }
    } // Asynctask
}
