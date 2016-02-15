package com.performancehorizon.measurementkit;

import android.support.annotation.NonNull;

/**
 * Created by owainbrown on 25/01/16.
 */
public interface RegisterRequestQueueDelegate {

    void registerRequestQueueDidComplete(RegisterRequestQueue queue, @NonNull RegisterRequest request, String result);
    void registerRequestQueueDidError(RegisterRequestQueue queue, @NonNull RegisterRequest request, Exception error);
}
