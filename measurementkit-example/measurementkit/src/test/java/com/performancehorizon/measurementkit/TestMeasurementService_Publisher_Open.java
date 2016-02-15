package com.performancehorizon.measurementkit;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.util.concurrent.RoboExecutorService;

import java.net.URL;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 09/02/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT,sdk = 21)
public class TestMeasurementService_Publisher_Open {

    @Mock private Intent clonedIntent;
    @Mock private Intent alternativeIntent;
    @Mock private Context context;
    @Mock private MeasurementService.IntentFactory intentFactory;

    @Before
    public void initialise() {
        MockitoAnnotations.initMocks(this);

        when(intentFactory.getIntent(any(Intent.class))).thenReturn(clonedIntent);
        when(intentFactory.getIntent(eq(Intent.ACTION_VIEW), any(Uri.class))).thenReturn(alternativeIntent);
    }

    @Test
    public void testOpenURI() throws Exception {

        MeasurementService.openIntentWithAlternativeURI(context, new Intent(Intent.ACTION_DEFAULT, Uri.parse("exactview://www.google.com")),
                "CAMREF", Uri.parse("http://www.google.com"), intentFactory);

        verify(context, timeout(100)).startActivity(clonedIntent);
    }

    @Test
    public void testOpenURIFirstIntentFails() throws Exception {

        //when(context.startActivity(eq(clonedIntent))).thenThrow(new Exception());

        doThrow(new ActivityNotFoundException()).when(context).startActivity(clonedIntent);

        MeasurementService.openIntentWithAlternativeURI(context, new Intent(Intent.ACTION_DEFAULT, Uri.parse("exactview://www.google.com")),
                "CAMREF", Uri.parse("http://www.google.com"), intentFactory);

        verify(context, timeout(100)).startActivity(clonedIntent);
        verify(context, timeout(100)).startActivity(alternativeIntent);
    }
}
