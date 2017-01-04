package com.performancehorizon.measurementkit;

import android.net.Uri;


/**
 * Created by owainbrown on 03/02/16.
 */
public class UriBuilderWrapper {
     private Uri.Builder builder;

    public UriBuilderWrapper( Uri.Builder builder) {
        this.builder = builder;
    }

    public UriBuilderWrapper scheme(String scheme) {
        this.builder.scheme(scheme);
        return this;
    }

    public UriBuilderWrapper authority(String authority) {
        this.builder.authority(authority);
        return this;
    }

    public UriBuilderWrapper appendPath(String newSegment) {
        this.builder.appendPath(newSegment);
        return this;
    }

    public UriBuilderWrapper appendEncodedPath(String newSegment) {
        this.builder.appendEncodedPath(newSegment);
        return this;
    }

    public UriBuilderWrapper appendQueryParameter(String key, String value) {

        this.builder.appendQueryParameter(key, value);
        return this;
    }

    public Uri build() throws UnsupportedOperationException {
        return this.builder.build();
    }
}
