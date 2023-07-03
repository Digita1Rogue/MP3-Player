package com.example.myapplication;

import static com.example.myapplication.AlbumDetailsAdapter.albumFiles;
import static com.example.myapplication.MainActivity.musicFiles;
import static com.example.myapplication.MainActivity.repeatBoolean;
import static com.example.myapplication.MainActivity.shuffleBoolean;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Random;

public class PlayerActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {

    TextView song_name,
            artist_name,
            duration_played,
            duration_total;

    ImageView cover_art;
    ImageView nextBtn;
    ImageView prevBtn;
    ImageView shuffleBtn;
    ImageView repeatBtn;

    FloatingActionButton playPauseBtn;

    SeekBar seekBar;

    int position = -1;

    static ArrayList<MusicFiles> songList = new ArrayList<>();
    static Uri uri;
    static MediaPlayer mediaPlayer;

    private final Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);
        initView();
        getIntentMethod();
        song_name.setText(songList.get(position).getTitle());
        artist_name.setText(songList.get(position).getArtist());
        mediaPlayer.setOnCompletionListener(this);

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
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                    duration_played.setText(formattedTime(mCurrentPosition));
                }
                handler.postDelayed(this, 100);
            }
        });
        shuffleBtn.setOnClickListener(v -> {
            if( shuffleBoolean){
                shuffleBoolean = false;
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_off);
            }
            else{
                shuffleBoolean = true;
                shuffleBtn.setImageResource(R.drawable.ic_shuffle_on);
            }
        });
        repeatBtn.setOnClickListener(v -> {
            if( repeatBoolean){
                repeatBoolean = false;
                repeatBtn.setImageResource(R.drawable.ic_repeat_off);
            }
            else{
                repeatBoolean = true;
                repeatBtn.setImageResource(R.drawable.ic_repeat_on);
            }
        });
    }


    @Override
    protected void onPostResume() {
        playThreadBtn();
        nextThreadBtn();
        prevThreadBtn();

        super.onPostResume();
    }

    private void playThreadBtn() {
        Thread playThread = new Thread() {
            @Override
            public void run() {
                super.run();
                playPauseBtn.setOnClickListener(v -> playPauseBtnClicked());
            }
        };
        playThread.start();
    }

    private void playPauseBtnClicked() {
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
                    int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                    seekBar.setProgress(mCurrentPosition);
                }
                handler.postDelayed(this, 100);
            }
        });
    }

    private void nextThreadBtn() {
        Thread nextThread = new Thread() {
            @Override
            public void run() {
                super.run();
                nextBtn.setOnClickListener(v -> nextBtnClicked());
            }
        };
        nextThread.start();
    }

    private void nextBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(songList.size()-1);
            }
            else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % songList.size());
            }
            changeMediaInfo();
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.ic_pause);
            mediaPlayer.start();
        }
        else{
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(songList.size()-1);
            }
            else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position + 1) % songList.size());
            }
            position = ((position + 1) % songList.size());
            changeMediaInfo();
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            mediaPlayer.setOnCompletionListener(this);
            playPauseBtn.setBackgroundResource(R.drawable.ic_play_arrow);
        }
    }

    private int getRandom(int i) {
        Random random = new Random();
        return random.nextInt(i+1);
    }

    private void prevThreadBtn() {
        Thread prevThread = new Thread() {
            @Override
            public void run() {
                super.run();
                prevBtn.setOnClickListener(v -> prevBtnClicked());
            }
        };
        prevThread.start();
    }

    private void prevBtnClicked() {
        if(mediaPlayer.isPlaying()){
            mediaPlayer.stop();
            mediaPlayer.release();
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(songList.size()-1);
            }
            else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (songList.size() - 1) : (position - 1) );
            }
            changeMediaInfo();
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
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
            if(shuffleBoolean && !repeatBoolean){
                position = getRandom(songList.size()-1);
            }
            else if (!shuffleBoolean && !repeatBoolean) {
                position = ((position - 1) < 0 ? (songList.size() - 1) : (position - 1) );
            }
            changeMediaInfo();
            PlayerActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (mediaPlayer != null){
                        int mCurrentPosition = mediaPlayer.getCurrentPosition() / 1000;
                        seekBar.setProgress(mCurrentPosition);
                    }
                    handler.postDelayed(this, 100);
                }
            });
            playPauseBtn.setImageResource(R.drawable.ic_play_arrow);
        }
    }
    private void changeMediaInfo(){
        uri = Uri.parse(songList.get(position).getPath());
        mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
        metaData(uri);
        song_name.setText(songList.get(position).getTitle());
        artist_name.setText(songList.get(position).getArtist());
        seekBar.setMax(mediaPlayer.getDuration() / 1000);
    }

    private String formattedTime(int mCurrentPosition) {
        String seconds = String.valueOf(mCurrentPosition % 60);
        String minutes = String.valueOf(mCurrentPosition / 60);
        String totalOut = minutes + ":" + seconds;
        String totalNew = minutes + ":" + "0" + seconds;
        if (seconds.length() == 1){
            return totalNew;
        }
        else return totalOut;
    }

    private void getIntentMethod() {
        position = getIntent().getIntExtra("position", -1);
        String sender = getIntent().getStringExtra("sender");
        if(sender != null && sender.equals("albumDetails")){
            songList = albumFiles;
        }
        else{
            songList = musicFiles;
        }

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

    @Override
    public void onCompletion(MediaPlayer mp) {
        nextBtnClicked();
        if (mediaPlayer != null){
            mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
            mediaPlayer.start();
            mediaPlayer.setOnCompletionListener(this);
        }
    }
}