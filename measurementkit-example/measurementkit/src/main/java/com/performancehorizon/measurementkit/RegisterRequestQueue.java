package com.performancehorizon.measurementkit;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by owainbrown on 25/01/16.
 */
public class RegisterRequestQueue implements TrackingRequestQueueDelegate {

    @NonNull private TrackingRequestFactory factory;
    @NonNull private TrackingRequestQueue requestQueue;
    @NonNull private TrackingURLHelper urlHelper;
    @NonNull private Map<TrackingRequest, RegisterRequest> requests;
    @NonNull private RegisterRequestJSONFactory jsonFactory;

    @Nullable private WeakReference<RegisterRequestQueueDelegate> delegate;

    protected static class RegisterRequestJSONFactory {

        @NonNull
        public RegisterRequestJSONBuilder jsonBuilder() {
            return new RegisterRequestJSONBuilder();
        }
    }

    public RegisterRequestQueue(@NonNull TrackingRequestQueue queue,
                             @NonNull TrackingRequestFactory factory,
                             @NonNull TrackingURLHelper urlHelper,
                                @NonNull RegisterRequestJSONFactory jsonfactory) {

        this.requestQueue = queue;
        this.requestQueue.setDelegate(this);
        this.factory = factory;
        this.urlHelper = urlHelper;
        this.requests = new HashMap<>();
        this.jsonFactory = jsonfactory;
    }

    public RegisterRequestQueue(@NonNull TrackingRequestQueue queue,
                                @NonNull TrackingRequestFactory factory,
                                @NonNull TrackingURLHelper urlHelper) {

        this(queue, factory, urlHelper, new RegisterRequestJSONFactory());
    }

    public void setQueueIsPaused(boolean isPaused) {
        this.requestQueue.setQueueIsPaused(isPaused);
    }

    public void setDelegate(RegisterRequestQueueDelegate delegate)
    {
        this.delegate = new WeakReference<>(delegate);
    }

    public void addRegisterRequest(RegisterRequest request) {

        RegisterRequestJSONBuilder requestbuilder = this.jsonFactory.jsonBuilder();

        JSONObject requestjson = requestbuilder.setRequest(request).build();

        if (requestjson != null) {
            TrackingRequest transportrequest = factory.getRequest(this.urlHelper.urlStringForTracking() + "/register", requestjson);

            this.requests.put(transportrequest, request);
            this.requestQueue.enqueueRequest(transportrequest);
        }
        else
        {
            MeasurementServiceLog.e("Request Queue - Invalid registration request has been generated, and will be ignored.");
        }
    }

    @Override
    public void requestQueueDidCompleteRequest(TrackingRequestQueue queue, @Nullable TrackingRequest request, String result) {
        if (this.delegate != null && this.delegate.get() != null) {

            RegisterRequest registerrequest = this.requests.get(request);

            if (registerrequest != null) {
                this.requests.remove(request);
                this.delegate.get().registerRequestQueueDidComplete(this, registerrequest, result);
            }
        }
    }

    @Override
    public void requestQueueErrorOnRequest(TrackingRequestQueue queue, TrackingRequest request, Exception error) {
        if (this.delegate != null && this.delegate.get() != null) {

            RegisterRequest registerrequest = this.requests.get(request);

            this.delegate.get().registerRequestQueueDidError(this, registerrequest, error);
        }
    }

    protected Map<TrackingRequest, RegisterRequest> getRequests() {
        return this.requests;
    }
}
