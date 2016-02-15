package com.performancehorizon.measurementkit;

import junit.framework.Assert;

import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by owainbrown on 03/02/16.
 */
public class TestEventRequest {

    @Test
    public void testEventWithID() {

        Event pretendevent = mock(Event.class);

        EventRequest eventrequest = new EventRequest(pretendevent, "tracking");

        Assert.assertEquals(eventrequest.getTrackingID(), "tracking");
        Assert.assertEquals(eventrequest.getEvent(), pretendevent);
    }

    @Test
    public void testEvent() {

        Event pretendevent = mock(Event.class);

        EventRequest eventrequest = new EventRequest(pretendevent);

        Assert.assertNull(eventrequest.getTrackingID());
        Assert.assertEquals(eventrequest.getEvent(), pretendevent);
    }

    @Test
    public void testSetTrackingID() {
        Event pretendevent = mock(Event.class);

        EventRequest eventrequest = new EventRequest(pretendevent);

        eventrequest.setTrackingID("tracking");

        Assert.assertEquals(eventrequest.getTrackingID(), "tracking");
    }

}
