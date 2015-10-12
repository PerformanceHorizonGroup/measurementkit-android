package com.performancehorizon.measurementkit;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.InstrumentationTestCase;
import android.test.mock.MockContext;
import android.test.suitebuilder.annotation.SmallTest;

import com.performancehorizon.measurementkit.MeasurementService;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;

import static org.mockito.Mockito.*;

import org.junit.Before;
import org.mockito.ArgumentMatcher;

/**
 * Created by owainbrown on 18/03/15.
 */
public class Test_MobileTrackingService_Setup extends InstrumentationTestCase
{
    private MeasurementService service;
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
    public void testSetupFromPreferences() {

        MeasurementService serviceundertest = new MeasurementService();

        String clickref = "clickreference";
        String adref = "ad reference";
        String camref = "campaign reference";

        //mock preferences
        when(mockedpreferences.getString(anyString(), anyString())).thenReturn(clickref);
        when(mockedpreferences.getBoolean(anyString(), anyBoolean())).thenReturn(true);

        //initialise tracking

        //mockedtracking.setClickRef("hello");
        serviceundertest.initialise(mockedContext,null, adref, camref);

        //verify results
        assert(serviceundertest.getClickRef().equals(clickref));
        assert(serviceundertest.getAdvertiserID().equals(adref));
        assert(serviceundertest.getCampaignID().equals(camref));
    }




    public void testSetupWithNoPreferences()
    {
        TrackingRequestQueue mockregistrationqueue = mock(TrackingRequestQueue.class);

        MeasurementService serviceundertest = new MeasurementService(mockregistrationqueue,
                new TrackingRequestQueue(new OkHttpClient()),
                new TrackingRequestFactory());

        //mock preferences
        when(mockedpreferences.getString(anyString(), anyString())).thenReturn(null);
        when(mockedpreferences.getBoolean(anyString(), anyBoolean())).thenReturn(true);

        String adref = "ad reference";
        String camref = "campaign reference";


        serviceundertest.initialise(mockedContext,null, adref, camref);

        //verify results
        assert(serviceundertest.getAdvertiserID().equals(adref));
        assert(serviceundertest.getCampaignID().equals(camref));
        verify(mockregistrationqueue).enqueueRequest(argThat(new Matchers.IsRegistrationRequest()));
        verify(mockregistrationqueue).setDelegate(serviceundertest);
    }

    public void testSetupWithNoContext()
    {
        TrackingRequestQueue mockregistrationqueue = mock(TrackingRequestQueue.class);

        MeasurementService serviceundertest = new MeasurementService(mockregistrationqueue,
                new TrackingRequestQueue(new OkHttpClient()),
                new TrackingRequestFactory());

        //mock preferences
        when(mockedpreferences.getString(anyString(), anyString())).thenReturn(null);
        when(mockedpreferences.getBoolean(anyString(), anyBoolean())).thenReturn(true);

        String adref = "ad reference";
        String camref = "campaign reference";

        serviceundertest.initialise(null,null, adref, camref);

        //verify results
        assert(serviceundertest.getAdvertiserID().equals(adref));
        assert(serviceundertest.getCampaignID().equals(camref));
        verify(mockregistrationqueue).enqueueRequest(argThat(new Matchers.IsRegistrationRequest()));
        verify(mockregistrationqueue).setDelegate(serviceundertest);
    }

    public void testSetupInactive()
    {
        TrackingRequestQueue mockregistrationqueue = mock(TrackingRequestQueue.class);

        MeasurementService serviceundertest = new MeasurementService(mockregistrationqueue,
                new TrackingRequestQueue(new OkHttpClient()),
                new TrackingRequestFactory());

        //mock preferences
        when(mockedpreferences.getBoolean(anyString(), anyBoolean())).thenReturn(false);

        String adref = "ad reference";
        String camref = "campaign reference";

        serviceundertest.initialise(mockedContext,null, adref, camref);

        assertNull(serviceundertest.getClickRef());
        assertFalse(serviceundertest.isTrackingActive());
        verify(mockregistrationqueue, times(0)).enqueueRequest(any(TrackingRequest.class));
    }

    public void testSetupCallback()
    {
        TrackingRequestQueue registrationqueue = new TrackingRequestQueue(new OkHttpClient());
        TrackingRequest fakerequest = new TrackingRequest("fakeurl", RequestBody.create(MediaType.parse("application/plain"), "fakecontents"));

        MeasurementService serviceundertest= new MeasurementService(registrationqueue, new TrackingRequestQueue(new OkHttpClient()), new TrackingRequestFactory());
        serviceundertest.initialise(mockedContext,null, "unused", "dontcare");

        String registrationresponse= "{\"request_params\": {\"this\": \"that\"},\"phgmobiletrackingid\": \"OsH4bdNu\"}";

        SharedPreferences.Editor mockededitor = mock(SharedPreferences.Editor.class);
        when(mockedpreferences.edit()).thenReturn(mockededitor);

        serviceundertest.requestQueueDidCompleteRequest(registrationqueue, fakerequest, registrationresponse);

        assert(serviceundertest.getClickRef().equals("OsH4bdNu"));
        verify(mockededitor).putString("com.performancehorizon.phgmt.id", "OsH4bdNu");
    }

    public void testSetupCallbackMalformedData()
    {
        TrackingRequestQueue registrationqueue = new TrackingRequestQueue(new OkHttpClient());
        TrackingRequest fakerequest = new TrackingRequest("fakeurl", RequestBody.create(MediaType.parse("application/plain"), "fakecontents"));

        MeasurementService serviceundertest= new MeasurementService(registrationqueue, new TrackingRequestQueue(new OkHttpClient()), new TrackingRequestFactory());
        serviceundertest.initialise(mockedContext,null, "unused", "dontcare");

        String registrationresponse= "{\"request_params\": {\"this\": \"that\"},\"whatever\": \"OsH4bdNu\"}";

        SharedPreferences.Editor mockededitor = mock(SharedPreferences.Editor.class);
        when(mockedpreferences.edit()).thenReturn(mockededitor);

        serviceundertest.requestQueueDidCompleteRequest(registrationqueue, fakerequest, registrationresponse);

        assertNull(serviceundertest.getClickRef());
        verify(mockededitor, times(0)).putString("com.performancehorizon.phgmt.id", "OsH4bdNu");
    }

    //if the value false is returned in the phgmobiletracking id, then the settings should be set to indicate no further actions.
    public void testSetupCallbackGoSilent()
    {
        TrackingRequestQueue registrationqueue = new TrackingRequestQueue(new OkHttpClient());
        TrackingRequest fakerequest = new TrackingRequest("fakeurl", RequestBody.create(MediaType.parse("application/plain"), "fakecontents"));

        MeasurementService serviceundertest= new MeasurementService(registrationqueue, new TrackingRequestQueue(new OkHttpClient()), new TrackingRequestFactory());
        serviceundertest.initialise(mockedContext,null, "unused", "dontcare");

        String registrationresponse= "{\"request_params\": {\"this\": \"that\"},\"phgmobiletrackingid\": false}";

        SharedPreferences.Editor mockededitor = mock(SharedPreferences.Editor.class);
        when(mockedpreferences.edit()).thenReturn(mockededitor);

        serviceundertest.requestQueueDidCompleteRequest(registrationqueue, fakerequest, registrationresponse);

        assert(serviceundertest.getClickRef().equals("OsH4bdNu"));
        verify(mockededitor).putBoolean("com.performancehorizon.com.phgmt.isactive", false);
    }
}
