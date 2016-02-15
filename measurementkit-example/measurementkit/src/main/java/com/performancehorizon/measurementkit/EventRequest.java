package com.performancehorizon.measurementkit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by owainbrown on 25/01/16.
 */
public class EventRequest {

    @Nullable
    private String trackingID;

    @NonNull
    private Event event;

    public EventRequest(@NonNull Event event, @NonNull String trackingID) {
        this.trackingID= trackingID;
        this.event = event;
    }

    public EventRequest(@NonNull Event event) {
        this.event = event;
    }

    @Nullable
    public String getTrackingID() {
        return trackingID;
    }

    public void setTrackingID(@NonNull String trackingID) {
        this.trackingID = trackingID;
    }

    @NonNull
    public Event getEvent() {
        return event;
    }
}
