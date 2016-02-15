package com.performancehorizon.measurementkit;

import android.content.Intent;
import android.net.Uri;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 09/02/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT,sdk = 21)
public class TestMeasurementService_RegisterComplete {

    private MeasurementService service;

    @Mock
    MeasurementServiceConfiguration configuration;
    @Mock RegisterRequestQueue registerQueue;
    @Mock EventRequestQueue eventQueue;

    @Mock RegisterRequest request;

    //mocks for constructor

    @Mock private Reachability reachability;
    @Mock private MeasurementServiceStorage storage;

    @Mock private RegistrationProcessor processor;
    @Mock private MeasurementService.RegistrationProcessorFactory processorFactory;
    @Mock private MeasurementServiceCallback callback;

    private static final String RESULT = "RESULT";
    private static final String CAMREF = "CAMREF";

    @Before
    public void initialise()
    {
        MockitoAnnotations.initMocks(this);

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

        verifyZeroInteractions(storage, eventQueue);
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.ACTIVE);
    }

    @Test
    public void testRegisterRequestInHaltedState() {

        service.putStatus(MeasurementService.MeasurementServiceStatus.HALTED);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        verifyZeroInteractions(storage, eventQueue);
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.HALTED);
    }

    @Test
    public void testRegisterRequestInInactiveState() {

        service.putStatus(MeasurementService.MeasurementServiceStatus.INACTIVE);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        verifyZeroInteractions(storage, eventQueue);
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.INACTIVE);
    }

    @Test
    public void testRegisterRequestInPreInitState() {

        service.putStatus(MeasurementService.MeasurementServiceStatus.AWAITING_INITIALISE);

        service.registerRequestQueueDidComplete(registerQueue, request, RESULT, processorFactory);

        verifyZeroInteractions(storage, eventQueue);
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
        verify(registerQueue).setQueueIsPaused(true);
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
        verify(registerQueue).setQueueIsPaused(true);
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
        verify(registerQueue).setQueueIsPaused(true);
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
