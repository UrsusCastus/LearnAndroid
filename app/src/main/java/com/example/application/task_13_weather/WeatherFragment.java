package com.example.application.task_13_weather;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.application.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

public class WeatherFragment extends Fragment implements SelectCityDialogFragment.toPassData {
    public static final String TAG_SAVE_WEATHER_FRAGMENT = "TagSaveWeatherFragment";
    private static final String LOG_WEATHER_FRAGMENT = "LogWeatherFragment";

    private static final String API_KEY = "6c8582d03d8c5dea07f358f4683f208c";
    private static final String API_LINK = "https://api.openweathermap.org/data/2.5/weather";

    private HashMap<String, Integer> mMapIdCities = new HashMap<String, Integer>(4);

    private TextView mCityTextView;
    private TextView mTemperatureTextView;
    private TextView mDescriptionTextView;

    private Button mButtonSelectCity;

    private String mCity;
    private String mTemperature;
    private String mDescription;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        mMapIdCities.put("Moscow", 524901);
        mMapIdCities.put("Saint Petersburg", 498817);
        mMapIdCities.put("Sochi", 491422);
        mMapIdCities.put("Ulyanovsk", 479123);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_weather, container, false);
        mCityTextView = rootView.findViewById(R.id.activity_weather_city);
        mTemperatureTextView = rootView.findViewById(R.id.activity_weather_temperature_text);
        mDescriptionTextView = rootView.findViewById(R.id.activity_weather_description);
        mButtonSelectCity = rootView.findViewById(R.id.activity_weather_button_select_city);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (savedInstanceState != null) {
            mCityTextView.setText(savedInstanceState.getString("CurrentCity"));
            mTemperatureTextView.setText(savedInstanceState.getString("Temperature"));
            mDescriptionTextView.setText(savedInstanceState.getString("Description"));
        } else {
            mCityTextView.setText(R.string.select_city);
            mTemperatureTextView.setText(R.string.temperature);
            mDescriptionTextView.setText(R.string.description);
        }
        mButtonSelectCity.setOnClickListener(v -> {
            FragmentManager fragmentManager = getFragmentManager();
            //SelectCityDialogFragment extends DialogFragment
            SelectCityDialogFragment selectCityDialogFragment = new SelectCityDialogFragment();
            selectCityDialogFragment.setCancelable(false);
            FragmentTransaction transaction = fragmentManager.beginTransaction();
            selectCityDialogFragment.show(transaction, "SelectCity");

            selectCityDialogFragment.setTargetFragment(WeatherFragment.this, 1);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CurrentCity", mCityTextView.getText().toString());
        outState.putString("Temperature", mTemperatureTextView.getText().toString());
        outState.putString("Description", mDescriptionTextView.getText().toString());
    }

    private static String apiRequestString(Integer cityID) {
        StringBuilder stringBuilder = new StringBuilder(API_LINK);
        stringBuilder.append(String.format("?id=%s&appid=%s&units=metric", cityID, API_KEY));
        return stringBuilder.toString();
    }

    private String getHTTPData(String urlString) {
        String httpData = null;
        try {
            URL url = new URL(urlString);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            //время ожидания соединения - 5 с
            httpURLConnection.setConnectTimeout(5_000);
            //время ожидания для считывания данных - 10 с
            httpURLConnection.setReadTimeout(10_000);
            //запуск сетевого подключения
            httpURLConnection.connect();

            //code "200" - OK
            if (httpURLConnection.getResponseCode() == 200) {
                Log.d(LOG_WEATHER_FRAGMENT, String.valueOf(httpURLConnection.getResponseCode()));
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                StringBuffer stringBuffer = new StringBuffer(1024);
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuffer.append(line).append('\n');
                }
                httpData = stringBuffer.toString();
                bufferedReader.close();
                httpURLConnection.disconnect();
            } else {
                Log.e(LOG_WEATHER_FRAGMENT, String.valueOf(httpURLConnection.getResponseCode()));
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            Log.e(LOG_WEATHER_FRAGMENT, "IOException");
            e.printStackTrace();
        }
        return httpData;
    }

    private void getWeatherData(Integer cityID) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String data = getHTTPData(apiRequestString(cityID));
                    if (data != null) {
                        JSONObject dataWeather = new JSONObject(data);

                        JSONObject main = dataWeather.getJSONObject("main");
                        double temperature = main.getDouble("temp");

                        JSONArray weatherArray = dataWeather.getJSONArray("weather");
                        JSONObject weatherObject = weatherArray.getJSONObject(0);

                        mDescription = weatherObject.getString("description");
                        mTemperature = Math.round(temperature) + " °C";
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mCityTextView.post(() -> mCityTextView.setText(mCity));
                mTemperatureTextView.post(() -> mTemperatureTextView.setText(String.valueOf(mTemperature)));
                mDescriptionTextView.post(() -> mDescriptionTextView.setText(mDescription));
            }
        };
        Thread thread = new Thread(runnable);
        Log.d(LOG_WEATHER_FRAGMENT, thread.getName());
        thread.start();
    }

    @Override
    public void getSelectedCity(String selectCity) {
        mCity = selectCity;
        Integer selectedCityID = mMapIdCities.get(selectCity);
        getWeatherData(selectedCityID);
    }
}
