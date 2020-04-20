package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;

public class CurrentImageActivity extends AppCompatActivity {

    private Button mBlackAndWhiteFilter;
    private Button mBlurFilter;
    private Button mBrightUpFilter;
    private Button mBrightDownFilter;
    private Button mReset;

    private Bitmap bitmapOriginal;
    private Bitmap bitmapChanged;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_current_image);

        mBlackAndWhiteFilter = (Button) findViewById(R.id.black_and_white_button);
        mBlurFilter = (Button) findViewById(R.id.blur_filter);
        mBrightUpFilter = (Button) findViewById(R.id.brightUp_filter);
        mBrightDownFilter = (Button) findViewById(R.id.brightDown_filter);
        mReset = (Button) findViewById(R.id.reset);

        mBlackAndWhiteFilter.getBackground().setColorFilter(0xFF3E8EC7, PorterDuff.Mode.MULTIPLY);
        mBlurFilter.getBackground().setColorFilter(0xFF3E8EC7, PorterDuff.Mode.MULTIPLY);
        mBrightUpFilter.getBackground().setColorFilter(0xFF3E8EC7, PorterDuff.Mode.MULTIPLY);
        mBrightDownFilter.getBackground().setColorFilter(0xFF3E8EC7, PorterDuff.Mode.MULTIPLY);
        mReset.getBackground().setColorFilter(0xFF0DA057, PorterDuff.Mode.MULTIPLY);

        final ImageView imageView = (ImageView) findViewById(R.id.current_image_view);

        final String pathOfImageFromAssets = getIntent().getStringExtra("pathOfImageFromAssets");
        if (pathOfImageFromAssets != null) {
            imageView.setImageBitmap(loadBitmapFromAssets(this, pathOfImageFromAssets));
        }

        String pathOfImageFromGallery = getIntent().getStringExtra("pathOfImageFromGallery");
        if (pathOfImageFromGallery != null) {
            imageView.setImageURI(Uri.parse(pathOfImageFromGallery));
        }

        //Извлечь bitmap из imageView
        bitmapOriginal = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        bitmapChanged = bitmapOriginal;

        mBlackAndWhiteFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapChanged = monochromeFilter(bitmapChanged);
                imageView.setImageBitmap(bitmapChanged);
            }
        });

        mBlurFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bitmapChanged = blurFilter(bitmapChanged, getApplicationContext());
                imageView.setImageBitmap(bitmapChanged);
            }
        });

        mBrightUpFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float colorCoefficient = 1.1f;
                bitmapChanged = changeBrightness(bitmapChanged, colorCoefficient);
                imageView.setImageBitmap(bitmapChanged);
            }
        });

        mBrightDownFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float colorCoefficient = 0.9f;
                bitmapChanged = changeBrightness(bitmapChanged, colorCoefficient);
                imageView.setImageBitmap(bitmapChanged);
            }
        });

        mReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageDrawable(null);
                bitmapChanged = bitmapOriginal;
                imageView.setImageBitmap(bitmapChanged);
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
    }

    public Bitmap loadBitmapFromAssets(Context context, String path) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(path);
            Bitmap bitmapLoad = BitmapFactory.decodeStream(stream);
            return bitmapLoad;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (stream != null) {
                    stream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
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
                0, 0, 0,                1, 0
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
