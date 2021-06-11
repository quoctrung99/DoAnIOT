package com.qt.quoctrung.bluetooth__final__camera.utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

public class ImageUtil {
    public static void loadImage(Context context, String url, ImageView view) {
        Glide.with(context).load(url).into(view);
    }
}
