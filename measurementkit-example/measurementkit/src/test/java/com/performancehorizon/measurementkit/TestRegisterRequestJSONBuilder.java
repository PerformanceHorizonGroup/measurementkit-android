package com.performancehorizon.measurementkit;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.HashMap;

/**
 * Created by owainbrown on 26/01/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT,sdk = 21)
public class TestRegisterRequestJSONBuilder {


    @Test
    public void testInvalidRequestNoAdvertiserID() {

        HashMap<String, String> fingerprint = new HashMap<>();

        RegisterRequest request = new RegisterRequest(null);
        request.setFingerprint(fingerprint);
        request.setCampaignID("bob");

        RegisterRequestJSONBuilder builder = new RegisterRequestJSONBuilder();
        Assert.assertNull(builder.setRequest(request).build());
    }

    @Test
    public void testInvalidRequestNoCampaignID() {

        HashMap<String, String> fingerprint = new HashMap<>();

        RegisterRequest request = new RegisterRequest(null);
        request.setFingerprint(fingerprint);
        request.setAdvertiserID("bob");

        RegisterRequestJSONBuilder builder = new RegisterRequestJSONBuilder();
        Assert.assertNull(builder.setRequest(request).build());
    }

    @Test
    public void testRequestNoFingerprint() {

        HashMap<String, String> fingerprint = new HashMap<>();

        RegisterRequest request = new RegisterRequest(null);
        request.setCampaignID("bob");
        request.setAdvertiserID("bob");

        RegisterRequestJSONBuilder builder = new RegisterRequestJSONBuilder();
        Assert.assertNull(builder.setRequest(request).build());
    }

    @Test
    public void testRequestBuild() throws Exception
    {
        HashMap<String, String> fingerprint = new HashMap<>();

        RegisterRequest request = new RegisterRequest(null);
        request.setCampaignID("bob");
        request.setAdvertiserID("bob");
        request.setFingerprint(fingerprint);

        RegisterRequestJSONBuilder builder = new RegisterRequestJSONBuilder();
        JSONObject registration = builder.setRequest(request).build();

        Assert.assertEquals(registration.getString("campaign_id"), "bob");
        Assert.assertEquals(registration.get("advertiser_id"), "bob");
        Assert.assertTrue(registration.has("fingerprint"));
    }

}
