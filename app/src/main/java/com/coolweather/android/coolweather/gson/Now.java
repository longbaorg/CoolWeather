package com.coolweather.android.coolweather.gson;

import com.google.gson.annotations.SerializedName;

public class Now {
    @SerializedName("tmp")
    public String temperature;

    @SerializedName("cond")
    public More mMore;
    public class More{
        @SerializedName("txt")
        public String info;
    }
}
