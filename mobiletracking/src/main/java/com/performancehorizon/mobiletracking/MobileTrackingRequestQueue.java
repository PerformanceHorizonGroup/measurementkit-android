package com.performancehorizon.mobiletracking;

import com.squareup.okhttp.OkHttpClient;

import bolts.Continuation;
import bolts.Task;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by owainbrown on 02/03/15.
 */
public class MobileTrackingRequestQueue {

    private boolean requestActive = false;
    private boolean queueIsPaused = false;
    private int failCount = 0;

    private List<MobileTrackingRequest> requestList;
    private WeakReference<MobileTrackingRequestQueueDelegate> delegate;
    private OkHttpClient client;

    public MobileTrackingRequestQueue(OkHttpClient client)
    {
        this.requestList = new ArrayList<MobileTrackingRequest>();
        this.client = client;
    }

    public void enqueueRequest(MobileTrackingRequest request)
    {
        synchronized (this) {
            this.requestList.add(request);
        }

        if (this.canStartRequest()) {
            this.makeRequest(requestList.get(0));
        }
    }

    private void nextRequest() {

        if (this.requestList.size() > 0 && this.canStartRequest()) {
            this.makeRequest(requestList.get(0));
        }
    }

    private void makeRequest(MobileTrackingRequest request)
    {
        synchronized(this)
        {
            this.setRequestActive(true);
        }

        final MobileTrackingRequest therequest = request;

        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {
                return therequest.execute(MobileTrackingRequestQueue.this.client);
            }

        }).continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> task) throws Exception {
                Exception taskerror= task.getError();

                synchronized (MobileTrackingRequestQueue.this) {
                    MobileTrackingRequestQueue.this.setRequestActive(false);
                }

                if (taskerror == null) {
                    if (MobileTrackingRequestQueue.this.delegate != null &&  MobileTrackingRequestQueue.this.delegate.get() != null) {
                        MobileTrackingRequestQueue.this.delegate.get().requestQueueDidCompleteRequest(MobileTrackingRequestQueue.this, therequest, task.getResult());
                    }

                    synchronized (MobileTrackingRequestQueue.this) { //as it was succesful, the request can be removed from the queue.
                        int requestedindex = MobileTrackingRequestQueue.this.requestList.indexOf(therequest);
                        MobileTrackingRequestQueue.this.requestList.remove(requestedindex);
                    }
                }
                else
                {
                    MobileTrackingRequestQueue.this.failCount += 1;

                    if (MobileTrackingRequestQueue.this.delegate != null &&  MobileTrackingRequestQueue.this.delegate.get() != null) {
                        MobileTrackingRequestQueue.this.delegate.get().requestQueueErrorOnRequest(MobileTrackingRequestQueue.this,therequest, taskerror);
                    }
                }

                MobileTrackingRequestQueue.this.nextRequest();

                return null;
            }
        }, Task.UI_THREAD_EXECUTOR);
    }


    private boolean canStartRequest()
    {
        if (this.failCount > 2 || queueIsPaused || requestActive) {
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

        if (!queueIsPaused) {
            this.failCount = 0;
            this.nextRequest();
        }
    }

    protected void setDelegate(MobileTrackingRequestQueueDelegate delegate)
    {
       this.delegate = new WeakReference<>(delegate);
    }
}
