package com.example.application.task_15;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterLargeViewerTask15 extends
        RecyclerView.Adapter<AdapterLargeViewerTask15.ViewHolderLargeViewerTask15> {
    public static final int SPAN_COUNT_ONE_TASK15 = 1;
    public static final int SPAN_COUNT_THREE_TASK15 = 3;
    private static final int ITEM_TYPE_LIST = 1;
    private static final int ITEM_TYPE_GRID = 2;

    private Activity mActivity;
    private ArrayList<DataStructure> mArrayListItemsLargeViewer;
    public GridLayoutManager mGridLayoutManagerLargeViewer;

    public AdapterLargeViewerTask15(Activity activity, ArrayList<DataStructure> arrayListItemsLargeViewer,
                                    GridLayoutManager gridLayoutManager) {
        mActivity = activity;
        mArrayListItemsLargeViewer = arrayListItemsLargeViewer;
        mGridLayoutManagerLargeViewer = gridLayoutManager;
    }

    @NonNull
    @Override
    public ViewHolderLargeViewerTask15 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if (viewType == ITEM_TYPE_LIST) {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task15_recycler_view_item_list_for_large_viewer, parent, false);
        } else {
            view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.task15_recycler_view_item_grid_for_large_viewer, parent, false);
        }
        return new ViewHolderLargeViewerTask15(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderLargeViewerTask15 holder, int position) {
        Picasso.get()
                .load(mArrayListItemsLargeViewer.get(position).getUrl())
                .placeholder(R.drawable.ic_image_not_download)
                .error(R.drawable.ic_error)
                //fit измеряет размеры imageView и применяет resize для загружемой картинки
                //получаятся минимальное разрешение для imageView
                .fit()
                .centerCrop()
                .into(holder.mImageView);

        holder.mImageView.setOnClickListener((imageView) -> {
            Intent intent = new Intent(mActivity, CurrentImageActivityTask15.class);
            intent.putExtra("getTitle", mArrayListItemsLargeViewer.get(position).getTitle());
            intent.putExtra("getUrl", mArrayListItemsLargeViewer.get(position).getUrl());
            mActivity.startActivity(intent);
        });
    }

    @Override
    public int getItemViewType(int position) {
        int spanCount = mGridLayoutManagerLargeViewer.getSpanCount();
        int lastItem = mArrayListItemsLargeViewer.size();
        if (spanCount == SPAN_COUNT_ONE_TASK15 && position != lastItem) {
            return ITEM_TYPE_LIST;
        } else {
            return ITEM_TYPE_GRID;
        }
    }

    @Override
    public int getItemCount() {
        return mArrayListItemsLargeViewer.size();
    }

    public class ViewHolderLargeViewerTask15 extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public ViewHolderLargeViewerTask15(@NonNull View itemView, int viewType) {
            super(itemView);

            switch (viewType) {
                case ITEM_TYPE_LIST:
                    mImageView = itemView.findViewById(R.id.task_15_vertical_image_view_list);
                    break;
                case ITEM_TYPE_GRID:
                    mImageView = itemView.findViewById(R.id.task_15_vertical_image_view_grid);
                    break;
            }
        }
    }
}
