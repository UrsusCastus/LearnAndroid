package com.example.application.task_13_weather;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.application.R;
import com.google.android.gms.maps.model.LatLng;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class WeatherFragment extends Fragment {
    public static final String TAG_SAVE_WEATHER_FRAGMENT = "TagSaveWeatherFragment";
    private static final String TAG_DIALOG_FRAGMENT = "TagSelectCityDialogFragment";
    private static final String LOG_WEATHER_FRAGMENT = "LogWeatherFragment";
    private static final String API_LINK_CURRENT_WEATHER = "https://api.openweathermap.org/data/2.5/weather";
    private static final String API_LINK_FORECASTS_WEATHER = "https://api.openweathermap.org/data/2.5/onecall";
    private static final String EXCLUDED_PARTS_ONE_CALL = "current,minutely";
    private static final String TIME_FORMAT = "HH:mm";
    private static final String DATE_FORMAT = "d.MM.yyyy";

    private static String sApiKey;

    private Context mContext;
    private Map<String, LatLng> mMapCoordinatesOfCites = new HashMap<String, LatLng>(4);
    private TextView mCityTextView;
    private TextView mTemperatureTextView;
    private TextView mDescriptionTextView;
    private Button mButtonSelectCity;
    private HorizontalScrollView mHorizontalScrollView;
    private TextView mDateTodayTextView;
    private TextView mDateTomorrowTextView;
    private int mIndexCheckedItem = -1;
    private String mCity;
    private String mTemperature;
    private String mDescription;
    private Handler mHandler;
    private LinkedHashMap<String, String> mMapOfHourlyDataToday = new LinkedHashMap<String, String>();
    private LinkedHashMap<String, String> mMapOfDailyDataTomorrow = new LinkedHashMap<String, String>();
    private GridLayout mGridLayoutDataToday;
    private GridLayout mGridLayoutDataTomorrow;
    private StringBuilder mStringBuilderDateToday = new StringBuilder();
    private StringBuilder mStringBuilderDateTomorrow = new StringBuilder();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mContext = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        Log.e(LOG_WEATHER_FRAGMENT, "onCreate");
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        sApiKey = getResources().getString(R.string.openWeatherMapKey);
        mHandler = new Handler();

        mMapCoordinatesOfCites.put("Moscow", new LatLng(55.755773, 37.617761));
        mMapCoordinatesOfCites.put("Saint Petersburg", new LatLng(59.938806, 30.314278));
        mMapCoordinatesOfCites.put("Sochi", new LatLng(43.581509, 39.722882));
        mMapCoordinatesOfCites.put("Ulyanovsk", new LatLng(54.317002, 48.402243));
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
        mHorizontalScrollView = rootView.findViewById(R.id.horizontal_scroll_view_for_hourly_data);
        mDateTodayTextView = rootView.findViewById(R.id.activity_weather_date_today);
        mDateTomorrowTextView = rootView.findViewById(R.id.activity_weather_date_tomorrow);
        mGridLayoutDataTomorrow = rootView.findViewById(R.id.activity_weather_data_tomorrow_grid);
        return rootView;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (savedInstanceState != null) {
            mCityTextView.setText(savedInstanceState.getString("CurrentCity"));
            mTemperatureTextView.setText(savedInstanceState.getString("Temperature"));
            mDescriptionTextView.setText(savedInstanceState.getString("Description"));

            mDateTodayTextView.setText(savedInstanceState.getString("currentDateString"));
            mDateTomorrowTextView.setText(savedInstanceState.getString("tomorrowDateString"));

            showHourlyDataToday();
            showDailyDataTomorrow();
        } else {
            mCityTextView.setText(R.string.select_city);
            mTemperatureTextView.setText(R.string.temperature);
            mDescriptionTextView.setText(R.string.description);
        }
        mButtonSelectCity.setOnClickListener(v -> {
            SelectCityDialogFragment selectCityDialogFragment = new SelectCityDialogFragment();
            selectCityDialogFragment.setSelectCityDialogListener(new SelectCityDialogListener() {
                @Override
                public void onCitySelected(String selectedCity, int indexItem) {
                    getWeatherData(selectedCity, indexItem);
                }
            });
            Bundle bundle = new Bundle();
            bundle.putInt("mIndexCheckedItem", mIndexCheckedItem);
            selectCityDialogFragment.setArguments(bundle);
            selectCityDialogFragment.setCancelable(false);
            selectCityDialogFragment.show(getFragmentManager(), TAG_DIALOG_FRAGMENT);
        });
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("CurrentCity", mCityTextView.getText().toString());
        outState.putString("Temperature", mTemperatureTextView.getText().toString());
        outState.putString("Description", mDescriptionTextView.getText().toString());

        String currentDateString = (String) mDateTodayTextView.getText();
        String tomorrowDateString = (String) mDateTomorrowTextView.getText();
        outState.putString("currentDateString", currentDateString);
        outState.putString("tomorrowDateString", tomorrowDateString);
    }

    @Override
    public void onResume() {
        Log.e(LOG_WEATHER_FRAGMENT, "onResume");
        super.onResume();
        SelectCityDialogFragment selectCityDialogFragment = (SelectCityDialogFragment) getFragmentManager()
                .findFragmentByTag(TAG_DIALOG_FRAGMENT);
        if (selectCityDialogFragment != null) {
            selectCityDialogFragment.setSelectCityDialogListener(new SelectCityDialogListener() {
                @Override
                public void onCitySelected(String selectedCity, int indexItem) {
                    getWeatherData(selectedCity, indexItem);
                }
            });
        }
    }

    private void getWeatherData(String selectedCity, int indexItem) {
        mCity = selectedCity;
        LatLng selectedCityLatLng = mMapCoordinatesOfCites.get(selectedCity);
        getCurrentWeatherData(selectedCityLatLng);
        mIndexCheckedItem = indexItem;
        getForecastsWeatherData(mCity);
    }

    private static String apiRequestStringCurrentWeather(LatLng latLng) {
        StringBuilder stringBuilder = new StringBuilder(API_LINK_CURRENT_WEATHER);
        stringBuilder.append(String.format("?lat=%s&lon=%s&appid=%s&units=metric",
                latLng.latitude, latLng.longitude, sApiKey));
        return stringBuilder.toString();
    }

    private String getHTTPDataCurrentWeather(String urlString) {
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
                String responseCode = String.valueOf(httpURLConnection.getResponseCode());
                Log.e(LOG_WEATHER_FRAGMENT, responseCode);
                Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                return null;
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(LOG_WEATHER_FRAGMENT, "IOException");
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }
        return httpData;
    }

    private void getCurrentWeatherData(LatLng latLng) {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    String data = getHTTPDataCurrentWeather(apiRequestStringCurrentWeather(latLng));
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

                mHandler.post(() -> {
                    mCityTextView.setText(mCity);
                    mTemperatureTextView.setText(String.valueOf(mTemperature));
                    mDescriptionTextView.setText(mDescription);
                });
            }
        };
        Thread thread = new Thread(runnable);
        Log.d(LOG_WEATHER_FRAGMENT, thread.getName());
        thread.start();
    }

    private String apiRequestStringForForecastsWeather(String selectedCity) {
        LatLng currentLatLng = mMapCoordinatesOfCites.get(selectedCity);
        StringBuilder stringBuilder = new StringBuilder(API_LINK_FORECASTS_WEATHER);
        stringBuilder.append(String.format("?lat=%s&lon=%s&exclude=%s&appid=%s&units=metric",
                currentLatLng.latitude, currentLatLng.longitude, EXCLUDED_PARTS_ONE_CALL, sApiKey));
        return stringBuilder.toString();
    }

    private void getForecastsWeatherData(String selectedCity) {
        String urlString = apiRequestStringForForecastsWeather(selectedCity);
        OkHttpClient okHttpClient = new OkHttpClient();
        Request request = new Request.Builder()
                .url(urlString)
                .build();
        //асинхронный запрос. Методы new Callback выполняются в отдельном потоке
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), e.toString(), Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                Log.d(LOG_WEATHER_FRAGMENT, "Thread onResponse - " + Thread.currentThread().getName());
                String ForecastsWeatherData = response.body().string();
                String dateToday = getCurrentDate();
                String dateTomorrow = getDateTomorrow();
                try {
                    JSONObject forecastsWeatherDataObject = new JSONObject(ForecastsWeatherData);

                    JSONArray oneCallDataHourlyArray = forecastsWeatherDataObject.getJSONArray("hourly");
                    //отбор данных сегодняшней даты
                    for (int i = 0; i < oneCallDataHourlyArray.length(); i++) {
                        JSONObject jsonObject = oneCallDataHourlyArray.getJSONObject(i);
                        long deltaT = jsonObject.getLong("dt");
                        String date = getTimeFromUtc(deltaT, DATE_FORMAT);
                        if (date.equals(dateToday)) {
                            String timeOfDay = getTimeFromUtc(deltaT, TIME_FORMAT);
                            double temperature = jsonObject.getDouble("temp");
                            StringBuilder stringBuilderTemperature = new StringBuilder();
                            if (temperature > 0) {
                                stringBuilderTemperature.append("+");
                            }
                            stringBuilderTemperature.append(Math.round(temperature)).append("°");
                            mMapOfHourlyDataToday.put(timeOfDay, stringBuilderTemperature.toString());
                        }
                    }

                    JSONArray oneCallDataDailyArray = forecastsWeatherDataObject.getJSONArray("daily");
                    //отбор данных завтрашней даты
                    for (int i = 0; i < oneCallDataDailyArray.length(); i++) {
                        JSONObject jsonObjectDaily = oneCallDataDailyArray.getJSONObject(i);
                        long deltaTime = jsonObjectDaily.getLong("dt");
                        String dateDaily = getTimeFromUtc(deltaTime, DATE_FORMAT);
                        if (dateDaily.equals(dateTomorrow)) {
                            JSONObject json = jsonObjectDaily.getJSONObject("temp");

                            StringBuilder stringBuilderNight = new StringBuilder();
                            StringBuilder stringBuilderMorning = new StringBuilder();
                            StringBuilder stringBuilderDay = new StringBuilder();
                            StringBuilder stringBuilderEvening = new StringBuilder();

                            double temperatureNight = json.getDouble("night");
                            double temperatureMorning = json.getDouble("morn");
                            double temperatureDay = json.getDouble("day");
                            double temperatureEvening = json.getDouble("eve");

                            if (temperatureNight > 0) {
                                stringBuilderNight.append("+");
                            }
                            if (temperatureMorning > 0) {
                                stringBuilderMorning.append("+");
                            }
                            if (temperatureDay > 0) {
                                stringBuilderDay.append("+");
                            }
                            if (temperatureEvening > 0) {
                                stringBuilderEvening.append("+");
                            }

                            stringBuilderNight.append(Math.round(temperatureNight)).append("°");
                            stringBuilderMorning.append(Math.round(temperatureMorning)).append("°");
                            stringBuilderDay.append(Math.round(temperatureDay)).append("°");
                            stringBuilderEvening.append(Math.round(temperatureEvening)).append("°");

                            mMapOfDailyDataTomorrow.put("Night", stringBuilderNight.toString());
                            mMapOfDailyDataTomorrow.put("Morning", stringBuilderMorning.toString());
                            mMapOfDailyDataTomorrow.put("Day", stringBuilderDay.toString());
                            mMapOfDailyDataTomorrow.put("Evening", stringBuilderEvening.toString());
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        Log.d(LOG_WEATHER_FRAGMENT, "mHandler.post - " + Thread.currentThread().getName());
                        mStringBuilderDateToday.append(changeDateFormat(dateToday, DATE_FORMAT))
                                .append(" ").append(getResources().getString(R.string.Today));
                        mStringBuilderDateTomorrow.append(changeDateFormat(dateTomorrow, DATE_FORMAT))
                                .append(" ").append(getResources().getString(R.string.Tomorrow));
                        mDateTodayTextView.setText(mStringBuilderDateToday);
                        mDateTomorrowTextView.setText(mStringBuilderDateTomorrow);

                        //удаляю ранее созданные View после выбора города
                        mHorizontalScrollView.removeAllViews();
                        mGridLayoutDataTomorrow.removeAllViews();

                        showHourlyDataToday();
                        showDailyDataTomorrow();

                        mStringBuilderDateToday.delete(0, mStringBuilderDateToday.length());
                        mStringBuilderDateTomorrow.delete(0, mStringBuilderDateTomorrow.length());
                    }
                });
            }
        });
    }

    private String getTimeFromUtc(long timeUtc, String dateFormat) {
        Date dateUtc = new Date(timeUtc * 1000L);
        SimpleDateFormat formatUtc = new SimpleDateFormat(dateFormat, Locale.ENGLISH);
        formatUtc.setTimeZone(TimeZone.getDefault());
        String date = formatUtc.format(dateUtc);
        return date;
    }

    private String getCurrentDate() {
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        String currentDate = formatDate.format(date);
        return currentDate;
    }

    private String changeDateFormat(String date, String formatDateInput) {
        String dateChangeFormatString = null;
        DateFormat inputFormat = new SimpleDateFormat(formatDateInput, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat("d MMMM", Locale.ENGLISH);
        try {
            Date dateForChangeFormat = inputFormat.parse(date);
            dateChangeFormatString = outputFormat.format(dateForChangeFormat);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return dateChangeFormatString;
    }

    private String getDateTomorrow() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = calendar.getTime();
        SimpleDateFormat formatDate = new SimpleDateFormat(DATE_FORMAT, Locale.ENGLISH);
        String dateTomorrow = formatDate.format(date);
        return dateTomorrow;
    }

    private void showHourlyDataToday() {
        mGridLayoutDataToday = new GridLayout(mContext);
        mGridLayoutDataToday.setLayoutParams(new ViewGroup.LayoutParams
                (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        mGridLayoutDataToday.setColumnCount(mMapOfHourlyDataToday.size());
        mGridLayoutDataToday.setRowCount(2);

        mHorizontalScrollView.addView(mGridLayoutDataToday);

        int countEntry = 0;
        for (Map.Entry<String, String> entry : mMapOfHourlyDataToday.entrySet()) {
            TextView textViewTime = new TextView(mContext);
            TextView textViewTemperature = new TextView(mContext);

            textViewTime.setText(entry.getKey());
            textViewTemperature.setText(entry.getValue());

            setSettingsTextViewTime(textViewTime);
            setSettingsTextViewTemperature(textViewTemperature);

            mGridLayoutDataToday.addView(textViewTime, new GridLayout.LayoutParams
                    (GridLayout.spec(0, GridLayout.CENTER), GridLayout.spec(countEntry, GridLayout.CENTER)));
            mGridLayoutDataToday.addView(textViewTemperature, new GridLayout.LayoutParams
                    (GridLayout.spec(1, GridLayout.CENTER), GridLayout.spec(countEntry, GridLayout.CENTER)));
            countEntry++;
        }
    }

    private void showDailyDataTomorrow() {
        int countEntry = 0;
        for (Map.Entry<String, String> entry : mMapOfDailyDataTomorrow.entrySet()) {
            TextView textViewTimeDay = new TextView(mContext);
            TextView textViewTemperature = new TextView(mContext);

            textViewTimeDay.setText(entry.getKey());
            textViewTemperature.setText(entry.getValue());

            setSettingsTextViewTime(textViewTimeDay);
            setSettingsTextViewTemperature(textViewTemperature);

            mGridLayoutDataTomorrow.addView(textViewTimeDay, new GridLayout.LayoutParams
                    (GridLayout.spec(0, GridLayout.CENTER), GridLayout.spec(countEntry, GridLayout.CENTER)));
            mGridLayoutDataTomorrow.addView(textViewTemperature, new GridLayout.LayoutParams
                    (GridLayout.spec(1, GridLayout.CENTER), GridLayout.spec(countEntry, GridLayout.CENTER)));
            countEntry++;
        }
    }

    private void setSettingsTextViewTime(TextView textViewTime) {
        textViewTime.setTextSize(20);
        textViewTime.setPadding(15, 10, 15, 5);
        textViewTime.setTextColor(Color.BLACK);
    }

    private void setSettingsTextViewTemperature(TextView textViewTemperature) {
        textViewTemperature.setTextSize(25);
        textViewTemperature.setPadding(15, 10, 15, 5);
        textViewTemperature.setTextColor(Color.parseColor("#04BD63"));
        textViewTemperature.setTypeface(null, Typeface.BOLD);
    }
}
