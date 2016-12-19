package com.performancehorizon.measurementkit;

import bolts.Continuation;
import bolts.Task;
import okhttp3.OkHttpClient;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by owainbrown on 02/03/15.
 */
public class TrackingRequestQueue {

    private boolean requestActive = false;
    private boolean queueIsPaused = false;

    private List<TrackingRequest> requestList;
    private WeakReference<TrackingRequestQueueDelegate> delegate;
    private OkHttpClient client;

    public TrackingRequestQueue(OkHttpClient client)
    {
        this.requestList = new ArrayList<TrackingRequest>();
        this.client = client;
    }

    public void enqueueRequest(TrackingRequest request)
    {
        synchronized (this) {
            this.requestList.add(request);
        }

        this.nextRequest();
    }

    private void nextRequest() {

        if (this.requestList.size() > 0 && this.canStartRequest()) {
            this.makeRequest(requestList.get(0));
        }
    }

    /*private void makeSynchronousRequest(TrackingRequest request)
    {
        synchronized(this)
        {
            this.setRequestActive(true);
        }

        final TrackingRequest therequest = request;

        Exception taskerror = null;
        String result = null;

        try {
            result = therequest.execute(new OkHttpClientWrapper(TrackingRequestQueue.this.client));
        }
        catch(Exception exception) {
            taskerror = exception;
        }

        synchronized (TrackingRequestQueue.this) {
            TrackingRequestQueue.this.setRequestActive(false);
        }

        if (taskerror == null) {
            if (TrackingRequestQueue.this.delegate != null &&  TrackingRequestQueue.this.delegate.get() != null) {
                TrackingRequestQueue.this.delegate.get().requestQueueDidCompleteRequest(TrackingRequestQueue.this, therequest, result);
            }
        }
        else
        {
            if (TrackingRequestQueue.this.delegate != null &&  TrackingRequestQueue.this.delegate.get() != null) {
                TrackingRequestQueue.this.delegate.get().requestQueueErrorOnRequest(TrackingRequestQueue.this,therequest, taskerror);
            }
        }

        synchronized (TrackingRequestQueue.this) { //as it was succesful, the request can be removed from the queue.
            int requestedindex = TrackingRequestQueue.this.requestList.indexOf(therequest);
            TrackingRequestQueue.this.requestList.remove(requestedindex);
        }

        TrackingRequestQueue.this.nextRequest();
    }*/

    private void makeRequest(TrackingRequest request)
    {
        synchronized(this)
        {
            this.setRequestActive(true);
            this.requestList.remove(request);
        }

        final TrackingRequest therequest = request;

        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return therequest.execute(new OkHttpClientWrapper(TrackingRequestQueue.this.client));
            }

        }).continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> task) throws Exception {
                Exception taskerror= task.getError();

                synchronized (TrackingRequestQueue.this) {
                    TrackingRequestQueue.this.setRequestActive(false);
                }

                if (taskerror == null) {
                    if (TrackingRequestQueue.this.delegate != null &&  TrackingRequestQueue.this.delegate.get() != null) {
                        TrackingRequestQueue.this.delegate.get().requestQueueDidCompleteRequest(TrackingRequestQueue.this, therequest, task.getResult());
                    }
                }
                else
                {
                    if (TrackingRequestQueue.this.delegate != null &&  TrackingRequestQueue.this.delegate.get() != null) {
                        TrackingRequestQueue.this.delegate.get().requestQueueErrorOnRequest(TrackingRequestQueue.this,therequest, taskerror);
                    }
                }

                TrackingRequestQueue.this.nextRequest();

                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }

    private boolean canStartRequest()
    {
        if (queueIsPaused || requestActive) {
            return false;
        }
        else {
            return true;
        }
    }

    protected void setRequestActive(boolean requestActive) {
        this.requestActive = requestActive;
    }

    protected boolean isRequestActive()
    {
        return this.requestActive;
    }

    protected void setQueueIsPaused(boolean queueIsPaused) {
        this.queueIsPaused = queueIsPaused;

        this.nextRequest();
    }

    protected void setDelegate(TrackingRequestQueueDelegate delegate)
    {
       this.delegate = new WeakReference<>(delegate);
    }
}
