package com.example.application;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WidgetWeatherActivity extends AppCompatActivity {

//    final String htmlWidgetWeather = getString(R.string.html_widget);

    private WebView mWebView;

    @SuppressLint({"SetJavaScriptEnabled", "SourceLockedOrientationActivity"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_widget_weather);

        final String htmlWidgetWeather = getString(R.string.html_widget);
        mWebView = findViewById(R.id.activity_widget_weather_web_view_1);
        mWebView.loadDataWithBaseURL(null, htmlWidgetWeather, "text/html", "UTF-8", null);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mWebView != null) {
            mWebView.destroy();
        }
    }
}
