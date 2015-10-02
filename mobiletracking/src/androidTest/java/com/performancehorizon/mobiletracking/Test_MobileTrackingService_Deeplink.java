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

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import static org.mockito.Mockito.*;

/**
 * Created by owainbrown on 23/03/15.
 */
public class Test_MobileTrackingService_Deeplink extends InstrumentationTestCase {

    private Context mockedContext;
    private SharedPreferences mockedPreferences;

    private ConnectivityManager mockedConnectivity;

    @Before
    public void setUp() {
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

        this.mockedContext = mock(MockContext.class);
        this.mockedConnectivity = mock(ConnectivityManager.class);
        this.mockedPreferences = mock(SharedPreferences.class);

        when(mockedContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockedPreferences);
        when(mockedPreferences.getString(eq(MobileTrackingService.TrackingConstants.TRACKING_PREF_ID), anyString())).thenReturn(null);
        when(mockedPreferences.getString(eq(MobileTrackingService.TrackingConstants.TRACKING_PREF_REFERRER), anyString())).thenReturn("bob");

        SharedPreferences.Editor mockededitor = mock(SharedPreferences.Editor.class);
        when(mockedPreferences.edit()).thenReturn(mockededitor);

        when(mockedContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockedConnectivity);
    }

    @SmallTest
    public void testDeeplink()
    {
        MobileTrackingRequestQueue registrationqueue = new MobileTrackingRequestQueue(new OkHttpClient());
        MobileTrackingRequest fakerequest = new MobileTrackingRequest("fakeurl", "fakecontents");

        MobileTrackingService serviceundertest= new MobileTrackingService(registrationqueue, new MobileTrackingRequestQueue(new OkHttpClient()), new MobileTrackingRequestFactory());
        serviceundertest.initialise(mockedContext, "unused", "dontcare");

        String registrationresponse= "{\"request_params\": {\"this\": \"that\"},\"phgmobiletrackingid\": \"OsH4bdNu\",  \"deeplink\":\"cleverapp%3A%2F%2Fdo%2Fsomething%2F123\"}";

        serviceundertest.requestQueueDidCompleteRequest(registrationqueue, fakerequest, registrationresponse);

        verify(mockedContext).startActivity(argThat(new Matchers.isDeepLinkIntent(Intent.ACTION_VIEW, "cleverapp://do/something/123")));
    }

    @SmallTest
    public void testDeepLinkWithAction() throws UnsupportedEncodingException
    {
        MobileTrackingRequestQueue registrationqueue = new MobileTrackingRequestQueue(new OkHttpClient());
        MobileTrackingRequest fakerequest = new MobileTrackingRequest("fakeurl", "fakecontents");

        MobileTrackingService serviceundertest= new MobileTrackingService(registrationqueue, new MobileTrackingRequestQueue(new OkHttpClient()), new MobileTrackingRequestFactory());
        serviceundertest.initialise(mockedContext, "unused", "dontcare");

        String action = "com.whatever.whatever.whatever";
        String registrationresponse= "{\"request_params\": {\"this\": \"that\"},\"phgmobiletrackingid\": \"OsH4bdNu\",  \"deeplink\": \"cleverapp%3A%2F%2Fdo%2Fsomething%2F123\"," +
                "\"deeplink_action\":\"" + URLEncoder.encode(action, "UTF-8") +
                " \"}";

        serviceundertest.requestQueueDidCompleteRequest(registrationqueue, fakerequest, registrationresponse);

        verify(mockedContext).startActivity(argThat(new Matchers.isDeepLinkIntent(action, "cleverapp://do/something/123")));
    }

    @SmallTest
    public void testNoDeepLink()
    {
        MobileTrackingRequestQueue registrationqueue = new MobileTrackingRequestQueue(new OkHttpClient());
        MobileTrackingRequest fakerequest = new MobileTrackingRequest("fakeurl", "fakecontents");

        MobileTrackingService serviceundertest= new MobileTrackingService(registrationqueue, new MobileTrackingRequestQueue(new OkHttpClient()), new MobileTrackingRequestFactory());
        serviceundertest.initialise(mockedContext, "unused", "dontcare");

        String registrationresponse= "{\"request_params\": {\"this\": \"that\"},\"phgmobiletrackingid\": \"OsH4bdNu\"}";

        serviceundertest.requestQueueDidCompleteRequest(registrationqueue, fakerequest, registrationresponse);

        verify(mockedContext, times(0)).startActivity(argThat(new Matchers.isDeepLinkIntent(Intent.ACTION_VIEW, "cleverapp://do/something/123")));
    }
}
