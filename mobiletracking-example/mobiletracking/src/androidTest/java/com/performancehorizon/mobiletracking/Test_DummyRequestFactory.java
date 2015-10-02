package com.performancehorizon.mobiletracking;


import android.test.InstrumentationTestCase;

import com.squareup.okhttp.Request;
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
        Request trackingrequest = new Request.Builder()
                .url(new URL("http://www.google.com"))
                .get()
                .build();

        String result = "HELLO";
        DummyMobileTrackingRequestFactory fakerequestfactory = new DummyMobileTrackingRequestFactory();
        fakerequestfactory.pushResult(result);

        assert(fakerequestfactory.getRequest("no", "problem").execute(new OkHttpClient()).equals(result));
    }

    public void testMockedFailure() throws MalformedURLException
    {
        String result = "HELLO";
        DummyMobileTrackingRequestFactory fakerequestfactory = new DummyMobileTrackingRequestFactory();
        fakerequestfactory.pushError();

        try {
            MobileTrackingRequest request = fakerequestfactory.getRequest("no", "problem");
            String requestresult = request.execute(new OkHttpClient());

            assert(requestresult.equals(result));
            fail("Shouldn't reach here.");
        }
        catch (Exception exception) {
            assertNotNull(exception);
        }
    }

    public void testSetDefaultRequestFactory() {

        DummyMobileTrackingRequestFactory fakerequestfactory = new DummyMobileTrackingRequestFactory();

        MobileTrackingRequestFactory.setDefaultRequestFactory(fakerequestfactory);

        assert(MobileTrackingRequestFactory.getDefaultRequestFactory().equals(fakerequestfactory));
    }
}
