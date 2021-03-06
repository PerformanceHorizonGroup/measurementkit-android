package com.performancehorizon.measurementkit;



import android.util.Log;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by owainbrown on 25/01/16.
 */
public class RegisterRequestJSONBuilder {


    private RegisterRequest request;

    private boolean isValid() {
        return this.request.getAdvertiserID() != null &&
                this.request.getCampaignID() != null &&
                this.request.getFingerprint() != null;

    }


    public JSONObject build() {

        if (this.isValid()) {
            try {
                JSONObject requestjson = new JSONObject();

                requestjson.put("advertiser_id", this.request.getAdvertiserID());
                requestjson.put("campaign_id", this.request.getCampaignID());

                JSONObject fingerprint = new JSONObject();

                for (Map.Entry<String, String> entry: this.request.getFingerprint().entrySet()) {
                    fingerprint.put(entry.getKey(), entry.getValue());
                }

                requestjson.put("fingerprint", fingerprint);

                if (this.request.getCamref() != null) {
                    requestjson.put("camref", this.request.getCamref());
                }

                if (this.request.getReferrer() != null) {
                    requestjson.put("google_playstore_referrer", this.request.getReferrer());
                }

                if (this.request.getAndroidAdvertisingIdentifier() != null) {
                    requestjson.put("aaid", this.request.getAndroidAdvertisingIdentifier());
                }

                if (this.request.getInstalled()) {
                    requestjson.put("install", true);
                }

                return requestjson;
            }
            catch(Exception jsonexception) {
                MeasurementServiceLog.e("Register Request Builder - registration");
            }
        }

        return null;
    }

    public RegisterRequestJSONBuilder setRequest( RegisterRequest request) {
        this.request = request;
        return this;
    }
}
