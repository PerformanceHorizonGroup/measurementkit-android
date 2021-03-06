package com.performancehorizon.measurementkit;

/**
 * Created by owainbrown on 14/01/16.
 */
public class MeasurementServiceConfiguration {
    private boolean debugModeActive = false;
    private boolean trackAndroidAdvertisingIdentifier = true;
    private boolean useActiveFingerprinting = false;
    private boolean debugLogActive = false;

    public String toString() {
        return String.format("Configuration:  [ debug urls %b, track AAID %b, active fingerprinting %b, Debug log active %b ]",
                this.debugModeActive, this.trackAndroidAdvertisingIdentifier, this.useActiveFingerprinting, this.debugLogActive);
    }

    protected boolean useActiveFingerprinting() {
        return this.useActiveFingerprinting;
    }

    protected void setUseActiveFingerprinting(boolean useActiveFingerprinting) {
        this.useActiveFingerprinting = useActiveFingerprinting;
    }

    public void setDebugLogActive(boolean shouldExtendedLog) {
        this.debugLogActive = shouldExtendedLog;
    }

    public boolean isDebugLogActive() {
        return this.debugLogActive;
    }

    public void setTrackAndroidAdvertisingIdentifier(boolean trackAaid)
    {
        this.trackAndroidAdvertisingIdentifier = trackAaid;
    }

    public boolean getTrackAndroidAdvertisingIdentifier() {
        return this.trackAndroidAdvertisingIdentifier;
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
        debugconfig.setUseActiveFingerprinting(false);

        return debugconfig;
    }

    public static MeasurementServiceConfiguration activeFingerprintConfig() {
        MeasurementServiceConfiguration activeconfig = new MeasurementServiceConfiguration();
        activeconfig.setUseActiveFingerprinting(true);
        activeconfig.setDebugMode(false);

        return activeconfig;
    }
}
