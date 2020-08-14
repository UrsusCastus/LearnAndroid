package com.example.application.task_15;

import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.application.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import imageviewerwebapi.ImageData;
import imageviewerwebapi.ItemPositionListener;
import imageviewerwebapi.LargeViewerWebApiAdapter;
import imageviewerwebapi.SmallViewerWebApiAdapter;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.Utils;

import static imageviewerwebapi.LargeViewerWebApiAdapter.NUMBER_OF_ITEM_TO_DISPLAY;
import static imageviewerwebapi.LargeViewerWebApiAdapter.SPAN_COUNT_ONE_WEB_API;
import static imageviewerwebapi.LargeViewerWebApiAdapter.SPAN_COUNT_THREE_WEB_API;

public class ImageViewerWebApiActivity extends AppCompatActivity implements ItemPositionListener {
    private final String tagImageViewerWebApi = "TagImageViewerWebApi";
    private final String saveKeyColumnCount = "columnCount";
    private final String saveKeyListData = "mListOfData";
    private final int columnCount = 1;

    private LargeViewerWebApiAdapter mLargeViewerWebApiAdapter;
    private RecyclerView mVerticalRecyclerView;
    private GridLayoutManager mGridLayoutManagerWebApi;
    private ArrayList<ImageData> mListOfData = new ArrayList<ImageData>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_api_activity_image_viewer);
        //? метод вызывается дважды
        initializationVerticalGridLayoutManager(columnCount);
        if (!Utils.Companion.isNetworkConnected(getApplicationContext())) {
            showAlertDialogConnection();
        } else if (savedInstanceState != null) {
            int savedColumnCount = savedInstanceState.getInt(saveKeyColumnCount);
            //?
            initializationVerticalGridLayoutManager(savedColumnCount);
            mListOfData = savedInstanceState.getParcelableArrayList(saveKeyListData);
            initialHorizontalRecyclerView(mListOfData);
            initialVerticalRecyclerView(mListOfData, mGridLayoutManagerWebApi);
        } else {
            Observable
                    .fromCallable(() -> {
                        final String apiLinkString = "http://xn--d1acpjx3f.xn--80adxhks/api/list/";
                        OkHttpClient okHttpClient = new OkHttpClient();
                        Request request = new Request.Builder()
                                .url(apiLinkString)
                                .build();
                        Response response = okHttpClient.newCall(request).execute();
                        String responseData = response.body().string();
                        return new JSONArray(responseData);
                    })
                    .subscribeOn(Schedulers.single())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(
                            jsonArray -> {
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    mListOfData.add(new ImageData(jsonObject.getString("url"),
                                            jsonObject.getString("title")));
                                }
                                initialHorizontalRecyclerView(mListOfData);
                                initialVerticalRecyclerView(mListOfData, mGridLayoutManagerWebApi);
                            },
                            error -> {
                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                                error.printStackTrace();
                            }
                    );
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(saveKeyColumnCount, mGridLayoutManagerWebApi.getSpanCount());
        outState.putParcelableArrayList(saveKeyListData, mListOfData);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_viewer, menu);
        if (mGridLayoutManagerWebApi.getSpanCount() == SPAN_COUNT_ONE_WEB_API) {
            menu.findItem(R.id.menu_switch_layout).setIcon(R.drawable.ic_icon_list);
        } else {
            menu.findItem(R.id.menu_switch_layout).setIcon(R.drawable.ic_icon_grid);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_switch_layout) {
            if (mGridLayoutManagerWebApi.getSpanCount() == SPAN_COUNT_ONE_WEB_API) {
                mGridLayoutManagerWebApi.setSpanCount(SPAN_COUNT_THREE_WEB_API);
                item.setIcon(R.drawable.ic_icon_grid);
            } else {
                mGridLayoutManagerWebApi.setSpanCount(SPAN_COUNT_ONE_WEB_API);
                item.setIcon(R.drawable.ic_icon_list);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mListOfData = null;
        if (mVerticalRecyclerView != null) {
            mVerticalRecyclerView.setAdapter(null);
        }
    }

    private void initializationVerticalGridLayoutManager(int columnCount) {
        if (Utils.Companion.isLandScape(getApplicationContext())) {
            mGridLayoutManagerWebApi = new GridLayoutManager(this,
                    columnCount, GridLayoutManager.HORIZONTAL, false);
        } else {
            mGridLayoutManagerWebApi = new GridLayoutManager(this,
                    columnCount, GridLayoutManager.VERTICAL, false);
        }
    }

    private void initialVerticalRecyclerView(ArrayList<ImageData> listData, GridLayoutManager gridLayoutManager) {
        mVerticalRecyclerView = findViewById(R.id.activity_image_viewer_web_api_rv_large_viewer);
        mLargeViewerWebApiAdapter = new LargeViewerWebApiAdapter(this, listData, gridLayoutManager);
        mVerticalRecyclerView.setAdapter(mLargeViewerWebApiAdapter);
        mVerticalRecyclerView.setLayoutManager(mGridLayoutManagerWebApi);
    }

    private void initialHorizontalRecyclerView(ArrayList<ImageData> listData) {
        RecyclerView horizontalRecyclerView = findViewById(R.id.activity_image_viewer_web_api_rv_small_viewer);
        if (Utils.Companion.isLandScape(getApplicationContext())) {
            horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false));
        } else {
            horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false));
        }
        SmallViewerWebApiAdapter smallViewerWebApiAdapter = new SmallViewerWebApiAdapter(listData);
        //callback initialization
        smallViewerWebApiAdapter.setSelectPositionListener(this);
        horizontalRecyclerView.setAdapter(smallViewerWebApiAdapter);
    }

    private void showAlertDialogConnection() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("You are offline");
        alertDialogBuilder.setMessage("Check your connection and try again");
        alertDialogBuilder.setNegativeButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                finish();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onItemClicked(int position) {
        mGridLayoutManagerWebApi.scrollToPositionWithOffset(position, getOffsetItem());
    }

    private int getOffsetItem() {
        //offset in px
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return mVerticalRecyclerView.getHeight() / NUMBER_OF_ITEM_TO_DISPLAY;
        }
        return mVerticalRecyclerView.getWidth() / NUMBER_OF_ITEM_TO_DISPLAY;
    }
}
