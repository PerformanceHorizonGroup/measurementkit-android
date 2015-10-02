package com.performancehorizon.mobiletracking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.test.InstrumentationTestCase;

import android.test.suitebuilder.annotation.SmallTest;
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
        Context mockcontext = mock(Context.class);
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

        referrer.onReceive(mockcontext, mockintent);

        //assert(MobileTrackingService.trackingInstance().getReferrer().equals(thereferrer));

        verify(mockeditor).putString(MobileTrackingService.TrackingConstants.TRACKING_PREF_REFERRER, thereferrer);
        verify(mockeditor).commit();
    }
}
