package com.coolweather.android.coolweather.util;

import okhttp3.OkHttpClient;
import okhttp3.Request;

public class HttpUtil {
    public static void sendOkHttpRequest(String address , okhttp3.Callback callback){
        OkHttpClient mOkHttpClient= new OkHttpClient();
        Request mRequest= new Request.Builder().url(address).build();
        mOkHttpClient.newCall(mRequest).enqueue(callback);
    }
}
