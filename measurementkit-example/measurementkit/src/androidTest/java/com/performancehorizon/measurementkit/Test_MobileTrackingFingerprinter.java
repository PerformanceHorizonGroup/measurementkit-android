package com.performancehorizon.measurementkit;

import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.performancehorizon.measurementkit.Fingerprinter;

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

        Fingerprinter fingerprinter = new Fingerprinter(null);

        Map<String, Object> fingerprint = fingerprinter.generateFingerprint();

        Assert.assertSame(fingerprint.size(), 5);
    }

    @SmallTest
    public void testFingerprint() {

        Fingerprinter fingerprinter = new Fingerprinter(getInstrumentation().getTargetContext());

        Map<String, Object> fingerprint = fingerprinter.generateFingerprint();
    }
}
