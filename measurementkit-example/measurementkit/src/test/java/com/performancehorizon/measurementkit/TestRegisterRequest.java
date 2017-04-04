package com.performancehorizon.measurementkit;

import junit.framework.Assert;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by owainbrown on 26/01/16.
 */

//Pretty much just a POJO, the only interesting method we'll do in an AndroidTest
public class TestRegisterRequest {

    //test all the basic pojo getters/setters
    @Test
    public void testBasics() {

        RegisterRequest request = new RegisterRequest(null);
        Map<String, String> fingerprint = new HashMap<>();

        request.setCamref("bob");
        request.setAdvertiserID("bob");
        request.setReferrer("bob");
        request.setFingerprint(fingerprint);

        Assert.assertEquals("bob", request.getCamref());
        Assert.assertEquals("bob", request.getAdvertiserID());
        Assert.assertEquals("bob", request.getReferrer());
        Assert.assertEquals(fingerprint, request.getFingerprint());
    }

    //test of equality
    @Test
    public void testEquality() {
        RegisterRequest requesta = new RegisterRequest(null);
        RegisterRequest requestb = new RegisterRequest(null);
        RegisterRequest requestc = new RegisterRequest(null);

        requesta.setCampaignID("jeff");
        requesta.setCamref("bob");
        requesta.setAdvertiserID("steve");
        requesta.setReferrer("james");

        requestb.setCampaignID("jeff");
        requestb.setCamref("bob");
        requestb.setAdvertiserID("steve");
        requestb.setReferrer("james");

        requestc.setCampaignID("rick");
        requestc.setCamref("nick");
        requestc.setAdvertiserID("tom");
        requestc.setReferrer("jens");

        Assert.assertTrue(requesta.equals(requestb));
        Assert.assertFalse(requesta.equals(requestc));
    }

    @Test
    public void testNotEqualsCamref() {
        RegisterRequest requesta = new RegisterRequest(null);
        RegisterRequest requestb = new RegisterRequest(null);

        requesta.setCamref("bob");
        requesta.setAdvertiserID("steve");
        requesta.setReferrer("james");
        requesta.setCampaignID("jeff");

        requestb.setCamref("alex");
        requestb.setAdvertiserID("steve");
        requestb.setReferrer("james");
        requestb.setCampaignID("jeff");

        Assert.assertFalse(requesta.equals(requestb));
    }

    @Test
    public void testNotEqualsAdvertiserID() {
        RegisterRequest requesta = new RegisterRequest(null);
        RegisterRequest requestb = new RegisterRequest(null);

        requesta.setCamref("bob");
        requesta.setAdvertiserID("steve");
        requesta.setReferrer("james");
        requesta.setCampaignID("jeff");

        requestb.setCamref("bob");
        requestb.setAdvertiserID("alex");
        requestb.setReferrer("james");
        requestb.setCampaignID("jeff");

        Assert.assertFalse(requesta.equals(requestb));
    }

    @Test
    public void testNotEqualsReferrer() {
        RegisterRequest requesta = new RegisterRequest(null);
        RegisterRequest requestb = new RegisterRequest(null);

        requesta.setCamref("bob");
        requesta.setAdvertiserID("steve");
        requesta.setReferrer("alex");
        requesta.setCampaignID("jeff");

        requestb.setCamref("bob");
        requestb.setAdvertiserID("steve");
        requestb.setReferrer("james");
        requestb.setCampaignID("jeff");

        Assert.assertFalse(requesta.equals(requestb));
    }

    @Test
    public void testNotEqualsCampaignID() {
        RegisterRequest requesta = new RegisterRequest(null);
        RegisterRequest requestb = new RegisterRequest(null);

        requesta.setCamref("bob");
        requesta.setAdvertiserID("steve");
        requesta.setReferrer("alex");
        requesta.setCampaignID("jeff");

        requestb.setCamref("bob");
        requestb.setAdvertiserID("steve");
        requestb.setReferrer("alex");
        requestb.setCampaignID("jake");

        Assert.assertFalse(requesta.equals(requestb));
    }

    @Test
    public void testNotEqualsAAID() {
        RegisterRequest requesta = new RegisterRequest(null);
        RegisterRequest requestb = new RegisterRequest(null);

        requesta.setCamref("bob");
        requesta.setAdvertiserID("steve");
        requesta.setReferrer("alex");
        requesta.setCampaignID("jeff");

        requestb.setCamref("bob");
        requestb.setAdvertiserID("steve");
        requestb.setReferrer("alex");
        requestb.setCampaignID("jake");

        Assert.assertFalse(requesta.equals(requestb));
    }


    //test of hashcode
    @Test
    public void testHashCode() {
        RegisterRequest requesta = new RegisterRequest(null);
        RegisterRequest requestb = new RegisterRequest(null);
        RegisterRequest requestc = new RegisterRequest(null);

        requesta.setCamref("bob");
        requesta.setAdvertiserID("steve");
        requesta.setReferrer("james");
        requesta.setCampaignID("campaign");

        requestb.setCamref("bob");
        requestb.setAdvertiserID("steve");
        requestb.setReferrer("james");
        requestb.setCampaignID("campaign");

        requestc.setCamref("nick");
        requestc.setAdvertiserID("tom");
        requestc.setReferrer("jens");
        requestc.setReferrer("othercampaign");

        Assert.assertTrue(requesta.hashCode() == requestb.hashCode());
        Assert.assertFalse(requesta.hashCode() == requestc.hashCode());
    }
}
