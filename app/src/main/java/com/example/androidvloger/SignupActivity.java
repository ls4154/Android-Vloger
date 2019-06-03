package com.example.androidvloger;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
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

public class SignupActivity extends AppCompatActivity {
    final String IP_ADDR = "13.124.45.74";
    EditText etId;
    EditText etName;
    EditText etPw;
    EditText etPw2;
    EditText etDesc;
    Button btnSend;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        
        etId = findViewById(R.id.et_su_id);
        etName = findViewById(R.id.et_su_name);
        etPw = findViewById(R.id.et_su_pw);
        etPw2 = findViewById(R.id.et_su_pw2);
        etDesc = findViewById(R.id.et_su_desc);
        btnSend = findViewById(R.id.btn_su_send);

        btnSend.setOnClickListener(signupSendListner);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Sign Up");
    }

    Button.OnClickListener signupSendListner = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String strId = etId.getText().toString();
            String strName = etName.getText().toString();
            String strPw = etPw.getText().toString();
            String strPw2 = etPw2.getText().toString();
            String strDesc = etDesc.getText().toString();

            if (!strPw.equals(strPw2)) {
                Toast.makeText(getApplicationContext(), "Password not match!", Toast.LENGTH_SHORT).show();
                return;
            }
            
            SendData task = new SendData();
            task.execute("http://" + IP_ADDR + "/insert2.php", strId, strName, strPw, strDesc);
        }
    };

    class SendData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String postParams = "id=" + strings[1] + "&name=" + strings[2] +
                    "&pw=" + strings[3] + "&desc=" + strings[4];

            Log.d("ASYNC TASK", "DO IN BACKGROUND");
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
                Toast.makeText(getApplicationContext(), "ID alreay exists!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Sign up completed!", Toast.LENGTH_LONG).show();
                Intent resultIntent = new Intent();
                resultIntent.putExtra("id", etId.getText().toString());
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    }
}
