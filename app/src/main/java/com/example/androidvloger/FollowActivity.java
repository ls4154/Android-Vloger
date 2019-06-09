package com.example.androidvloger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FollowActivity extends AppCompatActivity {
    final String IP_ADDR = "13.124.45.74";
    int type; // 0 : page id 가 팔로우 하는 사람 목록, 1 : page id를 팔로우 하는 사람 목록
    String userId;
    String pageId, pagename;
    TextView tvPageinfo;
    RecyclerView recyclerView;
    ArrayList<Pair<String,String>> followList;
    UserlistAdapter adapter;

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
        if (type == 0) info = pagename + "'s followings";
        else if (type == 1) info = pagename + "'s followers";
        tvPageinfo.setText(info);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        refresh();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    void refresh(){
        if (type == 0) {
            GetData task = new GetData();
            task.execute("http://" + IP_ADDR + "/get_following.php", pageId);
        } else {
            GetData2 task = new GetData2();
            task.execute("http://" + IP_ADDR + "/get_follower.php", pageId);
        }
    }

    void refreshUI(){
        adapter = new UserlistAdapter(followList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void onclickGotoUserpage(View view){
        Intent intent = new Intent(getBaseContext(), UserpageActivity.class);
        intent.putExtra("id", userId);
        String pageId = (String)view.getTag();
        intent.putExtra("pageid", pageId);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    // following 가져오기
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
                JSONArray ja = jo.getJSONArray("followings");
                
                followList = new ArrayList<>();
                for (int i = ja.length()-1; i >= 0; i--) {
                    String t1 = ja.getJSONObject(i).getString("id"); // id
                    String t2 = ja.getJSONObject(i).getString("name"); // 이름
                    Pair<String, String> tempPair = new Pair<>(t1, t2);
                    followList.add(tempPair); // id로 검색되게 하고 보이는건 id/name 둘다
                }
                refreshUI();

                // 어댑터 추가
            } catch (Exception e) {
                Log.d("JSON Parser", "Error" + e.getMessage());
            }
        }
    } // Asynctask


    // follower 가져오기
    class GetData2 extends AsyncTask<String, Void, String> {
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
                JSONArray ja = jo.getJSONArray("followers");

                followList = new ArrayList<>();
                for (int i = ja.length()-1; i >= 0; i--) {
                    String t1 = ja.getJSONObject(i).getString("id"); // id
                    String t2 = ja.getJSONObject(i).getString("name"); // 이름
                    Pair<String, String> tempPair = new Pair<>(t1, t2);
                    followList.add(tempPair); // id로 검색되게 하고 보이는건 id/name 둘다
                }
                refreshUI();
                // 어댑터 추가
            } catch (Exception e) {
                Log.d("JSON Parser", "Error" + e.getMessage());
            }
        }
    } // Asynctask
}