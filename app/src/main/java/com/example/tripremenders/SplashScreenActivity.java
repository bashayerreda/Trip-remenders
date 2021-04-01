package com.example.tripremenders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {
Animation top_down , down_top;
ImageView imageView;
TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash_screen);
        top_down = AnimationUtils.loadAnimation(this,R.anim.top_down);
        down_top = AnimationUtils.loadAnimation(this,R.anim.down_top);
        imageView = findViewById(R.id.splash_img);
        textView = findViewById(R.id.splash_text);
        imageView.setAnimation(top_down);
        textView.setAnimation(down_top);
        final int secondsDelayed = 3000;

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(
                    SplashScreenActivity.this, activity_tips.class);
            startActivity(intent);
            finishAffinity();
        }, secondsDelayed);
    }
}