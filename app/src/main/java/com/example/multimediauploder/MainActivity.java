package com.example.multimediauploder;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_PERMISSION = 1;
    private static final int REQUEST_CODE_GALLERY = 2;
    private static final int REQUEST_CODE_VIDEO = 3;

    private ImageView upload;
    private ImageView anotherImage;
    private ImageView playVideo;
    private ImageView playMusic;
    private Uri imageUri = null;
    private Uri videoUri = null;
    private Uri audioUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        upload = findViewById(R.id.uploadpdf);
        anotherImage = findViewById(R.id.anotherImage);
        playVideo = findViewById(R.id.playvideo);
        playMusic = findViewById(R.id.playMusic);

        upload.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleHoverEffect(v, event);
                return false;
            }
        });

        anotherImage.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleHoverEffect(v, event);
                return false;
            }
        });

        playVideo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleHoverEffect(v, event);
                return false;
            }
        });

        playMusic.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                handleHoverEffect(v, event);
                return false;
            }
        });

        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openPdfPicker();
            }
        });

        anotherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImageGallery();
            }
        });

        playVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openVideoPicker();
            }
        });

        playMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMusicPicker();
            }
        });
    }

    private void handleHoverEffect(View view, MotionEvent event) {
        ImageView imageView = (ImageView) view;
        Animation slideUpAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_up);
        Animation slideDownAnimation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.slide_down);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                imageView.startAnimation(slideUpAnimation);
                break;
            case MotionEvent.ACTION_UP:
                imageView.startAnimation(slideDownAnimation);
                break;
        }
    }

    private void openPdfPicker() {
        Intent galleryIntent = new Intent();
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.setType("application/pdf");
        startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
    }

    private void openImageGallery() {
        // Check if the required permission is granted
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission not granted, request it
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_CODE_PERMISSION);
        } else {
            // Permission already granted, open image gallery
            Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(galleryIntent, REQUEST_CODE_GALLERY);
        }
    }

    private void openVideoPicker() {
        Intent videoIntent = new Intent();
        videoIntent.setAction(Intent.ACTION_GET_CONTENT);
        videoIntent.setType("video/*");
        startActivityForResult(Intent.createChooser(videoIntent, "Select Video"), REQUEST_CODE_VIDEO);
    }

    private void openMusicPicker() {
        Intent musicIntent = new Intent();
        musicIntent.setAction(Intent.ACTION_GET_CONTENT);
        musicIntent.setType("audio/*");
        startActivityForResult(Intent.createChooser(musicIntent, "Select Audio"), REQUEST_CODE_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CODE_GALLERY) {
                if (data != null) {
                    Uri selectedUri = data.getData();
                    if (selectedUri != null) {
                        String fileType = getContentResolver().getType(selectedUri);
                        if (fileType != null && fileType.startsWith("image/")) {
                            imageUri = selectedUri;
                            openImageFullscreenActivity(imageUri);
                        } else if (fileType != null && fileType.startsWith("audio/")) {
                            audioUri = selectedUri;
                            openMusicPlayer();
                        }
                    }
                }
            } else if (requestCode == REQUEST_CODE_VIDEO) {
                if (data != null) {
                    videoUri= data.getData();
                    playVideo(videoUri);
                }
            }
        }
    }

    private void openImageFullscreenActivity(Uri imageUri) {
        Intent fullscreenIntent = new Intent(MainActivity.this, ImageFullscreenActivity.class);
        fullscreenIntent.setData(imageUri);
        startActivity(fullscreenIntent);
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down);
    }

    private void openMusicPlayer() {
        if (audioUri != null) {
            Intent musicPlayerIntent = new Intent(MainActivity.this, MusicPlayerActivity.class);
            musicPlayerIntent.setData(audioUri);
            startActivity(musicPlayerIntent);
        } else {
            Toast.makeText(this, "No audio file selected", Toast.LENGTH_SHORT).show();
        }
    }

    private void playVideo(Uri videoUri) {
        Intent videoIntent = new Intent(MainActivity.this, VideoPlayerActivity.class);
        videoIntent.setData(videoUri);
        startActivity(videoIntent);
    }
}
