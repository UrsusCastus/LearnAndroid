package com.example.application;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.application.task_2.AdapterForSmallViewer;
import com.example.application.task_2.AdapterForLargeViewer;

import java.util.ArrayList;

import static com.example.application.task_2.AdapterForLargeViewer.SPAN_COUNT_ONE;
import static com.example.application.task_2.AdapterForLargeViewer.SPAN_COUNT_THREE;

public class ImageViewerActivity extends AppCompatActivity {
    private final String COUNT_COLUMNS = "saveCountColumns";

    ArrayList<Integer> mItemsSmallViewer = new ArrayList<Integer>(38);
    ArrayList<String> mItemsLargeViewer = new ArrayList<String>(38);

    private GridLayoutManager mGridLayoutManager;
    private static AdapterForLargeViewer sAdapterForLargeViewer;

    private RecyclerView recyclerLargeViewer;

    private int mColumnCount = 1;

    //модификатор static для видимости в AdapterForHorizontalViewer
    public static AdapterForLargeViewer getAdapterForLargeViewer() {
        return sAdapterForLargeViewer;
    }

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        setInitialArrayListSmallViewer();
        initialRecyclerSmallViewer();

        setInitialArrayListLargeViewer();

        if (savedInstanceState != null) {
            mColumnCount = savedInstanceState.getInt(COUNT_COLUMNS);
        }

