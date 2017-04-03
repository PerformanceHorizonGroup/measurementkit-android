package com.performancehorizon.measurementkit;

import android.content.Intent;
import android.net.Uri;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 09/02/16.
 */

@RunWith(AndroidJUnit4.class)
public class TestMeasurementService_RegisterComplete {

    private MeasurementService service;

    private MeasurementServiceConfiguration configuration;
    private RegisterRequestQueue registerQueue;
    private EventRequestQueue eventQueue;
    private RegisterRequest request;
    private Reachability reachability;
    private MeasurementServiceStorage storage;
    private RegistrationProcessor processor;
    private MeasurementService.RegistrationProcessorFactory processorFactory;
    private MeasurementServiceCallback callback;

    private static final String RESULT = "RESULT";
    private static final String CAMREF = "CAMREF";

    @Before
    public void initialise()
    {
        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());

        configuration = mock(MeasurementServiceConfiguration.class);
        registerQueue = mock(RegisterRequestQueue.class);
        eventQueue = mock(EventRequestQueue.class);
        request = mock(RegisterRequest.class);
        reachability = mock(Reachability.class);
        storage = mock(MeasurementServiceStorage.class);
        processor = mock(RegistrationProcessor.class);
        processorFactory = mock(MeasurementService.RegistrationProcessorFactory.class);
        callback = mock(MeasurementServiceCallback.class);

        when(configuration.getDebugModeActive()).thenReturn(false);
        when(processorFactory.getRequestProcessor(RESULT)).thenReturn(processor);
        when(reachability.isNetworkActive()).thenReturn(true);

        service = new MeasurementService(configuration, registerQueue, eventQueue, null);
        service.putMeasurementStorage(storage);
        service.putReachability(reachability);
        service.putEventQueue(eventQueue);

        //we're not interested in interactions in the constructor.
        reset(eventQueue);
        reset(storage);
    }

    @Test
    public void testRegisterRequestInActiveState() {

        service.putStatus(MeasurementService.MeasurementServiceStatus.ACTIVE);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        //ignored, no tracking result stored.
        verify(storage, times(0)).putTrackingID(anyString());

        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.ACTIVE);
    }

    @Test
    public void testRegisterRequestInHaltedState() {

        service.putStatus(MeasurementService.MeasurementServiceStatus.HALTED);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        //ignored, no tracking result stored.
        verify(storage, times(0)).putTrackingID(anyString());

        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.HALTED);
    }

    @Test
    public void testRegisterRequestInInactiveState() {

        service.putStatus(MeasurementService.MeasurementServiceStatus.INACTIVE);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        //ignored, no tracking result stored.
        verify(storage, times(0)).putTrackingID(anyString());

        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.INACTIVE);
    }

    @Test
    public void testRegisterRequestInPreInitState() {

        service.putStatus(MeasurementService.MeasurementServiceStatus.AWAITING_INITIALISE);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        //ignored, no tracking result stored.
        verify(storage, times(0)).putTrackingID(anyString());

        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.AWAITING_INITIALISE);
    }

    @Test
    public void testMatchingCamrefCleared() {
        when(request.getCamref()).thenReturn(CAMREF);
        when(storage.getCamRef()).thenReturn(CAMREF);

        service.putStatus(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        verify(storage).clearCamref();
    }

    @Test
    public void testMatchingReferrerCleared() {
        when(request.getReferrer()).thenReturn("REFERRER");
        when(storage.getReferrer()).thenReturn("REFERRER");

        service.putStatus(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        verify(storage).clearReferrer();
    }

    @Test
    public void testRegistrationFailedWithOldTrackingID() {
        when(processor.hasRegistrationFailed()).thenReturn(true);
        when(storage.getTrackingID()).thenReturn("tracking_id");

        service.putStatus(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.ACTIVE);
        verify(registerQueue).setQueueIsPaused(false);
        verify(eventQueue).setQueueIsPaused(false);
        verify(eventQueue).setTrackingIDForIncompleteRequests("tracking_id");
    }

    @Test
    public void testRegistrationFailedWithNoPreviousTrackingID() {
        when(processor.hasRegistrationFailed()).thenReturn(true);
        when(storage.getTrackingID()).thenReturn(null);

        service.putStatus(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.INACTIVE);

        verify(registerQueue).setQueueIsPaused(false);
        verify(eventQueue).setQueueIsPaused(true);

        verify(eventQueue).clearIncompleteRequests();
        verify(storage).putTrackingInactive();
    }

    @Test
    public void testRegistrationSucess() {
        when(processor.getTrackingID()).thenReturn("tracking_id");

        service.putStatus(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.ACTIVE);

        verify(this.storage).putTrackingID("tracking_id");

        verify(registerQueue).setQueueIsPaused(false);
        verify(eventQueue).setQueueIsPaused(false);
    }

    @Test
    public void testRegistrationSucessCallback() {
        when(processor.getTrackingID()).thenReturn("tracking_id");

        service.putStatus(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.setCallback(callback);
        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.ACTIVE);

        verify(this.callback).MeasurementServiceDidCompleteRegistration(service, "tracking_id");
    }

    @Test
    public void testRegistrationSucessReferrer() throws Exception {
        when(processor.getTrackingID()).thenReturn("tracking_id");

        Uri referrer = Uri.parse("http://www.google.com");
        when(processor.getReferrer()).thenReturn(referrer);

        service.putStatus(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.ACTIVE);
        Assert.assertEquals(service.getReferrer(), referrer);
    }

    @Test
    public void testRegistrationSucessWithDeepLink() throws Exception {

        when(processor.getTrackingID()).thenReturn("tracking_id");

        Uri deeplink = Uri.parse("http://www.google.com");
        when(processor.getDeeplink()).thenReturn(deeplink);

        service.putStatus(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.ACTIVE);

        Intent deeplinkintent = service.getDeepLinkIntent();
        Assert.assertEquals(deeplinkintent.getData(), deeplink);
        Assert.assertEquals(deeplinkintent.getAction(), Intent.ACTION_VIEW);
    }

    @Test
    public void testRegistrationSucessWithDeepLinkAndCallback() throws Exception {

        when(processor.getTrackingID()).thenReturn("tracking_id");

        Uri deeplink = Uri.parse("http://www.google.com");
        when(processor.getDeeplink()).thenReturn(deeplink);

        service.putStatus(MeasurementService.MeasurementServiceStatus.QUERYING);
        service.setCallback(callback);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.ACTIVE);
        verify(callback).MeasurementServiceWillOpenDeepLink(service, deeplink);
    }

}
