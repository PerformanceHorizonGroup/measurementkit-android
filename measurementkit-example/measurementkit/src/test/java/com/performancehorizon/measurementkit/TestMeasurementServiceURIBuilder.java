package com.performancehorizon.measurementkit;

import android.net.Uri;

import junit.framework.Assert;

import org.bouncycastle.jce.provider.JCEBlockCipher;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.net.URLEncoder;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 03/02/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT,sdk = 21)
public class TestMeasurementServiceURIBuilder {

    private UriBuilderWrapper builder;
    private MeasurementServiceURIBuilder.UriBuilderFactory factory;
    private TrackingURLHelper helper;

    private final static String SCHEME = "http";
    private final static String HOST = "mobiletracking";
    private final static Uri DESTINATION = Uri.parse("http://www.google.com");
    private final static String CAMREF = "examplecamref";

    @Before
    public void initialise() {

        builder = mock(UriBuilderWrapper.class);

        when(builder.scheme(anyString())).thenReturn(builder);
        when(builder.authority(anyString())).thenReturn(builder);
        when(builder.appendPath(anyString())).thenReturn(builder);
        when(builder.appendEncodedPath(anyString())).thenReturn(builder);
        when(builder.appendQueryParameter(anyString(), anyString())).thenReturn(builder);

        helper = mock(TrackingURLHelper.class);
        when(helper.scheme()).thenReturn(SCHEME);
        when(helper.hostForMobileTracking()).thenReturn(HOST);

        factory = mock(MeasurementServiceURIBuilder.UriBuilderFactory.class);
        when(factory.getBuilder()).thenReturn(builder);
    }

    @Test
    public void testInvalidNoCamref() throws Exception{

        MeasurementServiceURIBuilder uribuilder = new MeasurementServiceURIBuilder(helper, factory);

        uribuilder.setDestination(Uri.parse("http://www.google.com"));

        Assert.assertNull(uribuilder.build());
    }

    @Test
    public void testInvalidNoHelper() throws Exception {

        MeasurementServiceURIBuilder uribuilder = new MeasurementServiceURIBuilder(null, factory);
        uribuilder.setCamref(CAMREF);

        Assert.assertNull(uribuilder.build());
    }

    @Test
    public void testInvalidNoDestination()
    {
        MeasurementServiceURIBuilder uribuilder = new MeasurementServiceURIBuilder(helper, factory);

        uribuilder.setCamref(CAMREF);

        Assert.assertNull(uribuilder.build());
    }

    @Test
    public void testValidBuildThrowsException()
    {
        MeasurementServiceURIBuilder uribuilder = new MeasurementServiceURIBuilder(helper, factory);
        uribuilder.setCamref(CAMREF).setDestination(DESTINATION);

        when(builder.build()).thenThrow(new UnsupportedOperationException());

        Assert.assertNull(uribuilder.build());
    }

    @Test
    public void testValidBuild() throws Exception {
        MeasurementServiceURIBuilder uribuilder = new MeasurementServiceURIBuilder(helper, factory);
        uribuilder.setCamref(CAMREF).setDestination(DESTINATION);

        Uri built = Uri.parse("http://www.performancehorizon.com");
        when(builder.build()).thenReturn(built);
        Assert.assertEquals(uribuilder.build(), built);


        String encodeddestination = URLEncoder.encode(DESTINATION.toString(), "utf-8");


        verify(builder).scheme(SCHEME);
        verify(builder).authority(HOST);
        verify(builder).appendPath("click");
        verify(builder).appendEncodedPath("camref:" + CAMREF);
        verify(builder).appendEncodedPath("destination:" + encodeddestination);
    }

    @Test
    public void testValidBuildWithAlias() {

        MeasurementServiceURIBuilder uribuilder = new MeasurementServiceURIBuilder(helper, factory);
        uribuilder.setCamref(CAMREF)
                .setDestination(DESTINATION)
                .putAlias("alias", "analias");

        Uri built = Uri.parse("http://www.performancehorizon.com");
        when(builder.build()).thenReturn(built);
        Assert.assertEquals(uribuilder.build(), built);

        verify(builder).appendQueryParameter("alias", "analias");

    }

    @Test
    public void testValidBuildSkippingDeepLinkWitDeepLink() throws Exception {

        MeasurementServiceURIBuilder uribuilder = new MeasurementServiceURIBuilder(helper, factory);
        uribuilder.setCamref(CAMREF)
                .setDestination(DESTINATION)
                .setDeeplink(Uri.parse("http://www.google.com"))
                .setSkipDeepLink(true);

        Uri built = Uri.parse("http://www.performancehorizon.com?deep_link=" + URLEncoder.encode("http://www.google.com", "utf-8"));
        when(builder.build()).thenReturn(built);
        Assert.assertEquals(uribuilder.build(), built);

        verify(builder).appendQueryParameter("skip_deep_link", "true");
    }

    @Test
    public void testValidBuildSkippingDeepLinkWithNoDeepLink() {

        MeasurementServiceURIBuilder uribuilder = new MeasurementServiceURIBuilder(helper, factory);
        uribuilder.setCamref(CAMREF)
                .setDestination(DESTINATION)
                .setSkipDeepLink(true);

        Uri built = Uri.parse("http://www.performancehorizon.com");
        when(builder.build()).thenReturn(built);
        Assert.assertEquals(uribuilder.build(), built);

    }

    @Test
    public void testValidBuildWithDeepLink() {

        MeasurementServiceURIBuilder uribuilder = new MeasurementServiceURIBuilder(helper, factory);
        uribuilder.setCamref(CAMREF)
                .setDestination(DESTINATION)
                .setDeeplink(DESTINATION);

        Uri built = Uri.parse("http://www.performancehorizon.com");
        when(builder.build()).thenReturn(built);
        Assert.assertEquals(uribuilder.build(), built);

        verify(builder).appendQueryParameter("deep_link", DESTINATION.toString());
    }
}
