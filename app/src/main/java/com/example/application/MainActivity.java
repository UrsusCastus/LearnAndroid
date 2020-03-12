package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);

        Button buttonFlashLight = (Button) findViewById(R.id.button_1);
        View.OnClickListener buttonClickListener_1 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RunFlashLight(view);
            }
        };
        buttonFlashLight.setOnClickListener(buttonClickListener_1);

        Button buttonImageViewer = (Button) findViewById(R.id.button_2);
        View.OnClickListener buttonClickListener_2 = new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RunImageViewer(view);
            }
        };
        buttonImageViewer.setOnClickListener(buttonClickListener_2);

    }

    public void RunFlashLight(View view) {
        Intent intentFlashLight = new Intent(MainActivity.this, FlashLightActivity.class);
        startActivity(intentFlashLight);
    }

    public void RunImageViewer(View view) {
        Intent intentImageViewer = new Intent(MainActivity.this, ImageViewerActivity.class);
        startActivity(intentImageViewer);
    }
}
