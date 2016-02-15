package com.performancehorizon.measurementkit;

/**
 * Created by owainbrown on 11/02/16.
 */


import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.configuration.MockAnnotationProcessor;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

/**
 * miscellaneous tests for measurement service
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT,sdk = 21)
public class TestMeasurementService {

    private MeasurementService service;

    @Mock MeasurementServiceConfiguration configuration;
    @Mock RegisterRequestQueue registerQueue;
    @Mock EventRequestQueue eventQueue;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testGetTrackingIDWithNoStorage()
    {
        service = new MeasurementService(configuration, registerQueue, eventQueue, null);

        Assert.assertNull(service.getTrackingID());
    }

}
