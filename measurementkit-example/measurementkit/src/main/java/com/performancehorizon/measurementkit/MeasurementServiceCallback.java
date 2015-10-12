package com.performancehorizon.measurementkit;


/**
 * Created by owainbrown on 23/09/15.
 */
public interface MeasurementServiceCallback {

    void MeasurementServiceDidRegister(MeasurementService service);
    void MeasurementServiceDidRegisterDidRetrieveDeepLink(MeasurementService service, String deeplink);
}
