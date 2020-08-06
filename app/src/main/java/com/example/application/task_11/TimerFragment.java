package com.example.application.task_11;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;

public class TimerFragment extends Fragment {

    private static final String TAG_LOG_TIMER_FRAGMENT = "LogTimerFragment";
    public static final String TAG_SAVE_TIMER_FRAGMENT = "CurrentTimerFragment";

    private static final byte QUANTITY_TIMERS = 10;

    //список для создания тегов
    private ArrayList<String> mListTagsForTimers = new ArrayList<String>(Arrays.asList(
            null, null, null, null, null, null, null, null, null, null
    ));

    //список тегов созданных вьюшек
    private ArrayList<String> mListUsedTags = new ArrayList<String>(10);

    private HashMap<String, HandlerThread> mHandlerThreadMap = new HashMap<String, HandlerThread>(10);

    private byte mCountTimers = 0;
    private Context mContext;
    private ScrollView mScrollOfTimer;
    private LinearLayout mLinearLayout;
    private EditText mEditTextInput;
    private FloatingActionButton mFabAdd;


    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
        Log.e(TAG_LOG_TIMER_FRAGMENT, "mListTagsForTimers - " + mListTagsForTimers.toString());
        Log.e(TAG_LOG_TIMER_FRAGMENT, "mListUsedTags - " + mListUsedTags.toString());
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_timer, container, false);
        mScrollOfTimer = rootView.findViewById(R.id.activity_timer__scroll_view_timer);
        //прокрутка scrollView в конец
        if (savedInstanceState != null) {
            mScrollOfTimer.post(() -> mScrollOfTimer.fullScroll(ScrollView.FOCUS_DOWN));
        }

        mLinearLayout = rootView.findViewById(R.id.activity_timer__layout_for_timer);
        mEditTextInput = rootView.findViewById(R.id.activity_timer__editText_1);
        mFabAdd = rootView.findViewById(R.id.activity_timer__fab_add);
        mFabAdd.setOnClickListener((v) -> {
            createTimer();
        });

        //создаем textView после изменения конфигурации
        if (mListUsedTags.size() != 0) {
            int k = 0;
            for (int i = 0; i < mListUsedTags.size(); i++) {
                TextView textViewTimer = new TextView(mContext);
                //настраиваем textView
                setSettingsTextView(textViewTimer);
                textViewTimer.setTag(mListUsedTags.get(i));
                mLinearLayout.addView(textViewTimer, k);
                k++;
            }
        }
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    //при нажатии кнопки "назад"
    @Override
    public void onDestroy() {
        super.onDestroy();
        for (HandlerThread handlerThread : mHandlerThreadMap.values()) {
//            Log.d(TAG_LOG_TIMER_FRAGMENT, handlerThread.getName() + " - deleted");
            handlerThread.quit();
        }
        mHandlerThreadMap = null;
    }

    private void createTimer() {
        String timerTag = setTimerTag(mListTagsForTimers);
        if (mCountTimers >= QUANTITY_TIMERS || timerTag == null) {
            Toast.makeText(mContext, "Error", Toast.LENGTH_SHORT).show();
            return;
        } else {
            HandlerThread handlerThread = new HandlerThread("TimerHandlerThread" + Integer.parseInt(timerTag));
            handlerThread.start();
            mHandlerThreadMap.put(handlerThread.getName(), handlerThread);

            Log.d(TAG_LOG_TIMER_FRAGMENT, Thread.currentThread().getName() + " - countTimers = " + mCountTimers);
            Log.d(TAG_LOG_TIMER_FRAGMENT, handlerThread.getName() + " - countTimers = " + mCountTimers);

            //бесконечный цикл созданного потока
            Looper tickLooper = handlerThread.getLooper();
            //handler для отправки задач в поток
            final Handler handler = new Handler(tickLooper);
            int startTime = getTimeFromEditText();

            if (startTime <= 0) {
                resetEditText();
                return;
            }
            //по клику создается новый timer
            TextView textViewTimer = new TextView(mContext);
            //настраиваем textView
            setSettingsTextView(textViewTimer);
//            Log.d(TAG_LOG_TIMER_FRAGMENT, "timerTag - " + timerTag);
            textViewTimer.setTag(timerTag);
            mListUsedTags.add(textViewTimer.getTag().toString());
            mLinearLayout.addView(textViewTimer, mCountTimers);

            final String tagCurrentTextView = textViewTimer.getTag().toString();
            Runnable runnable = new Runnable() {
                int timeRemaining = startTime;

                @Override
                public void run() {
                    if (timeRemaining > 0) {
                        timeRemaining = timeRemaining - 1;
                        String timeRemainingFormat = String.format(Locale.getDefault(), "%02d", timeRemaining);
                        //добавление в очередь runnable и запуск через 1 с
                        handler.postDelayed(this, 1000);
                        //находим корневую вьюшку
                        View rootView = TimerFragment.this.getView();
                        if (rootView != null) {
                            TextView textView = rootView.findViewWithTag(tagCurrentTextView);
                            textView.post(() -> textView.setText(timeRemainingFormat));
                        }
                    } else {
                        View rootView = TimerFragment.this.getView();
                        if (rootView != null) {
                            TextView textView = rootView.findViewWithTag(tagCurrentTextView);
                            //запуск анимации TextView
                            runAnimationTextView(textView);
                            //звуковой сигнал
                            playSoundTimer();
                        }
                        Runnable runnableDelay = new Runnable() {
                            @Override
                            public void run() {
                                View rootView = TimerFragment.this.getView();
                                if (rootView != null) {
                                    TextView textView = rootView.findViewWithTag(tagCurrentTextView);
                                    deleteViewTimer(textView);
                                    mHandlerThreadMap.remove(handlerThread.getName());
                                    //удалить все посты Runnable из очереди
                                    handler.removeCallbacks(this);
                                    //looper завершает свою работу
                                    handlerThread.quit();
                                    mListTagsForTimers.set(Integer.parseInt(textView.getTag().toString()), null);
                                    mListUsedTags.remove(String.valueOf(textView.getTag()));
                                }
                            }
                        };
                        //задержка для показа анимации
                        handler.postDelayed(runnableDelay, 2500);
                    }
                }
            };
            mCountTimers++;
            //запуск runnable
            handler.post(runnable);
        }
        resetEditText();
        hideKeyboard(mContext, mEditTextInput);
    }

    private String setTimerTag(ArrayList<String> arrayList) {
        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i) == null) {
                arrayList.set(i, String.valueOf(i));
                return String.valueOf(i);
            }
        }
        return null;
    }

    private void setSettingsTextView(TextView textView) {
        textView.setTextSize(30);
        textView.setText("");
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(Color.BLACK);
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
        });
    }

    private static void hideKeyboard(Context context, View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context
                .getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null) {
            inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
