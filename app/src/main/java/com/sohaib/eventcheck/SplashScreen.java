package com.sohaib.eventcheck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.airbnb.lottie.LottieAnimationView;

import java.util.Objects;

public class SplashScreen extends AppCompatActivity {

    LottieAnimationView lottieAnimationView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);



        lottieAnimationView = findViewById(R.id.lottieAnimId);

        lottieAnimationView.setAnimation(R.raw.event_anim);
        lottieAnimationView.playAnimation();
        lottieAnimationView.loop(false);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreen.this,AppMainScreen.class));
                finish();
            }
        },2500);
    }
}