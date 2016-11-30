package com.performancehorizon.measurementkit;

import android.net.Uri;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;


/**
 * Created by owainbrown on 05/02/16.
 */

@RunWith(AndroidJUnit4.class)
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
