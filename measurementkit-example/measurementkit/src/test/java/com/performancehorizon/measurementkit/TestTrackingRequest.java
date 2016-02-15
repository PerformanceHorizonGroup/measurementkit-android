package com.performancehorizon.measurementkit;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.Protocol;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;

import junit.framework.Assert;

import org.junit.Test;

import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 13/01/16.
 */
public class TestTrackingRequest {

    @Test //test the constructor
    public void testConstructRequest() {

        RequestBody mockrequestbody = mock(RequestBody.class);

        TrackingRequest request = new TrackingRequest("/somewhere", mockrequestbody);

        Assert.assertEquals(request.getUrl(), "/somewhere");
        Assert.assertEquals(request.getPostBody(), mockrequestbody);
    }

    @Test //test the post body being set
    public void testSetPostBody() {
        RequestBody mockrequestbody = mock(RequestBody.class);
        Map<String, Object> parameters = mock(Map.class);

        TrackingRequest request = new TrackingRequest("/somewhere", mockrequestbody);
        request.setRequestParameters(parameters);

        Assert.assertEquals(request.getRequestParameters(), parameters);
    }

    @Test
    public void testExecute() throws Exception{

        OkHttpClientWrapper mockclient = mock(OkHttpClientWrapper.class);
        Call mockcall = mock(Call.class);

        Request.Builder placeholder = new Request.Builder();
        Response.Builder responsebuilder = new Response.Builder();
        responsebuilder.request(placeholder.url("http://dontcare").build())
                .protocol(Protocol.HTTP_2)
                .body(ResponseBody.create(MediaType.parse("application/json"), "content"))
                .code(400);

        when(mockclient.newCall(any(Request.class))).thenReturn(mockcall);
        when(mockcall.execute()).thenReturn(responsebuilder.build());

        TrackingRequest request = new TrackingRequest("http://somewhere/", RequestBody.create(MediaType.parse("application/json"), "request"));

        String result = request.execute(mockclient);

        Assert.assertEquals(result, "content");
    }
}
