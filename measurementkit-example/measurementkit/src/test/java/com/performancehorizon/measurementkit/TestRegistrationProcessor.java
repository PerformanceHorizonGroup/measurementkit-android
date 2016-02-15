package com.performancehorizon.measurementkit;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by owainbrown on 26/01/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT,sdk = 21)
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
