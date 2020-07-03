package com.example.application.task_15;

import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Build;
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
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static com.example.application.task_15.AdapterLargeViewerTask15.SPAN_COUNT_ONE_TASK15;
import static com.example.application.task_15.AdapterLargeViewerTask15.SPAN_COUNT_THREE_TASK15;

public class ImageViewerTask15Activity extends AppCompatActivity {
    private final String LOG_IMAGE_VIEWER_TASK15 = "LogImageViewerTask15";
    private final String COUNT_COLUMNS = "saveCountColumns";
    private final String API_LINK_STRING = "http://xn--d1acpjx3f.xn--80adxhks/api/list/";

    private static AdapterLargeViewerTask15 sAdapterForVerticalViewer;

    private GridLayoutManager mGridLayoutManagerTask15;
    private ArrayList<DataStructure> mListOfData = new ArrayList<DataStructure>();
    private int mColumnCount = 1;

    public static AdapterLargeViewerTask15 getAdapterForLargeViewer() {
        return sAdapterForVerticalViewer;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.task15_activity_image_viewer);
        if (savedInstanceState != null) {
            mColumnCount = savedInstanceState.getInt(COUNT_COLUMNS);
        }
        initializationVerticalGridLayoutManager();

        if (!isNetworkConnected(getApplicationContext())) {
            showAlertDialogConnection();
        }

        Observable
                .fromCallable(() -> new JSONArray(getResponseData()))
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        jsonArray -> {
                            saveResponseData(jsonArray);
                            initialHorizontalRecyclerView(mListOfData);
                            initialVerticalRecyclerView(mListOfData);
                        },
                        error -> {
                            Toast.makeText(getApplicationContext(), error.toString(), Toast.LENGTH_SHORT).show();
                        }
                );
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(COUNT_COLUMNS, mGridLayoutManagerTask15.getSpanCount());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_image_viewer, menu);
        if (mGridLayoutManagerTask15.getSpanCount() == SPAN_COUNT_ONE_TASK15) {
            menu.findItem(R.id.menu_switch_layout).setIcon(R.drawable.ic_icon_list);
        } else {
            menu.findItem(R.id.menu_switch_layout).setIcon(R.drawable.ic_icon_grid);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu_switch_layout) {
            if (mGridLayoutManagerTask15.getSpanCount() == SPAN_COUNT_ONE_TASK15) {
                mGridLayoutManagerTask15.setSpanCount(SPAN_COUNT_THREE_TASK15);
                item.setIcon(R.drawable.ic_icon_grid);
            } else {
                mGridLayoutManagerTask15.setSpanCount(SPAN_COUNT_ONE_TASK15);
                item.setIcon(R.drawable.ic_icon_list);
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private String getScreenOrientation() {
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            return "PortraitOrientation";
        } else if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            return "LandscapeOrientation";
        } else return "";
    }

    private void initializationVerticalGridLayoutManager() {
        if (getScreenOrientation().equals("PortraitOrientation")) {
            mGridLayoutManagerTask15 = new GridLayoutManager(this,
                    mColumnCount, GridLayoutManager.VERTICAL, false);
        } else {
            mGridLayoutManagerTask15 = new GridLayoutManager(this,
                    mColumnCount, GridLayoutManager.HORIZONTAL, false);
        }
    }

    private void initialVerticalRecyclerView(ArrayList<DataStructure> listData) {
        RecyclerView verticalRecyclerView = findViewById(R.id.activity_image_viewer_task15_rv_large_viewer);
        sAdapterForVerticalViewer = new AdapterLargeViewerTask15(this,
                listData, mGridLayoutManagerTask15);
        verticalRecyclerView.setAdapter(sAdapterForVerticalViewer);
        verticalRecyclerView.setLayoutManager(mGridLayoutManagerTask15);
    }

    private void initialHorizontalRecyclerView(ArrayList<DataStructure> listData) {
        RecyclerView horizontalRecyclerView = findViewById(R.id.activity_image_viewer_task15_rv_small_viewer);
        if (getScreenOrientation().equals("LandscapeOrientation")) {
            horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.VERTICAL, false));
        } else {
            horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(this,
                    LinearLayoutManager.HORIZONTAL, false));
        }
        AdapterSmallViewerTask15 adapterForHorizontalViewer = new AdapterSmallViewerTask15(listData);
        horizontalRecyclerView.setAdapter(adapterForHorizontalViewer);
    }

    private boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            if (Build.VERSION.SDK_INT < 23) {
                //deprecated
                final NetworkInfo networkInfo = connectivityManager
                        .getActiveNetworkInfo();
                if (networkInfo != null) {
                    if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI ||
                            networkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
                        return true;
                    }
                }
            } else {
                final Network network = connectivityManager.getActiveNetwork();
                if (network != null) {
                    NetworkCapabilities networkCapabilities = connectivityManager
                            .getNetworkCapabilities(network);
                    if (networkCapabilities != null) {
                        if (networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) ||
                                networkCapabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
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

    private String getResponseData() {
        String responseData = null;
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(API_LINK_STRING)
                .build();
        try {
            Response response = okHttpClient.newCall(request).execute();
            responseData = response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseData;
    }

    private void saveResponseData(JSONArray jsonArray) {
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = null;
            try {
                jsonObject = jsonArray.getJSONObject(i);
                String url = jsonObject.getString("url");
                String title = jsonObject.getString("title");
                mListOfData.add(new DataStructure(url, title));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
