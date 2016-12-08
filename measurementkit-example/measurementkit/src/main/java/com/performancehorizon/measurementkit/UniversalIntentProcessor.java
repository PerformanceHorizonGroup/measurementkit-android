package com.performancehorizon.measurementkit;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import java.net.URLDecoder;

/**
 * Created by owainbrown on 22/01/16.
 */
public class UniversalIntentProcessor extends TrackedIntentProcessor {

    TrackingURLHelper helper;
    private String camref = null;
    private String encodedDestination = null;
    private String encodedDeepLink = null;

    protected UniversalIntentProcessor(Intent intent, TrackingURLHelper helper) {

        if (intent.getData() != null && intent.getData().getScheme().equals(helper.scheme()) &&
                intent.getData().getAuthority().equals(helper.hostForMobileTracking())) {

            //recover destination and deep link from the segments.
            for (String segment : intent.getData().getPathSegments()) {
                if (segment.startsWith("camref:")) {
                    camref = segment.substring(7);
                }
                if (segment.startsWith("destination:")) {
                    encodedDestination = segment.substring(12);
                }
            }

            String alternatedestination = null;

            if (intent.getData().getQueryParameter("deep_link") != null) {
                alternatedestination = intent.getData().getQueryParameter("deep_link");
            } else if (encodedDestination != null) {
                alternatedestination = encodedDestination;
            }

            try {
                Intent returnedintent = (Intent) intent.clone();

                if (alternatedestination != null) {
                    returnedintent.setData(Uri.parse(URLDecoder.decode(alternatedestination, "UTF-8")));
                }

                this.setFilteredIntent(returnedintent);
            }
            catch (Exception decodingexception) {
                MeasurementServiceLog.e("Universal Intent Processor - decoding alternate destination failed" + decodingexception.toString());
            }
        }
    }

    public String getCamref() {
        return this.camref;
    }
}
