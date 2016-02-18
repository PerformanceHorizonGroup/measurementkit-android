package com.performancehorizon.measurementkit;

import android.support.annotation.Nullable;

import com.google.android.gms.ads.identifier.AdvertisingIdClient;
import android.content.Context;

import java.util.Map;

/**
 * Created by owainbrown on 25/01/16.
 *
 * @Warning - do not construct an instance of this class on the main thread.
 */
public class RegisterRequest {

    @Nullable
    private String advertiserID;
    @Nullable
    private String campaignID;
    @Nullable
    private Map<String, String> fingerprint;
    @Nullable
    private String camref;
    @Nullable
    private String referrer;
    @Nullable
    private String aaid;
    private boolean installed = false;

    public RegisterRequest(@Nullable Context context) {
        this(context, false);
    }

    /**
     * @param context
     * @param doNotTrackAAID
     * @Warning Do not use this constructor on the main thread, this may cause unexpected exceptions.
     */
    public RegisterRequest(@Nullable Context context, boolean doNotTrackAAID) {

        if (context != null || doNotTrackAAID) {
            try {
                if (Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient") != null) {

                    AdvertisingIdClient.Info advertisingidinfo = AdvertisingIdClient.getAdvertisingIdInfo(context);

                    //please note, as we're only using the advertising for attribution, we don't consult the limit ad tracking setting.
                    aaid = advertisingidinfo.getId();
                }
            } catch (Exception failedaaid) {
                ServiceLog.debug("Retrieval of advertising identifier failed with exception: " + failedaaid.toString());
            }
        }
    }

    public RegisterRequest setAdvertiserID(@Nullable String advertiserID) {
        this.advertiserID = advertiserID;
        return this;
    }

    public RegisterRequest setCampaignID(@Nullable String campaignID) {
        this.campaignID = campaignID;
        return this;
    }

    public RegisterRequest setFingerprint(@Nullable Map<String, String> fingerprint) {
        this.fingerprint = fingerprint;
        return this;
    }

    public RegisterRequest setCamref(@Nullable String camref) {
        this.camref = camref;
        return this;
    }

    public RegisterRequest setReferrer(@Nullable String referrer) {
        this.referrer = referrer;
        return this;
    }

    public RegisterRequest setInstalled() {
        this.installed=true;
        return this;
    }

    @Nullable
    public String getAdvertiserID() {
        return advertiserID;
    }

    @Nullable
    public String getCampaignID() {
        return campaignID;
    }

    @Nullable
    public Map<String, String> getFingerprint() {
        return fingerprint;
    }

    @Nullable
    public String getCamref() {
        return camref;
    }

    @Nullable
    public String getReferrer() {
        return referrer;
    }

    @Nullable
    public String getAaid() {return aaid;};

    public boolean getInstalled() {return installed;}
}
