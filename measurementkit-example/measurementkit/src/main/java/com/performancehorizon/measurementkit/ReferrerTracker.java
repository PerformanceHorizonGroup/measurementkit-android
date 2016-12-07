package com.performancehorizon.measurementkit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.UrlQuerySanitizer;
import android.support.annotation.Nullable;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;


/**
 * Created by owainbrown on 20/03/15.
 */
public class ReferrerTracker extends BroadcastReceiver{

    protected static final String REFERER_KEY  = "phn_ref";
    protected static final String REFERER_PREFS = "phn_mmk_referrer";

    public ReferrerTracker() {
        super();
    }

    public void onReceive(Context context, Intent intent) {

        try {

            Log.v("Referrer tracker", "PHN referrer tracker running!");

            if (intent != null && (intent.getAction().equals("com.android.vending.INSTALL_REFERRER"))) {
                String encodedreferrer = intent.getStringExtra("referrer");

                if (encodedreferrer != null) {

                    String decodedreferrer = URLDecoder.decode(encodedreferrer, "utf-8");

                    UrlQuerySanitizer querydecoder = new UrlQuerySanitizer();
                    querydecoder.setAllowUnregisteredParamaters(true);
                    querydecoder.parseQuery(decodedreferrer);

                    if (querydecoder.hasParameter(REFERER_KEY)) {
                        context.getSharedPreferences(REFERER_PREFS, Context.MODE_PRIVATE).edit().putString(REFERER_KEY, querydecoder.getValue(REFERER_KEY)).apply();
                    }
                }
            }
        } catch (UnsupportedEncodingException exception) {
            Log.d(MeasurementService.TrackingConstants.TRACKING_LOG, "Mobile tracking receiver failed.");
        }
    }

    /**
      * get the mobile measurement reference encoded in the google play referrer field
      * @return the mobile measurement reference string
     */
    @Nullable
    public String getReferrer(Context context)
    {
        return context.getSharedPreferences(REFERER_PREFS, Context.MODE_PRIVATE).getString(REFERER_KEY, null);
    }

    protected void clearReferrer(Context context) {
        context.getSharedPreferences(REFERER_PREFS, Context.MODE_PRIVATE).edit().clear().apply();
    }

    protected void putReferrer(Context context, String referrer) {
        context.getSharedPreferences(REFERER_PREFS, Context.MODE_PRIVATE).edit().putString(REFERER_KEY, referrer).apply();
    }
}
