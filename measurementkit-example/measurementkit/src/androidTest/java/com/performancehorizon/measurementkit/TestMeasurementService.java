package com.performancehorizon.measurementkit;

/**
 * Created by owainbrown on 11/02/16.
 */

import android.support.test.runner.AndroidJUnit4;
import android.support.test.InstrumentationRegistry;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Mockito.mock;

/**
 * miscellaneous tests for measurement service
 */

@RunWith(AndroidJUnit4.class)
public class TestMeasurementService {

    private MeasurementService service;

    MeasurementServiceConfiguration configuration;
    RegisterRequestQueue registerQueue;
    EventRequestQueue eventQueue;

    @Before
    public void init() {

        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());

        this.configuration = mock(MeasurementServiceConfiguration.class);
        this.registerQueue = mock(RegisterRequestQueue.class);
        this.eventQueue = mock(EventRequestQueue.class);
    }

    @Test
    public void testGetTrackingIDWithNoStorage()
    {
        service = new MeasurementService(configuration, registerQueue, eventQueue, null);

        Assert.assertNull(service.getTrackingID());
    }

}
