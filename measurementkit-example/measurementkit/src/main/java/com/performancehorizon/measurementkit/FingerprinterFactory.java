package com.performancehorizon.measurementkit;

import android.content.Context;

import com.performancehorizon.measurementkit.ActiveFingerprinter;

/**
 * Created by owainbrown on 26/03/15.
 */
public class FingerprinterFactory {

    //TODO: Desperate need to cut down the size of the names.
    public ActiveFingerprinter getActiveFingerprinter(
            Context context,
            ActiveFingerprinter.MobileTrackingActiveFingerprinterCallback callback)
    {
        return new ActiveFingerprinter(context, callback);
    }
}
