package com.performancehorizon.measurementkit;

import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by owainbrown on 19/01/16.
 */
public class MeasurementServiceURIBuilder {

    protected static class UriBuilderFactory {
        protected UriBuilderWrapper getBuilder() {
            return new UriBuilderWrapper(new Uri.Builder());
        }
    }

    @NonNull private TrackingURLHelper helper;
    @NonNull private UriBuilderFactory factory;

    @Nullable private String camref;
    @Nullable private Uri destination;
    @Nullable private Uri deeplink;

    @NonNull private Map<String, String> aliases;

    private boolean skipDeepLink = false;

    protected MeasurementServiceURIBuilder(@NonNull TrackingURLHelper helper, @NonNull UriBuilderFactory factory) {
        this.helper = helper;
        this.aliases = new HashMap<>();
        this.factory = factory;
    }

    public MeasurementServiceURIBuilder(TrackingURLHelper helper) {
        this(helper, new UriBuilderFactory());
    }

    public boolean isValid() {
        return this.camref != null && this.destination != null &&
             this.helper != null;
    }

    public Uri build() {
        if (!this.isValid())  {
            return null;
        }
        else {
            try {
                UriBuilderWrapper builder = factory.getBuilder();

                builder.scheme(helper.scheme())
                        .authority(helper.hostForMobileTracking())
                        .appendPath("click")
                        .appendEncodedPath("camref:" + camref)
                        .appendEncodedPath("destination:" + URLEncoder.encode(destination.toString(), Charset.defaultCharset().name()));

                for (Map.Entry<String, String> alias : aliases.entrySet()) {
                    builder.appendQueryParameter(alias.getKey(), alias.getValue());
                }

                if (this.shouldSkipDeepLink() && deeplink != null) {
                    builder.appendQueryParameter("skip_deep_link", "true");
                }

                if (this.deeplink != null) {
                    builder.appendQueryParameter("deep_link", destination.toString());
                }

                return builder.build();
            }
            catch(Exception exception) {
                return null;
            }
        }
    }

    public MeasurementServiceURIBuilder setCamref(@NonNull String camref) {
        this.camref = camref;
        return this;
    }

    public MeasurementServiceURIBuilder setDestination(@NonNull Uri destination) {
        this.destination = destination;
        return this;
    }

    public MeasurementServiceURIBuilder setDeeplink(Uri deeplink) {
        this.deeplink = deeplink;
        return this;
    }

    public MeasurementServiceURIBuilder putAlias(String aliaskey, String aliasvalue) {
        this.aliases.put(aliaskey, aliasvalue);
        return this;
    }

    public boolean shouldSkipDeepLink() {
        return skipDeepLink;
    }

    public MeasurementServiceURIBuilder setSkipDeepLink(boolean skipDeepLink) {
        this.skipDeepLink = skipDeepLink;
        return this;
    }
}
