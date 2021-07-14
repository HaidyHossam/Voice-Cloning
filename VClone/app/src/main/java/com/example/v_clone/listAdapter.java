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
import java.util.ArrayList;

import static androidx.core.content.FileProvider.getUriForFile;

public class listAdapter extends BaseAdapter {
    private ArrayList<Integer> list = new ArrayList<Integer>();
    private Context context;
    boolean playing = false;
    boolean playAfterStop = false;
    ArrayList<String> Names;
    MediaPlayer music;
    int[] id;
    int resID;

    public listAdapter(Context context, ArrayList<Integer> list,ArrayList<String> Names,int[] id) {
        this.list = list;
        this.context = context;
        this.Names = Names;
        this.id=id;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View view = convertView;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.choose_layout, null);


        ImageButton play_pause = (ImageButton) view.findViewById(R.id.Play_Pause);
        ImageButton stop = (ImageButton) view.findViewById(R.id.stopBtn);
        TextView name = (TextView) view.findViewById(R.id.audioName);
        Button choose = (Button) view.findViewById(R.id.choose);
        name.setText(Names.get(position));

        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String item = list.get(position).toString();
                resID = Integer.valueOf(item);
                id[0]=resID;
            }
        });

        play_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(playAfterStop == false){
                    music = MediaPlayer.create(parent.getContext(), list.get(position));
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

        return view;
    }
}
