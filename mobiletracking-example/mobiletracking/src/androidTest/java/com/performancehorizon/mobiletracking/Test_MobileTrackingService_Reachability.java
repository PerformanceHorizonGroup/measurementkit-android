//package com.performancehorizon.mobiletracking;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.net.ConnectivityManager;
//import android.net.NetworkInfo;
//import android.test.InstrumentationTestCase;
//import android.test.mock.MockContext;
//import android.test.suitebuilder.annotation.SmallTest;
//
//import org.junit.Before;
//
//import java.util.HashMap;
//
//import static org.mockito.Matchers.anyBoolean;
//import static org.mockito.Matchers.anyInt;
//import static org.mockito.Matchers.anyString;
//import static org.mockito.Mockito.mock;
//import static org.mockito.Mockito.verify;
//import static org.mockito.Mockito.verifyZeroInteractions;
//import static org.mockito.Mockito.when;
//
///**
// * Created by owainbrown on 20/03/15.
// */
//public class Test_MobileTrackingService_Reachability extends InstrumentationTestCase{
//
//    private MobileTrackingService serviceundertest;
//    private Context mockedContext;
//    private ConnectivityManager mockedConnectivity;
//    private NetworkInfo mockedNetworkInfo;
//    private SharedPreferences mockedpreferences;
//
//    private MobileTrackingRequestQueue registrationQueue;
//    private MobileTrackingRequestQueue eventQueue;
//
//    @Before
//    public void setUp() {
//
//        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
//
//        mockedContext = mock(MockContext.class);
//        mockedConnectivity = mock(ConnectivityManager.class);
//        mockedNetworkInfo = mock(NetworkInfo.class);
//
//        registrationQueue = mock(MobileTrackingRequestQueue.class);
//        eventQueue = mock(MobileTrackingRequestQueue.class);
//
//        serviceundertest = new MobileTrackingService(registrationQueue, eventQueue, new MobileTrackingRequestFactory());
//        when(mockedContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockedConnectivity);
//        when(mockedConnectivity.getActiveNetworkInfo()).thenReturn(mockedNetworkInfo);
//        when(mockedNetworkInfo.isConnectedOrConnecting()).thenReturn(true);
//
//        mockedpreferences = mock(SharedPreferences.class);
//        when(mockedContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockedpreferences);
//
//        String clickref = "clickreference";
//        when(mockedpreferences.getString(anyString(), anyString())).thenReturn(clickref);
//        when(mockedpreferences.getBoolean(anyString(), anyBoolean())).thenReturn(true);
//    }
//
//    @SmallTest
//    public void testReachability()
//    {
//        String adref = "ad reference";
//        String camref = "campaign reference";
//        serviceundertest.initialise(mockedContext, adref, camref);
//
//        serviceundertest.getReachability().getCallback().onNetworkActive();
//
//        verify(eventQueue).setQueueIsPaused(false);
//        verify(eventQueue).setQueueIsPaused(false);
//    }
//
//    @SmallTest
//    public void testReachabilityNoContext()
//    {
//        String adref = "ad reference";
//        String camref = "campaign reference";
//        serviceundertest.initialise(null, adref, camref);
//
//        //no reachability
//        assertNull(serviceundertest.getReachability());
//    }
//
//    @SmallTest
//    public void testRestartQueuesOnEvent()
//    {
//        String adref = "ad reference";
//        String camref = "campaign reference";
//        serviceundertest.initialise(mockedContext, adref, camref);
//
//        MobileTrackingEvent fakeevent = mock(MobileTrackingEvent.class);
//        when(fakeevent.getEventTag()).thenReturn("dont");
//        when(fakeevent.getSalesData()).thenReturn(null);
//        when(fakeevent.getEventData()).thenReturn(new HashMap<String, Object>());
//
//        serviceundertest.trackEvent(fakeevent);
//
//        verify(this.eventQueue).setQueueIsPaused(false);
//        verify(this.registrationQueue).setQueueIsPaused(false);
//    }
//}
