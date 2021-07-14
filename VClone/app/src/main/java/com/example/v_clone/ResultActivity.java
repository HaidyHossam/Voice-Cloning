package com.example.v_clone;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.database.Cursor;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends AppCompatActivity {
    MediaPlayer music;
    boolean playAfterStop = false;
    boolean playing = false;

    private SeekBar ProceseekBar;
    private TextView currentTime,totalTime;
    private AudioManager audioManager;

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        DatabaseHelper DB = new DatabaseHelper(getApplicationContext());
        ProceseekBar=(SeekBar)findViewById(R.id.seekBar);
        currentTime=(TextView)findViewById(R.id.currentTime);
        totalTime=(TextView)findViewById(R.id.totalTime);
        ProceseekBar.setOnSeekBarChangeListener(new ProcessBarListener());


        int audioID = getIntent().getExtras().getInt("audioID",1);
        String text = getIntent().getExtras().getString("text"," ");
        String username = getIntent().getExtras().getString("username"," ");
        FloatingActionButton play = (FloatingActionButton) findViewById(R.id.playRes);
        FloatingActionButton stop = (FloatingActionButton) findViewById(R.id.stopRes);
        TextView name = (TextView) findViewById(R.id.outputAudio);

        Cursor cursor = DB.getAudios(username);
        int nextID = cursor.getCount();
        String UniqueAudioName = username+nextID;
        if(audioID != 100){
            Uri uri = Uri.parse("android.resource://com.my.package/" + audioID);

            uploadFile(uri,UniqueAudioName,username,text);
        }
        else{

        }



        music = MediaPlayer.create(getApplicationContext(), audioID);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playAfterStop == false){
                    music.start();
                    play.setImageResource(R.drawable.play_btn_foreground);
                    playAfterStop = true;
                }
                if(playing == false){
                    music.start();
                    play.setImageResource(R.drawable.pause_btn_foreground);
                    playing = true;
                }
                else{
                    music.pause();
                    play.setImageResource(R.drawable.play_btn_foreground);
                    playing = false;
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music.stop();
                playAfterStop = false;
                play.setImageResource(R.drawable.play_btn_foreground);
            }
        });

    }
    class ProcessBarListener implements SeekBar.OnSeekBarChangeListener {

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress,
                                      boolean fromUser) {
            // TODO Auto-generated method stub
            if (fromUser==true) {
                music.seekTo(progress);
                currentTime.setText(ShowTime(progress));
            }

        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
            // TODO Auto-generated method stub

        }


    }

    // Time display function, we get the music information in milliseconds, convert it to the familiar 00:00 format
    public String ShowTime(int time){
        time/=1000;
        int minute=time/60;
        int hour=minute/60;
        int second=time%60;
        minute%=60;
        return String.format("%02d:%02d", minute, second);
    }
    Handler handler=new Handler();
    public void StrartbarUpdate(){
        handler.post(r);
    }
    Runnable r=new Runnable() {

        @Override
        public void run() {
            // TODO Auto-generated method stub
            int CurrentPosition=music.getCurrentPosition();
            currentTime.setText(ShowTime(CurrentPosition));
            int mMax=music.getDuration();
            ProceseekBar.setMax(mMax);
            ProceseekBar.setProgress(CurrentPosition);
            handler.postDelayed(r, 100);
        }
    };
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void uploadFile(Uri fileUri,String Name, String ID, String Text) {
        PostApi service = ServiceGenerator.createService(PostApi.class);

        String[] arrayString = fileUri.getPath().split(":");
        File file = new File(arrayString[1]);
        RequestBody requestFile = RequestBody.create(file, MediaType.parse(getContentResolver().getType(fileUri)));

        MultipartBody.Part File = MultipartBody.Part.createFormData("file", file.getName(), requestFile);

        MultipartBody.Part N = MultipartBody.Part.createFormData("Name", Name);

        MultipartBody.Part APIID = MultipartBody.Part.createFormData("userID", ID);

        MultipartBody.Part APIText = MultipartBody.Part.createFormData("Text", Text);

        Call<ResponseBody> call = service.upload(File, N, APIID,APIText);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                boolean t = writeResponseBodyToDisk(response.body());
                Log.v("Upload", "success");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("Upload error:", t.getMessage());
            }
        });
    }
    private boolean writeResponseBodyToDisk(ResponseBody body) {
        try {
            // todo change the file location/name according to your needs
            File futureStudioIconFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                    , "audio1.mp3");
            Toast.makeText(ResultActivity.this,"test",Toast.LENGTH_SHORT).show();
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

