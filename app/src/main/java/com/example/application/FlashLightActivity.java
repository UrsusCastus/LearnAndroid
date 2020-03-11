package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;


public class FlashLightActivity extends AppCompatActivity {

    //если инициализировать поля тут, то приложение падает (?)
    boolean mFlashLightStatus;
    boolean mHasCameraFlash;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        Log.d("FlashLightActivity", "onCreate()");
        setContentView(R.layout.activity_flash_light);

        final CheckBox mCheckBoxFlash = (CheckBox) findViewById(R.id.flash);
        mCheckBoxFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mHasCameraFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                if (mCheckBoxFlash.isChecked()) {
                    //включаем вспышку
                    if (mHasCameraFlash) {
                        if (!mFlashLightStatus) {
                            blinkFlash();
                        }
                    } else {
                        Toast.makeText(FlashLightActivity.this, "No Flash available on your device",
                                Toast.LENGTH_SHORT).show();
                    }
                } else {
                    //выключаем фонарик
                    flashLightOff();
                }
            }
        });

        final CheckBox mMaxBrightness = (CheckBox) findViewById(R.id.max_brightness);
        mMaxBrightness.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mMaxBrightness.isChecked()) {
                    setBrightnessMax();
                } else {
                    setBrightnessCurrent();
                }
            }
        });

        final CheckBox mCheckBoxFlashLight = (CheckBox) findViewById(R.id.flashLight);
        mCheckBoxFlashLight.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mHasCameraFlash = getApplicationContext().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
                if (mCheckBoxFlashLight.isChecked()) {
                    //включаем фонарик
                    if (mHasCameraFlash) {
                        if (!mFlashLightStatus) {
                            flashLightOn();
                        }
                    } else {
                        Toast.makeText(FlashLightActivity.this, "No Flash available on your device",
                                Toast.LENGTH_SHORT).show();
                        //при повторном нажатии на checkBox приложение падает (?)
                    }
                } else {
                    //выключаем фонарик
                    flashLightOff();
                }
            }
        });
    }

    //метод включения светодиода
    private void flashLightOn() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, true);
            mFlashLightStatus = true;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //метод выключения светодиода
    private void flashLightOff() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        try {
            String cameraId = cameraManager.getCameraIdList()[0];
            cameraManager.setTorchMode(cameraId, false);
            mFlashLightStatus = false;
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    //метод мигания светодиода
    private void blinkFlash() {
        CameraManager cameraManager = (CameraManager) getSystemService(Context.CAMERA_SERVICE);
        String string = "000000000000000111111000111";
        long blinkDelay = 50;
        for (int i = 0; i < string.length(); i++) {
            if (string.charAt(i) == '0') {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraId, true);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    String cameraId = cameraManager.getCameraIdList()[0];
                    cameraManager.setTorchMode(cameraId, false);
                } catch (CameraAccessException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(blinkDelay);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    private void setBrightnessMax() {
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = 1F;
        getWindow().setAttributes(layout);
    }

    private void setBrightnessCurrent() {
        WindowManager.LayoutParams layout = getWindow().getAttributes();
        layout.screenBrightness = -1;
        getWindow().setAttributes(layout);
    }

}
