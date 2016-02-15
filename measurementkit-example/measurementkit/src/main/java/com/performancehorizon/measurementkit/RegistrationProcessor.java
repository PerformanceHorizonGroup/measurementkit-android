package com.performancehorizon.measurementkit;

import android.net.Uri;

import org.json.JSONObject;

import java.net.URLDecoder;
import java.util.concurrent.ExecutionException;

/**
 * Created by owainbrown on 25/01/16.
 */
public class RegistrationProcessor {

    private static final String TRACKING_ID_KEY = "mobiletracking_id";
    private static final String TRACKING_DEEPLINK_KEY = "deep_link";
    private static final String TRACKING_REFERRER_KEY = "referrer";

    private boolean registrationFailed = true;
    private String trackingID;
    private Uri referrer;
    private Uri deeplink;

    public RegistrationProcessor(String result) {

        try {
            JSONObject registrationjson = new JSONObject(result);

            Object registrationtrackingid = registrationjson.get(TRACKING_ID_KEY);

            //hate this.
            if ((registrationtrackingid instanceof String)) { //valid clickref
                this.registrationFailed = false;
                this.trackingID = (String)registrationtrackingid;

                if (registrationjson.has(TRACKING_DEEPLINK_KEY)) {
                    this.deeplink = Uri.parse(URLDecoder.decode(registrationjson.getString(TRACKING_DEEPLINK_KEY), "UTF-8"));
                }

                if (registrationjson.has(TRACKING_REFERRER_KEY)) {
                    this.referrer = Uri.parse(URLDecoder.decode(registrationjson.getString(TRACKING_REFERRER_KEY), "UTF-8"));
                }
            }
        }
        catch(Exception exception) {
            ServiceLog.debug("Service failed decoding registration response.");
        }
    }

    public boolean hasRegistrationFailed() {
        return registrationFailed;
    }

    public String getTrackingID() {
        return trackingID;
    }

    public Uri getReferrer() {
        return referrer;
    }

    public Uri getDeeplink() {
        return deeplink;
    }
}
