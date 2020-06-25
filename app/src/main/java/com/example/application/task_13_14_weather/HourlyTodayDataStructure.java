package com.example.application.task_13_14_weather;

public class HourlyTodayDataStructure {
    private String mHour;
    private String mTemperature;

    public HourlyTodayDataStructure(String hour, String temperature) {
        mHour = hour;
        mTemperature = temperature;
    }

    public String getHour() {
        return mHour;
    }

    public String getTemperature() {
        return mTemperature;
    }
}
