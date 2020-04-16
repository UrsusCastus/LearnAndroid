package com.example.application;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.application.task_7.CustomSpinnerAdapter;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MapActivity extends AppCompatActivity implements
        GoogleMap.OnCameraMoveStartedListener,
        OnMapReadyCallback {

    private static final String TAG = "MapActivityTag";
    private final String STATE_KEY_MAP_CAMERA = "MapActivitySaveStateCamera";
    private static final int LOCATION_PERMISSIONS_REQUEST_CODE = 101;
    private GoogleMap mGoogleMap;
    //класс LocationManager обеспечивает доступ к системам определения местоположения
    private LocationManager mLocationManager;
    private Location mLocation;
    private SupportMapFragment mSupportMapFragment;
    private Map<String, LatLng> mListOfCityAndCoordinates = new HashMap<String, LatLng>();
    private Spinner spCities;
    private List<String> mListOfCity;

    //callback onMapReady вызывается, когда карта готова к использованию
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "Map is ready. CallBack onMapReady is called");
        mGoogleMap = googleMap;

        Set<Map.Entry<String, LatLng>> pairsOfCityAndCoordinate = mListOfCityAndCoordinates.entrySet();

        for (Map.Entry<String, LatLng> pair : pairsOfCityAndCoordinate) {
            mGoogleMap.addMarker(new MarkerOptions()
                    .position(pair.getValue())
                    .icon(bitmapDescriptorFromVector(this, R.drawable.ic_location_marker))
                    .title(pair.getKey()));
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //предоставлено ли конкретное разрешение?
            //на доступ к конкретному местоположению
            //PackageManager.PERMISSION_GRANTED - результат проверки разрешения - разрешение предоставлено
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.d(TAG, "Map is ready. Version SDK" + Build.VERSION.SDK_INT + " Permission granted");
                //отображение стандартной кнопки определения местоположения и отображение текущего положения
                mGoogleMap.setMyLocationEnabled(true);
                //скрыть стандартную кнопку определения местоположения, текущее положение отображается
                mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            }
        } else {
            //если API < 23 (не проверял)
            //отображение стандартной кнопки определения местоположения и отображение текущего положения
            mGoogleMap.setMyLocationEnabled(true);
            //скрыть стандартную кнопку определения местоположения, текущее положение отображается
            mGoogleMap.getUiSettings().setMyLocationButtonEnabled(false);
            //подключаются стандартные кнопки zoom
            mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        }
        //подключаются стандартные кнопки zoom
        mGoogleMap.getUiSettings().setZoomControlsEnabled(true);
        //отслеживание позиции камеры
        mGoogleMap.setOnCameraMoveStartedListener(this);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        getSupportActionBar().hide();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Log.d(TAG, "1. Callback onCreate is called");
        //получение доступа к службе определения местоположения
        mLocationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (savedInstanceState != null) {
            CameraUpdate cameraUpdatePosition = CameraUpdateFactory.newCameraPosition(
                    (CameraPosition) (savedInstanceState.getParcelable(STATE_KEY_MAP_CAMERA)));
        }

        clickEventItemOfSpinner();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Log.d(TAG, "2. checkLocationPermission is called");
            checkLocationPermission();
        } else {
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                showGPSDisabledAlertToUser();
            }
        }

        mSupportMapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mSupportMapFragment.getMapAsync(this);

        Button buttonLocation = (Button) findViewById(R.id.view_location);
        buttonLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    //метод оповещения, что GPS отключен, предоставляется возможность включить GPS
                    showGPSDisabledAlertToUser();
                }
                getMyLocation();
                spCities.setSelection(mListOfCity.size());
            }
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        if (mGoogleMap != null) {
            outState.putParcelable(STATE_KEY_MAP_CAMERA, mGoogleMap.getCameraPosition());
        }
    }

    private BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorResourceId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorResourceId);
        vectorDrawable.setBounds(0, 0, vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(),
                vectorDrawable.getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    private void clickEventItemOfSpinner() {
        mListOfCityAndCoordinates.put("Moscow",           new LatLng(55.755773, 37.617761));
        mListOfCityAndCoordinates.put("Saint Petersburg", new LatLng(59.938806, 30.314278));
        mListOfCityAndCoordinates.put("Sochi",            new LatLng(43.581509, 39.722882));
        mListOfCityAndCoordinates.put("Ulyanovsk",        new LatLng(54.317002, 48.402243));

        mListOfCity = new ArrayList<String>(mListOfCityAndCoordinates.keySet());
        mListOfCity.add(mListOfCity.size(), "Unknown location");

        spCities = (Spinner) findViewById(R.id.spinner_of_city);

        final ArrayAdapter<String> mAdapterForArrayOfCity = new ArrayAdapter<String>(this,
                R.layout.custom_spinner_main, mListOfCity);

        mAdapterForArrayOfCity.setDropDownViewResource(R.layout.custom_spinner_dropdown);

        spCities.setAdapter(new CustomSpinnerAdapter(
                mAdapterForArrayOfCity, R.layout.custom_spinner_main, this));

        //не реагировать на дефолтное значение в спиннере
        spCities.setSelection(Adapter.NO_SELECTION, false);

        spCities.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == mListOfCity.size()) {
                    Toast.makeText(getApplicationContext(), "Select city, please.", Toast.LENGTH_SHORT).show();
                } else {
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(mListOfCityAndCoordinates
                            .get((String) parent.getItemAtPosition(position)), 10);
                    mGoogleMap.animateCamera(cameraUpdate);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void getMyLocation() {
        mLocationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        //ACCESS_COARSE_LOCATION - доступ к приблизительному местоположению (через Wi-Wi, мобильную сеть)
        //ACCESS_FINE_LOCATION - доступ к точному местоположению, местоположение в реальном времени (GPS + Wi-Fi, мобильная сеть)
        //если доступ не предоставлен, то...
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            Log.d(TAG, "ACCESS OF FINE LOCATION NOT GRANTED (getMyLocation)");
            return;
        }
        //getLastKnownLocation - получает последнее известное местоположение или null, если его нет
        mLocation = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (mLocation != null) {
            double longitude = mLocation.getLongitude();
            double latitude = mLocation.getLatitude();
            LatLng latLng = new LatLng(latitude, longitude);
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
            mGoogleMap.animateCamera(cameraUpdate);
        } else {
            //тост появляется при вызове метода showGPSDisabledAlertToUser в onCreate
            Toast.makeText(getApplicationContext(), "Wait...", Toast.LENGTH_SHORT).show();
        }
    }

    //проверка разрешения на получение местоположения
    public boolean checkLocationPermission() {
        //если разрешение не предоставлено...
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "ACCESS OF FINE LOCATION NOT GRANTED");
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSIONS_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this,
                        new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSIONS_REQUEST_CODE);
            }
            return false;
        } else {
            //если нет разрешения на доступ к GPS... (GPS отключен)
            if (!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                //метод оповещения, что GPS отключен
                //предоставляется возможность включить GPS
                showGPSDisabledAlertToUser();
            }
            return true;
        }
    }

    //метод для включения GPS, если тот отключен
    private void showGPSDisabledAlertToUser() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("GPS is disabled in your device. Would you like to enable it?")
                .setCancelable(false)
                .setPositiveButton("Settings", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivity(callGPSSettingIntent);
                        mSupportMapFragment.getMapAsync(MapActivity.this);
                    }
                });
        alertDialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                dialog.cancel();
            }
        });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onCameraMoveStarted(int reason) {
        if (reason == GoogleMap.OnCameraMoveStartedListener.REASON_GESTURE) {
            spCities.setSelection(mListOfCity.size());
        }
    }
}
