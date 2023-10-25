package com.example.smarttracker;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.airbnb.lottie.LottieAnimationView;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        LottieAnimationView SplashAnim = findViewById(R.id.SplashAnim);
        SplashAnim.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                Intent mainIntent = new Intent(Splash.this,Login.class);
                startActivity(mainIntent);
                finish();
            }
        });
        SplashAnim.setAnimation(R.raw.splash_anim); // Set the animation resource
        SplashAnim.playAnimation();
    }
}