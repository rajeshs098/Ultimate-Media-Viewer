package com.example.multimediauploder;

import android.net.Uri;
import android.os.Bundle;
import android.widget.MediaController;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;

public class VideoPlayerActivity extends AppCompatActivity {

    private VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        videoView = findViewById(R.id.videoView);

        Uri videoUri = getIntent().getData();

        if (videoUri != null) {
            playVideo(videoUri);
        }
    }

    private void playVideo(Uri videoUri) {
        MediaController mediaController = new MediaController(this);
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
        videoView.setVideoURI(videoUri);
        videoView.requestFocus();
        videoView.start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        // Remove this line if you want to handle the back button differently
    }
}
