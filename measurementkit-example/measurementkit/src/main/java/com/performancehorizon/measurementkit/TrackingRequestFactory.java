package com.performancehorizon.measurementkit;

import org.json.JSONObject;

import okhttp3.MediaType;
import okhttp3.RequestBody;

/**
 * Created by owainbrown on 13/03/15.
 */
public class TrackingRequestFactory {

    private static TrackingRequestFactory defaultRequestFactory;

    public TrackingRequest getRequest(String url, RequestBody postBody) {
        return new TrackingRequest(url, postBody);
    }

    public TrackingRequest getRequest(String url, JSONObject jsonObject) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        String json = jsonObject.toString();

        return new TrackingRequest(url, RequestBody.create(JSON, json));
    }

    public static void setDefaultRequestFactory(TrackingRequestFactory factory)
    {
        defaultRequestFactory = factory;
    }

    public static TrackingRequestFactory getDefaultRequestFactory()
    {
        return defaultRequestFactory;
    }
}
