package com.performancehorizon.measurementkit;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 02/02/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT,sdk = 21)
public class TestRegisterRequestQueue {

    @Test
    public void testPauseQueue() {

        TrackingRequestQueue requestqueue = mock(TrackingRequestQueue.class);
        TrackingRequestFactory requestfactory = mock(TrackingRequestFactory.class);
        TrackingURLHelper urlhelper = mock(TrackingURLHelper.class);

        RegisterRequestQueue queue = new RegisterRequestQueue(requestqueue, requestfactory, urlhelper);

        queue.setQueueIsPaused(true);

        verify(requestqueue).setQueueIsPaused(true);
    }

    @Test
    public void testAddRequest() {
        
        //lot of dependency set up for not alot of test...

        TrackingRequestQueue requestqueue = mock(TrackingRequestQueue.class);
        TrackingRequestFactory requestfactory = mock(TrackingRequestFactory.class);
        TrackingURLHelper urlhelper = mock(TrackingURLHelper.class);

        TrackingRequest mocktrackingrequest = mock(TrackingRequest.class);
        when(requestfactory.getRequest(anyString(), any(JSONObject.class))).thenReturn(mocktrackingrequest);
    
        RegisterRequestJSONBuilder builder = mock(RegisterRequestJSONBuilder.class);
        when(builder.setRequest(any(RegisterRequest.class))).thenReturn(builder);
        when(builder.build()).thenReturn(new JSONObject());
        
        RegisterRequestQueue.RegisterRequestJSONFactory builderfactory = mock(RegisterRequestQueue.RegisterRequestJSONFactory.class);
        when(builderfactory.jsonBuilder()).thenReturn(builder);
        
        RegisterRequest request = mock(RegisterRequest.class);
        
        RegisterRequestQueue queue = new RegisterRequestQueue(requestqueue, requestfactory, urlhelper, builderfactory);
        queue.addRegisterRequest(request);
        
        verify(requestqueue).enqueueRequest(mocktrackingrequest);
        
        Map<TrackingRequest, RegisterRequest> requests=  queue.getRequests();

        Assert.assertEquals(requests.size(), 1);
        Assert.assertEquals(requests.get(mocktrackingrequest), request);
    }

    @Test
    public void testAddFailedRequest() {

        //lot of dependency set up for not alot of test...

        TrackingRequestQueue requestqueue = mock(TrackingRequestQueue.class);
        TrackingRequestFactory requestfactory = mock(TrackingRequestFactory.class);
        TrackingURLHelper urlhelper = mock(TrackingURLHelper.class);

        TrackingRequest mocktrackingrequest = mock(TrackingRequest.class);
        when(requestfactory.getRequest(anyString(), any(JSONObject.class))).thenReturn(mocktrackingrequest);

        RegisterRequestJSONBuilder builder = mock(RegisterRequestJSONBuilder.class);
        when(builder.setRequest(any(RegisterRequest.class))).thenReturn(builder);
        when(builder.build()).thenReturn(null);

        RegisterRequestQueue.RegisterRequestJSONFactory builderfactory = mock(RegisterRequestQueue.RegisterRequestJSONFactory.class);
        when(builderfactory.jsonBuilder()).thenReturn(builder);

        RegisterRequest request = mock(RegisterRequest.class);

        RegisterRequestQueue queue = new RegisterRequestQueue(requestqueue, requestfactory, urlhelper, builderfactory);
        queue.addRegisterRequest(request);

        verify(requestqueue, times(0)).enqueueRequest(mocktrackingrequest);

        Map<TrackingRequest, RegisterRequest> requests=  queue.getRequests();

        Assert.assertEquals(requests.size(), 0);
    }

    @Test
    public void testCompletingRequest() {

        //adding a request setup, feel free to skip...
        TrackingRequestQueue requestqueue = mock(TrackingRequestQueue.class);
        TrackingRequestFactory requestfactory = mock(TrackingRequestFactory.class);
        TrackingURLHelper urlhelper = mock(TrackingURLHelper.class);

        TrackingRequest mocktrackingrequest = mock(TrackingRequest.class);
        when(requestfactory.getRequest(anyString(), any(JSONObject.class))).thenReturn(mocktrackingrequest);

        RegisterRequestJSONBuilder builder = mock(RegisterRequestJSONBuilder.class);
        when(builder.setRequest(any(RegisterRequest.class))).thenReturn(builder);
        when(builder.build()).thenReturn(new JSONObject());

        RegisterRequestQueue.RegisterRequestJSONFactory builderfactory = mock(RegisterRequestQueue.RegisterRequestJSONFactory.class);
        when(builderfactory.jsonBuilder()).thenReturn(builder);

        final RegisterRequest originalrequest = mock(RegisterRequest.class);
        final String theresult = "IAMARESULT";

        RegisterRequestQueue queue = new RegisterRequestQueue(requestqueue, requestfactory, urlhelper, builderfactory);
        queue.addRegisterRequest(originalrequest);

        RegisterRequestQueueDelegate delegate = new RegisterRequestQueueDelegate() {
            @Override
            public void registerRequestQueueDidComplete(RegisterRequestQueue queue, RegisterRequest request, String result) {
                Assert.assertEquals(request,originalrequest);
                Assert.assertEquals(result, theresult);
            }

            @Override
            public void registerRequestQueueDidError(RegisterRequestQueue queue, RegisterRequest request, Exception error) {
                Assert.assertEquals(1, 0);
            }
        };

        queue.setDelegate(delegate);
        queue.requestQueueDidCompleteRequest(requestqueue, mocktrackingrequest, theresult);
    }

    @Test
    public void testFailingRequest() {

        //adding a request setup, feel free to skip...
        TrackingRequestQueue requestqueue = mock(TrackingRequestQueue.class);
        TrackingRequestFactory requestfactory = mock(TrackingRequestFactory.class);
        TrackingURLHelper urlhelper = mock(TrackingURLHelper.class);

        TrackingRequest mocktrackingrequest = mock(TrackingRequest.class);
        when(requestfactory.getRequest(anyString(), any(JSONObject.class))).thenReturn(mocktrackingrequest);

        RegisterRequestJSONBuilder builder = mock(RegisterRequestJSONBuilder.class);
        when(builder.setRequest(any(RegisterRequest.class))).thenReturn(builder);
        when(builder.build()).thenReturn(new JSONObject());

        RegisterRequestQueue.RegisterRequestJSONFactory builderfactory = mock(RegisterRequestQueue.RegisterRequestJSONFactory.class);
        when(builderfactory.jsonBuilder()).thenReturn(builder);

        final RegisterRequest originalrequest = mock(RegisterRequest.class);
        final Exception exception = new Exception("I'm probably bad!");

        RegisterRequestQueue queue = new RegisterRequestQueue(requestqueue, requestfactory, urlhelper, builderfactory);
        queue.addRegisterRequest(originalrequest);

        RegisterRequestQueueDelegate delegate = new RegisterRequestQueueDelegate() {
            @Override
            public void registerRequestQueueDidComplete(RegisterRequestQueue queue, RegisterRequest request, String result) {
                Assert.assertEquals(1, 0);
            }

            @Override
            public void registerRequestQueueDidError(RegisterRequestQueue queue, RegisterRequest request, Exception error) {
                Assert.assertEquals(request,originalrequest);
                Assert.assertEquals(error, exception);
            }
        };

        queue.setDelegate(delegate);
        queue.requestQueueErrorOnRequest(requestqueue, mocktrackingrequest, exception);
    }

}
