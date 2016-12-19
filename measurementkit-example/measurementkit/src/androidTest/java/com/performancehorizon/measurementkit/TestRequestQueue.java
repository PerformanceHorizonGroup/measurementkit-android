package com.performancehorizon.measurementkit;

import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;



import java.io.IOException;
import java.util.ArrayList;

import okhttp3.OkHttpClient;

/**
 * Created by owainbrown on 17/03/15.
 */

@RunWith(AndroidJUnit4.class)
public class TestRequestQueue {

    TrackingRequestQueue queue;

    @Before
    public void setUp() {

        OkHttpClient client = new OkHttpClient();
        queue = new TrackingRequestQueue(client);
        //factory = new DummyTrackingRequestFactory();
    }

    @Test
    public void testSingleRequest() throws Exception
    {
        TrackingRequestQueueDelegate delegate = mock(TrackingRequestQueueDelegate.class);

        String result = "Performance Horizon Group";

        TrackingRequest request = mock(TrackingRequest.class);
        when(request.execute(any(OkHttpClientWrapper.class))).thenReturn(result);

        queue.setDelegate(delegate);
        queue.enqueueRequest(request);

        //check that the queue completes the request.
        verify(delegate, timeout(500)).requestQueueDidCompleteRequest(queue,request,result);

    }

    @Test
    public void testSingleFailingRequest() throws Exception
    {
        TrackingRequestQueueDelegate delegate = mock(TrackingRequestQueueDelegate.class);

        String result = "Performance Horizon Group";

        TrackingRequest request = mock(TrackingRequest.class);
        when(request.execute(any(OkHttpClientWrapper.class))).thenThrow(new IOException());

        queue.setDelegate(delegate);
        queue.enqueueRequest(request);

        verify(delegate, timeout(1000).times(1)).requestQueueErrorOnRequest(eq(queue), eq(request), any(Exception.class));
    }

    @Test
    public void testPausedQueue()
    {
        TrackingRequestQueueDelegate delegate = mock(TrackingRequestQueueDelegate.class);

        String result = "Performance Horizon Group";

        TrackingRequest request = mock(TrackingRequest.class);

        queue.setQueueIsPaused(true);
        queue.setDelegate(delegate);
        queue.enqueueRequest(request);

        //verify that nothing is happening!
        verify(delegate, after(100).times(0)).requestQueueErrorOnRequest(eq(queue), eq(request), any(Exception.class));
        verify(delegate, after(100).times(0)).requestQueueDidCompleteRequest(queue,request,result);
    }

    @Test
    public void testUnpausedQueue() throws Exception
    {
        TrackingRequestQueueDelegate delegate = mock(TrackingRequestQueueDelegate.class);

        String result = "Performance Horizon Group";

        TrackingRequest request = mock(TrackingRequest.class);

        queue.setQueueIsPaused(true);
        queue.setDelegate(delegate);
        queue.enqueueRequest(request);

        //verify that nothing is happening!
        verify(delegate, times(0)).requestQueueErrorOnRequest(eq(queue), eq(request), any(Exception.class));
        verify(delegate, times(0)).requestQueueDidCompleteRequest(queue,request,result);

        when(request.execute(any(OkHttpClientWrapper.class))).thenReturn(result);

        //queue becomes unpaused
        queue.setQueueIsPaused(false);
        verify(delegate, timeout(5000)).requestQueueDidCompleteRequest(queue,request,result);
    }

    @Test
    public void testMultipleRequests() throws Exception {

        TrackingRequestQueueDelegate delegate = mock(TrackingRequestQueueDelegate.class);

        String firstresult = "Performance Horizon Group";
        String secondresult = "Mobile tracking";

        TrackingRequest firstrequest = mock(TrackingRequest.class);
        TrackingRequest secondrequest = mock(TrackingRequest.class);

        when(firstrequest.execute(any(OkHttpClientWrapper.class))).thenReturn(firstresult);
        when(secondrequest.execute(any(OkHttpClientWrapper.class))).thenReturn(secondresult);

        queue.setDelegate(delegate);
        queue.enqueueRequest(firstrequest);
        queue.enqueueRequest(secondrequest);

        //check that the queue completes the request.
        verify(delegate, timeout(500)).requestQueueDidCompleteRequest(queue,firstrequest,firstresult);
        verify(delegate, timeout(500)).requestQueueDidCompleteRequest(queue,secondrequest,secondresult);;
    }

    @Test
    public void testMultipleWithError() throws Exception {

        TrackingRequestQueueDelegate delegate = mock(TrackingRequestQueueDelegate.class);

        String result = "Mobile tracking";
        IOException exception = new IOException();

        TrackingRequest firstrequest = mock(TrackingRequest.class);

        TrackingRequest secondrequest = mock(TrackingRequest.class);

        when(firstrequest.execute(any(OkHttpClientWrapper.class))).thenReturn(result);
        when(secondrequest.execute(any(OkHttpClientWrapper.class))).thenThrow(exception);

        queue.setDelegate(delegate);
        queue.enqueueRequest(firstrequest);
        queue.enqueueRequest(secondrequest);

        verify(delegate, timeout(500)).requestQueueDidCompleteRequest(queue,firstrequest,result);
        verify(delegate, timeout(500).times(1)).requestQueueErrorOnRequest(queue, secondrequest, exception);
    }
}
