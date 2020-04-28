package com.example.application.task_8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.CurrentImageActivity;
import com.example.application.R;

import java.io.IOException;
import java.io.InputStream;

public class CurrentImageFragment extends Fragment {

    public static final String TAG_CALLBACK = "CallBack";
    public static Bitmap sBitmapOriginal;
    public static Bitmap sSaveBitmap;

    static ImageView sImageViewFragment;

    private Context mContextOfFragment;

    public static ImageView getImageView() {
        return sImageViewFragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContextOfFragment = context;
        Log.d(TAG_CALLBACK, "onAttach - Fragment of image");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        Log.d(TAG_CALLBACK, "onCreate - Fragment of image");
    }

    //onCreateView создает и возвращает иерархию View, связанную с фрагментом
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        Log.d(TAG_CALLBACK, "onCreateView - Fragment of image");
        View rootView = inflater.inflate(R.layout.fragment_current_image, container, false);
        sImageViewFragment = (ImageView) rootView.findViewById(R.id.current_image_view);
        downloadImage(mContextOfFragment, sImageViewFragment);
        Log.d("BitmapOriginal", String.valueOf(sBitmapOriginal));
        return rootView;
    }

    @Override
    public void onPause() {
        super.onPause();
        sSaveBitmap = ((BitmapDrawable) sImageViewFragment.getDrawable()).getBitmap();
        Log.d(TAG_CALLBACK, "onPause - Fragment of image");
    }

    //для нажатия кнопки назад
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG_CALLBACK, "onDestroy - Fragment of image");
        sSaveBitmap = null;
    }

    public void downloadImage(Context context, ImageView imageView) {
        if (sSaveBitmap != null) {
            imageView.setImageBitmap(sSaveBitmap);
        } else if (CurrentImageActivity.sPathForImageAssets != null) {
            imageView.setImageBitmap(loadBitmapFromAssets(context, CurrentImageActivity.sPathForImageAssets));
            sBitmapOriginal = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else if (CurrentImageActivity.sPathForImageGallery != null) {
            imageView.setImageURI(Uri.parse(CurrentImageActivity.sPathForImageGallery));
            sBitmapOriginal = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        }
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
}
