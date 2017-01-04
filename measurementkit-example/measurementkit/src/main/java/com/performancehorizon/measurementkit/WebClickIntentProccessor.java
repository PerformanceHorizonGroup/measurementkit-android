package com.performancehorizon.measurementkit;

import android.content.Intent;
import android.net.Uri;


/**
 * Created by owainbrown on 13/01/16.
 */
public class WebClickIntentProccessor extends TrackedIntentProcessor {

    protected static final String TRACKING_ID_QUERY_PARAM = "phn_mtid";

    private String mobileTrackingID = null;

    protected WebClickIntentProccessor(Intent intent) {
        this(intent, TRACKING_ID_QUERY_PARAM);
    }

    protected WebClickIntentProccessor( Intent intent, String queryKey)
    {
        Uri intenturi = intent.getData();

        if (intent.getAction() == Intent.ACTION_VIEW &&
                intenturi != null) {

            String mobiletrackingid = intenturi.getQueryParameter(queryKey);

            if (mobiletrackingid != null) {
                this.mobileTrackingID = mobiletrackingid;

                Intent filteredintent =(Intent) intent.clone();

                Uri.Builder builder = filteredintent.getData().buildUpon().clearQuery();

                for (String queryparamname : filteredintent.getData().getQueryParameterNames()) {
                    if (! queryparamname.equals(queryKey)) {
                        builder.appendQueryParameter(queryparamname, filteredintent.getData().getQueryParameter(queryparamname));
                    }
                }

                filteredintent.setData(builder.build());

                this.setFilteredIntent(filteredintent);
            }
        }
    }

    protected String getMobileTrackingID() {
        return this.mobileTrackingID;
    }
}
