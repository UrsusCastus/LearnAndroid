package com.example.applistofimages;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.applistofimages.task_2.AdapterForLargeViewer;
import com.example.applistofimages.task_2.AdapterForSmallViewer;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.util.ArrayList;

public class ListOfImagesActivity extends AppCompatActivity {
    private final String COUNT_COLUMNS = "saveCountColumns";

    ArrayList<Integer> mItemsSmallViewer = new ArrayList<Integer>(38);
    ArrayList<String> mItemsLargeViewer = new ArrayList<String>(38);
    ArrayList<Uri> mItemsFromGallery = new ArrayList<Uri>();

    private GridLayoutManager mGridLayoutManager;
    private static AdapterForLargeViewer sAdapterForLargeViewer;

    private RecyclerView recyclerLargeViewer;

    private int mColumnCount = 1;

    //уникальный код запроса
    private final int REQUEST_CODE = 1;

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

        if (savedInstanceState != null) {
            mColumnCount = savedInstanceState.getInt(COUNT_COLUMNS);
            mItemsFromGallery = savedInstanceState.getParcelableArrayList("ArrayOfUri");
        }

        setInitialArrayListSmallViewer();
        initialRecyclerSmallViewer();

        setInitialArrayListLargeViewer();
        initialRecyclerLargeViewer();

        FloatingActionButton floatingActionButtonAdd = (FloatingActionButton) findViewById(R.id.floatingActionButtonAdd);
        floatingActionButtonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getImageFromGallery();
            }
        });
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
        startActivityForResult(imageChooseIntent, REQUEST_CODE);
    }

    //получение результата от активити, результат возвращается в качестве интента
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //проверка на соответствие кода запроса
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageURI = data.getData();
            mItemsFromGallery.add(selectedImageURI);
            sAdapterForLargeViewer.notifyDataSetChanged();
        }
    }

    private void initialRecyclerLargeViewer() {
        if (getScreenOrientation().equals("PortraitOrientation")) {
            mGridLayoutManager = new GridLayoutManager(this, mColumnCount, GridLayoutManager.VERTICAL, false);
        } else {
            mGridLayoutManager = new GridLayoutManager(this, mColumnCount, GridLayoutManager.HORIZONTAL, false);
        }
        sAdapterForLargeViewer = new AdapterForLargeViewer(this, mItemsLargeViewer, mGridLayoutManager, mItemsFromGallery);
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
        AdapterForSmallViewer adapterForSmallViewer = new AdapterForSmallViewer(this, mItemsSmallViewer, mItemsFromGallery);
        recyclerSmallViewer.setAdapter(adapterForSmallViewer);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(COUNT_COLUMNS, mGridLayoutManager.getSpanCount());
        outState.putParcelableArrayList("ArrayOfUri", mItemsFromGallery);
    }

    @SuppressLint("ResourceType")
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_viewer, menu);
        if (mGridLayoutManager.getSpanCount() == AdapterForLargeViewer.SPAN_COUNT_ONE) {
            menu.findItem(R.id.menu_switch_layout).setIcon(R.drawable.ic_icon_list);
        } else {
            menu.findItem(R.id.menu_switch_layout).setIcon(R.drawable.ic_icon_grid);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_switch_layout) {
            if (mGridLayoutManager.getSpanCount() == AdapterForLargeViewer.SPAN_COUNT_ONE) {
                mGridLayoutManager.setSpanCount(AdapterForLargeViewer.SPAN_COUNT_THREE);
                item.setIcon(R.drawable.ic_icon_grid);
            } else {
                mGridLayoutManager.setSpanCount(AdapterForLargeViewer.SPAN_COUNT_ONE);
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
