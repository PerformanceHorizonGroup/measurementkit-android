package com.performancehorizon.measurementkit;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.util.HashMap;

/**
 * Created by owainbrown on 22/12/2016.
 */

public class TestRegisterRequestJsonBuilder_WithAAID {

    @Test
    public void testRequestBuildWithNoAAIDTracking() throws Exception {
        Context testcontext = InstrumentationRegistry.getContext();

        HashMap<String, String> fingerprint = new HashMap<>();

        RegisterRequest request = new RegisterRequest(testcontext, false);

        request.setCampaignID("bob");
        request.setAdvertiserID("bob");
        request.setFingerprint(fingerprint);

        RegisterRequestJSONBuilder builder = new RegisterRequestJSONBuilder();
        JSONObject registration = builder.setRequest(request).build();

        Assert.assertNull(registration.optString("aaid", null));
    }


    @Test
    public void testRequestBuildWithAAIDEnabled() throws Exception
    {
        Context testcontext = InstrumentationRegistry.getContext();

        //what is the advertising id (tricky to mock out)
        AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(testcontext);

        HashMap<String, String> fingerprint = new HashMap<>();

        RegisterRequest request = new RegisterRequest(testcontext);

        request.setCampaignID("bob");
        request.setAdvertiserID("bob");
        request.setFingerprint(fingerprint);

        RegisterRequestJSONBuilder builder = new RegisterRequestJSONBuilder();
        JSONObject registration = builder.setRequest(request).build();

        Assert.assertEquals(registration.getString("aaid"), info.getId());
    }
}
