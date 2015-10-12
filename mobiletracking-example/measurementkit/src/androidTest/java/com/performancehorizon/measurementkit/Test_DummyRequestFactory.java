package com.performancehorizon.measurementkit;


import android.test.InstrumentationTestCase;

import com.squareup.okhttp.OkHttpClient;

import org.junit.Before;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by owainbrown on 13/03/15.
 */

public class Test_DummyRequestFactory extends InstrumentationTestCase {

    @Before
    public void setUp() {
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
    }

    public void testMockedSucesss() throws IOException
    {
        com.squareup.okhttp.Request trackingrequest = new com.squareup.okhttp.Request.Builder()
                .url(new URL("http://www.google.com"))
                .get()
                .build();

        String result = "HELLO";
        DummyTrackingRequestFactory fakerequestfactory = new DummyTrackingRequestFactory();
        fakerequestfactory.pushResult(result);

        assert(fakerequestfactory.getRequest("no", "problem").execute(new OkHttpClient()).equals(result));
    }

    public void testMockedFailure() throws MalformedURLException
    {
        String result = "HELLO";
        DummyTrackingRequestFactory fakerequestfactory = new DummyTrackingRequestFactory();
        fakerequestfactory.pushError();

        try {
            TrackingRequest request = fakerequestfactory.getRequest("no", "problem");
            String requestresult = request.execute(new OkHttpClient());

            assert(requestresult.equals(result));
            fail("Shouldn't reach here.");
        }
        catch (Exception exception) {
            assertNotNull(exception);
        }
    }

    public void testSetDefaultRequestFactory() {

        DummyTrackingRequestFactory fakerequestfactory = new DummyTrackingRequestFactory();

        DummyTrackingRequestFactory.setDefaultRequestFactory(fakerequestfactory);

        assert(DummyTrackingRequestFactory.getDefaultRequestFactory().equals(fakerequestfactory));
    }
}
