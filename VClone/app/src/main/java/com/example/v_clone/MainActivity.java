package com.example.v_clone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.OpenableColumns;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_AUDIO_PERMISSION_CODE = 1;
    boolean mStartRecording = true;
    MediaPlayer player;
    MediaRecorder recorder;
    String fileName;
    private String [] permissions = {RECORD_AUDIO};
    EditText text;
    private static final int PICKFILE_RESULT_CODE = 1212;
    String filePath;
    boolean isClosed = false;
    ListView Mylist;
    private ArrayList<Integer> myArrList2; //choose
    private ArrayList<String>Names;
    MediaPlayer music; //choose
    FloatingActionButton chooseFromSamples;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finishAffinity();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        String username = getIntent().getExtras().getString("username"," ");

        Button Record = findViewById(R.id.record_btn);
        Button upload = (Button) findViewById(R.id.uploadFile);
        Button startCloning = (Button) findViewById(R.id.startCloning);
        text = (EditText) findViewById(R.id.clonedText);
        int[] Audioid = {200};
        Audioid[0] = 100;

        Mylist=new ListView(this);

        chooseFromSamples=(FloatingActionButton)findViewById(R.id.chooseSample_btn);

        FloatingActionButton menu = (FloatingActionButton) findViewById(R.id.menu);
        FloatingActionButton logOut = (FloatingActionButton) findViewById(R.id.LogOut);
        FloatingActionButton newVid = (FloatingActionButton) findViewById(R.id.NewVideo);
        FloatingActionButton account = (FloatingActionButton) findViewById(R.id.Account);
        FloatingActionButton aboutApp = (FloatingActionButton) findViewById(R.id.aboutApp);
        FloatingActionButton MyAudios = (FloatingActionButton) findViewById(R.id.MyAudios);
        CardView logOutTxt = (CardView) findViewById(R.id.LogOutTxt);
        CardView MyAudiosTxt = (CardView) findViewById(R.id.MyAudiosTxt);
        CardView accountTxt = (CardView) findViewById(R.id.AccountTxt);
        CardView newVidTxt = (CardView) findViewById(R.id.NewVideoTxt);
        CardView aboutAppTxt = (CardView) findViewById(R.id.aboutAppTxt);

        Animation.init(logOut);
        Animation.init(logOutTxt);
        Animation.init(newVid);
        Animation.init(newVidTxt);
        Animation.init(MyAudios);
        Animation.init(MyAudiosTxt);
        Animation.init(account);
        Animation.init(accountTxt);
        Animation.init(aboutApp);
        Animation.init(aboutAppTxt);

        chooseFromSamples.setOnClickListener(new View.OnClickListener()  {
            @Override
            public void onClick(View v)  {

                myArrList2=new ArrayList<Integer>();
                Names = new ArrayList<String>();
                Field[] fields=R.raw.class.getFields();
                int resourceID = 0;
                for(int i = 0; i < fields.length; i++) {
                    try {
                        resourceID = fields[i].getInt(fields[i]);
                        Names.add(fields[i].getName());
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    myArrList2.add(resourceID);
                }
                listAdapter adapter=new listAdapter(getApplicationContext(),myArrList2,Names,Audioid);
                Mylist.setAdapter(adapter);
                ShowDialogeee();
            }
        });


        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isClosed == false) {
                    menu.setImageResource(R.drawable.close_btn_foreground);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Animation.showIn(account);
                            Animation.showIn(accountTxt);
                        }
                    }, 100);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Animation.showIn(newVid);
                            Animation.showIn(newVidTxt);
                        }
                    }, 150);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Animation.showIn(MyAudios);
                            Animation.showIn(MyAudiosTxt);
                        }
                    }, 200);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Animation.showIn(aboutApp);
                            Animation.showIn(aboutAppTxt);
                        }
                    }, 250);
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Animation.showIn(logOut);
                            Animation.showIn(logOutTxt);
                        }
                    }, 300);
                    isClosed = true;
                }
                else{
                    menu.setImageResource(R.drawable.drawer_btn_foreground);
                    Animation.showOut(logOut);
                    Animation.showOut(logOutTxt);
                    Animation.showOut(newVid);
                    Animation.showOut(newVidTxt);
                    Animation.showOut(account);
                    Animation.showOut(aboutApp);
                    Animation.showOut(MyAudios);
                    Animation.showOut(MyAudiosTxt);
                    Animation.showOut(aboutAppTxt);
                    Animation.showOut(accountTxt);
                    isClosed = false;
                }
            }
        });

        startCloning.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,ResultActivity.class);
                i.putExtra("audioID", Audioid[0]);
                i.putExtra("text", text.getText());
                i.putExtra("username", username);
                startActivity(i);
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_GET_CONTENT);
                i.setType("application/pdf\", \"text/*");
                i.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(i,PICKFILE_RESULT_CODE);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,LoginActivity.class);
                startActivity(i);
            }
        });

        account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,AccountActivity.class);
                i.putExtra("username", username);
                startActivity(i);
            }
        });

        MyAudios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,AudioActivity.class);
                i.putExtra("username", username);
                startActivity(i);
            }
        });
        newVid.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this,activity_create_video.class);
                startActivity(i);
            }
        });
        Record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        aboutApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity.this, activity_about_app.class);
                startActivity(i);
            }
        });
    }

    public  void ShowDialogeee(){
        AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
        builder.setCancelable(true);
        builder.setView(Mylist);
        AlertDialog d=builder.create();
        d.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        switch (requestCode) {
            case 1212:
                if (resultCode == RESULT_OK) {
                    Uri uri = data.getData();
                    String uriString = uri.toString();
                    File myFile = new File(uriString);
                    final String[] split = uri.getPath().split(":");//split the path.
                    filePath = split[1];
                    String displayName = null;

                    if (uriString.startsWith("content://")) {
                        Cursor cursor = null;
                        try {
                            cursor = getContentResolver().query(uri, null, null, null, null);
                            if (cursor != null && cursor.moveToFirst()) {
                                displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                            }
                        } finally {
                            cursor.close();
                        }
                    } else if (uriString.startsWith("file://")) {
                        displayName = myFile.getName();
                    }
                    text.setText(displayName);
                    Toast.makeText(this, filePath, Toast.LENGTH_SHORT).show();
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    public void onRecord(boolean start) {
        if (start) {
            startRecording();
        } else {
            stopRecording();
        }
    }
    public void startPlaying() {
        player = new MediaPlayer();
        try {
            player.setDataSource(fileName);
            player.prepare();
            player.start();
            Toast.makeText(this,fileName, Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            Log.e("MainActivity", "prepare() failed");
        }
    }

    public void stopPlaying() {
        player.release();
        player = null;
    }

    public void startRecording() {
        if (CheckPermissions()) {
            recorder = new MediaRecorder();
            fileName = Environment.getExternalStorageDirectory().getAbsolutePath();
            fileName += "/AudioRecording.3gp";

            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

            try {
                recorder.prepare();
            } catch (IOException e) {
                Log.e("MainActivity", "prepare() failed");
            }

            recorder.start();
        }
        else {
            RequestPermissions();
        }
    }
    public void stopRecording() {
        if(CheckPermissions()) {
            recorder.stop();
            recorder.release();
            recorder = null;
        }
        else
        {
            RequestPermissions();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        // this method is called when user will
        // grant the permission for audio recording.
        switch (requestCode) {
            case REQUEST_AUDIO_PERMISSION_CODE:
                if (grantResults.length > 0) {
                    boolean permissionToRecord = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                    boolean permissionToStore = grantResults[1] == PackageManager.PERMISSION_GRANTED;
                    if (permissionToRecord && permissionToStore) {
                        Toast.makeText(getApplicationContext(), "Permission Granted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;
        }
    }
    public boolean CheckPermissions() {
        // this method is used to check permission
        int result = ContextCompat.checkSelfPermission(getApplicationContext(), WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(), RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED && result1 == PackageManager.PERMISSION_GRANTED;
    }
    private void RequestPermissions() {
        // this method is used to request the
        // permission for audio recording and storage.
        ActivityCompat.requestPermissions(MainActivity.this, new String[]{RECORD_AUDIO, WRITE_EXTERNAL_STORAGE}, REQUEST_AUDIO_PERMISSION_CODE);
    }


}