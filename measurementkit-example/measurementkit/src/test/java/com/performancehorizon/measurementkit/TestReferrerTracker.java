package com.performancehorizon.measurementkit;

import android.content.Context;
import android.content.Intent;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.mock;

/**
 * Created by owainbrown on 02/02/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT,sdk = 21)
public class TestReferrerTracker {


    @Test
    public void testReferrerTrackerWithNoReferrer() {

        Context context = mock(Context.class);
        Intent intentwithoutareferrer = new Intent(Intent.ACTION_MAIN);

        ReferrerTracker referrertracker = new ReferrerTracker();

        referrertracker.onReceive(context, intentwithoutareferrer);

        Assert.assertNull(referrertracker.getReferrer());
        Assert.assertNull(referrertracker.getReferrerParameters());
    }

    @Test
    public void testReferrerTrackerWithSimpleReferrer() {

        Context context = mock(Context.class);
        Intent intentwithsimplereferrrer = new Intent("com.android.vending.INSTALL_REFERRER");
        intentwithsimplereferrrer.putExtra("referrer", "phn_ref%3Dbob%26stevenjones");

        ReferrerTracker referrertracker = new ReferrerTracker();

        referrertracker.onReceive(context, intentwithsimplereferrrer);

        Assert.assertEquals(referrertracker.getReferrer(), "bob");
        Assert.assertEquals(referrertracker.getReferrerParameters().get("stevenjones"), "");
    }

    @Test
    public void testReferrerTrackerWithReferrer() {

        Context context = mock(Context.class);
        Intent intentwithreferrrerparams = new Intent("com.android.vending.INSTALL_REFERRER");
        intentwithreferrrerparams.putExtra("referrer", "phn_ref%3Dbob%26thing%3Dstevenjones");

        ReferrerTracker referrertracker = new ReferrerTracker();

        referrertracker.onReceive(context, intentwithreferrrerparams);

        Assert.assertEquals(referrertracker.getReferrer(), "bob");
        Assert.assertEquals(referrertracker.getReferrerParameters().get("thing"), "stevenjones");
    }
}
