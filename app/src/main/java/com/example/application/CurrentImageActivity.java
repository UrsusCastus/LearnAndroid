package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;


public class CurrentImageActivity extends AppCompatActivity {

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_current_image);

        ImageView imageView = (ImageView) findViewById(R.id.current_image_view);

        String pathOfImageFromAssets = getIntent().getStringExtra("pathOfImageFromAssets");
        if (pathOfImageFromAssets != null) {
            imageView.setImageBitmap(loadBitmapFromAssets(this, pathOfImageFromAssets));
        }

        String pathOfImageFromGallery = getIntent().getStringExtra("pathOfImageFromGallery");
        if (pathOfImageFromGallery != null) {
            imageView.setImageURI(Uri.parse(pathOfImageFromGallery));
        }
    }

    public Bitmap loadBitmapFromAssets(Context context, String path) {
        InputStream stream = null;
        try {
            stream = context.getAssets().open(path);
            return BitmapFactory.decodeStream(stream);
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
}
