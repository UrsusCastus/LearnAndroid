package com.example.application.task_8;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.R;

import java.io.IOException;
import java.io.InputStream;

public class CurrentImageFragment extends Fragment {

    public static final String TAG_CURRENT_IMAGE_FRAGMENT = "CurrentImageFragment";

    public Bitmap bitmapOriginal;
    public Bitmap saveBitmap;

    private ImageView mImageView;

    private Context mContext;

    public ImageView getImageView() {
        return mImageView;
    }

    public void setImageBitmap(Bitmap bitmap) {
        getImageView().setImageBitmap(bitmap);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    //onCreateView создает и возвращает иерархию View, связанную с фрагментом
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_current_image, container, false);
        mImageView = (ImageView) rootView.findViewById(R.id.current_image_view);
        downloadImage(mContext, mImageView);
        return rootView;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        saveBitmap = ((BitmapDrawable) mImageView.getDrawable()).getBitmap();
    }

    //для нажатия кнопки назад
    @Override
    public void onDestroy() {
        super.onDestroy();
        saveBitmap = null;
    }

    private void downloadImage(Context context, ImageView imageView) {
        String pathForImageAssets = getActivity().getIntent().getStringExtra("pathOfImageFromAssets");
        String pathForImageGallery = getActivity().getIntent().getStringExtra("pathOfImageFromGallery");
        if (saveBitmap != null) {
            imageView.setImageBitmap(saveBitmap);
        } else if (pathForImageAssets != null) {
            imageView.setImageBitmap(loadBitmapFromAssets(context, pathForImageAssets));
            bitmapOriginal = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        } else if (pathForImageGallery != null) {
            imageView.setImageURI(Uri.parse(pathForImageGallery));
            bitmapOriginal = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        }
    }

    private Bitmap loadBitmapFromAssets(Context context, String path) {
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
