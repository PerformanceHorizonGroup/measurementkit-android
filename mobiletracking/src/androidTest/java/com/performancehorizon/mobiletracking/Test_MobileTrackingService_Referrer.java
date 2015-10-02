package com.performancehorizon.mobiletracking;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;
import android.test.suitebuilder.annotation.SmallTest;

import com.squareup.okhttp.OkHttpClient;

import org.junit.Before;

import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;

/**
 * Created by owainbrown on 23/03/15.
 */
public class Test_MobileTrackingService_Referrer extends InstrumentationTestCase {
    private Context mockedContext;
    private ConnectivityManager mockedConnectivity;
    private NetworkInfo mockedNetworkInfo;
    private SharedPreferences mockedpreferences;

    @Before
    public void setUp() {
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

        mockedContext = mock(MockContext.class);
        mockedConnectivity = mock(ConnectivityManager.class);
        mockedNetworkInfo = mock(NetworkInfo.class);

        when(mockedContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockedConnectivity);
        when(mockedConnectivity.getActiveNetworkInfo()).thenReturn(mockedNetworkInfo);
        when(mockedNetworkInfo.isAvailable()).thenReturn(true);

        mockedpreferences = mock(SharedPreferences.class);

        when(mockedContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockedpreferences);
    }

    @SmallTest
    public void testSetupNoReferrerWaiting() {

        MobileTrackingRequestQueue clickqueue= mock(MobileTrackingRequestQueue.class);
        MobileTrackingService serviceundertest = new MobileTrackingService(clickqueue, new MobileTrackingRequestQueue(new OkHttpClient()),
                new MobileTrackingRequestFactory());

        String adref = "ad reference";
        String camref = "campaign reference";

        //mock preferences
        when(mockedpreferences.getString(eq(MobileTrackingService.TrackingConstants.TRACKING_PREF_ID), anyString())).thenReturn(null);
        when(mockedpreferences.getBoolean(eq(MobileTrackingService.TrackingConstants.TRACKING_PREF_ACTIVE), anyBoolean())).thenReturn(true);

        //initialise tracking
        serviceundertest.setAwaitReferrer(false);
        serviceundertest.initialise(mockedContext, adref, camref);

        //verify results
        assert(serviceundertest.getAdvertiserID().equals(adref));
        assert(serviceundertest.getCampaignID().equals(camref));

        verify(clickqueue).enqueueRequest(argThat(new Matchers.IsRegistrationRequest()));
    }

    @SmallTest
    public void testWaitForReferrerWillWait() {

        MobileTrackingRequestQueue clickqueue= mock(MobileTrackingRequestQueue.class);
        MobileTrackingService serviceundertest = new MobileTrackingService(clickqueue, new MobileTrackingRequestQueue(new OkHttpClient()),
                new MobileTrackingRequestFactory());

        String adref = "ad reference";
        String camref = "campaign reference";

        //mock preferences
        when(mockedpreferences.getString(eq(MobileTrackingService.TrackingConstants.TRACKING_PREF_ID), anyString())).thenReturn(null);
        when(mockedpreferences.getBoolean(eq(MobileTrackingService.TrackingConstants.TRACKING_PREF_ACTIVE), anyBoolean())).thenReturn(true);

        //initialise tracking
        serviceundertest.initialise(mockedContext, adref, camref);

        //verify results
        assert(serviceundertest.getAdvertiserID().equals(adref));
        assert(serviceundertest.getCampaignID().equals(camref));

        verify(clickqueue, times(0)).enqueueRequest(argThat(new Matchers.IsRegistrationRequest()));
    }

    @SmallTest
     public void testWaitForReferrer_Integration() {

        MobileTrackingRequestQueue clickqueue= mock(MobileTrackingRequestQueue.class);
        MobileTrackingService serviceundertest = new MobileTrackingService(clickqueue, new MobileTrackingRequestQueue(new OkHttpClient()),
                new MobileTrackingRequestFactory());
        MobileTrackingService.setTrackingInstance(serviceundertest);

        SharedPreferences.Editor mockeditor = mock(SharedPreferences.Editor.class);
        when(mockedpreferences.edit()).thenReturn(mockeditor);

        String adref = "ad reference";
        String camref = "campaign reference";

        //mock preferences
        when(mockedpreferences.getString(eq(MobileTrackingService.TrackingConstants.TRACKING_PREF_ID), anyString())).thenReturn(null);
        when(mockedpreferences.getBoolean(eq(MobileTrackingService.TrackingConstants.TRACKING_PREF_ACTIVE), anyBoolean())).thenReturn(true);
        when(mockeditor.putString(anyString(), anyString())).thenReturn(mockeditor);

        //mock intent
        Intent mockintent = mock(Intent.class);
        String thereferrer = "something";

        when(mockintent.getStringExtra("referrer")).thenReturn(thereferrer);
        when(mockintent.getAction()).thenReturn("com.android.vending.INSTALL_REFERRER");

        //initialise tracking
        serviceundertest.initialise(mockedContext, adref, camref);

        //verify results
        assert(serviceundertest.getAdvertiserID().equals(adref));
        assert(serviceundertest.getCampaignID().equals(camref));

        verify(clickqueue, times(0)).enqueueRequest(argThat(new Matchers.IsRegistrationRequest()));

        ReferrerTracker referrer = new ReferrerTracker();
        referrer.onReceive(mockedContext, mockintent);

        verify(clickqueue).enqueueRequest(argThat(new Matchers.IsRegistrationRequest()));
    }

    @SmallTest
    public void testReferrerPrecedes_Integration() {

        MobileTrackingRequestQueue clickqueue= mock(MobileTrackingRequestQueue.class);
        MobileTrackingService serviceundertest = new MobileTrackingService(clickqueue, new MobileTrackingRequestQueue(new OkHttpClient()),
                new MobileTrackingRequestFactory());
        MobileTrackingService.setTrackingInstance(serviceundertest);

        SharedPreferences.Editor mockeditor = mock(SharedPreferences.Editor.class);
        when(mockedpreferences.edit()).thenReturn(mockeditor);

        String adref = "ad reference";
        String camref = "campaign reference";

        //mock preferences
        when(mockedpreferences.getString(eq(MobileTrackingService.TrackingConstants.TRACKING_PREF_ID), anyString())).thenReturn(null);
        when(mockedpreferences.getBoolean(eq(MobileTrackingService.TrackingConstants.TRACKING_PREF_ACTIVE), anyBoolean())).thenReturn(true);
        when(mockeditor.putString(anyString(), anyString())).thenReturn(mockeditor);

        //mock intent
        Intent mockintent = mock(Intent.class);
        String thereferrer = "something";

        when(mockintent.getStringExtra("referrer")).thenReturn(thereferrer);
        when(mockintent.getAction()).thenReturn("com.android.vending.INSTALL_REFERRER");

        ReferrerTracker referrer = new ReferrerTracker();
        referrer.onReceive(mockedContext, mockintent);

        //initialise tracking
        serviceundertest.initialise(mockedContext, adref, camref);

        //verify results
        assert(serviceundertest.getAdvertiserID().equals(adref));
        assert(serviceundertest.getCampaignID().equals(camref));

        verify(clickqueue).enqueueRequest(argThat(new Matchers.IsRegistrationRequest()));
    }


}
