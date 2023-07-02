package com.example.myapplication;

import static com.example.myapplication.MainActivity.musicFiles;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    TextView song_name,
            artist_name,
            duration_played,
            duration_total;

    ImageView cover_art,
            nextBtn,
            prevBtn,
            backBtn,
            shuffleBtn,
            repeatBtn;

    FloatingActionButton playPauseBtn;

    SeekBar seekBar;

    int position = -1;

    static ArrayList<MusicFiles> songList = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;

    private Handler handler = new Handler();

    private Thread playThread, prevThread, nextThread;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        getIntentMethod();
        song_name.setText(songList.get(position).getTitle());
        artist_name.setText(songList.get(position).getArtist());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(mediaPlayer != null && fromUser){
                    mediaPlayer.seekTo(progress * 1000);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null){
                    int mCurentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurentPosition);
                    duration_played.setText(formattedTime(mCurentPosition));
                }
                handler.postDelayed(this, 100);
            }
        });
    }


    @Override
    protected void onPostResume() {
        playThreadbtn();
        nextThreadbtn();
        prevThreadbtn();

        super.onPostResume();
    }

    private void playThreadbtn() {
        playThread = new Thread(){
            @Override
            public void run(){
                super.run();
                playPauseBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playPouseBtnClicked();
                    }
                });
            }
        };
        playThread.start();
    }

    private void playPouseBtnClicked() {
        if(mediaPlayer.isPlaying()){
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
            mediaPlayer.pause();
        }
        else{
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
        PlayerActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null){
                    int mCurentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurentPosition);
                }
                handler.postDelayed(this, 100);
            }
        });
    }

    private void nextThreadbtn() {
        nextThread = new Thread(){
            @Override
            public void run(){
                super.run();
                nextBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        nextBtnClicked();
                    }
                });
            }
        };
        nextThread.start();
    }

    private void nextBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position + 1) % songList.size());
            uri = Uri.parse(songList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(songList.get(position).getTitle());
            artist_name.setText(songList.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int mCurentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position + 1) % songList.size());
            uri = Uri.parse(songList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(songList.get(position).getTitle());
            artist_name.setText(songList.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int mCurentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    private void prevThreadbtn() {
        prevThread = new Thread(){
            @Override
            public void run(){
                super.run();
                prevBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        prevBtnClicked();
                    }
                });
            }
        };
        prevThread.start();
    }

    private void prevBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position - 1) < 0 ? (songList.size() - 1) : (position - 1) );
            uri = Uri.parse(songList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(songList.get(position).getTitle());
            artist_name.setText(songList.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int mCurentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            position = ((position - 1) < 0 ? (songList.size() - 1) : (position - 1) );
            uri = Uri.parse(songList.get(position).getPath());
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            metaData(uri);
            song_name.setText(songList.get(position).getTitle());
            artist_name.setText(songList.get(position).getArtist());
            seekBar.setMax(mediaPlayer.getDuration() / 1000);
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int mCurentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
        }
    }

    private String formattedTime(int mCurentPosition) {
        String totalout = "";
        String totalnew = "";
        String seconds = String.valueOf(mCurentPosition % 60);
        String minutes = String.valueOf(mCurentPosition / 60);
        totalout = minutes + ":" + seconds;
        totalnew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1){
            return totalnew;
        }
        else return totalout;
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        songList = musicFiles;
        if(songList != null){
            playPauseBtn.setImageResource(R.drawable.ic_pause);
            uri = Uri.parse(songList.get(position).getPath());
        }
        if(mediaPlayer != null){
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
        }
        else{
            mediaPlayer = MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        seekBar.setMax(mediaPlayer.getDuration()/1000);
        metaData(uri);
    }

    private void initView() {
        song_name = findViewById(R.id.song_name);
        artist_name = findViewById(R.id.song_artist);
        duration_played = findViewById(R.id.durationPlayed);
        duration_total = findViewById(R.id.durationTotal);
        cover_art = findViewById(R.id.cov_art);
        nextBtn = findViewById(R.id.next);
        prevBtn = findViewById(R.id.prev);
        backBtn = findViewById(R.id.back_btn);
        shuffleBtn = findViewById(R.id.shuffle);
        repeatBtn = findViewById(R.id.repeat);
        playPauseBtn = findViewById(R.id.play_pause);
        seekBar = findViewById(R.id.seekBar);
    }

    private void metaData(Uri uri){
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        retriever.setDataSource(uri.toString());
        int durationTotal = Integer.parseInt(songList.get(position).getDuration()) / 1000;
        duration_total.setText(formattedTime(durationTotal));
        byte[] art = retriever.getEmbeddedPicture();
        if (art != null ){
            Glide.with(this).asBitmap().load(art).into(cover_art);
        }
        else{
            Glide.with(this).asBitmap().load(R.drawable.no_image).into(cover_art);
        }
    }
}