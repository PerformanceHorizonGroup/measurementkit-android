package com.performancehorizon.measurementkit;

import android.content.Intent;
import android.net.Uri;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 09/02/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT,sdk = 21)
public class TestMeasurementService_Publisher_Uris {

    private static final String CAMREF = "camref";

    private static final Uri DESTINATION = Uri.parse("http://www.google.com");
    private static final Uri APPSTOREDESTINATION = Uri.parse("http://play.google.com/something");
    private static final Uri DEEPLINK = Uri.parse("exactview://open");

    private static final Uri SERVICEURI = Uri.parse("http://m.prf.hn");

    @Mock private MeasurementService.UriBuilderFactory factory;
    @Mock private MeasurementServiceURIBuilder builder;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

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
