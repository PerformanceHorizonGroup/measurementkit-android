package com.performancehorizon.measurementkit;

import android.annotation.TargetApi;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 02/02/16.
 */

@RunWith(AndroidJUnit4.class)
public class TestReachability {

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Test
    public void testConstructor() {

        ConnectivityManager mockconnectivity = mock(ConnectivityManager.class);

        Reachability reachability = new Reachability(mockconnectivity, null);

        verify(mockconnectivity).addDefaultNetworkActiveListener(any(ConnectivityManager.OnNetworkActiveListener.class));
    }

    @Test
    public void testNetworkActiveWithNoConnectivity() {

        ConnectivityManager mockconnectivity = mock(ConnectivityManager.class);

        Reachability reachability = new Reachability(mockconnectivity, null);

        Assert.assertFalse(reachability.isNetworkActive());
    }

    @Test
    public void testNetworkActiveWithNullNetworkInfo() {

        ConnectivityManager mockconnectivity = mock(ConnectivityManager.class);
        when(mockconnectivity.getActiveNetworkInfo()).thenReturn(null);

        Reachability reachability = new Reachability(mockconnectivity, null);

        Assert.assertFalse(reachability.isNetworkActive());
    }

    @Test
    public void testNetworkActiveWithConnectedNetwork() {
        NetworkInfo mockedinfo = mock(NetworkInfo.class);
        when(mockedinfo.isConnectedOrConnecting()).thenReturn(true);

        ConnectivityManager mockconnectivity = mock(ConnectivityManager.class);
        when(mockconnectivity.getActiveNetworkInfo()).thenReturn(mockedinfo);

        Reachability reachability = new Reachability(mockconnectivity, null);

        Assert.assertTrue(reachability.isNetworkActive());
    }

    public void testNetworkActiveWithNotConnectedNetwork() {
        NetworkInfo mockedinfo = mock(NetworkInfo.class);
        when(mockedinfo.isConnectedOrConnecting()).thenReturn(false);

        ConnectivityManager mockconnectivity = mock(ConnectivityManager.class);
        when(mockconnectivity.getActiveNetworkInfo()).thenReturn(mockedinfo);

        Reachability reachability = new Reachability(mockconnectivity, null);

        Assert.assertFalse(reachability.isNetworkActive());
    }

}
