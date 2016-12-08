package com.performancehorizon.measurementkit;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by owainbrown on 26/01/16.
 */
public class MeasurementServiceLog {

    protected final static String TRACKING_LOG = "PHN_MT";

    public static void d(@NonNull String log) {
        Log.d(TRACKING_LOG, log);
    }

    public static void e(@NonNull String log) {
        Log.e(TRACKING_LOG, log);
    }
}
