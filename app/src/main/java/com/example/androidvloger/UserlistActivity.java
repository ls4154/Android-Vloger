package com.example.androidvloger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.EditText;
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

public class UserlistActivity extends AppCompatActivity {
    final String IP_ADDR = "13.124.45.74";
    ArrayList<Pair<String, String>> userList;
    EditText etSearch;
    RecyclerView recyclerView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_userlist);

        etSearch = (EditText) findViewById(R.id.etSearch);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        GetData getData = new GetData();
        getData.execute("http://" + IP_ADDR + "/getuser.php");
    }

    
    class GetData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            Log.d("USER LIST ASYNCTASK", "DO IN BACKGROUND");
            try {
                URL url = new URL(strings[0]);
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                huc.setReadTimeout(5000);
                huc.setConnectTimeout(5000);
                huc.setRequestMethod("GET");

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
            userList = new ArrayList<>();
            Toast.makeText(getApplicationContext(), "Done", Toast.LENGTH_LONG).show();
            try {
                JSONObject jo = new JSONObject(s);
                JSONArray ja = jo.getJSONArray("users");
                for (int i = 0; i < ja.length(); i++) {
                    String t1 = ja.getJSONObject(i).getString("id");
                    String t2 = ja.getJSONObject(i).getString("name");
                    Pair<String, String> tempPair = new Pair<>(t1, t2);
                    userList.add(tempPair);
                }
            } catch (Exception e) {
                Log.d("JSON Parser", "Error");
            }
        }
    } // Asynctask

    void search(View view){ // xml에 buttonSearch onclick으로 돼있음
        String keyword = etSearch.getText().toString();
    }

    void refresh(){

    }
}
