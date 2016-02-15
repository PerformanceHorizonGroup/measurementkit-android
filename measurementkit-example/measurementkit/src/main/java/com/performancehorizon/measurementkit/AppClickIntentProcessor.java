package com.performancehorizon.measurementkit;

import android.content.Intent;

/**
 * Created by owainbrown on 14/01/16.
 */
public class AppClickIntentProcessor extends TrackedIntentProcessor{

    private String camref;

    public AppClickIntentProcessor(Intent intent, String camrefKey)
    {
        if (intent.hasExtra(camrefKey)) {
            this.camref = intent.getStringExtra(camrefKey);

            Intent filteredintent = (Intent)intent.clone();
            filteredintent.removeExtra(camrefKey);
            this.setFilteredIntent(filteredintent);
        }
    }

    public AppClickIntentProcessor(Intent intent) {

        this(intent, MeasurementService.TrackingConstants.TRACKING_INTENT_CAMREF);
    }

    public String getCamref() {
        return this.camref;
    }
}
