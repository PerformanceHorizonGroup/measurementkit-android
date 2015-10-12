package com.performancehorizon.measurementkit;

import android.test.InstrumentationTestCase;


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
        ActiveFingerprinter.MobileTrackingActiveFingerprinterCallback callback=
                mock(ActiveFingerprinter.MobileTrackingActiveFingerprinterCallback.class);

        ActiveFingerprinter fingerprinter = new ActiveFingerprinter(getInstrumentation().getTargetContext(), callback);
        fingerprinter.generateFingerprint();

        verify(callback, timeout(1000)).activeFingerprintComplete(eq(fingerprinter), any(Map.class));
    }

}
