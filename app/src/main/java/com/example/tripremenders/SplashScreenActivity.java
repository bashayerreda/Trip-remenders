package com.example.tripremenders;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        final int secondsDelayed = 3000;

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(
                    SplashScreenActivity.this, RegistrationActivity.class);
            startActivity(intent);
            finishAffinity();
        }, secondsDelayed);
    }
}