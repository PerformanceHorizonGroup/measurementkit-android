package com.performancehorizon.mobiletracking;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.RequestBody;

import org.json.JSONObject;

/**
 * Created by owainbrown on 13/03/15.
 */
public class MobileTrackingRequestFactory {

    private static MobileTrackingRequestFactory defaultRequestFactory;

    public MobileTrackingRequest getRequest(String url, RequestBody postBody) {
        return new MobileTrackingRequest(url, postBody);
    }

    public MobileTrackingRequest getRequest(String url, JSONObject jsonObject) {

        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        return new MobileTrackingRequest(url, RequestBody.create(JSON, jsonObject.toString()));
    }

    public static void setDefaultRequestFactory(MobileTrackingRequestFactory factory)
    {
        defaultRequestFactory = factory;
    }

    public static MobileTrackingRequestFactory getDefaultRequestFactory()
    {
        return defaultRequestFactory;
    }
}
