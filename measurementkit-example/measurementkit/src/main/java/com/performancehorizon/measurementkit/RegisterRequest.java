package com.performancehorizon.measurementkit;

import android.content.Context;

import java.util.Map;

/**
 * Register Request - parameters required for a call to the /register endpoint in the mobile tracking API
 *
 * Do not construct an instance of this class on the main thread.
 */
public class RegisterRequest {

    private String advertiserID;
    private String campaignID;
    private Map<String, String> fingerprint;
    private String camref;
    private String referrer;
    private String androidAdvertisingIdentifier;
    private boolean installed = false;
    private boolean shouldOverwrite = true;

    public RegisterRequest( Context context) {
        this(context, true);
    }

    /**
     * @param context
     * @param trackAndroidAdvertisingIdentifier
     *
     * Do not use this constructor on the main thread, this may cause unexpected exceptions.
     */
    public RegisterRequest( Context context, boolean trackAndroidAdvertisingIdentifier) {

        if (context != null && trackAndroidAdvertisingIdentifier) {
            try {

                // to remove the need for a compile-time dependency, which is problematic, we'll access the google play services-ads
                // advertising identifier

                if (Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient") != null) {

                    // ad-id class
                    Class<?> advertisingidclientclass = Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient");

                    // instance of AdvertisingIdClient.info
                    Object advertisingidinfo = advertisingidclientclass.getMethod("getAdvertisingIdInfo", Context.class).invoke(null, context);

                    this.androidAdvertisingIdentifier = (String) advertisingidinfo.getClass().getMethod("getId").invoke(advertisingidinfo);

                    //please note, as we're only using the advertising for attribution, we don't consult the limit ad tracking setting.
                }
            } catch (Exception failedaaid) {
                MeasurementServiceLog.d("Register Request - Retrieval of advertising identifier failed with exception: " + failedaaid.toString());
            }
        }
    }

    public RegisterRequest setAdvertiserID( String advertiserID) {
        this.advertiserID = advertiserID;
        return this;
    }

    public RegisterRequest setCampaignID( String campaignID) {
        this.campaignID = campaignID;
        return this;
    }

    public RegisterRequest setFingerprint( Map<String, String> fingerprint) {
        this.fingerprint = fingerprint;
        return this;
    }

    public RegisterRequest setCamref( String camref) {
        this.camref = camref;
        return this;
    }

    public RegisterRequest setReferrer( String referrer) {
        this.referrer = referrer;
        return this;
    }

    public RegisterRequest setInstalled() {
        this.installed=true;
        return this;
    }


    public String getAdvertiserID() {
        return advertiserID;
    }


    public String getCampaignID() {
        return campaignID;
    }


    public Map<String, String> getFingerprint() {
        return fingerprint;
    }


    public String getCamref() {
        return camref;
    }


    public String getReferrer() {
        return referrer;
    }


    public String getAndroidAdvertisingIdentifier() {return androidAdvertisingIdentifier;};

    public boolean getInstalled() {return installed;}

    public boolean getShouldOverwrite() { return shouldOverwrite;}

    public void setShouldOverwrite(boolean shouldOverwrite) { this.shouldOverwrite = shouldOverwrite;}
}
