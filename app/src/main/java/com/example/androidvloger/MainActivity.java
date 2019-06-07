package com.example.androidvloger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

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

public class MainActivity extends AppCompatActivity {

    VideoView vv;
    
    TextView tvTest;
    
    String userId;
    String userName;

    RecyclerView recyclerView;
    TimelineAdapter adapter;
    final String IP_ADDR = "13.124.45.74";

    final static int SIGNUP_RC = 1111; // sign up request code
    final static int LOGIN_RC = 1112; // sign up request code
    final static int SEARCH_RC = 1113; // sign up request code
    final static int HOME_RC = 1114; // sign up request code
    final static int UPLOAD_RC = 1115; // sign up request code

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        Intent intent = getIntent();
        try {
            userId = intent.getExtras().getString("id");
        }
        catch(Exception e){
            userId = null;
        }
        
        getSupportActionBar().setTitle("Timeline");
        
        refresh();
    }

    @Override
    protected void onStart() {
        super.onStart();
        //initPlayer();

        Intent intent = new Intent(getBaseContext(), LoginActivity.class);
        if (userId == null) {
            startActivityForResult(intent, LOGIN_RC);
        } else {
            // get data
        }
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == LOGIN_RC) {
            if (resultCode == RESULT_OK) {
                userId = data.getExtras().getString("id");
                Toast.makeText(this, "Welcome " + userId, Toast.LENGTH_LONG).show();
                
                GetData getData = new GetData();
                getData.execute("http://" + IP_ADDR + "/get_timeline.php", userId);
            }
        }
    }

    void refresh(){
        // TODO
        // new TimelineAdapter에 들어가는 데이터 데베에서 받아온걸로 바꿔야함
        ArrayList<ThumbItem> thumblist = new ArrayList<>();
        ThumbItem t = new ThumbItem();
        t.imgPath = "http://" + IP_ADDR + "/thumb1.jpg";
        t.title = "goooood";
        t.uploader = "muzi";
        t.videoId = 1;
        t.uploadTime = "2019-05-01 13:23";
        thumblist.add(t);
        /*
        t = new ThumbItem();
        t.imgPath = "http://" + IP_ADDR + "/thumb2.jpg";
        t.title = "goooood";
        t.uploader = "muzi";
        t.uploadTime = "2019-05-01 13:23";
        thumblist.add(t);
        */

        adapter = new TimelineAdapter(thumblist);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    public void onclickGotoDetail(View view){
        Intent intent = new Intent(getBaseContext(), DetailActivity.class);
        intent.putExtra("id", userId);
        intent.putExtra("videoId", (Integer)view.getTag());
        startActivity(intent);
    }

    public void onclickGotoTimeline(View view){
        refresh();
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

    public class ThumbItem {
        String imgPath;
        String uploader;
        String title;
        String uploadTime;
        int videoId;
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
                userName = jo.getString("name");
                
                for (int i = ja.length()-1; i >= 0; i--) {
                    String t1 = ja.getJSONObject(i).getString("id"); // video id
                    String t2 = ja.getJSONObject(i).getString("title"); // video title
                    String t3 = ja.getJSONObject(i).getString("uploader");
                    String t4 = ja.getJSONObject(i).getString("desc");
                    String t5 = ja.getJSONObject(i).getString("date");
                }
            } catch (Exception e) {
                Log.d("JSON Parser", "Error");
            }
        }
    } // Asynctask

}