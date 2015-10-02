package com.performancehorizon.mobiletracking;

/**
 * Created by owainbrown on 23/09/15.
 */
public interface MobileTrackingServiceCallback {

    void MobileTrackingServiceDidRegister(MobileTrackingService service);
    void MobileTrackingServiceDidRetrieveDeepLink(MobileTrackingService service, String deeplink);
}
