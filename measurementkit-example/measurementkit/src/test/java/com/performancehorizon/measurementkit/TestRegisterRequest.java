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
}
