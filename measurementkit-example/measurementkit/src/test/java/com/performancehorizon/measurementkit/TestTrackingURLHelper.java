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

        assert(helper.urlStringForTracking().equals("http://m.prf.local"));
    }

    @Test
    public void testTrackingURLStringLive()
    {
        TrackingURLHelper helper = new TrackingURLHelper(false);

        assert(helper.urlStringForTracking().equals("https://m.prf.hn"));
    }
}
