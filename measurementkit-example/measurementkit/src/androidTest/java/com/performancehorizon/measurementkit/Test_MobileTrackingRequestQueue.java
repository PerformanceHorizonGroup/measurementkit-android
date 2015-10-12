package com.performancehorizon.measurementkit;

import android.test.InstrumentationTestCase;

import com.squareup.okhttp.OkHttpClient;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;


/**
 * Created by owainbrown on 17/03/15.
 */
public class Test_MobileTrackingRequestQueue extends InstrumentationTestCase {

    TrackingRequestQueue queue;
    DummyTrackingRequestFactory factory;


    @Before
    public void setUp() {

        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());

        OkHttpClient client = new OkHttpClient();
        queue = new TrackingRequestQueue(client);
        factory = new DummyTrackingRequestFactory();

    }

    @Test
    public void testSingleRequest()
    {
        TrackingRequestQueueDelegate delegate = mock(TrackingRequestQueueDelegate.class);

        String result = "Performance Horizon Group";

        factory.pushResult(result);

        TrackingRequest request = factory.getRequest("don't", "care");

        queue.setDelegate(delegate);
        queue.enqueueRequest(request);

        //check that the queue completes the request.
        verify(delegate, timeout(5000)).requestQueueDidCompleteRequest(queue,request,result);

    }

    public void testSingleFailingRequest()
    {
        TrackingRequestQueueDelegate delegate = mock(TrackingRequestQueueDelegate.class);

        String result = "Performance Horizon Group";

        factory.pushError();

        TrackingRequest request = factory.getRequest("don't", "care");

        queue.setDelegate(delegate);
        queue.enqueueRequest(request);

        verify(delegate, timeout(1000).times(3)).requestQueueErrorOnRequest(eq(queue), eq(request), any(Exception.class));
    }

    public void testPausedQueue()
    {
        TrackingRequestQueueDelegate delegate = mock(TrackingRequestQueueDelegate.class);

        String result = "Performance Horizon Group";

        factory.pushError();

        TrackingRequest request = factory.getRequest("don't", "care");

        queue.setQueueIsPaused(true);
        queue.setDelegate(delegate);
        queue.enqueueRequest(request);

        //verify that nothing is happening!
        verify(delegate, after(100).times(0)).requestQueueErrorOnRequest(eq(queue), eq(request), any(Exception.class));
        verify(delegate, after(100).times(0)).requestQueueDidCompleteRequest(queue,request,result);
    }

    public void testUnpausedQueue()
    {
        TrackingRequestQueueDelegate delegate = mock(TrackingRequestQueueDelegate.class);

        String result = "Performance Horizon Group";

        factory.pushResult(result);

        TrackingRequest request = factory.getRequest("don't", "care");

        queue.setQueueIsPaused(true);
        queue.setDelegate(delegate);
        queue.enqueueRequest(request);

        //verify that nothing is happening!
        verify(delegate, times(0)).requestQueueErrorOnRequest(eq(queue), eq(request), any(Exception.class));
        verify(delegate, times(0)).requestQueueDidCompleteRequest(queue,request,result);

        //queue becomes unpaused
        queue.setQueueIsPaused(false);
        verify(delegate, timeout(5000)).requestQueueDidCompleteRequest(queue,request,result);
    }

    public void testMultipleRequests() {

        TrackingRequestQueueDelegate delegate = mock(TrackingRequestQueueDelegate.class);

        String firstresult = "Performance Horizon Group";
        String secondresult = "Mobile tracking";

        factory.pushResult(firstresult);
        factory.pushResult(secondresult);

        TrackingRequest firstrequest = factory.getRequest("don't", "care");
        TrackingRequest secondrequest = factory.getRequest("orabout", "this");

        queue.setDelegate(delegate);
        queue.enqueueRequest(firstrequest);
        queue.enqueueRequest(secondrequest);

        //check that the queue completes the request.
        verify(delegate, timeout(500)).requestQueueDidCompleteRequest(queue,firstrequest,firstresult);
        verify(delegate, timeout(500)).requestQueueDidCompleteRequest(queue,secondrequest,secondresult);;
    }

    public void testMultipleWithError() {

        TrackingRequestQueueDelegate delegate = mock(TrackingRequestQueueDelegate.class);

        String firstresult = "Performance Horizon Group";
        String secondresult = "Mobile tracking";

        factory.pushResult(firstresult);
        factory.pushError();

        TrackingRequest firstrequest = factory.getRequest("don't", "care");
        TrackingRequest secondrequest = factory.getRequest("orabout", "this");

        queue.setDelegate(delegate);
        queue.enqueueRequest(firstrequest);
        queue.enqueueRequest(secondrequest);


        verify(delegate, timeout(500)).requestQueueDidCompleteRequest(queue,firstrequest,firstresult);
        verify(delegate, timeout(500).times(3)).requestQueueErrorOnRequest(eq(queue), eq(secondrequest), any(Exception.class));


    }
}
