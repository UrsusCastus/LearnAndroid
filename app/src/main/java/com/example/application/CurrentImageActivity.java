package com.example.application;

import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

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

import com.example.application.task_8.ButtonOfFilterFragment;
import com.example.application.task_8.CurrentImageFragment;

public class CurrentImageActivity extends FragmentActivity {

    private Bitmap mBitmapChanged;
    private CurrentImageFragment mCurrentImageFragment;
    private ButtonOfFilterFragment mButtonOfFilterFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

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
        //image, которая изменяется - достается из ImageView фрагмента
        mBitmapChanged = ((BitmapDrawable) mCurrentImageFragment.getImageView().getDrawable()).getBitmap();

        mButtonOfFilterFragment.blackAndWhiteFilter.setOnClickListener(v -> onClickBlackAndWhiteFilterButton());
        mButtonOfFilterFragment.blurFilter.setOnClickListener(v -> onClickBlurFilterButton());
        mButtonOfFilterFragment.brightUpFilter.setOnClickListener(v -> onClickBrightUpFilterButton());
        mButtonOfFilterFragment.brightDownFilter.setOnClickListener(v -> onClickBrightDownFilterButton());
        mButtonOfFilterFragment.reset.setOnClickListener(v -> onClickResetButton());
    }

    protected void onClickBlackAndWhiteFilterButton() {
        mBitmapChanged = monochromeFilter(mBitmapChanged);
        mCurrentImageFragment.setImageBitmap(mBitmapChanged);
    }

    protected void onClickBlurFilterButton() {
        mBitmapChanged = blurFilter(mBitmapChanged, getApplicationContext());
        mCurrentImageFragment.setImageBitmap(mBitmapChanged);
    }

    protected void onClickBrightUpFilterButton() {
        float colorCoefficient = 1.1f;
        mBitmapChanged = changeBrightness(mBitmapChanged, colorCoefficient);
        mCurrentImageFragment.setImageBitmap(mBitmapChanged);
    }

    protected void onClickBrightDownFilterButton() {
        float colorCoefficient = 0.9f;
        mBitmapChanged = changeBrightness(mBitmapChanged, colorCoefficient);
        mCurrentImageFragment.setImageBitmap(mBitmapChanged);
    }

    protected void onClickResetButton() {
        mCurrentImageFragment.setImageBitmap(null);
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
