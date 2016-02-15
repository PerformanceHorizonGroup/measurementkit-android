package com.performancehorizon.measurementkit;

import android.net.Uri;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by owainbrown on 05/02/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT, sdk = 21)
public class TestAppStoreSpotter {

    @Test
    public void testPlayStoreUsingHTTP() throws Exception {
        Assert.assertTrue(AppStoreUriSpotter.isAppStoreURI(Uri.parse("http://play.google.com/store/apps/details?id=someid")));
    }

    @Test
    public void testPlayStoreUsingScheme() throws Exception {
        Assert.assertTrue(AppStoreUriSpotter.isAppStoreURI(Uri.parse("market://details?id=com.example.android")));
    }

    @Test
    public void testAmazonStoreUsingScheme() throws Exception {
        Assert.assertTrue(AppStoreUriSpotter.isAppStoreURI(Uri.parse("amzn://apps/android?something=something")));
    }

    @Test
    public void testAmazonStoreUsingHTTP() throws Exception {
        Assert.assertTrue(AppStoreUriSpotter.isAppStoreURI(Uri.parse("http://www.amazon.com/gp/mas/dl/android?something=something")));
    }

    @Test
    public void testSamsungStore() throws Exception {
        Assert.assertTrue(AppStoreUriSpotter.isAppStoreURI(Uri.parse("samsungapps://idontactuallyknowtheformat")));
    }
}
