package com.example.application;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.application.task_11.TimerFragment;

public class TimerActivity extends AppCompatActivity {

    private TimerFragment mTimerFragment;
    private FragmentManager mTimerFragmentManager;
    private FragmentTransaction mTimerFragmentTransaction;

    private ScrollView mScrollOfTimer;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        mTimerFragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            mTimerFragment = new TimerFragment();
            mTimerFragmentTransaction = mTimerFragmentManager.beginTransaction();
            mTimerFragmentTransaction
                    .replace(R.id.activity_timer_container_for_fragment, mTimerFragment, mTimerFragment.TAG_SAVE_TIMER_FRAGMENT)
                    .commit();
        } else {
            //получаем ссылку по тегу на уже созданный фрагмент
            mTimerFragment = (TimerFragment) mTimerFragmentManager
                    .findFragmentByTag(TimerFragment.TAG_SAVE_TIMER_FRAGMENT);

            mTimerFragment.mRootView.findViewWithTag("RootView");

        }
        mScrollOfTimer = findViewById(R.id.activity_timer__scroll_view_timer);
        //прокрутка scrollView в конец
        mScrollOfTimer.post(() -> mScrollOfTimer.fullScroll(ScrollView.FOCUS_DOWN));
    }
}
