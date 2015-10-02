package com.performancehorizon.mobiletracking;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

/**
 * Created by owainbrown on 25/03/15.
 */
public class Test_MobileTrackingURLHelper extends InstrumentationTestCase{

    @SmallTest
    public void testTheObvious()
    {
        MobileTrackingURLHelper helper = new MobileTrackingURLHelper();

        helper.setDebug(true);

        assert(helper.urlStringForTracking().equals("http://api.local/mobile"));
    }

    @SmallTest
    public void testTheEquallyObvious()
    {
        MobileTrackingURLHelper helper = new MobileTrackingURLHelper();

        helper.setDebug(false);

        assert(helper.urlStringForTracking().equals("https://mobile.prf.hn"));
    }
}
