package com.qt.quoctrung.bluetooth__final__camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.qt.quoctrung.bluetooth__final__camera.model.Video;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import dev.shreyaspatil.MaterialDialog.MaterialDialog;

public class ListVideoActivity extends AppCompatActivity {
    private MyAdapterListVideo adapter;
    public  String FOLDER_VIDEO = "/data/user/0/com.qt.quoctrung.bluetooth__final__camera/files/videos/";
    public  String value;
    ImageView imgEmptyData, imgBackActivity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_video);
        RecyclerView recyclerListVideo = findViewById(R.id.recyclerListVideo);
        imgEmptyData = findViewById(R.id.imgEmptyData);
        imgBackActivity = findViewById(R.id.imgBack);

        imgBackActivity.setOnClickListener(view -> {
            onBackPressed();
        });

        List<Video> listVideoUrl = new ArrayList<>();
            File file = new File(FOLDER_VIDEO);
            File[] listFile = file.listFiles();
                for (int i = 0; i<listFile.length; i++) {
                    String name = listFile[i].getName();
                    String url  = listFile[i].getPath();
                    Video video = new Video(name, url);
                    listVideoUrl.add(video);
                }

        adapter = new MyAdapterListVideo(listVideoUrl);
        recyclerListVideo.setAdapter(adapter);

        if (adapter.getItemCount() == 0) {
            imgEmptyData.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

    }

    private String fileSelectedUrl;

    public void setNameFileSelected(String fileSelectedUrl) {
        this.fileSelectedUrl = fileSelectedUrl;
    }

    public void displayDialog(int gravity){
        final Dialog dialog = new Dialog(this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_item_list_video);
        Window window = dialog.getWindow();
        if (window == null) return;
        window.getAttributes().windowAnimations = R.style.Animation_Design_BottomSheetDialog;
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT,WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams windowAttributes = window.getAttributes();
        windowAttributes.gravity = gravity;
        window.setAttributes(windowAttributes);
        if(Gravity.CENTER == gravity){
            dialog.setCancelable(true);

        }else{
            dialog.setCancelable(false);
        }
        TextView txtPlayVideo, txtCancelVideo, txtDeleteVideo;
        txtCancelVideo = dialog.findViewById(R.id.txtCancelVideo);
        txtPlayVideo  = dialog.findViewById(R.id.txtPlayVideo);
        txtDeleteVideo = dialog.findViewById(R.id.txtDeleteVideo);
        txtPlayVideo.setOnClickListener(view -> {
            Intent intent = new Intent(view.getContext(), PlayVideoActivity.class);
            value = adapter.updateFolder();
            intent.putExtra("video", value);
            startActivity(intent);
            dialog.dismiss();

        });
        txtDeleteVideo.setOnClickListener(view -> {
            Toast.makeText(this, "delete", Toast.LENGTH_SHORT).show();
            File folder = new File(fileSelectedUrl);
            if (folder.exists()) {
                boolean deleted = folder.delete();
                adapter.removeItem();
                if (adapter.getItemCount() == 0) {
                    imgEmptyData.setVisibility(View.VISIBLE);
                }
                Toast.makeText(this,deleted ? "Delete success" : "Delete fail", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        txtCancelVideo.setOnClickListener(view -> {
            dialog.dismiss();
        });

        dialog.show();
    }

}