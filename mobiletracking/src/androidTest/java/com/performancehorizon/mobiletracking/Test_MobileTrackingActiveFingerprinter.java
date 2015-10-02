package com.performancehorizon.mobiletracking;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;

import org.junit.Before;

import java.util.Map;

import static org.mockito.Mockito.*;

/**
 * Created by owainbrown on 26/03/15.
 */
public class Test_MobileTrackingActiveFingerprinter extends InstrumentationTestCase {

    @Before
    public void setUp() {
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
    }

    public void testActiveFingerprinter() {

        //when explicit class naming goes wrong....
        MobileTrackingActiveFingerprinter.MobileTrackingActiveFingerprinterCallback callback=
                mock(MobileTrackingActiveFingerprinter.MobileTrackingActiveFingerprinterCallback.class);

        MobileTrackingActiveFingerprinter fingerprinter = new MobileTrackingActiveFingerprinter(getInstrumentation().getTargetContext(), callback);
        fingerprinter.generateFingerprint();

        verify(callback, timeout(1000)).activeFingerprintComplete(eq(fingerprinter), any(Map.class));
    }

}
