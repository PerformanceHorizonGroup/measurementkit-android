package com.performancehorizon.measurementkit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;


/**
 * Created by owainbrown on 20/03/15.
 */
public class ReferrerTracker extends BroadcastReceiver{

    private static String referrer = null;

    public ReferrerTracker() {
        super();

        Log.d("Something", "Something");
    }

    public void onReceive(Context context, Intent intent) {

        Log.d("Something", "something");

        try {

            setResultCode(1);

            if (intent != null && (intent.getAction().equals("com.android.vending.INSTALL_REFERRER"))) {
                String encodedreferrer = intent.getStringExtra("referrer");

                if (encodedreferrer != null) {

                    ReferrerTracker.referrer = URLDecoder.decode(encodedreferrer, "UTF-8");

                    setResultCode(2);


                    //TODO: Consider rebroadcasting (order of events unclear)

                    //LocalBroadcastManager broadcastmanager = LocalBroadcastManager.getInstance()

                    /*MobileTrackingService.trackingInstance().setReferrer(referrer);

                    if (MobileTrackingService.trackingInstance().readyToRegister()) {
                        MobileTrackingService.trackingInstance().register();
                    }*/
                }
            }
        } catch (UnsupportedEncodingException exception) {
            Log.d(MeasurementService.TrackingConstants.TRACKING_LOG, "Mobile tracking receiver failed.");
        }
    }

    public static String getReferrer()
    {
        return ReferrerTracker.referrer;
    }

    //FIXME: I need removing.
    public static void forceReferrer(String referrer) {
        ReferrerTracker.referrer = referrer;
    }
}
