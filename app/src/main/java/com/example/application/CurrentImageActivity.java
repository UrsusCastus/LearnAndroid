package com.example.application;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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

        String path = getIntent().getStringExtra("image_path");
        if (path != null) {
            imageView.setImageBitmap(loadBitmapFromAssets(this, path));
        }

        String path_from_gallery = getIntent().getStringExtra("image_path_from_gallery");
        Log.d("Question0", String.valueOf(path_from_gallery));
        if (path_from_gallery != null) {
            imageView.setImageURI(Uri.parse(path_from_gallery));
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
