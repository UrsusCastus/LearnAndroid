package com.example.application;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class WidgetWeatherActivity extends AppCompatActivity {
    private static final String TAG_WIDGET_WEATHER_ACTIVITY = "TagWidgetWeatherActivity";
    private WebView mWebView;

    @SuppressLint({"SetJavaScriptEnabled", "SourceLockedOrientationActivity", "LongLogTag"})
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG_WIDGET_WEATHER_ACTIVITY, "Run onCreate");
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
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
