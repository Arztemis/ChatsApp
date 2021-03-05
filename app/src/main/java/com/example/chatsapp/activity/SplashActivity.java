package com.example.chatsapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.airbnb.lottie.LottieAnimationView;
import com.example.chatsapp.R;
import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //Khởi tạo class FirebaseAuth để dùng các đối tượng bên trong
        auth = FirebaseAuth.getInstance();
        //Set animation
        TextView tv = findViewById(R.id.app_name);
        LottieAnimationView lottieAnimationView = findViewById(R.id.lotteAnim);
        Animation animation = AnimationUtils.loadAnimation(SplashActivity.this, R.anim.tv_anim);
        tv.startAnimation(animation);
        lottieAnimationView.startAnimation(animation);
        //Chỉ định 1 Looper cố định thì Handler sẽ không bị decreapted
        //getMainLooper : Trả về Looper chính của ứng dụng
        //Replace "new Runnable()" thành "() ->"

        new Handler(Looper.getMainLooper()).postDelayed(() ->
                {
                    if (auth.getCurrentUser() != null) {
                        Intent intent = new Intent(SplashActivity.this, DashBoardActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }
                , 3000);
    }
}