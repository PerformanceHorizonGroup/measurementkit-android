package com.performancehorizon.measurementkit;

import junit.framework.Assert;

import org.junit.Test;

/**
 * Created by owainbrown on 02/05/2017.
 */

public class TestMeasurementServiceConfiguration {

    @Test
    public void testToString() {

        MeasurementServiceConfiguration config = new MeasurementServiceConfiguration();

        config.setDebugLogActive(true);
        config.setDebugMode(true);
        config.setTrackAndroidAdvertisingIdentifier(true);
        config.setUseActiveFingerprinting(true);

        Assert.assertEquals("Configuration:  [ debug urls true, track AAID true, active fingerprinting true, Debug log active true ]", config.toString());

    }

    @Test
    public void testToStringDebugLog() {

        MeasurementServiceConfiguration config = new MeasurementServiceConfiguration();

        config.setDebugLogActive(false);
        config.setDebugMode(true);
        config.setTrackAndroidAdvertisingIdentifier(true);
        config.setUseActiveFingerprinting(true);

        Assert.assertTrue(config.toString().contains("Debug log active false"));
    }

    @Test
    public void testToStringDebugMode() {

        MeasurementServiceConfiguration config = new MeasurementServiceConfiguration();

        config.setDebugLogActive(true);
        config.setDebugMode(false);
        config.setTrackAndroidAdvertisingIdentifier(true);
        config.setUseActiveFingerprinting(true);

        Assert.assertTrue(config.toString().contains("debug urls false"));
    }

    @Test
    public void testToStringAAID() {

        MeasurementServiceConfiguration config = new MeasurementServiceConfiguration();

        config.setDebugLogActive(true);
        config.setDebugMode(true);
        config.setTrackAndroidAdvertisingIdentifier(false);
        config.setUseActiveFingerprinting(true);

        Assert.assertTrue(config.toString().contains("track AAID false"));
    }

    @Test
    public void testToStringActiveFingerprinting() {

        MeasurementServiceConfiguration config = new MeasurementServiceConfiguration();

        config.setDebugLogActive(true);
        config.setDebugMode(true);
        config.setTrackAndroidAdvertisingIdentifier(true);
        config.setUseActiveFingerprinting(false);

        Assert.assertTrue(config.toString().contains("active fingerprinting false"));
    }
}
