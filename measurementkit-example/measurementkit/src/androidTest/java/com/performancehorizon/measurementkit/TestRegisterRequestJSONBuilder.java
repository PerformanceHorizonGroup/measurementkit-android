package com.performancehorizon.measurementkit;

import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;

/**
 * Created by owainbrown on 26/01/16.
 */

@RunWith(AndroidJUnit4.class)
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

        //all the other values should not be present
        Assert.assertNull(registration.opt("install"));
        Assert.assertNull(registration.optString("camref", null));
        Assert.assertNull(registration.optString("google_playstore_referrer", null));
        Assert.assertNull(registration.optString("aaid", null));
    }

    @Test
    public void testRequestBuildWithInstall() throws Exception
    {
        HashMap<String, String> fingerprint = new HashMap<>();

        RegisterRequest request = new RegisterRequest(null);
        request.setCampaignID("bob");
        request.setAdvertiserID("bob");
        request.setFingerprint(fingerprint);
        request.setInstalled();

        RegisterRequestJSONBuilder builder = new RegisterRequestJSONBuilder();
        JSONObject registration = builder.setRequest(request).build();

        Assert.assertEquals(registration.getString("campaign_id"), "bob");
        Assert.assertEquals(registration.get("advertiser_id"), "bob");
        Assert.assertTrue(registration.has("fingerprint"));
        Assert.assertTrue(registration.getBoolean("install"));
    }

    @Test
    public void testRequestBuildWithCamref() throws Exception
    {
        HashMap<String, String> fingerprint = new HashMap<>();

        RegisterRequest request = new RegisterRequest(null);
        request.setCampaignID("bob");
        request.setAdvertiserID("bob");
        request.setFingerprint(fingerprint);
        request.setCamref("camref");

        RegisterRequestJSONBuilder builder = new RegisterRequestJSONBuilder();
        JSONObject registration = builder.setRequest(request).build();

        Assert.assertEquals(registration.getString("campaign_id"), "bob");
        Assert.assertEquals(registration.get("advertiser_id"), "bob");
        Assert.assertTrue(registration.has("fingerprint"));

        Assert.assertEquals(registration.getString("camref"), "camref");
    }

    @Test
    public void testRequestBuildWithReferrer() throws Exception
    {
        HashMap<String, String> fingerprint = new HashMap<>();

        RegisterRequest request = new RegisterRequest(null);
        request.setCampaignID("bob");
        request.setAdvertiserID("bob");
        request.setFingerprint(fingerprint);
        request.setReferrer("agoogleplayreferrer");

        RegisterRequestJSONBuilder builder = new RegisterRequestJSONBuilder();
        JSONObject registration = builder.setRequest(request).build();

        Assert.assertEquals(registration.getString("google_playstore_referrer"), "agoogleplayreferrer");

        System.out.print(registration.toString());
    }


}
