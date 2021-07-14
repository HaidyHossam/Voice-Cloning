package com.example.v_clone;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AudioActivity extends AppCompatActivity {
    private ListView myList;
    private ArrayList<Integer>myArrList;
    private ArrayList<String>Names;
    private CustomAdapter adapter;
    private static final int MY_PERMISSON = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_audio);

        DatabaseHelper DB = new DatabaseHelper(this);
        String username = getIntent().getExtras().getString("username"," ");
        if(ContextCompat.checkSelfPermission(AudioActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(AudioActivity.this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},MY_PERMISSON);
        }

        try {
            ArrayList<String> audios = new ArrayList<String>();
            Cursor cursor = DB.getAudios(username);
            while (cursor.moveToNext()) {
                audios.add(cursor.getString(0));
            }
            for(int i=0;i<audios.size();i++){
                downloadfile("http://10.5.50.238:8082/file/upload/"+audios);
            }

            showsongsinList(audios);
            registerForContextMenu(myList);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }

    }

    void showsongsinList(ArrayList<String> audios) throws IllegalAccessException {
        myList = (ListView) findViewById(R.id.myAudios);

        adapter = new CustomAdapter(audios, getApplicationContext());
        myList.setAdapter(adapter);
    }
    private void downloadfile(String url){
        Retrofit.Builder builder= new Retrofit.Builder().baseUrl("http://10.5.50.238:8082")
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        GetApi Ainterface = retrofit.create(GetApi.class);

        Call<ResponseBody> call = Ainterface.downloadFile(url);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                boolean t = writeResponseBodyToDisk(response.body());

                Toast.makeText(AudioActivity.this,"YAYY"+t,Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(AudioActivity.this,"no",Toast.LENGTH_SHORT).show();
            }
        });
    }
    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    , "audio1.mp3");
            Toast.makeText(AudioActivity.this,"test",Toast.LENGTH_SHORT).show();
            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];

                long fileSize = body.contentLength();
                long fileSizeDownloaded = 0;

                inputStream = body.byteStream();
                outputStream = new FileOutputStream(futureStudioIconFile);

                while (true) {
                    int read = inputStream.read(fileReader);

                    if (read == -1) {
                        break;
                    }

                    outputStream.write(fileReader, 0, read);

                    fileSizeDownloaded += read;

                    String TAG = "100";
                    Log.d(TAG, "file download: " + fileSizeDownloaded + " of " + fileSize);
                }

                outputStream.flush();

                return true;
            } catch (IOException e) {
                String TAG = "100";
                Log.d(TAG, "this"+e.toString());
                return false;
            } finally {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (outputStream != null) {
                    outputStream.close();
                }
            }
        } catch (IOException e) {
            String TAG = "100";
            Log.d(TAG, "this"+e.toString());
            return false;
        }
    }
}