package com.example.application;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Log;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.application.task_8.ButtonOfFilterFragment;
import com.example.application.task_8.CurrentImageFragment;

import org.jetbrains.annotations.NotNull;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import task_10.BrightDownFilter;

public class CurrentImageActivity extends FragmentActivity {

    private static final String LOG_CURRENT_THREAD = "LogCurrentThread";
    private static int sNumberThread = 2;

    private Bitmap mBitmapChanged;
    private CurrentImageFragment mCurrentImageFragment;
    private ButtonOfFilterFragment mButtonOfFilterFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private ExecutorService mExecutorServiceBlur;

    private BrightDownFilter mBrightDownFilter = new BrightDownFilter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_image);

        mFragmentManager = getSupportFragmentManager();
        mButtonOfFilterFragment = (ButtonOfFilterFragment) mFragmentManager.findFragmentById(R.id.fragment_button_filter);

        if (savedInstanceState == null) {
            mCurrentImageFragment = new CurrentImageFragment();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction
                    .replace(R.id.container_for_fragment, mCurrentImageFragment, CurrentImageFragment.TAG_CURRENT_IMAGE_FRAGMENT)
                    .commit();
        } else {
            //получаем ссылку по тегу на уже созданный фрагмент
            mCurrentImageFragment = (CurrentImageFragment) mFragmentManager
                    .findFragmentByTag(CurrentImageFragment.TAG_CURRENT_IMAGE_FRAGMENT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        mExecutorServiceBlur = Executors.newFixedThreadPool(sNumberThread);

        //image, которая изменяется - достается из ImageView фрагмента
        mBitmapChanged = ((BitmapDrawable) mCurrentImageFragment.getImageView().getDrawable()).getBitmap();

        mButtonOfFilterFragment.blackAndWhiteFilter.setOnClickListener(v -> onClickBlackAndWhiteFilterButton());
        mButtonOfFilterFragment.blurFilter.setOnClickListener(v -> onClickBlurFilterButton());
        mButtonOfFilterFragment.brightUpFilter.setOnClickListener(v -> onClickBrightUpFilterButton());
        mButtonOfFilterFragment.brightDownFilter.setOnClickListener(v -> onClickBrightDownFilterButton());

        mButtonOfFilterFragment.reset.setOnClickListener(v -> onClickResetButton());
    }

    //реализация потока через new Thread
    protected void onClickBlackAndWhiteFilterButton() {
        Runnable runnable = new Runnable() {
            Bitmap bitmapSave = Bitmap.createBitmap(mBitmapChanged);

            @Override
            public void run() {
                Log.d(LOG_CURRENT_THREAD, "Thread of BlackAndWhiteFilter - " + Thread.currentThread().getName());
                bitmapSave = monochromeFilter(bitmapSave);
                //обновление ImageView в главном потоке
                //передаем new Runnable (действие) в Message, которое добавляется в очередь Looper of UIThread
                //Объект Message - это инструкция
                mCurrentImageFragment.getImageView().post(new Runnable() {
                    @Override
                    public void run() {
                        mBitmapChanged = bitmapSave;
                        mCurrentImageFragment.setImageBitmap(mBitmapChanged);
                    }
                });
            }
        };
        Thread threadBlackAndWhiteFilter = new Thread(runnable);
        threadBlackAndWhiteFilter.start();
    }

    //реализация потока с помощью ThreadPool
    protected void onClickBlurFilterButton() {
        Runnable runnable = new Runnable() {
            Bitmap bitmapSave = Bitmap.createBitmap(mBitmapChanged);

            @Override
            public void run() {
                Log.d(LOG_CURRENT_THREAD, "Thread of BlurFilter - " + Thread.currentThread().getName());
                bitmapSave = blurFilter(bitmapSave, getApplicationContext());
                mCurrentImageFragment.getImageView().post(() -> {
                    mBitmapChanged = bitmapSave;
                    mCurrentImageFragment.setImageBitmap(mBitmapChanged);
                });
            }
        };

        //постановка задачи на выполнение
        mExecutorServiceBlur.execute(runnable);
    }

    //реализация потока с помощью RxJava
    protected void onClickBrightUpFilterButton() {
        float colorCoefficient = 1.1f;

        Observable
                .fromCallable(() -> {
                    Log.d(LOG_CURRENT_THREAD, "Thread of BrightUpFilter - fromCallable - " + Thread.currentThread().getName());
                    mBitmapChanged = changeBrightness(mBitmapChanged, colorCoefficient);
                    return mBitmapChanged;
                })
                .subscribeOn(Schedulers.io())                               //задается поток
                .observeOn(AndroidSchedulers.mainThread())                  //в какой поток вернется результат, вызывается один раз
        .subscribe(bitmap -> mCurrentImageFragment.setImageBitmap(bitmap));
    }

    //реализация с помощью coroutines (Kotlin)
    protected void onClickBrightDownFilterButton() {
        mBrightDownFilter.applyBrightnessFilter(mBitmapChanged, new Continuation<Bitmap>() {
            @NotNull
            @Override
            public CoroutineContext getContext() {
                return EmptyCoroutineContext.INSTANCE;
            }

            @Override
            public void resumeWith(@NotNull Object o) {
                mBitmapChanged = mBrightDownFilter.mBitmap;
            }
        });
        mCurrentImageFragment.setImageBitmap(mBitmapChanged);
    }

    protected void onClickResetButton() {
        mBitmapChanged = mCurrentImageFragment.bitmapOriginal;
        mCurrentImageFragment.setImageBitmap(mBitmapChanged);
        mCurrentImageFragment.saveBitmap = null;
    }

    public static Bitmap monochromeFilter(Bitmap originalBitmap) {
        int width = originalBitmap.getWidth();
        int height = originalBitmap.getHeight();
        Bitmap outputBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outputBitmap);
        ColorMatrix colorMatrix = new ColorMatrix();
        //метод setSaturation управляет насыщенностью цвета. 0 - черно-белая картинка
        colorMatrix.setSaturation(0);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(originalBitmap, 0, 0, paint);
        return outputBitmap;
    }

    public static Bitmap blurFilter(Bitmap originalBitmap, Context context) {
        //RenderScript - фреймворк для выполнения ресурсоемких задач
        RenderScript renderScript = RenderScript.create(context);
        Allocation allocation = Allocation.createFromBitmap(renderScript, originalBitmap);
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setRadius(10);
        scriptIntrinsicBlur.setInput(allocation);

        Bitmap outputBitmap = Bitmap.createBitmap(originalBitmap.getWidth(), originalBitmap.getHeight(),
                originalBitmap.getConfig());
        Allocation outputAllocation = Allocation.createFromBitmap(renderScript, outputBitmap);
        scriptIntrinsicBlur.forEach(outputAllocation);
        outputAllocation.copyTo(outputBitmap);

        renderScript.destroy();
        return outputBitmap;
    }

    public static Bitmap changeBrightness(Bitmap bitmap, float colorCoefficient) {

        float[] changeBrightnessMatrix = {
                colorCoefficient, 0, 0, 0, 0,
                0, colorCoefficient, 0, 0, 0,
                0, 0, colorCoefficient, 0, 0,
                0, 0, 0, 1, 0
        };

        ColorMatrix colorMatrix = new ColorMatrix(changeBrightnessMatrix);
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        Bitmap resultBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(resultBitmap);
        Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(colorMatrix));
        canvas.drawBitmap(bitmap, 0, 0, paint);
        return resultBitmap;
    }
}
