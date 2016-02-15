package com.performancehorizon.measurementkit;

import android.content.Context;

//import com.performancehorizon.measurementkit.ActiveFingerprinter;

/**
 * Created by owainbrown on 26/03/15.
 */
public class FingerprinterFactory {

    /*public ActiveFingerprinter getActiveFingerprinter(
            Context context,
            ActiveFingerprinter.MobileTrackingActiveFingerprinterCallback callback)
    {
        return new ActiveFingerprinter(context, callback);
    }*/

    public Fingerprinter getFingerprinter(Context context) {
        return new Fingerprinter(context);
    }
}
