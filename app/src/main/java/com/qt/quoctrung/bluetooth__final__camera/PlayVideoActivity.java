package com.qt.quoctrung.bluetooth__final__camera;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.io.File;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;
import dev.shreyaspatil.MaterialDialog.interfaces.DialogInterface;

public class PlayVideoActivity extends AppCompatActivity {

    VideoView videoView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        videoView = findViewById(R.id.videoView);

        String video = getIntent().getStringExtra("video");
        videoView.setVideoPath(video);
        videoView.start();
        MediaController media = new MediaController(PlayVideoActivity.this);
        videoView.setMediaController(media);



    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }
}