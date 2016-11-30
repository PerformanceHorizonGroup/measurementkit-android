package com.performancehorizon.measurementkit;

import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 09/02/16.
 */

@RunWith(AndroidJUnit4.class)
public class TestMeasurementService_Publisher_Uris {

    private static final String CAMREF = "camref";

    private static final Uri DESTINATION = Uri.parse("http://www.google.com");
    private static final Uri APPSTOREDESTINATION = Uri.parse("http://play.google.com/something");
    private static final Uri DEEPLINK = Uri.parse("exactview://open");

    private static final Uri SERVICEURI = Uri.parse("http://m.prf.hn");

    private MeasurementService.UriBuilderFactory factory;
    private MeasurementServiceURIBuilder builder;

    @Before
    public void init() {

        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());

        factory = mock(MeasurementService.UriBuilderFactory.class);
        builder = mock(MeasurementServiceURIBuilder.class);

        when(factory.getTrackingUriBuilder(any(TrackingURLHelper.class))).thenReturn(builder);

        when(builder.setCamref(anyString())).thenReturn(builder);
        when(builder.setDeeplink(any(Uri.class))).thenReturn(builder);
        when(builder.setDestination(any(Uri.class))).thenReturn(builder);
        when(builder.setSkipDeepLink(anyBoolean())).thenReturn(builder);
        when(builder.build()).thenReturn(SERVICEURI);
        when(builder.putAlias(anyString(), anyString())).thenReturn(builder);
    }

    @Test
     public void testMeasurementServiceURIWithNoDeepLink() {

        Assert.assertEquals(
                MeasurementService.measurementServiceURI(CAMREF, null, DESTINATION, null, false, factory),
                SERVICEURI);

        verify(builder).setDestination(DESTINATION);
        verify(builder).setCamref(CAMREF);

        verify(builder).build();
        verifyNoMoreInteractions(builder);
    }

    @Test
    public void testMeasurementServiceURIWithAAIDAndNoDeepLink() {

        Assert.assertEquals(
                MeasurementService.measurementServiceURI(CAMREF, "AAID", DESTINATION, null, false, factory),
                SERVICEURI);

        verify(builder).setDestination(DESTINATION);
        verify(builder).setCamref(CAMREF);
        verify(builder).putAlias("aaid", "AAID");

        verify(builder).build();
        verifyNoMoreInteractions(builder);
    }

    @Test
    public void testMeasurementServiceURIWithDeepLink() {

        Assert.assertEquals(
                MeasurementService.measurementServiceURI(CAMREF, null, DESTINATION, DEEPLINK, false, factory),
                SERVICEURI);

        verify(builder).setDestination(DESTINATION);
        verify(builder).setDeeplink(DEEPLINK);
        verify(builder).setSkipDeepLink(true);
        verify(builder).setCamref(CAMREF);

        verify(builder).build();
        verifyNoMoreInteractions(builder);
    }

    @Test
    public void testMeasurementServiceURIWithDeepLinkToAppStore() {

        Assert.assertEquals(
                MeasurementService.measurementServiceURI(CAMREF, null , APPSTOREDESTINATION, DEEPLINK, false, factory),
                SERVICEURI);

        verify(builder).setDestination(APPSTOREDESTINATION);
        verify(builder).setDeeplink(DEEPLINK);
        verify(builder).setSkipDeepLink(true);
        verify(builder).setCamref(CAMREF);

        verify(builder).build();
        verifyNoMoreInteractions(builder);
    }
}
