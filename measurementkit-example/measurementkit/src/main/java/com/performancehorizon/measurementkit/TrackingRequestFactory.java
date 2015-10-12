package com.performancehorizon.measurementkit;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

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
        return new TrackingRequest(url, RequestBody.create(JSON, jsonObject.toString()));
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
