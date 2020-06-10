package com.example.application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        Button buttonFlashLight = (Button) findViewById(R.id.activity_main_button_1);
        View.OnClickListener buttonClickListener_1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RunFlashLight(view);
            }
        };
        buttonFlashLight.setOnClickListener(buttonClickListener_1);


        Button buttonImageViewer = (Button) findViewById(R.id.activity_main_button_2);
        View.OnClickListener buttonClickListener_2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RunImageViewer(view);
            }
        };
        buttonImageViewer.setOnClickListener(buttonClickListener_2);


        Button buttonMap = (Button) findViewById(R.id.activity_main_button_map);
        View.OnClickListener buttonClickListenerMap = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runMap(view);
            }
        };
        buttonMap.setOnClickListener(buttonClickListenerMap);

        Button buttonTimer = (Button) findViewById(R.id.activity_main_button_timer);
        View.OnClickListener buttonClickListenerTimer = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                runTimer(view);
            }
        };
        buttonTimer.setOnClickListener(buttonClickListenerTimer);

        Button buttonWeather = findViewById(R.id.activity_main_button_weather_app);
        buttonWeather.setOnClickListener(view -> {
            Intent intentWeather = new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intentWeather);
        });
    }

    public void RunFlashLight(View view) {
        Intent intentFlashLight = new Intent(MainActivity.this, FlashLightActivity.class);
        startActivity(intentFlashLight);
    }

    public void RunImageViewer(View view) {
        Intent intentImageViewer = new Intent(MainActivity.this, ImageViewerActivity.class);
        startActivity(intentImageViewer);
    }

    public void runMap(View view) {
        Intent intentMap = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intentMap);
    }

    public void runTimer(View view) {
        Intent intentTimer = new Intent(MainActivity.this, TimerActivity.class);
        startActivity(intentTimer);
    }
}
