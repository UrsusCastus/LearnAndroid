package com.example.application.task_2;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.CurrentImageActivity;
import com.example.application.R;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AdapterForLargeViewer extends RecyclerView.Adapter<AdapterForLargeViewer.ViewHolderLargeViewer> {

    private ArrayList<String> mArrayListItemsLargeViewer;
    private Activity mActivity;

    //модификатор public для видимости в AdapterForHorizontalViewer
    public LinearLayoutManager mLinearLayoutManagerLargeViewer;

    public AdapterForLargeViewer(Activity activity, ArrayList<String> itemsImageLargeViewer, LinearLayoutManager linearLayoutManager) {
        this.mActivity = activity;
        this.mArrayListItemsLargeViewer = itemsImageLargeViewer;
        this.mLinearLayoutManagerLargeViewer = linearLayoutManager;
    }

    /*
        //вариант загрузки из папки assets
        public Drawable assetsDownloadImage(AssetManager manager, String filename) {
            InputStream inputStream = null;
            Drawable drawable = null;
            try {
                inputStream = manager.open(filename);
                drawable = Drawable.createFromStream(inputStream, null);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return drawable;
        }
*/
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
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item_largeviewer, parent, false);
        return new ViewHolderLargeViewer(view);
    }

    //выполняется привязка объекта ViewHolder к объекту Item по определенной позиции
    @Override
    public void onBindViewHolder(@NonNull final ViewHolderLargeViewer holder, final int position) {
        final Bitmap currentBitmap = loadBitmapFromAssets(mActivity.getApplicationContext(), mArrayListItemsLargeViewer.get(position));
        holder.mItemImageView.setImageBitmap(currentBitmap);
        holder.mItemImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mActivity, CurrentImageActivity.class);
                intent.putExtra("image_path", mArrayListItemsLargeViewer.get(position));
                Log.e("Position", String.valueOf(position));
                mActivity.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mArrayListItemsLargeViewer.size();
    }

    class ViewHolderLargeViewer extends RecyclerView.ViewHolder {
        private ImageView mItemImageView;

        public ViewHolderLargeViewer(@NonNull View itemView) {
            super(itemView);
            mItemImageView = itemView.findViewById(R.id.vertical_image_view);
        }
    }
}
