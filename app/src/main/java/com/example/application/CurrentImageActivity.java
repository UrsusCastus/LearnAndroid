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

import java.util.concurrent.Callable;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Observer;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import task_10.BrightDownFilter;

public class CurrentImageActivity extends FragmentActivity {

    private static final String LOG_CURRENT_THREAD = "LogCurrentThread - ";
    private static final float DOWN_BRIGHTNESS_COLOR_COEFFICIENT = 0.9f;  //-10%

    //устанавливаем время ожидания незанятого потока перед завершением
    private static final int KEEP_ALIVE_TIME = 1000;

    //устанавливаем TimeUnit на миллисекунды
    private static final TimeUnit KEEP_ALIVE_TIME_UNIT = TimeUnit.MICROSECONDS;

    //получаем количество доступных ядер процессора
//    private static int sNumberOfCores = Runtime.getRuntime().availableProcessors();
    private static int sNumberThread = 2;

    private Bitmap mBitmapChanged;
    private CurrentImageFragment mCurrentImageFragment;
    private ButtonOfFilterFragment mButtonOfFilterFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    private CompositeDisposable mCompositeDisposable = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_image);

        Log.d(LOG_CURRENT_THREAD, "sNumberOfCores - " + sNumberThread);
        Log.d(LOG_CURRENT_THREAD, "MainThreadName - " + Thread.currentThread().getName());

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
        //image, которая изменяется - достается из ImageView фрагмента
        mBitmapChanged = ((BitmapDrawable) mCurrentImageFragment.getImageView().getDrawable()).getBitmap();

        //?
        BrightDownFilter brightDownFilter = new BrightDownFilter();

        mButtonOfFilterFragment.blackAndWhiteFilter.setOnClickListener(v -> onClickBlackAndWhiteFilterButton());
        mButtonOfFilterFragment.blurFilter.setOnClickListener(v -> onClickBlurFilterButton());
        mButtonOfFilterFragment.brightUpFilter.setOnClickListener(v -> onClickBrightUpFilterButton());

        mButtonOfFilterFragment.brightDownFilter.setOnClickListener(v -> {
            //?
            //реализация с помощью coroutines (Kotlin)
            mBitmapChanged = brightDownFilter.changeBrightnessBitmap(mBitmapChanged, DOWN_BRIGHTNESS_COLOR_COEFFICIENT);
            mCurrentImageFragment.setImageBitmap(mBitmapChanged);
        });

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

    //реализация потока с помощью ThreadPoolExecutor
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
        //создание инстанса через конструктор ThreadPoolExecutor
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                sNumberThread >> 1,        //начальный размер пула (в два раза меньше sNumberOfCores)
                sNumberThread,                         //максимальный размер пула
                KEEP_ALIVE_TIME,                        //время ожидания потока до его завершения
                KEEP_ALIVE_TIME_UNIT,                   //установка TimeUnit для времени ожидания потока
                new LinkedBlockingQueue<Runnable>());   //очередь задач для потоков

/*
        //создание инстанса через фабричный метод класса Executors
        //возвращается пул потоков, чей максимальный размер N
        ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors.newFixedThreadPool(1);
*/

        //постановка задачи на выполнение
        threadPoolExecutor.execute(runnable);
    }

    //реализация потока с помощью RxJava
    protected void onClickBrightUpFilterButton() {
        float colorCoefficient = 1.1f;

        Observable<Bitmap> observable = Observable
                .fromCallable(new Callable<Bitmap>() {
                    @Override
                    public Bitmap call() throws Exception {
                        Log.d(LOG_CURRENT_THREAD, "Thread of BrightUpFilter - fromCallable - " + Thread.currentThread().getName());
                        mBitmapChanged = changeBrightness(mBitmapChanged, colorCoefficient);
                        return mBitmapChanged;
                    }
                })
                .subscribeOn(Schedulers.io())                   //задается поток
                .observeOn(AndroidSchedulers.mainThread());     //в какой поток вернется результат, вызывается один раз
        //подписка на наблюдаемый объект
        observable.subscribe(new Observer<Bitmap>() {
            //вызывается, когда Observer подписывается к Observable
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                mCompositeDisposable.add(d);
            }

            //вызывается, когда Observable начнет эмитить данные (поступает новая порция данных)
            @Override
            public void onNext(@NonNull Bitmap bitmap) {
//                Log.d(LOG_CURRENT_THREAD, "Thread of BrightUpFilter - onNext - " + Thread.currentThread().getName());
            }

            //вызывается в случае ошибки
            @Override
            public void onError(@NonNull Throwable e) {

            }

            //вызывается, когда Observable завершает работу
            @Override
            public void onComplete() {
                Log.d(LOG_CURRENT_THREAD, "Thread of BrightUpFilter - onComplete - " + Thread.currentThread().getName());
                mCurrentImageFragment.getImageView().setImageBitmap(mBitmapChanged);
            }
        });
    }


//    protected void onClickBrightDownFilterButton() {
//        float colorCoefficient = 0.9f;
//        Runnable runnable = new Runnable() {
//            Bitmap bitmapSave = Bitmap.createBitmap(mBitmapChanged);
//
//            @Override
//            public void run() {
//                bitmapSave = changeBrightness(bitmapSave, colorCoefficient);
//                mCurrentImageFragment.getImageView().post(new Runnable() {
//                    @Override
//                    public void run() {
//                        mBitmapChanged = bitmapSave;
//                        mCurrentImageFragment.setImageBitmap(mBitmapChanged);
//                    }
//                });
//            }
//        };
//        Thread threadBlurDownFilter = new Thread(runnable);
//        threadBlurDownFilter.start();
//    }

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
