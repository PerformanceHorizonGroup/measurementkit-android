package com.performancehorizon.measurementkit;

/**
 * Created by owainbrown on 25/03/15.
 */
public class TrackingURLHelper {

    private boolean isDebug = false;

    public String urlStringForTracking() {
        return this.scheme() + "://" + this.hostForMobileTracking();
    }

    public String scheme() {
        return (this.isDebug) ? "http" : "https";
    }

    public String hostForMobileTracking() {
        return (this.isDebug) ? "m.prf.local" : "m.prf.hn";
    }

    public String hostForTracking() {
        return (this.isDebug) ? "prf.local" : "prf.hn";
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }

    public TrackingURLHelper(boolean isDebug) {
        this.isDebug = isDebug;
    }
}
