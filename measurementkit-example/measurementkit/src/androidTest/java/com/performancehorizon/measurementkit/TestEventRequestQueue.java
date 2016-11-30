package com.performancehorizon.measurementkit;

import android.support.test.InstrumentationRegistry;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 03/02/16.
 */
public class TestEventRequestQueue {

    private EventRequestQueue.EventRequestJSONFactory jsonFactory;
    private EventRequestJSONBuilder builder;

    private TrackingRequestQueue trackingQueue;
    private TrackingRequestFactory requestFactory;
    private TrackingURLHelper trackingHelper;

    private String TRACKINGURL = "http://mobiletracking";

    @Before
    public void startup() {

        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());

        //dependency setup
        this.builder = mock(EventRequestJSONBuilder.class);
        when(this.builder.setCampaignID(anyString())).thenReturn(this.builder);
        when(this.builder.setEvent(any(Event.class))).thenReturn(this.builder);
        when(this.builder.setMobileTrackingID(anyString())).thenReturn(this.builder);

        this.jsonFactory = mock(EventRequestQueue.EventRequestJSONFactory.class);
        when(this.jsonFactory.getBuilder()).thenReturn(this.builder);

        this.trackingQueue = mock(TrackingRequestQueue.class);

        this.trackingHelper = mock(TrackingURLHelper.class);
        when(this.trackingHelper.urlStringForTracking()).thenReturn(TRACKINGURL);

