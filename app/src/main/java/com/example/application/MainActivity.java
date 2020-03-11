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
    }

    public void RunFlashLight(View view) {
        Intent intentFlashLight = new Intent(MainActivity.this, FlashLightActivity.class);
        startActivity(intentFlashLight);
    }
}
