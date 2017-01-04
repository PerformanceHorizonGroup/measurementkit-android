package com.performancehorizon.measurementkit;

/**
 * Created by owainbrown on 25/01/16.
 */
public class EventRequest {

    private String trackingID;
    private Event event;

    public EventRequest( Event event, String trackingID) {
        this.trackingID= trackingID;
        this.event = event;
    }

    public EventRequest(Event event) {
        this.event = event;
    }

    public String getTrackingID() {
        return trackingID;
    }

    public void setTrackingID(String trackingID) {
        this.trackingID = trackingID;
    }

    public Event getEvent() {
        return event;
    }
}
