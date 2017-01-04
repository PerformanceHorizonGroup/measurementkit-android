package com.performancehorizon.measurementkit;



/**
 * Created by owainbrown on 25/01/16.
 */
public interface RegisterRequestQueueDelegate {

    void registerRequestQueueDidComplete(RegisterRequestQueue queue,  RegisterRequest request, String result);
    void registerRequestQueueDidError(RegisterRequestQueue queue,  RegisterRequest request, Exception error);
}
