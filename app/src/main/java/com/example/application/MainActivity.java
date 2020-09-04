package com.example.application;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.IdRes;
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

        setupButton(R.id.activity_main_flashlight_button, FlashLightActivity.class);
        setupButton(R.id.activity_main_image_viewer_button, ImageViewerActivity.class);
        setupButton(R.id.activity_main_map_button, MapActivity.class);
        setupButton(R.id.activity_main_timer_button, TimerActivity.class);
        setupButton(R.id.activity_main_weather_app_button, WeatherActivity.class);
        setupButton(R.id.activity_main_widget_weather_button, WidgetWeatherActivity.class);
        setupButton(R.id.activity_main_web_api_button, ImageViewerWebApiActivity.class);
    }

    private void setupButton(@IdRes int buttonId, Class<? extends Activity> activityToStart) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(view ->
                startActivity(new Intent(MainActivity.this, activityToStart)));
    }
}
