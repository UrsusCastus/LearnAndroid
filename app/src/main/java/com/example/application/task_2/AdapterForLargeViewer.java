package com.example.application.task_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.CurrentImageActivity;
import com.example.application.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AdapterForLargeViewer extends RecyclerView.Adapter<AdapterForLargeViewer.ViewHolderLargeViewer> {

    public static final int SPAN_COUNT_ONE = 1;
    public static final int SPAN_COUNT_THREE = 3;

    private static final int ITEM_TYPE_LIST = 1;
    private static final int ITEM_TYPE_GRID = 2;

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

    @NonNull
    @Override
    //метод для создания объекта ViewHolder, объект ViewHolder хранит данные по одному объекту Item списка
    public ViewHolderLargeViewer onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_TYPE_LIST) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_for_list_largeviewer,
                    parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_for_grid_largeviewer,
                    parent, false);
        }
        return new ViewHolderLargeViewer(view, viewType);
    }

    //выполняется привязка объекта ViewHolder к объекту Item по определенной позиции
    @Override
    public void onBindViewHolder(@NonNull final ViewHolderLargeViewer holder, final int position) {
        if (position < mArrayListItemsLargeViewer.size()) {
            final Bitmap currentBitmap = loadBitmapFromAssets(mActivity.getApplicationContext(), mArrayListItemsLargeViewer.get(position));
            holder.mItemImageView.setImageBitmap(currentBitmap);
        } else {
            holder.mItemImageView.setImageURI(mArrayListItemsFromGallery.get(position - mArrayListItemsLargeViewer.size()));
        }

        holder.mItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, CurrentImageActivity.class);
                if (position < mArrayListItemsLargeViewer.size()) {
                    intent.putExtra("pathOfImageFromAssets", mArrayListItemsLargeViewer.get(position));
                } else {
                    Uri UriImageGallery = mArrayListItemsFromGallery.get(position - mArrayListItemsLargeViewer.size());
                    String pathOfImageGallery = UriImageGallery.toString();
                    intent.putExtra("pathOfImageFromGallery", pathOfImageGallery);
                }
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemViewType(int position) {
        int spanCount = mGridLayoutManagerLargeViewer.getSpanCount();
        if (spanCount == SPAN_COUNT_ONE) {
            return ITEM_TYPE_LIST;
        } else {
            return ITEM_TYPE_GRID;
        }
    }

    @Override
    public int getItemCount() {
        return mArrayListItemsLargeViewer.size() + mArrayListItemsFromGallery.size();
    }

    class ViewHolderLargeViewer extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;

        public ViewHolderLargeViewer(@NonNull View itemView, int viewType) {
            super(itemView);
            if (viewType == ITEM_TYPE_LIST) {
                mItemImageView = itemView.findViewById(R.id.vertical_image_view);
            } else {
                mItemImageView = itemView.findViewById(R.id.grid_image_view);
            }
        }
    }
}