        initialRecyclerLargeViewer();
    }

    private void initialRecyclerLargeViewer() {
        if (getScreenOrientation().equals("PortraitOrientation")) {
            mGridLayoutManager = new GridLayoutManager(this, mColumnCount, GridLayoutManager.VERTICAL, false);
        } else {
            mGridLayoutManager = new GridLayoutManager(this, mColumnCount, GridLayoutManager.HORIZONTAL, false);
        }
        sAdapterForLargeViewer = new AdapterForLargeViewer(this, mItemsLargeViewer, mGridLayoutManager);
        recyclerLargeViewer = (RecyclerView) findViewById(R.id.rv_large_viewer);
        recyclerLargeViewer.setAdapter(sAdapterForLargeViewer);
        recyclerLargeViewer.setLayoutManager(mGridLayoutManager);
    }

    private void initialRecyclerSmallViewer() {
        RecyclerView recyclerSmallViewer = (RecyclerView) findViewById(R.id.rv_small_viewer);
        if (getScreenOrientation().equals("LandscapeOrientation")) {
            recyclerSmallViewer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        } else {
            recyclerSmallViewer.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        }
        AdapterForSmallViewer adapterForSmallViewer = new AdapterForSmallViewer(this, mItemsSmallViewer);
        recyclerSmallViewer.setAdapter(adapterForSmallViewer);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(COUNT_COLUMNS, mGridLayoutManager.getSpanCount());
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_viewer, menu);
        if (mGridLayoutManager.getSpanCount() == SPAN_COUNT_ONE) {
            menu.findItem(R.id.menu_switch_layout).setIcon(R.drawable.ic_icon_list);
        } else {
            menu.findItem(R.id.menu_switch_layout).setIcon(R.drawable.ic_icon_grid);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_switch_layout) {
            if (mGridLayoutManager.getSpanCount() == SPAN_COUNT_ONE) {
                mGridLayoutManager.setSpanCount(SPAN_COUNT_THREE);
                item.setIcon(R.drawable.ic_icon_grid);
            } else {
                mGridLayoutManager.setSpanCount(SPAN_COUNT_ONE);
                item.setIcon(R.drawable.ic_icon_list);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setInitialArrayListSmallViewer() {
        mItemsSmallViewer.add(R.drawable.image_1_256_pix);
        mItemsSmallViewer.add(R.drawable.image_2_256_pix);
        mItemsSmallViewer.add(R.drawable.image_3_256_pix);
        mItemsSmallViewer.add(R.drawable.image_4_256_pix);
        mItemsSmallViewer.add(R.drawable.image_5_256_pix);
        mItemsSmallViewer.add(R.drawable.image_6_256_pix);
        mItemsSmallViewer.add(R.drawable.image_7_256_pix);
        mItemsSmallViewer.add(R.drawable.image_8_256_pix);
        mItemsSmallViewer.add(R.drawable.image_9_256_pix);
        mItemsSmallViewer.add(R.drawable.image_10_256_pix);
        mItemsSmallViewer.add(R.drawable.image_11_256_pix);
        mItemsSmallViewer.add(R.drawable.image_12_256_pix);
        mItemsSmallViewer.add(R.drawable.image_13_256_pix);
        mItemsSmallViewer.add(R.drawable.image_14_256_pix);
        mItemsSmallViewer.add(R.drawable.image_15_256_pix);
        mItemsSmallViewer.add(R.drawable.image_16_256_pix);
        mItemsSmallViewer.add(R.drawable.image_17_256_pix);
        mItemsSmallViewer.add(R.drawable.image_18_256_pix);
        mItemsSmallViewer.add(R.drawable.image_19_256_pix);
        mItemsSmallViewer.add(R.drawable.image_20_256_pix);
        mItemsSmallViewer.add(R.drawable.image_21_256_pix);
        mItemsSmallViewer.add(R.drawable.image_22_256_pix);
        mItemsSmallViewer.add(R.drawable.image_23_256_pix);
        mItemsSmallViewer.add(R.drawable.image_24_256_pix);
        mItemsSmallViewer.add(R.drawable.image_25_256_pix);
        mItemsSmallViewer.add(R.drawable.image_26_256_pix);
        mItemsSmallViewer.add(R.drawable.image_27_256_pix);
        mItemsSmallViewer.add(R.drawable.image_28_256_pix);
        mItemsSmallViewer.add(R.drawable.image_29_256_pix);
        mItemsSmallViewer.add(R.drawable.image_30_256_pix);
        mItemsSmallViewer.add(R.drawable.image_31_256_pix);
        mItemsSmallViewer.add(R.drawable.image_32_256_pix);
        mItemsSmallViewer.add(R.drawable.image_33_256_pix);
        mItemsSmallViewer.add(R.drawable.image_34_256_pix);
        mItemsSmallViewer.add(R.drawable.image_35_256_pix);
        mItemsSmallViewer.add(R.drawable.image_36_256_pix);
        mItemsSmallViewer.add(R.drawable.image_37_256_pix);
        mItemsSmallViewer.add(R.drawable.image_38_256_pix);
    }

    private void setInitialArrayListLargeViewer() {
        mItemsLargeViewer.add("image_1.jpg");
        mItemsLargeViewer.add("image_2.png");
        mItemsLargeViewer.add("image_3.png");
        mItemsLargeViewer.add("image_4.png");
        mItemsLargeViewer.add("image_5.png");
        mItemsLargeViewer.add("image_6.png");
        mItemsLargeViewer.add("image_7.png");
        mItemsLargeViewer.add("image_8.png");
        mItemsLargeViewer.add("image_9.png");
        mItemsLargeViewer.add("image_10.jpg");
        mItemsLargeViewer.add("image_11.jpg");
        mItemsLargeViewer.add("image_12.jpg");
        mItemsLargeViewer.add("image_13.jpg");
        mItemsLargeViewer.add("image_14.jpg");
        mItemsLargeViewer.add("image_15.jpg");
        mItemsLargeViewer.add("image_16.jpg");
        mItemsLargeViewer.add("image_17.JPG");
        mItemsLargeViewer.add("image_18.JPG");
        mItemsLargeViewer.add("image_19.JPG");
        mItemsLargeViewer.add("image_20.JPG");
        mItemsLargeViewer.add("image_21.JPG");
        mItemsLargeViewer.add("image_22.JPG");
        mItemsLargeViewer.add("image_23.JPG");
        mItemsLargeViewer.add("image_24.JPG");
        mItemsLargeViewer.add("image_25.JPG");
        mItemsLargeViewer.add("image_26.JPG");
        mItemsLargeViewer.add("image_27.jpg");
        mItemsLargeViewer.add("image_28.jpg");
        mItemsLargeViewer.add("image_29.jpg");
        mItemsLargeViewer.add("image_30.jpg");
        mItemsLargeViewer.add("image_31.jpg");
        mItemsLargeViewer.add("image_32.jpg");
        mItemsLargeViewer.add("image_33.jpg");
        mItemsLargeViewer.add("image_34.jpg");
        mItemsLargeViewer.add("image_35.jpg");
        mItemsLargeViewer.add("image_36.jpg");
        mItemsLargeViewer.add("image_37.jpg");
        mItemsLargeViewer.add("image_38.png");
    }

    private String getScreenOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return "PortraitOrientation";
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return "LandscapeOrientation";
        } else return "";
    }
}
