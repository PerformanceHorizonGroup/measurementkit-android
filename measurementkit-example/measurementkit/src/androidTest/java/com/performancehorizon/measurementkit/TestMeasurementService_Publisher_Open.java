package com.performancehorizon.measurementkit;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 09/02/16.
 */

//@RunWith(AndroidJUnit4.class)
//public class TestMeasurementService_Publisher_Open {
//
//    private Intent clonedIntent;
//    private Intent alternativeIntent;
//    private Context context;
//    private MeasurementService.IntentFactory intentFactory;
//
//    @Before
//    public void initialise() {
//
//        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());
//
//        clonedIntent = mock(Intent.class);
//        alternativeIntent = mock(Intent.class);
//        context = mock(Context.class);
//        intentFactory = mock(MeasurementService.IntentFactory.class);
//
//        when(intentFactory.getIntent(any(Intent.class))).thenReturn(clonedIntent);
//        when(intentFactory.getIntent(eq(Intent.ACTION_VIEW), any(Uri.class))).thenReturn(alternativeIntent);
//    }
//
//    @Test
//    public void testOpenURI() throws Exception {
//
//        MeasurementService.openIntentWithAlternativeURI(context, new Intent(Intent.ACTION_DEFAULT, Uri.parse("exactview://www.google.com")),
//                "CAMREF", Uri.parse("http://www.google.com"), intentFactory, false);
//
//        verify(context, timeout(100)).startActivity(clonedIntent);
//    }
//
//    @Test
//    public void testOpenURIFirstIntentFails() throws Exception {
//
//        //when(context.startActivity(eq(clonedIntent))).thenThrow(new Exception());
//
//        doThrow(new ActivityNotFoundException()).when(context).startActivity(clonedIntent);
//
//        MeasurementService.openIntentWithAlternativeURI(context, new Intent(Intent.ACTION_DEFAULT, Uri.parse("exactview://www.google.com")),
//                "CAMREF", Uri.parse("http://www.google.com"), intentFactory, false);
//
//        verify(context, timeout(100)).startActivity(clonedIntent);
//        verify(context, timeout(100)).startActivity(alternativeIntent);
//    }
//}
