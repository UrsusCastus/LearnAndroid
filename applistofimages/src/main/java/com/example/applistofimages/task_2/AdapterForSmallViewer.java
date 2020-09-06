package com.example.applistofimages.task_2;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applistofimages.ListOfImagesActivity;
import com.example.applistofimages.R;

import java.util.ArrayList;

public class AdapterForSmallViewer extends RecyclerView.Adapter<AdapterForSmallViewer.ViewHolder> {

    private Context mContext;
    private ArrayList<Integer> mArrayListHorizontalItems;
    private ArrayList<Uri> mArrayListItemsFromGallery;

    public AdapterForSmallViewer(Context context, ArrayList<Integer> arrayListItemsHorizontal, ArrayList<Uri> itemsFromGallery) {
        mContext = context;
        mArrayListHorizontalItems = arrayListItemsHorizontal;
        mArrayListItemsFromGallery = itemsFromGallery;
    }

    @NonNull
    @Override
    public AdapterForSmallViewer.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_smallviewer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterForSmallViewer.ViewHolder holder, final int position) {

        if (position < mArrayListHorizontalItems.size()) {
            holder.itemImage.setImageResource(mArrayListHorizontalItems.get(position));
        } else {
            holder.itemImage.setImageURI(mArrayListItemsFromGallery.get(position - mArrayListHorizontalItems.size()));
        }

        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdapterForLargeViewer adapterForLargeViewer = ListOfImagesActivity.getAdapterForLargeViewer();
                adapterForLargeViewer.mGridLayoutManagerLargeViewer.scrollToPosition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayListHorizontalItems.size() + mArrayListItemsFromGallery.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;

        public ViewHolder(View view) {
            super(view);
            itemImage = view.findViewById(R.id.horizontal_image_view);
        }
    }
}
