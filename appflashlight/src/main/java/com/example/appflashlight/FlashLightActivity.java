package com.example.appflashlight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class FlashLightActivity extends AppCompatActivity {

    private Handler mHandler;
    private Runnable mBlinkFlashRunnable;
    private CameraManager mCameraManager;
    private String mCameraId;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_flash_light);

        final boolean hasCameraFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (mCameraManager != null) {
            try {
                mCameraId = mCameraManager.getCameraIdList()[0];
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
        mHandler = new Handler();
        final CheckBox blinkFlashCheckBox = findViewById(R.id.activity_flash_light_blink_flash);
        final CheckBox maxBrightnessCheckBox = findViewById(R.id.activity_flash_light_max_brightness);
        final CheckBox flashLightCheckBox = findViewById(R.id.activity_flash_light_flashlight);
        Button flashButton = findViewById(R.id.activity_flash_light_flash);

        if (hasCameraFlash && mCameraManager != null && mCameraId != null) {
            blinkFlashCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (blinkFlashCheckBox.isChecked()) {
                    flashLightCheckBox.setEnabled(false);
                    flashButton.setEnabled(false);
                    blinkFlash();
                } else {
                    mHandler.removeCallbacks(mBlinkFlashRunnable);
                    flashLightCheckBox.setEnabled(true);
                    flashButton.setEnabled(true);
                }
            });

            maxBrightnessCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (maxBrightnessCheckBox.isChecked()) {
                    setBrightnessMax();
                } else {
                    setBrightnessCurrent();
                }
            });

            flashLightCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (flashLightCheckBox.isChecked()) {
                    blinkFlashCheckBox.setEnabled(false);
                    flashButton.setEnabled(false);
                    flashLightOn();
                } else {
                    blinkFlashCheckBox.setEnabled(true);
                    flashButton.setEnabled(true);
                    flashLightOff();
                }
            });

            flashButton.setOnClickListener(view -> flash());

        } else {
            blinkFlashCheckBox.setEnabled(false);
            flashLightCheckBox.setEnabled(false);
            flashButton.setEnabled(false);
        }
    }

    private void flashLightOn() {
        try {
            mCameraManager.setTorchMode(mCameraId, true);
        } catch (CameraAccessException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void flashLightOff() {
        try {
            mCameraManager.setTorchMode(mCameraId, false);
        } catch (CameraAccessException e) {
            Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
    }

    private void blinkFlash() {
        mBlinkFlashRunnable = () -> {
            flashLightOn();
            mHandler.postDelayed(() -> flashLightOff(), 500);
            mHandler.postDelayed(mBlinkFlashRunnable, 1000);
        };
        mBlinkFlashRunnable.run();
    }

    private void flash() {
        Runnable runnable = () -> {
            mHandler.post(() -> flashLightOn());
            mHandler.postDelayed(() -> flashLightOff(), 750);
            mHandler.postDelayed(() -> flashLightOn(), 1050);
            mHandler.postDelayed(() -> flashLightOff(), 1200);
        };
        runnable.run();
    }

    private void setBrightnessMax() {
        WindowManager.LayoutParams layoutParams = getWindow().getAttributes();
        layoutParams.screenBrightness = 1F;
        getWindow().setAttributes(layoutParams);
    }

    private void setBrightnessCurrent() {
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = -1;
        getWindow().setAttributes(layout);
    }
}
