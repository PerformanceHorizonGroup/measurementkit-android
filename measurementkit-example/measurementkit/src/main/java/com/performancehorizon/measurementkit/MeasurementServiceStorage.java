package com.performancehorizon.measurementkit;

import android.content.Context;
import android.content.SharedPreferences;



import com.performancehorizon.measurementkit.MeasurementService.MeasurementServiceStatus;

import java.lang.ref.WeakReference;

/**
 * Created by owainbrown on 15/01/16.
 */
public class MeasurementServiceStorage {
    private WeakReference<Context> context;

    private  String mobileTrackingID;
    private boolean isTrackingInactive = false;
    private boolean isTrackingHalted = false;

    private  String camRef;
    private  String googlePlayReferrer;

    public static void clearPreferences(Context context) {
        SharedPreferences.Editor editor = context.getSharedPreferences(StorageConstants.TRACKING_PREF, Context.MODE_PRIVATE).edit();

        editor.clear();
        editor.apply();
    }

    protected class StorageConstants {
        protected final static String TRACKING_PREF = "com.performancehorizon.mmk";

        protected final static String TRACKING_PREF_ID = "com.performancehorizon.mmk.id";
        protected final static String TRACKING_PREF_INACTIVE = "com.performancehorizon.mmk.isinactive";
        protected final static String TRACKING_PREF_HALT = "com.performancehorizon.mmk.halted";

        //query items
        protected final static String TRACKING_PREF_REFERRER = "com.performancehorizon.com.phnmmk.referrrer";
        protected final static String TRACKING_PREF_CAMREF = "com.performancehorizon.com.phnmmk.camref";
    }

    public MeasurementServiceStorage( Context context) {
        this.context = new WeakReference<>(context);
    }

    private boolean contextAvailable() {
        return this.context != null &&
                this.context.get() != null;
    }

    public MeasurementServiceStatus loadFromPreferences() {

        if (this.contextAvailable()) {
            SharedPreferences preferences = this.context.get().getSharedPreferences(StorageConstants.TRACKING_PREF, Context.MODE_PRIVATE);

            //id
            this.mobileTrackingID = preferences.getString(StorageConstants.TRACKING_PREF_ID, null);

            //activity
            this.isTrackingInactive = preferences.getBoolean(StorageConstants.TRACKING_PREF_INACTIVE, false);
            this.isTrackingHalted = preferences.getBoolean(StorageConstants.TRACKING_PREF_HALT, false);

            //queries.
            this.camRef = preferences.getString(StorageConstants.TRACKING_PREF_CAMREF, null);
            this.googlePlayReferrer = preferences.getString(StorageConstants.TRACKING_PREF_REFERRER, null);
        }

        return this.status();
    }

    public void putTrackingID(String trackingID) {
        this.mobileTrackingID = trackingID;

        if (this.contextAvailable()) {
            SharedPreferences.Editor prefeditor = this.context.get().getSharedPreferences(StorageConstants.TRACKING_PREF, Context.MODE_PRIVATE).edit();

            prefeditor.putString(StorageConstants.TRACKING_PREF_ID, trackingID);
            prefeditor.putBoolean(StorageConstants.TRACKING_PREF_INACTIVE, false);

            prefeditor.apply();
        }
    }

    public void putTrackingInactive() {
        this.isTrackingInactive = true;

        if (this.contextAvailable()) {
            SharedPreferences.Editor prefeditor = this.context.get().getSharedPreferences(StorageConstants.TRACKING_PREF, Context.MODE_PRIVATE).edit();

            prefeditor.putBoolean(StorageConstants.TRACKING_PREF_INACTIVE, true);
            prefeditor.apply();
        }
    }

    public void putHalted(boolean isTrackingHalted) {
        this.isTrackingHalted = isTrackingHalted;

        if (this.contextAvailable()) {
            SharedPreferences.Editor prefeditor = this.context.get().getSharedPreferences(StorageConstants.TRACKING_PREF, Context.MODE_PRIVATE).edit();

            prefeditor.putBoolean(StorageConstants.TRACKING_PREF_HALT, isTrackingHalted);
            prefeditor.apply();
        }
    }

    public void putCamrefQuery(String camRef) {
        this.camRef = camRef;

        if (this.contextAvailable()) {
            SharedPreferences.Editor prefeditor = this.context.get().getSharedPreferences(StorageConstants.TRACKING_PREF, Context.MODE_PRIVATE).edit();

            prefeditor.putString(StorageConstants.TRACKING_PREF_CAMREF, camRef);
            prefeditor.apply();
        }
    }

    public void putReferrerQuery(String referrer) {
        this.googlePlayReferrer = referrer;

        if (this.contextAvailable()) {
            SharedPreferences.Editor prefeditor = this.context.get().getSharedPreferences(StorageConstants.TRACKING_PREF, Context.MODE_PRIVATE).edit();

            prefeditor.putString(StorageConstants.TRACKING_PREF_REFERRER, referrer);
            prefeditor.apply();
        }
    }

    protected void clearPreferences() {
        if (this.contextAvailable()) {
            SharedPreferences.Editor editor = this.context.get().getSharedPreferences(StorageConstants.TRACKING_PREF, Context.MODE_PRIVATE).edit();

            editor.clear();
            editor.apply();
        }
    }

    public void clearCamref() {
        this.camRef = null;

        if (this.contextAvailable()) {
            SharedPreferences.Editor prefeditor = this.context.get().getSharedPreferences(StorageConstants.TRACKING_PREF, Context.MODE_PRIVATE).edit();
            prefeditor.remove(StorageConstants.TRACKING_PREF_CAMREF);
            prefeditor.apply();
        }
    }

    public void clearReferrer() {
        this.googlePlayReferrer = null;

        if (this.contextAvailable()) {
            SharedPreferences.Editor prefeditor = this.context.get().getSharedPreferences(StorageConstants.TRACKING_PREF, Context.MODE_PRIVATE).edit();
            prefeditor.remove(StorageConstants.TRACKING_PREF_REFERRER);
            prefeditor.apply();
        }
    }

    public  String getTrackingID() {
        return this.mobileTrackingID;
    };

    public boolean getIsTrackingInactive() {
        return this.isTrackingInactive;
    }

    public boolean getIsTrackingHalted() {
        return this.isTrackingHalted;
    }

    private boolean hasQuery() {
        return this.getCamRef() != null || (this.getReferrer() != null);
    }

    protected MeasurementServiceStatus status() {
        if (this.isTrackingHalted) {
            return MeasurementServiceStatus.HALTED;
        }
        else if (this.hasQuery()) {
            return MeasurementServiceStatus.QUERYING;
        }
        else if (this.isTrackingInactive) {
            return MeasurementServiceStatus.INACTIVE;
        }
        else if (this.mobileTrackingID != null) {
            return MeasurementServiceStatus.ACTIVE;
        }
        else {
            return MeasurementServiceStatus.QUERYING;
        }
    }


    public String getCamRef() {
        return camRef;
    }


    public String getReferrer() {
        return googlePlayReferrer;
    }
}
