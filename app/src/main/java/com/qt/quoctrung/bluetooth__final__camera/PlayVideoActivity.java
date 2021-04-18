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

public class PlayVideoActivity extends AppCompatActivity {

    VideoView videoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_video);

        videoView = findViewById(R.id.videoView);
        Button deleteResource = findViewById(R.id.deleteResource);
        deleteResource.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File folder = new File(getFilesDir() + "/images");
                if (folder.exists()) {
                    boolean a = folder.delete();
                    Toast.makeText(PlayVideoActivity.this, a ? "Delete success" : "Delete fail", Toast.LENGTH_SHORT).show();
                    Log.d("GGG", "takePicture: " + a);
                }
            }
        });


        String video = getIntent().getStringExtra("video");

        videoView.setVideoPath(video);
        videoView.start();

        MediaController m = new MediaController(PlayVideoActivity.this);
        videoView.setMediaController(m);
    }
}