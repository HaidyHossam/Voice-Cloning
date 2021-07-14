package com.example.v_clone;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import static androidx.core.content.FileProvider.getUriForFile;

public class CustomAdapter extends BaseAdapter {
    private ArrayList<String> list = new ArrayList<String>();
    private Context context;
    boolean playing = false;
    boolean playAfterStop = false;
    ArrayList<String> Names;
    MediaPlayer music;

    public CustomAdapter(ArrayList<String> list, Context context) {
        this.list = list;
        this.context = context;
        this.Names = Names;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    public long getItemId(int position) {
        return position;
    }

    @Override
    public Object getItem(int pos) {
        return list.get(pos);
    }

    private void copyFile(InputStream in, OutputStream out) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1)
            out.write(buffer, 0, read);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.custom_layout, null);

        music = new MediaPlayer();

        try {
            music.setDataSource("/storage/emulated/0/Download/"+ list.get(position));
            music.prepare();
            music.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

        ImageButton play_pause = (ImageButton) view.findViewById(R.id.Play_Pause);
        ImageButton stop = (ImageButton) view.findViewById(R.id.stopBtn);
        ImageButton share = (ImageButton) view.findViewById(R.id.shareBtn);
        TextView name = (TextView) view.findViewById(R.id.audioName);

        name.setText(Names.get(position));

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playAfterStop == false){
                    try {
                        music.setDataSource("/storage/emulated/0/Download/"+ list.get(position));
                        music.prepare();
                        music.start();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    music.start();
                    play_pause.setImageResource(R.drawable.pause_audio_btn_foreground);
                    playAfterStop = true;
                }
                if(playing == false){
                    music.start();
                    play_pause.setImageResource(R.drawable.pause_audio_btn_foreground);
                    playing = true;
                }
                else{
                    music.pause();
                    play_pause.setImageResource(R.drawable.play_audio_btn_foreground);
                    playing = false;
                }
            }
        });
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                music.stop();
                playAfterStop = false;
                play_pause.setImageResource(R.drawable.play_audio_btn_foreground);
            }
        });
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                File sound;
                try {
                    InputStream inputStream = parent.getResources().openRawResource(R.raw.audio_0); // equivalent to R.raw.yoursound
                    sound = File.createTempFile("sound", ".mp3");
                    copyFile(inputStream, new FileOutputStream(sound));
                } catch (IOException e) {
                    throw new RuntimeException("Can't create temp file", e);
                }

                final String AUTHORITY = BuildConfig.APPLICATION_ID + ".provider";
                Uri uri = getUriForFile(context , AUTHORITY, sound);
                Intent share = new Intent(Intent.ACTION_SEND);
                share.setType("audio/mp3"); // or whatever.
                share.putExtra(Intent.EXTRA_STREAM, uri);
                share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                share.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.getApplicationContext().startActivity(Intent.createChooser(share, "Share"));
            }
        });

        return view;
    }
}
