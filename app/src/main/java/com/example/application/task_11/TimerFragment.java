package com.example.application.task_11;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.HashMap;
import java.util.Locale;

public class TimerFragment extends Fragment {

    private static final String TAG_LOG_TIMER_FRAGMENT = "LogTimerFragment";

    public static final String TAG_SAVE_TIMER_FRAGMENT = "CurrentTimerFragment";
    private static final byte QUANTITY_TIMERS = 10;

    private HashMap<String, HandlerThread> mHandlerThreadMap = new HashMap<String, HandlerThread>(10);

    private byte mCountTimers = 0;

    public View mRootView;

    private Context mContext;
    private LinearLayout mLinearLayout;
    private EditText mEditTextInput;
    private FloatingActionButton mFabAdd;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG_LOG_TIMER_FRAGMENT, "Run onAttach");
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG_LOG_TIMER_FRAGMENT, "Run onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.e(TAG_LOG_TIMER_FRAGMENT, "Run onCreateView");
        if (savedInstanceState == null) {
            mRootView = inflater.inflate(R.layout.fragment_timer, container, false);
            Log.d(TAG_LOG_TIMER_FRAGMENT, "rootView created - " + mRootView);
            mRootView.setTag("RootView");

            mEditTextInput = mRootView.findViewById(R.id.activity_timer__editText_1);
            mFabAdd = mRootView.findViewById(R.id.activity_timer__fab_add);
            mLinearLayout = mRootView.findViewById(R.id.activity_timer__layout_for_timer);
        }
        mFabAdd.setOnClickListener((view) -> {
            createTimer();
        });
        return mRootView;
    }

    @Override
    public void onDestroyView() {
        Log.d(TAG_LOG_TIMER_FRAGMENT, "Run onDestroyView");
        super.onDestroyView();
    }

    //при нажатии кнопки "назад"
    @Override
    public void onDestroy() {
        Log.d(TAG_LOG_TIMER_FRAGMENT, "Run onDestroy");
        super.onDestroy();
        for (HandlerThread handlerThread : mHandlerThreadMap.values()) {
            Log.d(TAG_LOG_TIMER_FRAGMENT, handlerThread.getName() + " - deleted");
            handlerThread.quit();
        }
        mHandlerThreadMap = null;
    }

    private void createTimer() {
        if (mCountTimers < QUANTITY_TIMERS) {
            HandlerThread handlerThread = new HandlerThread("TimerHandlerThread - " + mCountTimers);
            handlerThread.start();
            mHandlerThreadMap.put(handlerThread.getName(), handlerThread);
//            Log.d("LogTagCurrentThread", Thread.currentThread().getName() + " - countTimers = " + mCountTimers);
            //бесконечный цикл созданного потока
            Looper tickLooper = handlerThread.getLooper();

            //handler для отправки задач в поток
            final Handler handler = new Handler(tickLooper);
            int startTime = getTimeFromEditText();

            if (startTime <= 0) {
                resetEditText();
                return;
            }
            //создается новая вьюшка
            View viewTimer = getLayoutInflater().inflate(R.layout.text_view_for_timer, null);
            mLinearLayout.addView(viewTimer, mCountTimers);

            //отслеживание индекса вьюшки в LinearLayout
            Log.d(TAG_LOG_TIMER_FRAGMENT, String.valueOf(mLinearLayout.indexOfChild(viewTimer)));

            TextView textTimerDown = viewTimer.findViewById(R.id.activity_timer__textView_timer_1);

            Runnable runnable = new Runnable() {
                int timeRemaining = startTime;

                @Override
                public void run() {
                    if (timeRemaining > 0) {
                        timeRemaining = timeRemaining - 1;
                        String timeRemainingFormat = String.format(Locale.getDefault(), "%02d", timeRemaining);
                        //добавление в очередь runnable и запуск через 1 с
                        handler.postDelayed(this, 1000);
                        textTimerDown.post(() -> textTimerDown.setText(timeRemainingFormat));
                    } else {
                        //запуск анимации TextView
                        runAnimationTextView(textTimerDown);
                        //звуковой сигнал
                        playSoundTimer();

                        Runnable runnableDelay = new Runnable() {
                            @Override
                            public void run() {
                                deleteViewTimer(viewTimer);
                                mHandlerThreadMap.remove(handlerThread.getName());
                                //удалить все посты Runnable из очереди
                                handler.removeCallbacks(this);
                                //looper завершает свою работу
                                handlerThread.quit();
                            }
                        };
                        handler.postDelayed(runnableDelay, 2500);
                    }
                }
            };
            mCountTimers++;
            //запуск runnable
            handler.post(runnable);
        } else {
            return;
        }
        resetEditText();
        hideKeyboard(mContext, mEditTextInput);
    }

    private int getTimeFromEditText() {
        int startTimeSecond = 0;
        String inputTime = mEditTextInput.getText().toString();
        if (inputTime.length() != 0) {
            startTimeSecond = Integer.parseInt(inputTime);
        }
        return startTimeSecond;
    }

    private void resetEditText() {
        mEditTextInput.setText("");
    }

    private void runAnimationTextView(TextView textTimerDown) {
        //анимация альфа канала (прозрачности от 0 до 1)
        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        //длительность анимации
        animation.setDuration(400);
        //режим повтора
        animation.setRepeatCount(3);
        //накладываем анимацию на TextView
        textTimerDown.startAnimation(animation);
    }

    private void playSoundTimer() {
        MediaPlayer mediaPlayer = MediaPlayer.create(mContext, R.raw.timer);
        mediaPlayer.start();
    }

    private void deleteViewTimer(View view) {
        mLinearLayout.post(() -> {
            mLinearLayout.removeView(view);
            mCountTimers--;
            Log.d("LogTagCurrentThread", Thread.currentThread().getName() + " - countTimers = " + mCountTimers);
        });
    }

    private static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
