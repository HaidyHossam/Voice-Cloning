package com.example.v_clone;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.VideoView;

public class activity_create_video extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {

    int VIDEO_SELECT = 1;
    Uri SelectedMediaUri;
    String VideoPath;
    Boolean isSeekbarTouch = false;
    VideoView Videoview;
    SeekBar Bar;
    Handler updateHandler = new Handler();

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VIDEO_SELECT) {
            if (resultCode == RESULT_OK) {
                SelectedMediaUri = data.getData();
                if (SelectedMediaUri.toString().contains("video")) {
                    //handle video
                    VideoPath = SelectedMediaUri.toString();
                    Videoview.setVideoPath(VideoPath);
                }
            }
        }
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

        if (isSeekbarTouch) {
            // Mean that the seekbar value is changed by user
            Videoview.seekTo((progress * Videoview.getDuration()) / 100); // Verify this
        } else {
            // Ignore becuase is due to seekBar programmatically change
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_video);

        Videoview = findViewById(R.id.videoView);
        Bar = findViewById(R.id.videoBar);
        Button UploadVideo = findViewById(R.id.uploadVideo);
        Button PlayVideo = findViewById(R.id.Play);
        Button PauseVideo = findViewById(R.id.Pause);
        Button StopVideo = findViewById(R.id.Stop);

        UploadVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent Video_intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
                Video_intent.setType("video/*");
                startActivityForResult(Video_intent, VIDEO_SELECT);
            }
        });
        Runnable updateVideoTime = new Runnable() //adjust Video on SeekBar
        {
            @Override
            public void run() {
                long currentPosition = Videoview.getCurrentPosition();
                Bar.setProgress((int) currentPosition);
                updateHandler.postDelayed(this, 100);
            }
        };
        Videoview.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                Bar.setProgress(0);
                Bar.setMax(Videoview.getDuration());
                updateHandler.postDelayed(updateVideoTime, 100);
            }
        });
        PlayVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Videoview.isPlaying()) {
                    Videoview.resume();
                } else
                    Videoview.start();
            }
        });
        PauseVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Videoview.pause();

            }
        });
        StopVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                 Videoview.stopPlayback();
                 Videoview.setVideoPath(VideoPath);
            }
        });
    }
}