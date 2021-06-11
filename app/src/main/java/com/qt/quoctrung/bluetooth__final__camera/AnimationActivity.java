package com.qt.quoctrung.bluetooth__final__camera;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;

public class AnimationActivity extends AppCompatActivity {
    LottieAnimationView lottieAnimationView;
    TextView txtWelcome;
    int flags = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_animation);

          lottieAnimationView = findViewById(R.id.lottie);
         // lottieAnimationView.animate().translationY(2000).setDuration(2000).setStartDelay(4000);
        if(flags == 0){
            lottieAnimationView.playAnimation();
            Log.d("ggg", "flags before" + flags);
//            flags = 1;
//            if(flags == 1){
//                Log.d("ggg", "flags" + flags);
//                Animation animation = AnimationUtils.loadAnimation(AnimationActivity.this, R.anim.animation_sayhi);
//                lottieAnimationView.startAnimation(animation);
//            }

        }



//        txtWelcome.animate().translationY(600).setDuration(2000).setStartDelay(0);
//
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(getApplicationContext(),LoginActivity2.class);
            startActivity(intent);
        },1000);



    }
}