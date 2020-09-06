package com.example.applistofimages.task_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applistofimages.CurrentImageActivity;
import com.example.applistofimages.R;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AdapterForLargeViewer extends RecyclerView.Adapter<AdapterForLargeViewer.ViewHolderLargeViewer> {

    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_THREE = 3;

    private static final int ITEM_TYPE_LIST = 1;
    private static final int ITEM_TYPE_GRID = 2;
    private static final int ITEM_TYPE_BUTTON_ADD = 3;

    private final int REQUEST_CODE = 1;

    private ArrayList<String> mArrayListItemsLargeViewer;
    private ArrayList<Uri> mArrayListItemsFromGallery;

    private Activity mActivity;

    //модификатор public для видимости в AdapterForHorizontalViewer
    public GridLayoutManager mGridLayoutManagerLargeViewer;

    public AdapterForLargeViewer(Activity activity, ArrayList<String> itemsImageLargeViewer,
                                 GridLayoutManager gridLayoutManager, ArrayList<Uri> itemsFromGallery) {
        mActivity = activity;
        mArrayListItemsLargeViewer = itemsImageLargeViewer;
        mGridLayoutManagerLargeViewer = gridLayoutManager;
        mArrayListItemsFromGallery = itemsFromGallery;
    }

    @NonNull
    @Override
    //метод для создания объекта ViewHolder, объект ViewHolder хранит данные по одному объекту Item списка
    public ViewHolderLargeViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_TYPE_LIST) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_for_list_largeviewer,
                    parent, false);
        } else if (viewType == ITEM_TYPE_GRID) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_for_grid_largeviewer,
                    parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_button_add_image_largeviewer,
                    parent, false);
        }
        return new ViewHolderLargeViewer(view, viewType);
    }

    //выполняется привязка объекта ViewHolder к объекту Item по определенной позиции
    @Override
    public void onBindViewHolder(@NonNull final ViewHolderLargeViewer holder, final int position) {
        if (position < mArrayListItemsLargeViewer.size()) {
            final Bitmap currentBitmap = loadBitmapFromAssets(mActivity.getApplicationContext(),
                    mArrayListItemsLargeViewer.get(position));
            holder.mItemImageView.setImageBitmap(currentBitmap);

            holder.mItemImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity, CurrentImageActivity.class);
                    intent.putExtra("pathOfImageFromAssets", mArrayListItemsLargeViewer.get(position));
                    mActivity.startActivity(intent);
                }
            });

        } else if (position < mArrayListItemsLargeViewer.size() + mArrayListItemsFromGallery.size()) {
            holder.mItemImageView.setImageURI(mArrayListItemsFromGallery.get(position - mArrayListItemsLargeViewer.size()));

            holder.mItemImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(mActivity, CurrentImageActivity.class);
                    Uri UriImageGallery = mArrayListItemsFromGallery.get(position - mArrayListItemsLargeViewer.size());
                    String pathOfImageGallery = UriImageGallery.toString();
                    intent.putExtra("pathOfImageFromGallery", pathOfImageGallery);
                    mActivity.startActivity(intent);
                }
            });

        } else {
            holder.mButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    getImageFromGallery();
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        int spanCount = mGridLayoutManagerLargeViewer.getSpanCount();
        int lastItem = mArrayListItemsLargeViewer.size() + mArrayListItemsFromGallery.size();
        if (spanCount == SPAN_COUNT_ONE && position != lastItem) {
            return ITEM_TYPE_LIST;
        } else if (spanCount == SPAN_COUNT_THREE && position != lastItem) {
            return ITEM_TYPE_GRID;
        } else {
            return ITEM_TYPE_BUTTON_ADD;
        }
    }

    @Override
    public int getItemCount() {
        return mArrayListItemsLargeViewer.size() + mArrayListItemsFromGallery.size() + 1;
    }

    class ViewHolderLargeViewer extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;
        private Button mButton;

        public ViewHolderLargeViewer(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == ITEM_TYPE_LIST) {
                mItemImageView = itemView.findViewById(R.id.vertical_image_view);
            } else if (viewType == ITEM_TYPE_GRID) {
                mItemImageView = itemView.findViewById(R.id.grid_image_view);
            } else {
                mButton = itemView.findViewById(R.id.button_add_image);
            }
        }
    }

    //вариант загрузки из папки assets
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

    private void getImageFromGallery() {
        //Намерение ACTION_PICK вызывает отображение галереи всех изображений, хранящихся на телефоне, позволяя выбрать одно изображение
        Intent imageChooseIntent = new Intent(Intent.ACTION_PICK);
        //указываю, где найти файл
        File imageDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String imageDirectoryPath = imageDirectory.getPath();
        //сохраняю путь к файлу
        Uri imageData = Uri.parse(imageDirectoryPath);
        imageChooseIntent.setDataAndType(imageData, "image/*");
        //запуск активити на результат
        mActivity.startActivityForResult(imageChooseIntent, REQUEST_CODE);
    }
}