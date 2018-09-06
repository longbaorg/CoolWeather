package com.coolweather.android.coolweather.db;

import org.litepal.crud.DataSupport;

public class County extends DataSupport {
    private int id;
    private String countyName;
    private String weatherid;
    private int cityid;

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getCountyName() {
        return countyName;
    }

    public void setCountyName(String countyName) {
        this.countyName = countyName;
    }

    public void setWeatherid(String weatherid) {
        this.weatherid = weatherid;
    }

    public String getWeatherid() {
        return weatherid;
    }

    public void setCityid(int cityid) {
        this.cityid = cityid;
    }

    public int getCityid() {
        return cityid;
    }
}
