package com.example.application.task_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
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
    private Activity mActivity;
//    private Context mContext;

    //модификатор public для видимости в AdapterForHorizontalViewer
    public GridLayoutManager mGridLayoutManagerLargeViewer;

    public AdapterForLargeViewer(Activity activity, ArrayList<String> itemsImageLargeViewer,
                                 GridLayoutManager gridLayoutManager) {
        mActivity = activity;
        mArrayListItemsLargeViewer = itemsImageLargeViewer;
        mGridLayoutManagerLargeViewer = gridLayoutManager;
    }

//    public AdapterForLargeViewer(Context context, ArrayList<String> itemsImageLargeViewer,
//                                 GridLayoutManager gridLayoutManager) {
//        mContext = context;
//        mArrayListItemsLargeViewer = itemsImageLargeViewer;
//        mGridLayoutManagerLargeViewer = gridLayoutManager;
//    }

/*    private Drawable loadThumb(Context context, String path) {
        try {
            InputStream stream = context.getAssets().open(path);
            Drawable drawable = Drawable.createFromStream(stream, null);
            return drawable;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }*/

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
        final Bitmap currentBitmap = loadBitmapFromAssets(mActivity.getApplicationContext(), mArrayListItemsLargeViewer.get(position));
        holder.mItemImageView.setImageBitmap(currentBitmap);

//        holder.mItemImageView.setImageDrawable(loadThumb(mContext, mArrayListItemsLargeViewer.get(position)));

        holder.mItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, CurrentImageActivity.class);
                intent.putExtra("image_path", mArrayListItemsLargeViewer.get(position));
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
        return mArrayListItemsLargeViewer.size();
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

//    class ViewHolderLargeViewerFromGallery extends RecyclerView.ViewHolder {
//
//        public ViewHolderLargeViewerFromGallery(@NonNull View itemView) {
//            super(itemView);
//
//        }
//    }

    /*public void updateArrayList(ArrayList<String> itemsImageLargeViewer) {
        mArrayListItemsLargeViewer = itemsImageLargeViewer;
        notifyDataSetChanged();
    }*/

}
