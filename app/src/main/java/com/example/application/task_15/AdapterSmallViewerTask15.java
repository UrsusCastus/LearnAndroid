package com.example.application.task_15;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class AdapterSmallViewerTask15 extends
        RecyclerView.Adapter<AdapterSmallViewerTask15.ViewHolderSmallViewerTask15> {

    private ArrayList<DataStructure> mArrayListItemsSmallViewer;

    public AdapterSmallViewerTask15(ArrayList<DataStructure> arrayListItemsSmallViewer) {
        mArrayListItemsSmallViewer = arrayListItemsSmallViewer;
    }

    @NonNull
    @Override
    public ViewHolderSmallViewerTask15 onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.task15_recycler_view_item_for_small_viewr, parent, false);
        return new ViewHolderSmallViewerTask15(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolderSmallViewerTask15 holder, int position) {
        Picasso.get()
                .load(mArrayListItemsSmallViewer.get(position).getUrl())
                .placeholder(R.drawable.ic_image_not_download)
                .error(R.drawable.ic_error)
                //fit измеряет размеры imageView и применяет resize для загружемой картинки
                //получаятся минимальное разрешение для imageView
                .fit()
                .centerCrop()
                .into(holder.mImageView);
        holder.mImageView.setOnClickListener((view) -> {
            AdapterLargeViewerTask15 adapterLargeViewerTask15 = ImageViewerTask15Activity.getAdapterForLargeViewer();
            adapterLargeViewerTask15.mGridLayoutManagerLargeViewer.scrollToPosition(position);
        });
    }

    @Override
    public int getItemCount() {
        return mArrayListItemsSmallViewer.size();
    }

    public class ViewHolderSmallViewerTask15 extends RecyclerView.ViewHolder {
        private ImageView mImageView;

        public ViewHolderSmallViewerTask15(@NonNull View itemView) {
            super(itemView);
            mImageView = itemView.findViewById(R.id.task15_horizontal_image_view);
        }
    }
}
