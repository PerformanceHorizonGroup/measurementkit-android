package com.performancehorizon.measurementkit;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.Nullable;

/**
 * Created by owainbrown on 18/03/15.
 *
 */
public class Reachability {

    private ReachabilityCallback callback;

    @Nullable
    private ConnectivityManager connectivityManager;

    public Reachability(ConnectivityManager connectivity, final ReachabilityCallback callback)
    {
        this.callback= callback;
        this.connectivityManager = connectivity;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            connectivity.addDefaultNetworkActiveListener(new ConnectivityManager.OnNetworkActiveListener() {
                @Override
                public void onNetworkActive() {
                    callback.onNetworkActive();
                }
            });
        }
    }

    public boolean isNetworkActive() {

        if (connectivityManager == null) return false;

        NetworkInfo networkinfo = connectivityManager.getActiveNetworkInfo();

        return (networkinfo != null) && networkinfo.isConnectedOrConnecting();
    }

    protected ReachabilityCallback getCallback() {
        return this.callback;
    }
}
