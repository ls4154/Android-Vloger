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
import android.view.Menu;
import android.view.MenuItem;
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

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class UserpageActivity extends AppCompatActivity {
    final String IP_ADDR = "13.124.45.74";
    String userId;
    String pageId;

    final static int UPLOAD_RC = 1115; // sign up request code

    RecyclerView recyclerView;
    UserpageAdapter adapter;
    TextView tvUsername, tvFollowingsNum, tvFollowersNum, tvUserDesc;
    Button buttonFollow;
    ArrayList<String[]> thumblist;

    final static int SEARCH_RC = 1113; // sign up request code
    final static int HOME_RC = 1114; // sign up request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userpage);
        recyclerView = findViewById(R.id.recyclerView);
        tvUsername = findViewById(R.id.tvUsername);
        tvFollowingsNum = findViewById(R.id.tvFollowingsNum);
        tvFollowersNum = findViewById(R.id.tvFollowersNum);
        tvUserDesc = findViewById(R.id.tvUserDesc);
        buttonFollow = findViewById(R.id.buttonFollow);

        thumblist = new ArrayList<>();

        Intent intent = getIntent();
        userId = intent.getStringExtra("id"); // login user id
        pageId = intent.getStringExtra("pageid"); // page user id
        tvUsername.setText(pageId);
        
        // 본인 페이지면 팔로우 불가능 
        if (userId.equals(pageId)) {
            buttonFollow.setEnabled(false);
        }

        // 팔로잉수 팔로워수도 데베에서 읽어와서 처리하기
        refresh();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_main_search) {
            Intent intent = new Intent(getBaseContext(), UserlistActivity.class);
            startActivityForResult(intent, SEARCH_RC);
        }
        else if(id == R.id.action_main_home){
            Intent intent = new Intent(getBaseContext(), UserpageActivity.class);
            intent.putExtra("id", userId);
            intent.putExtra("pageid", userId);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        return super.onOptionsItemSelected(item);
    }

    void refresh(){
        GetData getData = new GetData();
        getData.execute("http://" + IP_ADDR + "/get_userpage.php", pageId);
    }

    void refreshUI(){
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
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
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
                
                tvUserDesc.setText(jo.getString("desc"));

                thumblist = new ArrayList<>();
                for (int i = ja.length()-1; i >= 0; i--) {
                    String[] t = new String[5];
                    t[0] = ja.getJSONObject(i).getString("id");
                    t[1] = ja.getJSONObject(i).getString("title"); // video title
                    t[2] = ja.getJSONObject(i).getString("uploader");
                    t[3] = ja.getJSONObject(i).getString("desc");
                    t[4] = ja.getJSONObject(i).getString("date");
                    thumblist.add(t);
                }
            } catch (Exception e) {
                Log.d("JSON Parser", "Error");
            }
            refreshUI();
        }
    } // Asynctask

    public void onclickUpload(View view) {
        Intent intent = new Intent(getBaseContext(), UploadActivity.class);
        intent.putExtra("id", userId);
        startActivityForResult(intent, UPLOAD_RC);
    }

    public void onclickGotoDetail(View view){
        Intent intent = new Intent(this, DetailActivity.class);
        String[] t = (String[])view.getTag();
        intent.putExtra("info",t);
        startActivity(intent);
    }
}
