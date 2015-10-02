package com.performancehorizon.mobiletracking;

/**
 * Created by owainbrown on 02/03/15.
 */
public interface MobileTrackingRequestQueueDelegate {

    public void requestQueueDidCompleteRequest(MobileTrackingRequestQueue queue, MobileTrackingRequest request, String result);
    public void requestQueueErrorOnRequest(MobileTrackingRequestQueue queue, MobileTrackingRequest request, Exception error);
}
