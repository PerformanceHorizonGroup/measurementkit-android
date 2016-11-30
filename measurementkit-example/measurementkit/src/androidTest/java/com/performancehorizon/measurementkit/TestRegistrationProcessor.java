package com.performancehorizon.measurementkit;

import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;

/**
 * Created by owainbrown on 26/01/16.
 */

@RunWith(AndroidJUnit4.class)
public class TestRegistrationProcessor {

    @Test
    public void testFailedRegistration() {

        String failedregistrationstring = "{\n" +
                "\t\"mobiletracking_id\": false\n" +
                "}";

        RegistrationProcessor registration = new RegistrationProcessor(failedregistrationstring);

        Assert.assertTrue(registration.hasRegistrationFailed());
        Assert.assertNull(registration.getTrackingID());
        Assert.assertNull(registration.getDeeplink());
        Assert.assertNull(registration.getReferrer());
    }

    @Test
    public void testSucessfulRegistration() {

        String registrationstring = "{\n" +
                "\t\"mobiletracking_id\": \"id\"\n" +
                "}";

        RegistrationProcessor registration = new RegistrationProcessor(registrationstring);

        Assert.assertFalse(registration.hasRegistrationFailed());
        Assert.assertEquals(registration.getTrackingID(), "id");
        Assert.assertNull(registration.getDeeplink());
        Assert.assertNull(registration.getReferrer());
    }

    @Test
    public void testSucessfulRegistrationWithDeepLinkAndReferrer() {

        String registrationstring = "{\n" +
                "\t\"mobiletracking_id\": \"id\",\n" +
                "\t \"deep_link\": \"http%3A%2F%2Fwww.google.com\",\n" +
                "\t \"referrer\": \"http%3A%2F%2Fwww.google.com\"\n" +
                "}";

        RegistrationProcessor registration = new RegistrationProcessor(registrationstring);

        Assert.assertFalse(registration.hasRegistrationFailed());
        Assert.assertEquals(registration.getTrackingID(), "id");
        Assert.assertEquals(registration.getDeeplink().toString(), "http://www.google.com");
        Assert.assertEquals(registration.getReferrer().toString(), "http://www.google.com");
    }

}
