package com.example.v_clone;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.VideoView;

import java.util.ArrayList;

public class PreMainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pre_main);

        String username = getIntent().getExtras().getString("username"," ");

        Button audio = (Button) findViewById(R.id.Audio);
        Button video = (Button) findViewById(R.id.Video);

        audio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PreMainActivity.this,MainActivity.class);
                i.putExtra("username", username);
                startActivity(i);
            }
        });
        video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(PreMainActivity.this,activity_create_video.class);
                i.putExtra("username", username);
                startActivity(i);
            }
        });
    }
}