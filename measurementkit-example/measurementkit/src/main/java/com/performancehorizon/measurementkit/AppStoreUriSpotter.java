package com.performancehorizon.measurementkit;

/**
 * Created by owainbrown on 05/02/16.
 */

import android.net.Uri;

/**identifies app store uris*/
public class AppStoreUriSpotter {

    public static boolean isAppStoreURI(Uri uri) {

        return uri.getAuthority().contains("play.google.com") || //play store
                uri.getScheme().equals("market") || //play store scheme
                uri.getScheme().equals("amzn") || //amazon store
                uri.toString().contains("www.amazon.com/gp/mas/dl/android")|| //amazon store http
                uri.getScheme().equals("samsungapps"); //samsung app store
    }
}
