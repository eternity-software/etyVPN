/*
 * Copyright (c) 2012-2024 eternity software
 * Distributed under the GNU GPL v2 with additional terms. For full terms see the file doc/LICENSE.txt
 */

package ru.oig.etyvpn.models;

import android.util.Log;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FetchData {

    private static final String TAG = "FetchData";

    public static AppData fetchData() {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url("https://etysoft.ru/vpn/config.json").build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                String jsonData = response.body().string();
                return new Gson().fromJson(jsonData, AppData.class);
            } else {
                Log.e(TAG, "Error response code: " + response.code());
            }
        } catch (IOException e) {
            Log.e(TAG, "Error fetching data from URL", e);
        }

        return null;
    }

    public static String getConfig(String url)
    {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try {
            Response response = client.newCall(request).execute();

            if (response.isSuccessful()) {
                return response.body().string();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
}