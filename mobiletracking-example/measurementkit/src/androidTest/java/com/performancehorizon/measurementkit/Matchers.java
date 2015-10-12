package com.performancehorizon.measurementkit;

import android.content.Intent;

import org.mockito.ArgumentMatcher;

/**
 * Created by owainbrown on 23/03/15.
 */
public class Matchers {

    protected static class IsRegistrationRequest extends ArgumentMatcher<TrackingRequest> {
        public boolean matches(Object item) {
            TrackingRequest request= (TrackingRequest)item;

            return (request.getUrl().equals("http://api.local/mobile/register") ||
                    request.getUrl().equals("https://mobile.prf.hn/register") );
        }
    }

    protected static class IsEventRequest extends ArgumentMatcher<TrackingRequest> {
        public boolean matches(Object item) {
            TrackingRequest request= (TrackingRequest)item;

            return (request.getUrl().equals("http://api.local/mobile/event")
                    || request.getUrl().equals("https://mobile.prf.hn/event"));
        }
    }

    protected static class isDeepLinkIntent extends ArgumentMatcher<Intent> {
        private String requiredAction;
        private String requiredURI;

        public isDeepLinkIntent(String action, String uri)
        {
            super();

            this.requiredAction = action;
            this.requiredURI = uri;
        }

        @Override
        public boolean matches(Object argument) {

            Intent intent = (Intent) argument;

            String intentstring = intent.getData().toString();

            return intentstring.equals(this.requiredURI) &&
                    intent.getAction().equals(this.requiredAction);
        }
    }
}
