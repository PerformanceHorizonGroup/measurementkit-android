package com.performancehorizon.measurementkit;

import android.content.Intent;
import android.net.Uri;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * Created by owainbrown on 04/02/16.
 */


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT, sdk = 21)
public class TestWebIntentProcessor {

    @Test
    public void testIntentNoData()
    {
        Intent intent = new Intent(Intent.ACTION_MAIN);

        WebClickIntentProccessor webprocessor = new WebClickIntentProccessor(intent);

        Assert.assertNull(webprocessor.getMobileTrackingID());
        Assert.assertNull(webprocessor.getFilteredIntent());
    }

    @Test
    public void testIntentNotViewed() throws Exception
    {
        Intent intent = new Intent(Intent.ACTION_MAIN, Uri.parse("http://www.google.com/"));

        WebClickIntentProccessor webprocessor = new WebClickIntentProccessor(intent);

        Assert.assertNull(webprocessor.getMobileTrackingID());
        Assert.assertNull(webprocessor.getFilteredIntent());
    }

    @Test
    public void testIntentNoMobileTrackingID() throws Exception
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com/"));

        WebClickIntentProccessor webprocessor = new WebClickIntentProccessor(intent);

        Assert.assertNull(webprocessor.getMobileTrackingID());
        Assert.assertNull(webprocessor.getFilteredIntent());
    }

    @Test
    public void testIntentWithMobileTrackingID() throws Exception
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com?phn_mtid=trackingid"));

        WebClickIntentProccessor webprocessor = new WebClickIntentProccessor(intent);

        Assert.assertEquals(webprocessor.getMobileTrackingID(), "trackingid");
        Assert.assertEquals(webprocessor.getFilteredIntent(), new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com")));
    }

    @Test
    public void testIntentWithMobileTrackingIDAndExtraQueryParams() throws Exception
    {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com?phn_mtid=trackingid&other=query"));

        WebClickIntentProccessor webprocessor = new WebClickIntentProccessor(intent);

        Assert.assertEquals(webprocessor.getMobileTrackingID(), "trackingid");
        Assert.assertEquals(webprocessor.getFilteredIntent(), new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.google.com?other=query")));
    }
}
