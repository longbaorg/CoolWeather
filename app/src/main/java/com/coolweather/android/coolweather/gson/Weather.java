package com.coolweather.android.coolweather.gson;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather {
    public String status;
    public Basic Basic;
    public Forecast Forecast;
    public AQI AQI;
    public Now Now;

    @SerializedName("daily_forecast")
    public List<Forecast> Forecasts;
}
