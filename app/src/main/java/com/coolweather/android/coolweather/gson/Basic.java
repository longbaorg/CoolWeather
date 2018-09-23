package com.coolweather.android.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Basic {
    @SerializedName("city")
    public String cityName;

    @SerializedName("id")
    public String weatherTd;

    public Update mUpdate;

    public class Update {
        @SerializedName("loc")
        public String updateTime;
    }

}
