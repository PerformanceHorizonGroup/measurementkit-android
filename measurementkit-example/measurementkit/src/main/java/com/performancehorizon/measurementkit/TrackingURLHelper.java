package com.performancehorizon.measurementkit;

/**
 * Created by owainbrown on 25/03/15.
 */
public class TrackingURLHelper {

    private boolean isDebug = false;

    public String urlStringForTracking() {
        return (this.isDebug) ? "http://m.prf.local": "https://m.prfhn.com";
    }

    public boolean isDebug() {
        return isDebug;
    }

    public void setDebug(boolean isDebug) {
        this.isDebug = isDebug;
    }
}
