package com.performancehorizon.measurementkit;

import android.content.Intent;
import android.net.Uri;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.net.URLEncoder;


/**
 * Created by owainbrown on 09/02/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT,sdk = 21)
public class TestMeasurementService_Publisher_Tracked {

    private static final String CAMREF = "camref";

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void testTrackedIntentWithIntent() {

        Intent boringintent = new Intent(Intent.ACTION_MAIN);

        Intent trackedintent = MeasurementService.trackedIntent(boringintent, CAMREF);

        Assert.assertEquals(CAMREF,
                trackedintent.getStringExtra(MeasurementService.TrackingConstants.TRACKING_INTENT_CAMREF));
        Assert.assertEquals(trackedintent.getData(), boringintent.getData());
    }

    @Test
    public void testTrackedIntentWithIntentWithData() throws Exception {

        Intent originalintent = new Intent(Intent.ACTION_VIEW, Uri.parse("exactview://open"));

        Intent trackedintent = MeasurementService.trackedIntent(originalintent, CAMREF);

        Assert.assertEquals(CAMREF,
                trackedintent.getStringExtra(MeasurementService.TrackingConstants.TRACKING_INTENT_CAMREF));
        Assert.assertEquals(trackedintent.getData(), originalintent.getData());
    }

    @Test
    public void testTrackedIntentWithUniversalIntentWithData() throws Exception {

        Intent originalintent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com"));
        Intent trackedintent = MeasurementService.trackedIntent(originalintent, CAMREF);

        Assert.assertEquals(CAMREF,
                trackedintent.getStringExtra(MeasurementService.TrackingConstants.TRACKING_INTENT_CAMREF));

        // we're approximating the output of MeasurementServiceURI here.
        // not want to expand injection to this method.

        String intent = trackedintent.getDataString();
        String authority = trackedintent.getData().getAuthority();
        String destination = "/destination:" + URLEncoder.encode(originalintent.getData().toString(), "utf-8");
        String path = trackedintent.getData().getPath();

        Assert.assertNotSame(trackedintent.getData(), originalintent.getData());
        Assert.assertTrue(trackedintent.getData().getAuthority().equals("m.prf.hn"));
        Assert.assertTrue(trackedintent.getData().getPath().contains("/destination:" + originalintent.getData().toString()));
        Assert.assertTrue(trackedintent.getData().getPath().contains("/camref:" + CAMREF));
    }



}
