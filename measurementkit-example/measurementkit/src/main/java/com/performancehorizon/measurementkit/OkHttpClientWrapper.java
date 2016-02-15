package com.performancehorizon.measurementkit;

import okhttp3.Call;
import okhttp3.OkHttpClient;

/**
 * Created by owainbrown on 26/01/16.
 */
public class OkHttpClientWrapper {
    private OkHttpClient client;

    public OkHttpClientWrapper(OkHttpClient client) {
        this.client = client;
    }

    public Call newCall(okhttp3.Request request) {
        return this.client.newCall(request);
    }
}
