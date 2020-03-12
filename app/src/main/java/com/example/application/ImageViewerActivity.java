package com.example.application;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import com.example.application.task_2.AdapterForHorizontalViewer;
import com.example.application.task_2.AdapterForVerticalViewer;

import java.util.ArrayList;

public class ImageViewerActivity extends AppCompatActivity {

    ArrayList<Integer> mItemsHorizontal = new ArrayList<Integer>(38);
    ArrayList<String> mItemsVertical = new ArrayList<String>(38);

    private static AdapterForVerticalViewer sAdapterForVerticalViewer;

    //модификатор static для видимости в AdapterForHorizontalViewer
    public static AdapterForVerticalViewer getAdapterForVerticalViewer() {
        return sAdapterForVerticalViewer;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_image_viewer);

        setInitialImageHorizontal();
        initialRecyclerViewHorizontal();

        setInitialImageVertical();
        initialRecyclerViewVertical();
    }

    private void initialRecyclerViewHorizontal() {
        RecyclerView recyclerViewHorizontal = (RecyclerView) findViewById(R.id.rv_horizontal);
        recyclerViewHorizontal.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        AdapterForHorizontalViewer imageHorizontalAdapter = new AdapterForHorizontalViewer(this, mItemsHorizontal);
        recyclerViewHorizontal.setAdapter(imageHorizontalAdapter);
    }

    private void initialRecyclerViewVertical() {
        RecyclerView recyclerViewVertical = (RecyclerView) findViewById(R.id.rv_vertical);
        LinearLayoutManager mLinearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recyclerViewVertical.setLayoutManager(mLinearLayoutManager);
        sAdapterForVerticalViewer = new AdapterForVerticalViewer(this, mItemsVertical, mLinearLayoutManager);
        recyclerViewVertical.setAdapter(sAdapterForVerticalViewer);
    }

    private void setInitialImageHorizontal() {
        mItemsHorizontal.add(R.drawable.image_1_256_pix);
        mItemsHorizontal.add(R.drawable.image_2_256_pix);
        mItemsHorizontal.add(R.drawable.image_3_256_pix);
        mItemsHorizontal.add(R.drawable.image_4_256_pix);
        mItemsHorizontal.add(R.drawable.image_5_256_pix);
        mItemsHorizontal.add(R.drawable.image_6_256_pix);
        mItemsHorizontal.add(R.drawable.image_7_256_pix);
        mItemsHorizontal.add(R.drawable.image_8_256_pix);
        mItemsHorizontal.add(R.drawable.image_9_256_pix);
        mItemsHorizontal.add(R.drawable.image_10_256_pix);
        mItemsHorizontal.add(R.drawable.image_11_256_pix);
        mItemsHorizontal.add(R.drawable.image_12_256_pix);
        mItemsHorizontal.add(R.drawable.image_13_256_pix);
        mItemsHorizontal.add(R.drawable.image_14_256_pix);
        mItemsHorizontal.add(R.drawable.image_15_256_pix);
        mItemsHorizontal.add(R.drawable.image_16_256_pix);
        mItemsHorizontal.add(R.drawable.image_17_256_pix);
        mItemsHorizontal.add(R.drawable.image_18_256_pix);
        mItemsHorizontal.add(R.drawable.image_19_256_pix);
        mItemsHorizontal.add(R.drawable.image_20_256_pix);
        mItemsHorizontal.add(R.drawable.image_21_256_pix);
        mItemsHorizontal.add(R.drawable.image_22_256_pix);
        mItemsHorizontal.add(R.drawable.image_23_256_pix);
        mItemsHorizontal.add(R.drawable.image_24_256_pix);
        mItemsHorizontal.add(R.drawable.image_25_256_pix);
        mItemsHorizontal.add(R.drawable.image_26_256_pix);
        mItemsHorizontal.add(R.drawable.image_27_256_pix);
        mItemsHorizontal.add(R.drawable.image_28_256_pix);
        mItemsHorizontal.add(R.drawable.image_29_256_pix);
        mItemsHorizontal.add(R.drawable.image_30_256_pix);
        mItemsHorizontal.add(R.drawable.image_31_256_pix);
        mItemsHorizontal.add(R.drawable.image_32_256_pix);
        mItemsHorizontal.add(R.drawable.image_33_256_pix);
        mItemsHorizontal.add(R.drawable.image_34_256_pix);
        mItemsHorizontal.add(R.drawable.image_35_256_pix);
        mItemsHorizontal.add(R.drawable.image_36_256_pix);
        mItemsHorizontal.add(R.drawable.image_37_256_pix);
        mItemsHorizontal.add(R.drawable.image_38_256_pix);
    }

    private void setInitialImageVertical() {
        mItemsVertical.add("image_1.jpg");
        mItemsVertical.add("image_2.png");
        mItemsVertical.add("image_3.png");
        mItemsVertical.add("image_4.png");
        mItemsVertical.add("image_5.png");
        mItemsVertical.add("image_6.png");
        mItemsVertical.add("image_7.png");
        mItemsVertical.add("image_8.png");
        mItemsVertical.add("image_9.png");
        mItemsVertical.add("image_10.jpg");
        mItemsVertical.add("image_11.jpg");
        mItemsVertical.add("image_12.jpg");
        mItemsVertical.add("image_13.jpg");
        mItemsVertical.add("image_14.jpg");
        mItemsVertical.add("image_15.jpg");
        mItemsVertical.add("image_16.jpg");
        mItemsVertical.add("image_17.JPG");
        mItemsVertical.add("image_18.JPG");
        mItemsVertical.add("image_19.JPG");
        mItemsVertical.add("image_20.JPG");
        mItemsVertical.add("image_21.JPG");
        mItemsVertical.add("image_22.JPG");
        mItemsVertical.add("image_23.JPG");
        mItemsVertical.add("image_24.JPG");
        mItemsVertical.add("image_25.JPG");
        mItemsVertical.add("image_26.JPG");
        mItemsVertical.add("image_27.jpg");
        mItemsVertical.add("image_28.jpg");
        mItemsVertical.add("image_29.jpg");
        mItemsVertical.add("image_30.jpg");
        mItemsVertical.add("image_31.jpg");
        mItemsVertical.add("image_32.jpg");
        mItemsVertical.add("image_33.jpg");
        mItemsVertical.add("image_34.jpg");
        mItemsVertical.add("image_35.jpg");
        mItemsVertical.add("image_36.jpg");
        mItemsVertical.add("image_37.jpg");
        mItemsVertical.add("image_38.png");
    }
}
