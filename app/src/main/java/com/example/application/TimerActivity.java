package com.example.application;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.example.application.task_11.TimerFragment;

public class TimerActivity extends AppCompatActivity {

    private TimerFragment mTimerFragment;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        FragmentManager timerFragmentManager = getSupportFragmentManager();
        Fragment timeFragment = timerFragmentManager.findFragmentByTag(TimerFragment.TAG_SAVE_TIMER_FRAGMENT);

        if (timeFragment != null) {
            //получаем ссылку по тегу на уже созданный фрагмент
            mTimerFragment = (TimerFragment) timeFragment;
        } else {
            mTimerFragment = new TimerFragment();
            timerFragmentManager.beginTransaction()
                    .add(R.id.activity_timer_container_for_fragment, mTimerFragment, TimerFragment.TAG_SAVE_TIMER_FRAGMENT)
                    .commit();
        }
    }
}
