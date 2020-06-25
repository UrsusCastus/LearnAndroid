package com.example.application.task_13_14_weather;

public class TomorrowDataStructure {
    private String mTimesOfDay;
    private String mTemperature;

    public TomorrowDataStructure(String timesOfDay, String temperature) {
        mTimesOfDay = timesOfDay;
        mTemperature = temperature;
    }

    public String getTimesOfDay() {
        return mTimesOfDay;
    }

    public String getTemperature() {
        return mTemperature;
    }
}
