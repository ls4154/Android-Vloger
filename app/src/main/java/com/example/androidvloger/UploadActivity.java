package com.example.androidvloger;

import android.Manifest;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadActivity extends AppCompatActivity {
    final String IP_ADDR = "13.124.45.74";
    EditText etTitle;
    EditText etDesc;
    Button btnUpload;
    String userId;
    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);
        
        etTitle = findViewById(R.id.et_up_title);
        etDesc = findViewById(R.id.et_up_desc);
        btnUpload = findViewById(R.id.btn_up_up);
        
        btnUpload.setOnClickListener(uploadOnClickListener);
        
        userId = getIntent().getStringExtra("id");
    }
    
    

    Button.OnClickListener uploadOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if (checkStoragePermission()) {
                chooseAndUpload();
            }
        }
    };

    public boolean checkStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                return false;
            }
        }
        else {
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            chooseAndUpload();
        }
    }
    
    void chooseAndUpload() {
        String vTitle = etTitle.getText().toString();
        String vDesc = etDesc.getText().toString();
        if (vTitle.equals("")) {
            Toast.makeText(getApplicationContext(), "Enter a title", Toast.LENGTH_LONG).show();
            return;
        }
        
        if (vDesc.equals("")) {
            Toast.makeText(getApplicationContext(), "Enter a description", Toast.LENGTH_LONG).show();
            return;
        }
        
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        Uri uri = Uri.parse(Environment.getExternalStorageDirectory().getPath());
        intent.setDataAndType(uri, "video/mp4");
        Intent.createChooser(intent, "Choose video!");
        startActivityForResult(intent, 2222);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 2222) {
            Uri uri = data.getData();
            String src = getPath(getApplicationContext(), uri);
            
            SendData task = new SendData();
            task.execute("http://" + IP_ADDR + "/upload_vid.php", etTitle.getText().toString(), userId, etDesc.getText().toString(), src);
        }
    }


    class SendData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... strings) {
            String postParams = "title=" + strings[1] + "&uploader=" + strings[2] +
                    "&desc=" + strings[3];

            String src = strings[4];
            File file = new File(src);
            
            String twoHyphens = "--";
            String boundary = "*****" + Long.toString(System.currentTimeMillis()) + "*****";
            String lineEnd = "\r\n";

            int bytesRead, bytesAvailable, bufferSize;
            byte[] buffer;
            int maxBufferSize = 1 * 1024 * 1024;
            
            String[] q = src.split("/");
            int idx = q.length - 1;

            Log.d("UPLOAD ASYNC TASK", "DO IN BACKGROUND");
            try {
                FileInputStream fis = new FileInputStream(file);
                
                URL url = new URL(strings[0]);
                HttpURLConnection huc = (HttpURLConnection) url.openConnection();
                
                huc.setReadTimeout(10000);
                huc.setConnectTimeout(10000);

                huc.setDoInput(true);
                huc.setDoOutput(true);
                huc.setUseCaches(false);

                huc.setRequestMethod("POST");
                huc.setRequestProperty("Connection", "Keep-Alive");
                huc.setRequestProperty("User-Agent", "Android Multipart HTTP Client 1.0");
                huc.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);

                DataOutputStream os = new DataOutputStream(huc.getOutputStream());
                os.writeBytes(twoHyphens + boundary + lineEnd);
                os.writeBytes("Content-Disposition: form-data; name=\"" + "upload" + "\"; filename=\"" + q[idx] + "\"" + lineEnd);
                os.writeBytes("Content-Type: " + "video/mp4" + lineEnd);
                os.writeBytes("Content-Transfer-Encoding: binary" + lineEnd);
                os.writeBytes(lineEnd);
                
                bytesAvailable = fis.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                buffer = new byte[bufferSize];
                
                bytesRead = fis.read(buffer, 0, bufferSize);
                while (bytesRead > 0) {
                    os.write(buffer, 0, bufferSize);
                    bytesAvailable = fis.available();
                    bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    bytesRead = fis.read(buffer, 0, bufferSize);
                }
                os.writeBytes(lineEnd);
                
                Log.d("UPload", "------------------");
                
                // POST Param title
                os.writeBytes(twoHyphens + boundary + lineEnd);
                os.writeBytes("Content-Disposition: form-data; name=\"" + "title" + "\"" + lineEnd);
                os.writeBytes("Content-Type: text/plain" + lineEnd);
                os.writeBytes(lineEnd);
                os.write(strings[1].getBytes("UTF-8"));
                os.writeBytes(lineEnd);

                // POST Param uploader
                os.writeBytes(twoHyphens + boundary + lineEnd);
                os.writeBytes("Content-Disposition: form-data; name=\"" + "uploader" + "\"" + lineEnd);
                os.writeBytes("Content-Type: text/plain" + lineEnd);
                os.writeBytes(lineEnd);
                os.write(strings[2].getBytes("UTF-8"));
                os.writeBytes(lineEnd);

                // POST Param desc
                os.writeBytes(twoHyphens + boundary + lineEnd);
                os.writeBytes("Content-Disposition: form-data; name=\"" + "desc" + "\"" + lineEnd);
                os.writeBytes("Content-Type: text/plain" + lineEnd);
                os.writeBytes(lineEnd);
                os.write(strings[3].getBytes("UTF-8"));
                os.writeBytes(lineEnd);
                
                os.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                
                fis.close();
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
                Log.d("Upload Error", e.getMessage());
                return "Error "+ e.getMessage();
            }

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("ONPOST UPLOAD", s);
            if (s.substring(0, 5).equalsIgnoreCase("Error")) {
                Toast.makeText(getApplicationContext(), "File upload failed!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(getApplicationContext(), "Upload completed!", Toast.LENGTH_LONG).show();
                Intent resultIntent = new Intent();
                setResult(RESULT_OK, resultIntent);
                finish();
            }
        }
    } //AsyncTask
    
    
    

    /*
     * from https://github.com/iPaulPro/aFileChooser/blob/master/aFileChooser/src/com/ipaulpro/afilechooser/utils/FileUtils.java
     */
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }
    
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }


    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }
    
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }
}
