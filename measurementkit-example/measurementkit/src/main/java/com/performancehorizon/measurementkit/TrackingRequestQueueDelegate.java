package com.performancehorizon.measurementkit;

/**
 * Created by owainbrown on 02/03/15.
 */
public interface TrackingRequestQueueDelegate {

    public void requestQueueDidCompleteRequest(TrackingRequestQueue queue, TrackingRequest request, String result);
    public void requestQueueErrorOnRequest(TrackingRequestQueue queue, TrackingRequest request, Exception error);
}
