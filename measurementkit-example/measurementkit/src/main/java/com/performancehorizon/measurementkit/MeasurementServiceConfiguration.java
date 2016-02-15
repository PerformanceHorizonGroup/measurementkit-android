package com.performancehorizon.measurementkit;

/**
 * Created by owainbrown on 14/01/16.
 */
public class MeasurementServiceConfiguration {
    private boolean debugModeActive = false;
    private boolean doNotTrackAAID = false;

    /*private String trackingIDQueryKey = null;
    private String camrefExtraKey = null;

    public String getTrackingIDQueryKey() {
        return trackingIDQueryKey;
    }

    public void setTrackingIDQueryKey(String trackingIDQueryKey) {
        this.trackingIDQueryKey = trackingIDQueryKey;
    }

    public String getCamrefExtraKey() {
        return camrefExtraKey;
    }

    public void setCamrefExtraKey(String camrefExtraKey) {
        this.camrefExtraKey = camrefExtraKey;
    }*/

    public void setDoNotTrackAAID(boolean doNotTrackAAID)
    {
        this.doNotTrackAAID = doNotTrackAAID;
    }

    public boolean getDoNoTrackAAID() {
        return this.doNotTrackAAID;
    }

    public void setDebugMode(boolean debugIsActive) {
        this.debugModeActive = debugIsActive;
    }

    public boolean getDebugModeActive() {
        return this.debugModeActive;
    }

    public static MeasurementServiceConfiguration debugConfig()
    {
        MeasurementServiceConfiguration debugconfig = new MeasurementServiceConfiguration();
        debugconfig.setDebugMode(true);

        return debugconfig;
    }
}
