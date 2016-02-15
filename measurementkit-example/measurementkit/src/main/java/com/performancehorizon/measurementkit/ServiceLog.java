package com.performancehorizon.measurementkit;

import android.support.annotation.NonNull;
import android.util.Log;

/**
 * Created by owainbrown on 26/01/16.
 */
public class ServiceLog {

    public static void debug(@NonNull String log) {
        Log.d("PH MeasurementKit", log);
    }
}
