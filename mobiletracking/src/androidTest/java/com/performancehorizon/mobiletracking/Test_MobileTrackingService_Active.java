package com.performancehorizon.mobiletracking;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.test.InstrumentationTestCase;

import com.squareup.okhttp.OkHttpClient;

import junit.framework.Assert;

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

        MobileTrackingRequestQueue setupqueue = mock(MobileTrackingRequestQueue.class);

        MobileTrackingFingerprinterFactory mockfactory = mock(MobileTrackingFingerprinterFactory.class);
        MobileTrackingActiveFingerprinter mockfingerprinter = mock(MobileTrackingActiveFingerprinter.class);

        when(mockfactory.getActiveFingerprinter(any(Context.class),
                any(MobileTrackingActiveFingerprinter.MobileTrackingActiveFingerprinterCallback.class)))
                .thenReturn(mockfingerprinter);

        MobileTrackingService service = new MobileTrackingService(setupqueue,
                new MobileTrackingRequestQueue(new OkHttpClient()),
                new MobileTrackingRequestFactory(), mockfactory);

        service.setGenerateActiveFingerprint(true);

        service.initialise(mockedContext, "don't", "care");

        verify(setupqueue, times(0)).enqueueRequest(any(MobileTrackingRequest.class));
        verify(mockfingerprinter).generateFingerprint();
    }

    public void testActiveFingerprinterRegister() {

        MobileTrackingRequestQueue setupqueue = mock(MobileTrackingRequestQueue.class);

        MobileTrackingFingerprinterFactory mockfactory = mock(MobileTrackingFingerprinterFactory.class);
        MobileTrackingActiveFingerprinter mockfingerprinter = mock(MobileTrackingActiveFingerprinter.class);

        ArgumentCaptor<MobileTrackingActiveFingerprinter.MobileTrackingActiveFingerprinterCallback> callbackcaptor =
                ArgumentCaptor.forClass(MobileTrackingActiveFingerprinter.MobileTrackingActiveFingerprinterCallback.class);

        when(mockfactory.getActiveFingerprinter(any(Context.class),
                callbackcaptor.capture()))
                .thenReturn(mockfingerprinter);

        MobileTrackingService service = new MobileTrackingService(setupqueue,
                new MobileTrackingRequestQueue(new OkHttpClient()),
                new MobileTrackingRequestFactory(), mockfactory);

        service.setGenerateActiveFingerprint(true);

        service.initialise(mockedContext, "don't", "care");

        //mock the behaviour of the active fingerprinter class (call the callback)
        callbackcaptor.getValue().activeFingerprintComplete(mockfingerprinter, new HashMap<String, Object>());

        verify(setupqueue).enqueueRequest(any(MobileTrackingRequest.class));
    }



}
