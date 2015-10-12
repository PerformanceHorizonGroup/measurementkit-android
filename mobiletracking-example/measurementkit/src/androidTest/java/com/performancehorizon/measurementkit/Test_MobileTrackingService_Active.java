package com.performancehorizon.measurementkit;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.InstrumentationTestCase;

import com.squareup.okhttp.OkHttpClient;

import org.junit.Before;
import org.mockito.ArgumentCaptor;

import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Created by owainbrown on 26/03/15.
 */
public class Test_MobileTrackingService_Active extends InstrumentationTestCase{

    private Context mockedContext;
    private ConnectivityManager mockedConnectivity;
    private NetworkInfo mockedNetworkInfo;
    private SharedPreferences mockedpreferences;

    @Before
    public void setUp() {
        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

        mockedContext = mock(Context.class);
        mockedConnectivity = mock(ConnectivityManager.class);
        mockedNetworkInfo = mock(NetworkInfo.class);

        when(mockedContext.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mockedConnectivity);
        when(mockedConnectivity.getActiveNetworkInfo()).thenReturn(mockedNetworkInfo);
        when(mockedNetworkInfo.isAvailable()).thenReturn(true);

        this.mockedpreferences = mock(SharedPreferences.class);

        when(mockedContext.getSharedPreferences(anyString(), anyInt())).thenReturn(mockedpreferences);
        when(mockedpreferences.getBoolean(anyString(), anyBoolean())).thenReturn(true);
        when(mockedpreferences.getString(anyString(), anyString())).thenReturn(null);
    }

    //registration will not proceed until the active fingerprinter is complete.
    public void testHaltForActiveFingerprinter() {

        TrackingRequestQueue setupqueue = mock(TrackingRequestQueue.class);

        FingerprinterFactory mockfactory = mock(FingerprinterFactory.class);
        ActiveFingerprinter mockfingerprinter = mock(ActiveFingerprinter.class);

        when(mockfactory.getActiveFingerprinter(any(Context.class),
                any(ActiveFingerprinter.MobileTrackingActiveFingerprinterCallback.class)))
                .thenReturn(mockfingerprinter);

        MeasurementService service = new MeasurementService(setupqueue,
                new TrackingRequestQueue(new OkHttpClient()),
                new DummyTrackingRequestFactory(), mockfactory);

        service.setGenerateActiveFingerprint(true);

        service.initialise(mockedContext,null, "don't", "care");

        verify(setupqueue, times(0)).enqueueRequest(any(TrackingRequest.class));
        verify(mockfingerprinter).generateFingerprint();
    }

    public void testActiveFingerprinterRegister() {

        TrackingRequestQueue setupqueue = mock(TrackingRequestQueue.class);

        FingerprinterFactory mockfactory = mock(FingerprinterFactory.class);
        ActiveFingerprinter mockfingerprinter = mock(ActiveFingerprinter.class);

        ArgumentCaptor<ActiveFingerprinter.MobileTrackingActiveFingerprinterCallback> callbackcaptor =
                ArgumentCaptor.forClass(ActiveFingerprinter.MobileTrackingActiveFingerprinterCallback.class);

        when(mockfactory.getActiveFingerprinter(any(Context.class),
                callbackcaptor.capture()))
                .thenReturn(mockfingerprinter);

        MeasurementService service = new MeasurementService(setupqueue,
                new TrackingRequestQueue(new OkHttpClient()),
                new DummyTrackingRequestFactory(), mockfactory);

        service.setGenerateActiveFingerprint(true);

        service.initialise(mockedContext,null, "don't", "care");

        //mock the behaviour of the active fingerprinter class (call the callback)
        callbackcaptor.getValue().activeFingerprintComplete(mockfingerprinter, new HashMap<String, Object>());

        verify(setupqueue).enqueueRequest(any(TrackingRequest.class));
    }



}
