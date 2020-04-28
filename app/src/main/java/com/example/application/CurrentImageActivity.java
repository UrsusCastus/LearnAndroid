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
import android.util.Log;
import android.widget.ImageView;

import com.example.application.task_8.ButtonOfFilterFragment;
import com.example.application.task_8.CurrentImageFragment;

public class CurrentImageActivity extends FragmentActivity {

    private static final String TAG_CURRENT_IMAGE_FRAGMENT = "CurrentImageFragment";

    private Bitmap mBitmapOriginal;

    private Bitmap mBitmapChanged;

    private CurrentImageFragment mCurrentImageFragment;
    private FragmentManager mFragmentManager;
    private FragmentTransaction mFragmentTransaction;

    public static String sPathForImageAssets;
    public static String sPathForImageGallery;

    public static ImageView sImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_current_image);

        Log.d(CurrentImageFragment.TAG_CALLBACK, "onCreate - Activity");

        sPathForImageAssets = getIntent().getStringExtra("pathOfImageFromAssets");
        sPathForImageGallery = getIntent().getStringExtra("pathOfImageFromGallery");

        mFragmentManager = getSupportFragmentManager();

        if (savedInstanceState == null) {
            mCurrentImageFragment = new CurrentImageFragment();
            mFragmentTransaction = mFragmentManager.beginTransaction();
            mFragmentTransaction
                    .replace(R.id.container_for_fragment, mCurrentImageFragment, TAG_CURRENT_IMAGE_FRAGMENT)
                    .commit();
        } else {
            //получаем ссылку по тегу на уже созданный фрагмент
            mCurrentImageFragment = (CurrentImageFragment) mFragmentManager
                    .findFragmentByTag(TAG_CURRENT_IMAGE_FRAGMENT);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(CurrentImageFragment.TAG_CALLBACK, "onStart - Activity");

        sImageView = CurrentImageFragment.getImageView();

        //получить оригинал картинки
        //image, которая изменяется - достается из ImageView фрагмента
        mBitmapOriginal = CurrentImageFragment.sBitmapOriginal;
        mBitmapChanged = ((BitmapDrawable) sImageView.getDrawable()).getBitmap();

        ButtonOfFilterFragment.blackAndWhiteFilter.setOnClickListener(v -> onClickBlackAndWhiteFilterButton());
        ButtonOfFilterFragment.blurFilter.setOnClickListener(v -> OnClickBlurFilterButton());
        ButtonOfFilterFragment.brightUpFilter.setOnClickListener(v -> OnClickBrightUpFilterButton());
        ButtonOfFilterFragment.brightDownFilter.setOnClickListener(v -> OnClickBrightDownFilterButton());
        ButtonOfFilterFragment.reset.setOnClickListener(v -> OnClickResetButton());
    }

    protected void onClickBlackAndWhiteFilterButton() {
        mBitmapChanged = monochromeFilter(mBitmapChanged);
        sImageView.setImageBitmap(mBitmapChanged);
    }

    protected void OnClickBlurFilterButton() {
        mBitmapChanged = blurFilter(mBitmapChanged, getApplicationContext());
        sImageView.setImageBitmap(mBitmapChanged);
    }

    protected void OnClickBrightUpFilterButton() {
        float colorCoefficient = 1.1f;
        mBitmapChanged = changeBrightness(mBitmapChanged, colorCoefficient);
        sImageView.setImageBitmap(mBitmapChanged);
    }

    protected void OnClickBrightDownFilterButton() {
        float colorCoefficient = 0.9f;
        mBitmapChanged = changeBrightness(mBitmapChanged, colorCoefficient);
        sImageView.setImageBitmap(mBitmapChanged);
    }

    protected void OnClickResetButton() {
        sImageView.setImageDrawable(null);
        mBitmapChanged = CurrentImageFragment.sBitmapOriginal;
        sImageView.setImageBitmap(mBitmapChanged);
        CurrentImageFragment.sSaveBitmap = null;
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
