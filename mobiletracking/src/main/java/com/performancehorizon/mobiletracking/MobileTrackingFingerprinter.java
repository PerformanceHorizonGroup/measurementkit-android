package com.performancehorizon.mobiletracking;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.text.AndroidCharacter;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Created by owainbrown on 25/03/15.
 */
public class MobileTrackingFingerprinter {
    private WeakReference<Context> weakContext;

    private static class FingerprintConstants
    {
        public final static String ANDROID_PACKAGE = "and_package";
        public final static String ANDROID_APP_NAME = "and_app_name";
        public final static String ANDROID_APP_VERSION_CODE = "and_app_version_code";
        public final static String ANDROID_APP_VERSION = "and_app_version";
        public final static String ANDROID_APP_MODIFIED = "and_last_modified";
        public final static String ANDROID_APP_INSTALLER = "and_app_installer";
        public final static String ANDROID_MODEL = "and_model";
        public final static String ANDROID_DEVICE = "and_device";
        public final static String ANDROID_MANUFACTURER = "and_manufacturer";
        public final static String ANDROID_VERSION_CODE = "and_version_code";
        public final static String ANDROID_LOCALE = "and_locale";
        public final static String ANDROID_SCREEN_DENSITY = "and_screen_density";
        public final static String ANDROID_SCREEN_DIMENSIONS = "and_screen_dimensions";
        public final static String ANDROID_OPERATOR = "and_operator";
    }

    public MobileTrackingFingerprinter(Context context)
    {
        this.weakContext = new WeakReference<>(context);
    }

    public Map<String, Object> generateFingerprint()
    {
        HashMap<String, Object> returnedfingerprint= new HashMap<>();

        //SDK values not reliant on context.
        returnedfingerprint.put(FingerprintConstants.ANDROID_MODEL, Build.MODEL);
        returnedfingerprint.put(FingerprintConstants.ANDROID_MANUFACTURER, Build.MANUFACTURER);
        returnedfingerprint.put(FingerprintConstants.ANDROID_VERSION_CODE, Build.VERSION.RELEASE);
        returnedfingerprint.put(FingerprintConstants.ANDROID_LOCALE, Locale.getDefault().getDisplayName());
        returnedfingerprint.put(FingerprintConstants.ANDROID_DEVICE, Build.DEVICE);

        if (this.weakContext.get() != null) {
            Context context = this.weakContext.get();

            String applicationpackage = context.getPackageName();
            returnedfingerprint.put(FingerprintConstants.ANDROID_PACKAGE, applicationpackage);

            PackageManager packagemanager = context.getPackageManager();

            if (packagemanager != null) {
                try {
                    ApplicationInfo applicationinfo = packagemanager.getApplicationInfo(applicationpackage, 0);

                    if (applicationinfo != null) {

                        File applicationsourcedir = new File(applicationinfo.sourceDir);
                        returnedfingerprint.put(FingerprintConstants.ANDROID_APP_MODIFIED, new Long(applicationsourcedir.lastModified() / 1000));
                    }

                    PackageInfo packageinfo = packagemanager.getPackageInfo(applicationpackage, 0);

                    if (packageinfo != null) {
                        returnedfingerprint.put(FingerprintConstants.ANDROID_APP_VERSION_CODE, new Integer(packageinfo.versionCode));
                        returnedfingerprint.put(FingerprintConstants.ANDROID_APP_VERSION, packageinfo.versionName);
                    }

                    returnedfingerprint.put(FingerprintConstants.ANDROID_APP_NAME, packagemanager.getApplicationLabel(applicationinfo).toString());
                    returnedfingerprint.put(FingerprintConstants.ANDROID_APP_INSTALLER, packagemanager.getInstallerPackageName(applicationpackage));


                } catch (PackageManager.NameNotFoundException namexception) {

                }
            }

            WindowManager windowmanager = (WindowManager)context.getSystemService(Context.WINDOW_SERVICE);
            if (windowmanager != null) {
                //display size/density
                DisplayMetrics display = new DisplayMetrics();
                windowmanager.getDefaultDisplay().getMetrics(display);

                returnedfingerprint.put(FingerprintConstants.ANDROID_SCREEN_DIMENSIONS, Integer.toString(display.widthPixels) + "x" + Integer.toString(display.heightPixels));
                returnedfingerprint.put(FingerprintConstants.ANDROID_SCREEN_DENSITY, Float.toString(display.xdpi) + "x" + Float.toString(display.ydpi));
            }

            TelephonyManager telephonymanager = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
            if (telephonymanager != null) {
                returnedfingerprint.put(FingerprintConstants.ANDROID_OPERATOR, telephonymanager.getNetworkOperator());
            }
        }

        return returnedfingerprint;
    }


}
