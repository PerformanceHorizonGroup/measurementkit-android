package com.performancehorizon.measurementkit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.test.InstrumentationTestCase;

import android.test.mock.MockContext;
import android.test.suitebuilder.annotation.SmallTest;

import com.performancehorizon.measurementkit.ReferrerTracker;

import org.junit.Before;

import static org.mockito.Mockito.*;


/**
 * Created by owainbrown on 20/03/15.
 */
public class Test_MobileTrackingReferrer extends InstrumentationTestCase{

    @Before
    public void setUp() {
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
    }

    @SmallTest
    public void testReferral()
    {
        Context mockcontext = mock(MockContext.class);
        SharedPreferences mockprefs = mock(SharedPreferences.class);
        SharedPreferences.Editor mockeditor = mock(SharedPreferences.Editor.class);

        when(mockcontext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockprefs);
        when(mockprefs.edit()).thenReturn(mockeditor);
        when(mockeditor.putString(anyString(), anyString())).thenReturn(mockeditor);

        Intent mockintent = mock(Intent.class);
        String thereferrer = "something";

        when(mockintent.getStringExtra("referrer")).thenReturn(thereferrer);
        when(mockintent.getAction()).thenReturn("com.android.vending.INSTALL_REFERRER");

        ReferrerTracker referrer = new ReferrerTracker();

        referrer.setResult(0, "hello", null);
        referrer.onReceive(mockcontext, mockintent);

        //assert(MobileTrackingService.trackingInstance().getReferrer().equals(thereferrer));

        verify(mockeditor).putString(MeasurementService.TrackingConstants.TRACKING_PREF_REFERRER, thereferrer);
        verify(mockeditor).commit();
    }
}