        this.requestFactory = mock(TrackingRequestFactory.class);
    }

    @Test
    public void testAddRequestWithNoCampaignID() {

        EventRequestQueue queue = new EventRequestQueue(this.trackingQueue, this.requestFactory, this.trackingHelper, this.jsonFactory);

        EventRequest arequest = new EventRequest(mock(Event.class));
        queue.addEventRequest(arequest);

        //don't queue it, put it in the incompletes.
        verify(trackingQueue, times(0)).enqueueRequest(any(TrackingRequest.class));
        Assert.assertEquals(queue.getIncompleteEventRequests().size(), 1);
    }

    @Test
    public void testAddRequestWithNoTrackingID() {

        EventRequestQueue queue = new EventRequestQueue(this.trackingQueue, this.requestFactory, this.trackingHelper, this.jsonFactory);
        queue.setCampaignID("campaignid");

        EventRequest arequest = new EventRequest(mock(Event.class));
        queue.addEventRequest(arequest);

        //don't queue it, put it in the incompletes.
        verify(trackingQueue, times(0)).enqueueRequest(any(TrackingRequest.class));
        Assert.assertEquals(queue.getIncompleteEventRequests().size(), 1);
    }

    @Test
    public void testAddRequestWithInvalidJSON() {

        when(builder.build()).thenReturn(null);

        EventRequestQueue queue = new EventRequestQueue(this.trackingQueue, this.requestFactory, this.trackingHelper, this.jsonFactory);
        queue.setCampaignID("campaignid");

        EventRequest arequest = new EventRequest(mock(Event.class));
        arequest.setTrackingID("trackingid");

        queue.addEventRequest(arequest);

        //don't queue it, don't put it in the incompletes
        verify(trackingQueue, times(0)).enqueueRequest(any(TrackingRequest.class));
        Assert.assertEquals(queue.getIncompleteEventRequests().size(), 0);
    }

    @Test
    public void testAddValidRequest() {

        JSONObject requestjson = new JSONObject();
        when(builder.build()).thenReturn(requestjson);

        when(this.requestFactory.getRequest(anyString(), eq(requestjson))).thenReturn(mock(TrackingRequest.class));

        EventRequestQueue queue = new EventRequestQueue(this.trackingQueue, this.requestFactory, this.trackingHelper, this.jsonFactory);
        queue.setCampaignID("campaignid");

        EventRequest arequest = new EventRequest(mock(Event.class));
        arequest.setTrackingID("trackingid");

        queue.addEventRequest(arequest);

        //should be added to queue
        verify(trackingQueue).enqueueRequest(any(TrackingRequest.class));
        Assert.assertEquals(queue.getIncompleteEventRequests().size(), 0);
    }

    @Test
    public void testSetDelegate() {

        EventRequestQueue queue = new EventRequestQueue(this.trackingQueue, this.requestFactory, this.trackingHelper, this.jsonFactory);

        TrackingRequestQueueDelegate mockdelegate = mock(TrackingRequestQueueDelegate.class);

        queue.setDelegate(mockdelegate);

        verify(trackingQueue).setDelegate(mockdelegate);

    }

    @Test
    public void testSetQueueIsPaused() {
        EventRequestQueue queue = new EventRequestQueue(this.trackingQueue, this.requestFactory, this.trackingHelper, this.jsonFactory);

        queue.setQueueIsPaused(true);
        verify(trackingQueue).setQueueIsPaused(true);
    }

    @Test
    public void testClearIncompleteRequest() {

        //add an incomplete request
        EventRequestQueue queue = new EventRequestQueue(this.trackingQueue, this.requestFactory, this.trackingHelper, this.jsonFactory);

        EventRequest arequest = new EventRequest(mock(Event.class));
        queue.addEventRequest(arequest);

        Assert.assertEquals(1, queue.getIncompleteEventRequests().size());

        queue.clearIncompleteRequests();

        Assert.assertEquals(0, queue.getIncompleteEventRequests().size());
    }

    @Test
    public void testEnqueueIncompletesLeaveInQueue() {

        EventRequestQueue queue = new EventRequestQueue(this.trackingQueue, this.requestFactory, this.trackingHelper, this.jsonFactory);

        EventRequest arequest = new EventRequest(mock(Event.class));
        queue.addEventRequest(arequest);

        queue.enqueueIncompletes();

        //should still be in the incompletes
        verify(trackingQueue, times(0)).enqueueRequest(any(TrackingRequest.class));
        Assert.assertEquals(queue.getIncompleteEventRequests().size(), 1);
    }

    @Test
    public void testEnqueueIncompletesAddToQueue() {

        EventRequestQueue queue = new EventRequestQueue(this.trackingQueue, this.requestFactory, this.trackingHelper, this.jsonFactory);
        queue.setCampaignID("campaign_id");

        EventRequest arequest = new EventRequest(mock(Event.class));

        queue.addEventRequest(arequest);

        Assert.assertEquals(1, queue.getIncompleteEventRequests().size());

        arequest.setTrackingID("trackingid");

        JSONObject requestjson = new JSONObject();
        when(builder.build()).thenReturn(requestjson);

        when(this.requestFactory.getRequest(anyString(), eq(requestjson))).thenReturn(mock(TrackingRequest.class));

        queue.enqueueIncompletes();

        verify(trackingQueue).enqueueRequest(any(TrackingRequest.class));
        Assert.assertEquals(queue.getIncompleteEventRequests().size(), 0);
    }

    @Test
    public void testEnqueueIncompletesBySettingTrackingID() {

        EventRequestQueue queue = new EventRequestQueue(this.trackingQueue, this.requestFactory, this.trackingHelper, this.jsonFactory);
        queue.setCampaignID("campaign_id");

        EventRequest arequest = new EventRequest(mock(Event.class));

        queue.addEventRequest(arequest);

        Assert.assertEquals(1, queue.getIncompleteEventRequests().size());

        JSONObject requestjson = new JSONObject();
        when(builder.build()).thenReturn(requestjson);

        when(this.requestFactory.getRequest(anyString(), eq(requestjson))).thenReturn(mock(TrackingRequest.class));

        queue.setTrackingIDForIncompleteRequests("trackingid");

        verify(trackingQueue).enqueueRequest(any(TrackingRequest.class));
        Assert.assertEquals(queue.getIncompleteEventRequests().size(), 0);
    }
}
