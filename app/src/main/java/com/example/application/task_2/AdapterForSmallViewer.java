package com.example.application.task_2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.ImageViewerActivity;
import com.example.application.R;

import java.util.ArrayList;

public class AdapterForSmallViewer extends RecyclerView.Adapter<AdapterForSmallViewer.ViewHolder> {

    private Context mContext;
    private ArrayList<Integer> mArrayListHorizontalItems;

    public AdapterForSmallViewer(Context context, ArrayList<Integer> arrayListItemsHorizontal) {
        this.mContext = context;
        this.mArrayListHorizontalItems = arrayListItemsHorizontal;
    }

    @NonNull
    @Override
    public AdapterForSmallViewer.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.recycler_view_item_smallviewer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterForSmallViewer.ViewHolder holder, final int position) {
        holder.itemImage.setImageResource(mArrayListHorizontalItems.get(position));

        holder.itemImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AdapterForLargeViewer adapterForLargeViewer = ImageViewerActivity.getAdapterForLargeViewer();
                adapterForLargeViewer.mLinearLayoutManagerLargeViewer.scrollToPosition(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayListHorizontalItems.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private ImageView itemImage;

        public ViewHolder(View view) {
            super(view);
            itemImage = view.findViewById(R.id.horizontal_image_view);
        }
    }
}
