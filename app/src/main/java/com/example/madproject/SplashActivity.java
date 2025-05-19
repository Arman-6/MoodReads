package com.example.madproject;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_DURATION = 2000; // 2 seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ImageView logo = findViewById(R.id.logoImage);
        TextView appName = findViewById(R.id.appName);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Fade-in Animation
        logo.animate().alpha(1).setDuration(1000);
        appName.animate().alpha(1).setDuration(1500);

        // Show Progress Bar after some delay
        new Handler().postDelayed(() -> progressBar.setVisibility(View.VISIBLE), 1000);

        new Handler().postDelayed(() -> {
            Intent intent;
            intent = new Intent(SplashActivity.this, PreferenceTestActivity.class);
            startActivity(intent);
            finish();
        }, SPLASH_DURATION);
    }
}