package com.example.androidvloger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class UserpageActivity extends AppCompatActivity {
    final String IP_ADDR = "13.124.45.74";
    String userId;
    String pageId;

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
        userId = intent.getExtras().getString("id"); // login user id
        pageId = intent.getExtras().getString("pageid"); // page user id
        tvUsername.setText(pageId);
        
        
        // 팔로잉수 팔로워수도 데베에서 읽어와서 처리하기
        GetData getData = new GetData();
        getData.execute("http://" + IP_ADDR + "/get_userpage.php", pageId);

        refresh();
    }

    void refresh(){
        // TODO
        // 받아온 데이터로 리스트 만들기
        thumblist = new ArrayList<>();
        String _path = "http://" + IP_ADDR + "/thumb2.jpg";
        String _title = "good";
        thumblist.add(new Pair<>(_title, _path));
        adapter = new UserpageAdapter(thumblist);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    void onclickFollow(View view){

    }

    public void onclickGotoTimeline(View view){
        Intent intent = new Intent(getBaseContext(), MainActivity.class);
        intent.putExtra("id", userId);
        startActivity(intent);
    }

    public void onclickGotoNotification(View view){
        Intent intent = new Intent(getBaseContext(), NotificationActivity.class);
        intent.putExtra("id", userId);
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
                JSONArray ja = jo.getJSONArray("videos");

                int cntFollower = jo.getInt("followers");
                tvFollowersNum.setText("" + cntFollower);

                int cntFollowing = jo.getInt("followings");
                tvFollowingsNum.setText("" + cntFollowing);
                
                tvUsername.setText(jo.getString("name"));
                
                // desc 텍스트뷰 만들면 사용 
                //tvDesc.setText(jo.getString("desc"));
                
                for (int i = 0; i < ja.length(); i++) {
                    String t1 = ja.getJSONObject(i).getString("id");
                    String t2 = ja.getJSONObject(i).getString("title");
                    String t3 = ja.getJSONObject(i).getString("uploader");
                    String t4 = ja.getJSONObject(i).getString("desc");
                    String t5 = ja.getJSONObject(i).getString("date");
                    
                    // 리스트에 추가한다 
                }
                
                
                
                
                
            } catch (Exception e) {
                Log.d("JSON Parser", "Error");
            }
        }
    } // Asynctask
}
