package com.performancehorizon.measurementkit;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


public class ActiveFingerprinter {
    private Callback callback;
    private WeakReference<Context> weakContext;

    public interface Callback {
        void activeFingerprintComplete(ActiveFingerprinter fingerprinter, Map<String, String> fingerprint);
    }

    public ActiveFingerprinter(Context context, Callback callback) {
        this.callback = callback;
        this.weakContext = new WeakReference<>(context);
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

                        HashMap<String, String> fingerprint = new HashMap<>();

                        fingerprint.put("and_active_ua", webview.getSettings().getUserAgentString());

                        if (callback != null) {
                            callback.activeFingerprintComplete(ActiveFingerprinter.this,
                                    fingerprint);
                        }
                    }
                }
                catch(Exception exception) {
                    Log.d(MeasurementService.TrackingConstants.TRACKING_LOG, "Active Fingerprinter - Fingerprint failed with Exception.");
                }
                catch(VerifyError verificationerror) {
                    Log.d(MeasurementService.TrackingConstants.TRACKING_LOG, "Active Fingerprinter - Fingerprint failed with Verification error.");
                }
            }
        });
    }
}