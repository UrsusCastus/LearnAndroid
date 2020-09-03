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
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FlashLightActivity extends AppCompatActivity {

    private boolean mFlashLightOn;
    private Handler mHandler;
    private CameraManager mCameraManager;
    private String mCameraId;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_flash_light);

        mHandler = new Handler();
        mCameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (mCameraManager != null) {
            try {
                mCameraId = mCameraManager.getCameraIdList()[0];
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }

        final boolean hasCameraFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        final CheckBox flashCheckBox = findViewById(R.id.activity_flash_light_flash);

        flashCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (flashCheckBox.isChecked()) {
                if (hasCameraFlash) {
                    if (!mFlashLightOn) {
                        Observable.fromRunnable(() -> blinkFlash())
                                .subscribeOn(Schedulers.single())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe();
                    }
                } else {
                    flashCheckBox.setEnabled(false);
                }
            } else {
                flashLightOff();
            }
        });

        final CheckBox maxBrightnessCheckBox = findViewById(R.id.activity_flash_light_max_brightness);
        maxBrightnessCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (maxBrightnessCheckBox.isChecked()) {
                setBrightnessMax();
            } else {
                setBrightnessCurrent();
            }
        });

        final CheckBox flashLightCheckBox = findViewById(R.id.activity_flash_light_flashlight);
        flashLightCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (flashLightCheckBox.isChecked()) {
                if (hasCameraFlash) {
                    if (!mFlashLightOn) {
                        flashLightOn();
                        mFlashLightOn = true;
                    }
                } else {
                    flashLightCheckBox.setEnabled(false);
                }
            } else {
                flashLightOff();
                mFlashLightOn = false;
            }
        });
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
        Runnable runnableOnFlash = () -> flashLightOn();
        Runnable runnableOffFlash = () -> flashLightOff();
        mHandler.post(runnableOnFlash);
        mHandler.postDelayed(runnableOffFlash, 750);
        mHandler.postDelayed(runnableOnFlash, 1050);
        mHandler.postDelayed(runnableOffFlash, 1200);
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
