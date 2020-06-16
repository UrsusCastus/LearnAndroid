package com.example.application;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.application.task_13_weather.WeatherFragment;

public class WeatherActivity extends AppCompatActivity {

    private WeatherFragment mWeatherFragment;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.Theme_AppCompat_DayNight_DarkActionBar);
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_weather);

        FragmentManager weatherFragmentManager = getSupportFragmentManager();
        Fragment weatherFragment = weatherFragmentManager.findFragmentByTag(WeatherFragment.TAG_SAVE_WEATHER_FRAGMENT);

        if (savedInstanceState != null) {
            mWeatherFragment = (WeatherFragment) weatherFragment;
        } else {
            mWeatherFragment = new WeatherFragment();
            weatherFragmentManager.beginTransaction()
                    .replace(R.id.activity_weather_container_for_fragment, mWeatherFragment,
                            WeatherFragment.TAG_SAVE_WEATHER_FRAGMENT)
                    .commit();
        }
    }
}
