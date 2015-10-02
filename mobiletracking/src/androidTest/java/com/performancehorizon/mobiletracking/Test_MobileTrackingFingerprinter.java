package com.performancehorizon.mobiletracking;

import android.animation.ObjectAnimator;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import junit.framework.Assert;

import org.junit.Before;

import java.util.Map;

/**
 * Created by owainbrown on 26/03/15.
 */
public class Test_MobileTrackingFingerprinter extends InstrumentationTestCase {


    @Before
    public void setUp() {
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
    }

    @SmallTest
    public void testFingerprintNoContext() {

        MobileTrackingFingerprinter fingerprinter = new MobileTrackingFingerprinter(null);

        Map<String, Object> fingerprint = fingerprinter.generateFingerprint();

        Assert.assertSame(fingerprint.size(), 4);
    }

    @SmallTest
    public void testFingerprint() {

        MobileTrackingFingerprinter fingerprinter = new MobileTrackingFingerprinter(getInstrumentation().getTargetContext());

        Map<String, Object> fingerprint = fingerprinter.generateFingerprint();
    }
}
