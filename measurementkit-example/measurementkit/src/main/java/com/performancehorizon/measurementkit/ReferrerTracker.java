package com.performancehorizon.measurementkit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by owainbrown on 20/03/15.
 */
public class ReferrerTracker extends BroadcastReceiver{

    private static @Nullable String referrer = null;
    private static @Nullable Map<String, String> referrerparameters;

    private static final String REFERER_KEY  = "phn_ref";

    public ReferrerTracker() {
        super();
    }

    public void onReceive(Context context, Intent intent) {

        try {

            if (intent != null && (intent.getAction().equals("com.android.vending.INSTALL_REFERRER"))) {
                String encodedreferrer = intent.getStringExtra("referrer");

                if (encodedreferrer != null) {

                    String decodedreferrer = URLDecoder.decode(encodedreferrer, "utf-8");

                    UrlQuerySanitizer querydecoder = new UrlQuerySanitizer();
                    querydecoder.setAllowUnregisteredParamaters(true);
                    querydecoder.parseQuery(decodedreferrer);

                    if (querydecoder.hasParameter(REFERER_KEY)) {
                        ReferrerTracker.referrer = querydecoder.getValue(REFERER_KEY);

                        //write the original parameter set to referrerparameters
                        HashMap<String, String> parameters = new HashMap<>();

                        for (String parameterkey : querydecoder.getParameterSet()) {
                            //if you're not the param, add you to the list.
                            if (!parameterkey.equals(REFERER_KEY)) {
                                parameters.put(parameterkey, querydecoder.getValue(parameterkey));
                            }

                            ReferrerTracker.referrerparameters = parameters;
                        }
                    }
                }
            }
        } catch (UnsupportedEncodingException exception) {
            Log.d(MeasurementService.TrackingConstants.TRACKING_LOG, "Mobile tracking receiver failed.");
        }
    }

    protected static void putReferrer(String referrer) {
        ReferrerTracker.referrer = referrer;
    }

    /**
     * get the mobile measurement reference encoded in the google play referrer field
     * @return the mobile measurement reference string
     */
    @Nullable
    public static String getReferrer()
    {
        return ReferrerTracker.referrer;
    }

    /**
     * get the referrer parameters with the mobile measurement reference value filtered out
     * @warning please note it is assumed that the already-present referrer parameter are in a form that can be query-encoded.
     * (So for example a single value referrer will be represented as value = "")
     * @return Map representing original referrer parameters
     **/
    @Nullable
    public static Map<String, String> getReferrerParameters()
    {
        return ReferrerTracker.referrerparameters;
    }
}
