package com.performancehorizon.measurementkit;

import android.content.Context;
import android.content.Intent;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Test;
import org.junit.runner.RunWith;


import static org.mockito.Mockito.mock;

/**
 * Created by owainbrown on 02/02/16.
 */

@RunWith(AndroidJUnit4.class)
public class TestReferrerTracker {


    @Test
    public void testReferrerTrackerWithNoReferrer() {

        Context acontext = InstrumentationRegistry.getContext();
        Intent intentwithoutareferrer = new Intent(Intent.ACTION_MAIN);

        ReferrerTracker referrertracker = new ReferrerTracker();
        referrertracker.clearReferrer(acontext);

        referrertracker.onReceive(acontext, intentwithoutareferrer);
        Assert.assertNull(referrertracker.getReferrer(acontext));
    }

    @Test
    public void testReferrerTrackerWithSimpleReferrer() {

        Context acontext = InstrumentationRegistry.getContext();

        Intent intentwithsimplereferrrer = new Intent("com.android.vending.INSTALL_REFERRER");
        intentwithsimplereferrrer.putExtra("referrer", "phn_ref%3Dbob%26stevenjones");

        ReferrerTracker referrertracker = new ReferrerTracker();

        referrertracker.onReceive(acontext, intentwithsimplereferrrer);

        Assert.assertEquals(referrertracker.getReferrer(acontext), "bob");
        //Assert.assertEquals(referrertracker.getReferrerParameters().get("stevenjones"), "");
    }

    @Test
    public void testReferrerTrackerWithReferrer() {

        Context acontext = InstrumentationRegistry.getContext();

        Intent intentwithreferrrerparams = new Intent("com.android.vending.INSTALL_REFERRER");
        intentwithreferrrerparams.putExtra("referrer", "phn_ref%3Dbob%26thing%3Dstevenjones");

        ReferrerTracker referrertracker = new ReferrerTracker();

        referrertracker.onReceive(acontext, intentwithreferrrerparams);

        Assert.assertEquals(referrertracker.getReferrer(acontext), "bob");
        //Assert.assertEquals(referrertracker.getReferrerParameters().get("thing"), "stevenjones");
    }
}
