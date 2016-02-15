package com.performancehorizon.measurementkit;


import android.net.Uri;

/**
 * Created by owainbrown on 23/09/15.
 */
public interface MeasurementServiceCallback {

    void MeasurementServiceDidCompleteRegistration(MeasurementService service, String mobileTrackingID);
    boolean MeasurementServiceWillOpenDeepLink(MeasurementService service, Uri deeplink);
}
