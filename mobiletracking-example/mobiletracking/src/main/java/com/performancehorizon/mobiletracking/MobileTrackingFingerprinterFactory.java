package com.performancehorizon.mobiletracking;

import android.content.Context;

/**
 * Created by owainbrown on 26/03/15.
 */
public class MobileTrackingFingerprinterFactory {

    //TODO: Desperate need to cut down the size of the names.
    public MobileTrackingActiveFingerprinter getActiveFingerprinter(
            Context context,
            MobileTrackingActiveFingerprinter.MobileTrackingActiveFingerprinterCallback callback)
    {
        return new MobileTrackingActiveFingerprinter(context, callback);
    }
}
