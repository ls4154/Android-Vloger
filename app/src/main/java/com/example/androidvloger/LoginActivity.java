package com.example.androidvloger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class LoginActivity extends AppCompatActivity {
    final static int SIGNUP_RC = 1111;
    
    final String IP_ADDR = "13.124.45.74";
    EditText etId;
    EditText etPw;
    Button btnSend;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        etId = findViewById(R.id.et_login_id);
        etPw = findViewById(R.id.et_login_pw);
        btnSend = findViewById(R.id.btn_login_send);

        btnSend.setOnClickListener(loginSendListner);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Login");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_login_signup) {
            Intent intent = new Intent(getBaseContext(), SignupActivity.class);
            startActivityForResult(intent, SIGNUP_RC);
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SIGNUP_RC) {
            if (resultCode == RESULT_OK) {
                String uid = data.getExtras().getString("id");
                Intent resultIntent = new Intent();
                resultIntent.putExtra("id", uid);
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    Button.OnClickListener loginSendListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String strId = etId.getText().toString();
            String strPw = etPw.getText().toString();

            // 이하는 sign up form이 valid한지 체크하는 코드
            if(!strId.matches("[a-zA-Z0-9]+")){ // id가 영어 숫자로만 이뤄져 있지 않을때
                Toast.makeText(getApplicationContext(), "Wrong ID format", Toast.LENGTH_SHORT).show();
                return;
            }
            if(!strPw.matches("[a-zA-Z0-9]+")){ // pw가 영어 숫자로만 이뤄져 있지 않을때
                Toast.makeText(getApplicationContext(), "Use only letter or number for PW", Toast.LENGTH_SHORT).show();
                return;
            }
            if(strId.length() > 20){ // id 길이가 20자 초과할때
                Toast.makeText(getApplicationContext(), "ID should be less than 20 chars", Toast.LENGTH_SHORT).show();
                return;
            }
            if(strPw.length() > 20 || strPw.length() < 4){ // pw 길이가 20자 초과 or 4자 미만일때
                Toast.makeText(getApplicationContext(), "Password should be less than 20 chars", Toast.LENGTH_SHORT).show();
                return;
            }
            
            LoginActivity.SendData task = new LoginActivity.SendData();
            task.execute("http://" + IP_ADDR + "/login.php", strId, strPw);
        }
    };

    class SendData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String postParams = "id=" + strings[1] + "&pw=" + strings[2];

            Log.d("LOGIN TASK", "DO IN BACKGROUND");
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
            if (s.substring(0, 5).equalsIgnoreCase("Error")) {
                Toast.makeText(getApplicationContext(), "Wrong account!", Toast.LENGTH_LONG).show();
            } else {
                Intent resultIntent = new Intent();
                resultIntent.putExtra("id", etId.getText().toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }
}
