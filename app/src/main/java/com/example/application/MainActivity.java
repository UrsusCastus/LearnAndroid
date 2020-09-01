package com.example.application;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.appflashlight.FlashLightActivity;
import com.example.application.task_15.ImageViewerWebApiActivity;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        Button flashLightButton = findViewById(R.id.activity_main_flashlight_button);
        flashLightButton.setOnClickListener(view -> runFlashLight());

        Button imageViewerButton = findViewById(R.id.activity_main_image_viewer_button);
        imageViewerButton.setOnClickListener(view -> runImageViewer());

        Button mapButton = findViewById(R.id.activity_main_map_button);
        mapButton.setOnClickListener(view -> runMap());

        Button timerButton = findViewById(R.id.activity_main_timer_button);
        timerButton.setOnClickListener(view -> runTimer());

        Button weatherAppButton = findViewById(R.id.activity_main_weather_app_button);
        weatherAppButton.setOnClickListener(view -> runWeather());

        Button widgetWeatherButton = findViewById(R.id.activity_main_widget_weather_button);
        widgetWeatherButton.setOnClickListener(view -> runWidgetWeather());

        Button webApiButton = findViewById(R.id.activity_main_web_api_button);
        webApiButton.setOnClickListener(view -> runWebApi());
    }

    private void runFlashLight() {
        Intent intentFlashLight = new Intent(MainActivity.this, FlashLightActivity.class);
        startActivity(intentFlashLight);
    }

    private void runImageViewer() {
        Intent intentImageViewer = new Intent(MainActivity.this, ImageViewerActivity.class);
        startActivity(intentImageViewer);
    }

    private void runMap() {
        Intent intentMap = new Intent(MainActivity.this, MapActivity.class);
        startActivity(intentMap);
    }

    private void runTimer() {
        Intent intentTimer = new Intent(MainActivity.this, TimerActivity.class);
        startActivity(intentTimer);
    }

    private void runWeather() {
        Intent intentWeather = new Intent(MainActivity.this, WeatherActivity.class);
        startActivity(intentWeather);
    }

    private void runWidgetWeather() {
        Intent intentWidgetWeather = new Intent(MainActivity.this, WidgetWeatherActivity.class);
        startActivity(intentWidgetWeather);
    }

    private void runWebApi() {
        Intent intentWebApi = new Intent(MainActivity.this, ImageViewerWebApiActivity.class);
        startActivity(intentWebApi);
    }
}
