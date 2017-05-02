package com.performancehorizon.measurementkit;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 04/02/16.
 */

@RunWith(AndroidJUnit4.class)
public class TestMeasurementService_Event {


    private MeasurementService service;

    MeasurementServiceConfiguration configuration;
    RegisterRequestQueue registerQueue;
    EventRequestQueue eventQueue;

    //mocks for constructor

    private Reachability reachability;
    private MeasurementServiceStorage storage;

    private MeasurementService.EventRequestFactory eventRequestFactory;
    private EventRequest eventRequest;

    @Before
    public void initialise()
    {
        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());

        configuration = mock(MeasurementServiceConfiguration.class);
        registerQueue = mock(RegisterRequestQueue.class);
        eventQueue = mock(EventRequestQueue.class);
        reachability = mock(Reachability.class);
        storage = mock(MeasurementServiceStorage.class);
        eventRequestFactory = mock (MeasurementService.EventRequestFactory.class);
        eventRequest = mock(EventRequest.class);

        when(configuration.getDebugModeActive()).thenReturn(false);

        service = new MeasurementService(configuration, registerQueue, eventQueue, null);
        service.putMeasurementStorage(storage);
        service.putReachability(reachability);

        when(eventRequestFactory.getEventRequest(any(Event.class), anyString())).thenReturn(eventRequest);
        when(eventRequestFactory.getEventRequest(any(Event.class))).thenReturn(eventRequest);
    }


    @Test
    public void testTrackEventBeforeInitialise()
    {
        Event event = mock(Event.class);

        service.trackEvent(event);

        verify(registerQueue, times(0)).setQueueIsPaused(anyBoolean());
        verify(eventQueue, times(0)).setQueueIsPaused(anyBoolean());
        verify(this.eventQueue, times(0)).addEventRequest(argThat(new EventRequestNoTrackingID()));
    }

    @Test
    public void testTrackEventWhenActiveNullTrackingID()
    {
        Event event = mock(Event.class);

        service.putStatus(MeasurementService.MeasurementServiceStatus.ACTIVE);

        service.trackEvent(event, eventRequestFactory);

        verify(eventRequestFactory).getEventRequest(event);
        verify(eventQueue).addEventRequest(eventRequest);
    }

    @Test
    public void testTrackEventWhenActiveWithTrackingID()
    {
        Event event = mock(Event.class);

        service.putStatus(MeasurementService.MeasurementServiceStatus.ACTIVE);
        when(storage.getTrackingID()).thenReturn("tracking_id");

        service.trackEvent(event, eventRequestFactory);

        verify(eventRequestFactory).getEventRequest(event, "tracking_id");
        verify(eventQueue).addEventRequest(eventRequest);
    }

    @Test
    public void testTrackEventWhenInactive()
    {
        Event event = mock(Event.class);

        service.putStatus(MeasurementService.MeasurementServiceStatus.INACTIVE);
        service.trackEvent(event, eventRequestFactory);

        verify(eventQueue, times(0)).addEventRequest(eventRequest);
    }

    @Test
    public void testTrackEventWhenQuerying()
    {
        Event event = mock(Event.class);

        service.putStatus(MeasurementService.MeasurementServiceStatus.QUERYING);
        service.trackEvent(event, eventRequestFactory);

        verify(eventRequestFactory).getEventRequest(event);
        verify(eventQueue).addEventRequest(eventRequest);
    }

    @Test
    public void testTrackEventWhenHalted()
    {
        Event event = mock(Event.class);

        service.putStatus(MeasurementService.MeasurementServiceStatus.HALTED);
        service.trackEvent(event, eventRequestFactory);

        verify(eventRequestFactory).getEventRequest(event);
        verify(eventQueue).addEventRequest(eventRequest);
    }


    //tests for queue restarts on events.

    @Test
    public void testQueuesRestartOnEventBeforeInitialise()
    {
        Event event = mock(Event.class);

        when(reachability.isNetworkActive()).thenReturn(true);

        service.trackEvent(event);

        verify(eventQueue).setQueueIsPaused(true);
        verify(registerQueue).setQueueIsPaused(false);
    }


    @Test
    public void testQueuesRestartOnEventWhenActive()
    {
        Event event = mock(Event.class);

        service.putStatus(MeasurementService.MeasurementServiceStatus.ACTIVE);
        when(reachability.isNetworkActive()).thenReturn(true);

        service.trackEvent(event);

        verify(eventQueue).setQueueIsPaused(false);
        verify(registerQueue).setQueueIsPaused(false);
    }

    @Test
    public void testQueuesRestartOnEventWhenInactive()
    {
        Event event = mock(Event.class);

        service.putStatus(MeasurementService.MeasurementServiceStatus.INACTIVE);
        when(reachability.isNetworkActive()).thenReturn(true);

        service.trackEvent(event);

        verify(eventQueue).setQueueIsPaused(true);
        verify(registerQueue).setQueueIsPaused(false);
    }

    @Test
    public void testQueuesRestartOnEventWhenQuerying()
    {
        Event event = mock(Event.class);

        service.putStatus(MeasurementService.MeasurementServiceStatus.QUERYING);
        when(reachability.isNetworkActive()).thenReturn(true);

        service.trackEvent(event);

        verify(eventQueue).setQueueIsPaused(true);
        verify(registerQueue).setQueueIsPaused(false);
    }

    @Test
    public void testQueuesRestartOnEventWhenHalted()
    {
        Event event = mock(Event.class);

        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.HALTED);

        service.putStatus(MeasurementService.MeasurementServiceStatus.HALTED);
        when(reachability.isNetworkActive()).thenReturn(true);

        service.trackEvent(event);

        verify(eventQueue).setQueueIsPaused(true);
        verify(registerQueue).setQueueIsPaused(false);
    }

    private class EventRequestNoTrackingID extends ArgumentMatcher<EventRequest>{

        @Override
        public boolean matches(Object argument) {
            EventRequest request = (EventRequest)argument;

            return request.getTrackingID() == null;
        }
    }
}
