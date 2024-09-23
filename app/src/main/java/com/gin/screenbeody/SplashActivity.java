package com.gin.screenbeody;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        // Duración de la pantalla de carga
        int splashDuration = 2000; // 2000 ms = 2 segundos

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // Inicia MainActivity después de la duración del splash
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                startActivity(intent);
                finish(); // Termina SplashActivity para que no regrese cuando presiones atrás
            }
        }, splashDuration);
    }
}
