package com.example.multimediauploder;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class MusicPlayerActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    private Uri audioUri;
    private boolean isPlaying = false;
    private ImageButton playButton;
    private ImageButton forwardButton;
    private ImageButton nextButton; // Added next button
    private SeekBar seekBar;
    private TextView currentTimeText;
    private TextView totalTimeText;
    private Handler mHandler;
    private Runnable mRunnable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_player);

        playButton = findViewById(R.id.playButton);
        forwardButton = findViewById(R.id.forwardButton);
        nextButton = findViewById(R.id.nextButton); // Initialize next button
        seekBar = findViewById(R.id.seekBar);
        currentTimeText = findViewById(R.id.currentTime);
        totalTimeText = findViewById(R.id.totalTime);

        audioUri = getIntent().getData();

        if (audioUri != null) {
            mediaPlayer = new MediaPlayer();
            try {
                mediaPlayer.setDataSource(getApplicationContext(), audioUri);
                mediaPlayer.prepare();
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
                        initializeSeekBar();
                    }
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    if (isPlaying) {
                        pauseAudio();
                    } else {
                        playAudio();
                    }
                }
            }
        });

        forwardButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    int currentPosition = mediaPlayer.getCurrentPosition();
                    int duration = mediaPlayer.getDuration();
                    int forwardTime = 10000; // Forward 10 seconds

                    if (currentPosition + forwardTime <= duration) {
                        mediaPlayer.seekTo(currentPosition + forwardTime);
                    } else {
                        mediaPlayer.seekTo(duration);
                    }
                }
            }
        });

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mediaPlayer != null) {
                    mediaPlayer.stop();
                    mediaPlayer.reset();
                    try {
                        mediaPlayer.setDataSource(getApplicationContext(), audioUri);
                        mediaPlayer.prepare();
                        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                initializeSeekBar();
                                playAudio();
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void initializeSeekBar() {
        seekBar.setMax(mediaPlayer.getDuration());
        String totalTime = String.format("%02d:%02d",
                TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration()),
                TimeUnit.MILLISECONDS.toSeconds(mediaPlayer.getDuration()) -
                        TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mediaPlayer.getDuration())));
        totalTimeText.setText(totalTime);

        mHandler = new Handler();
        mRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaPlayer != null) {
                    int mCurrentPosition = mediaPlayer.getCurrentPosition();
                    seekBar.setProgress(mCurrentPosition);

                    String currentTime = String.format("%02d:%02d",
                            TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition),
                            TimeUnit.MILLISECONDS.toSeconds(mCurrentPosition) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(mCurrentPosition)));
                    currentTimeText.setText(currentTime);

                    mHandler.postDelayed(mRunnable, 1000);
                }
            }
        };
        mHandler.postDelayed(mRunnable, 1000);
    }

    private void playAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.start();
            isPlaying = true;
            playButton.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private void pauseAudio() {
        if (mediaPlayer != null) {
            mediaPlayer.pause();
            isPlaying = false;
            playButton.setImageResource(android.R.drawable.ic_media_play);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
        if (mHandler != null) {
            mHandler.removeCallbacks(mRunnable);
        }
    }
}
