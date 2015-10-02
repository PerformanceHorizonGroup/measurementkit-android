package com.performancehorizon.mobiletracking;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;

import javax.security.auth.callback.Callback;

/**
 * Created by owainbrown on 26/03/15.
 */
public class MobileTrackingActiveFingerprinter {
    private MobileTrackingActiveFingerprinterCallback callback;
    private WeakReference<Context> weakContext;

    public interface MobileTrackingActiveFingerprinterCallback {
        public void activeFingerprintComplete(MobileTrackingActiveFingerprinter fingerprinter, Map<String, Object> fingerprint);
    }

    public MobileTrackingActiveFingerprinter(Context context, MobileTrackingActiveFingerprinterCallback callback) {
       this.callback = callback;
       this.weakContext = new WeakReference<Context>(context);
    }

    public void generateFingerprint(){

       Handler mainthreadhandler = new Handler(Looper.getMainLooper());
       mainthreadhandler.post(new Runnable() {
           @Override
           public void run() {

               try {
                   Context context = weakContext.get();

                   if (context != null) {
                       WebView webview = new WebView(context);

                       HashMap<String, Object> fingerprint = new HashMap<>();

                       fingerprint.put("and_active_ua", webview.getSettings().getUserAgentString());

                       if (callback != null) {
                           callback.activeFingerprintComplete(MobileTrackingActiveFingerprinter.this,
                                   fingerprint);
                       }
                   }
               }
               catch(Exception exception) {
                   Log.v(MobileTrackingService.TrackingConstants.TRACKING_LOG, "Active Fingerprint failed with Exception.");
               }
               catch(VerifyError verificationerror) {
                   Log.v(MobileTrackingService.TrackingConstants.TRACKING_LOG, "Active Fingerprint failed with Verification error.");
               }
           }
       });
    }
}
