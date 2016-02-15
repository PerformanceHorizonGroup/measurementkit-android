package com.performancehorizon.measurementkit;

import android.content.Intent;

/**
 * Created by owainbrown on 13/01/16.
 */
public abstract class TrackedIntentProcessor {

    private Intent filteredIntent = null;

    public Intent getFilteredIntent() {
        return this.filteredIntent;
    }

    protected void setFilteredIntent(Intent intent) {
        this.filteredIntent = intent;
    }
}
