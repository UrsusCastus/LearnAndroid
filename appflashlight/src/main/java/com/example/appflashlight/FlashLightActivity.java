package com.example.appflashlight;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FlashLightActivity extends AppCompatActivity {

    private boolean mFlashLightOn;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_flash_light);

        final boolean hasCameraFlash = getApplicationContext().getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
        final CheckBox flashCheckBox = findViewById(R.id.activity_flash_light_flash);

        flashCheckBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (flashCheckBox.isChecked()) {
                if (hasCameraFlash) {
                    if (!mFlashLightOn) {
                        blinkFlash();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.no_flash_available_message, Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(getApplicationContext(), R.string.no_flash_available_message, Toast.LENGTH_SHORT).show();
                }
            } else {
                flashLightOff();
                mFlashLightOn = false;
            }
        });
    }

    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager != null) {
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, true);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        if (cameraManager != null) {
            try {
                String cameraId = cameraManager.getCameraIdList()[0];
                cameraManager.setTorchMode(cameraId, false);
            } catch (CameraAccessException e) {
                e.printStackTrace();
            }
        }
    }

    private void blinkFlash() {
        final List<Integer> blinkFlash =
                Arrays.asList(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 1, 0, 0, 0, 1, 1, 1);
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);

        Observable.fromIterable(blinkFlash)
                .doOnNext(number -> {
                    String cameraId;
                    if (cameraManager != null) {
                        cameraId = cameraManager.getCameraIdList()[0];
                        if (number == 0) {
                            cameraManager.setTorchMode(cameraId, true);
                        } else {
                            cameraManager.setTorchMode(cameraId, false);
                        }
                    }
                    Thread.sleep(50);
                })
                .subscribeOn(Schedulers.single())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
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
