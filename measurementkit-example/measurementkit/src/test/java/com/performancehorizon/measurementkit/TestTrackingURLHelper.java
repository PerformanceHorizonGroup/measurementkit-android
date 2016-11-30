package com.performancehorizon.measurementkit;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by owainbrown on 07/01/16.
 */
public class TestTrackingURLHelper {


    @Test
    public void testTrackingURLStringDebug()
    {
        TrackingURLHelper helper = new TrackingURLHelper(true);

        Assert.assertEquals(helper.urlStringForTracking(), "http://m.prf.local");
    }

    @Test
    public void testTrackingURLStringLive()
    {
        TrackingURLHelper helper = new TrackingURLHelper(false);

        Assert.assertEquals(helper.urlStringForTracking(), "https://m.prf.hn");
    }
}
