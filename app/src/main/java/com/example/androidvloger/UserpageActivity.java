package com.example.androidvloger;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
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

public class UserpageActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {
    final String IP_ADDR = "13.124.45.74";
    String userId;
    String pageId;
    String pagename;

    final static int UPLOAD_RC = 1115; // sign up request code

    RecyclerView recyclerView;
    UserpageAdapter adapter;
    TextView tvUsername, tvFollowingsNum, tvFollowersNum, tvUserDesc;
    Button buttonFollow;
    ArrayList<String[]> thumblist;
    SwipeRefreshLayout swipeRefreshLayout;

    final static int SEARCH_RC = 1113; // sign up request code
    final static int HOME_RC = 1114; // sign up request code
    
    boolean bufBack = false;
    boolean following = false;

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
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(this);

        thumblist = new ArrayList<>();

        Intent intent = getIntent();
        userId = intent.getStringExtra("id"); // login user id
        pageId = intent.getStringExtra("pageid"); // page user id
        tvUsername.setText(pageId);
        
        // 본인 페이지면 팔로우 불가능 
        if (userId.equals(pageId)) {
            buttonFollow.setText("MyPage");
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
            intent.putExtra("id", userId);
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
    @Override
    public void onBackPressed() {
        if (bufBack) {
            super.onBackPressed();
            return;
        }

        bufBack = true;
        Toast.makeText(this, "Click again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bufBack = false;
            }
        }, 2000);
    }
    
    void refresh(){
        GetData getData = new GetData();
        getData.execute("http://" + IP_ADDR + "/get_userpage.php", pageId, userId);
    }

    void refreshUI(){
        adapter = new UserpageAdapter(thumblist);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    public void onclickFollow(View view){
        if (userId.equals(pageId)) {
            Toast.makeText(getApplicationContext(), "It's you! :P", Toast.LENGTH_SHORT).show();
            return;
        }
        buttonFollow.setEnabled(false);
        if (!following) {
            SendData task = new SendData();
            task.execute("http://" + IP_ADDR + "/follow.php", pageId, userId);
        } else {
            SendData2 task = new SendData2();
            task.execute("http://" + IP_ADDR + "/unfollow.php", pageId, userId);
        }
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

    public void onclickUpload(View view) {
        Intent intent = new Intent(getBaseContext(), UploadActivity.class);
        intent.putExtra("id", userId);
        startActivityForResult(intent, UPLOAD_RC);
    }

    public void onclickGotoDetail(View view){
        Intent intent = new Intent(this, DetailActivity.class);
        String t = (String)view.getTag();
        intent.putExtra("vidid",t);
        intent.putExtra("id", userId);
        startActivityForResult(intent, 3737);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 3737) {
            refresh();
        } else if (requestCode == UPLOAD_RC) {
            refresh();
        }
    }

    public void onclickGotoFollowings(View view){
        int type = 0;
        Intent intent = new Intent(this, FollowActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("id", userId);
        intent.putExtra("pagename", pagename);
        intent.putExtra("pageid", pageId);
        startActivity(intent);
    }

    public void onclickGotoFollowers(View view){
        int type = 1;
        Intent intent = new Intent(this, FollowActivity.class);
        intent.putExtra("type", type);
        intent.putExtra("pagename", pagename);
        intent.putExtra("id", userId);
        intent.putExtra("pageid", pageId);
        startActivity(intent);
    }

    class GetData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String postParams = "id=" + strings[1] + "&myid=" +strings[2];
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
                
                if (jo.getString("follow").equals("true")) {
                    buttonFollow.setText("Following");
                    following = true;
                }

                int cntFollower = jo.getInt("followers");
                tvFollowersNum.setText("" + cntFollower);

                int cntFollowing = jo.getInt("followings");
                tvFollowingsNum.setText("" + cntFollowing);
                
                tvUsername.setText(jo.getString("name"));
                pagename = jo.getString("name");
                
                tvUserDesc.setText(jo.getString("desc"));

                JSONArray ja = jo.getJSONArray("videos");
                thumblist = new ArrayList<>();
                for (int i = ja.length()-1; i >= 0; i--) {
                    String[] t = new String[6];
                    t[0] = ja.getJSONObject(i).getString("id");
                    t[1] = ja.getJSONObject(i).getString("title"); // video title
                    t[2] = ja.getJSONObject(i).getString("uploader");
                    t[3] = ja.getJSONObject(i).getString("desc");
                    t[4] = ja.getJSONObject(i).getString("date");
                    t[5] = jo.getString("name");
                    thumblist.add(t);
                }
            } catch (Exception e) {
                Log.d("JSON Parser", "Error");
            }
            refreshUI();
        }
    } // Asynctask Get

    // 팔로우
    class SendData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String postParams = "id=" + strings[1] + "&myid=" + strings[2];

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
                Toast.makeText(getApplicationContext(), "Error occured! Try agiain.", Toast.LENGTH_SHORT).show();
            } else {
                buttonFollow.setText("Following");
                Toast.makeText(getApplicationContext(), "Followed", Toast.LENGTH_SHORT).show();
                following = true;
                
            }
            buttonFollow.setEnabled(true);
        }
    } // Asynctask Send

    // 언팔로우
    class SendData2 extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String postParams = "id=" + strings[1] + "&myid=" + strings[2];

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
                Toast.makeText(getApplicationContext(), "Error occured! Try agiain.", Toast.LENGTH_SHORT).show();
            } else {
                buttonFollow.setText("Follow");
                Toast.makeText(getApplicationContext(), "Unfollowed", Toast.LENGTH_SHORT).show();
                following = false;
            }
            buttonFollow.setEnabled(true);
        }
    } // Asynctask Send

    

    @Override
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

}
