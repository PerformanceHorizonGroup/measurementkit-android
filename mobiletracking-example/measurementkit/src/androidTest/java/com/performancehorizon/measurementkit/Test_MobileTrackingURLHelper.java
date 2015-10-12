package com.performancehorizon.measurementkit;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.performancehorizon.measurementkit.TrackingURLHelper;

/**
 * Created by owainbrown on 25/03/15.
 */
public class Test_MobileTrackingURLHelper extends InstrumentationTestCase{

    @SmallTest
    public void testTheObvious()
    {
        TrackingURLHelper helper = new TrackingURLHelper();

        helper.setDebug(true);

        assert(helper.urlStringForTracking().equals("http://api.local/mobile"));
    }

    @SmallTest
    public void testTheEquallyObvious()
    {
        TrackingURLHelper helper = new TrackingURLHelper();

        helper.setDebug(false);

        assert(helper.urlStringForTracking().equals("https://mobile.prf.hn"));
    }
}
