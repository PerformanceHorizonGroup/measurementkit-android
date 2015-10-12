package com.performancehorizon.measurementkit;

import android.test.InstrumentationTestCase;
import android.test.mock.MockResources;
import android.test.suitebuilder.annotation.SmallTest;

import com.squareup.okhttp.*;


import static org.mockito.Mockito.*;
import org.junit.Before;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;


import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ResponseCache;

import okio.BufferedSource;

/**
 * Created by owainbrown on 24/03/15.
 */

public class Test_MobileTrackingRequest extends InstrumentationTestCase {

    private OkHttpClient mockclient;
    private Call mockcall;

    @Before
    public void setUp() {

        this.mockclient = mock(OkHttpClient.class);
        this.mockcall = mock(Call.class);
        when(mockclient.newCall(any(com.squareup.okhttp.Request.class))).thenReturn(mockcall);

        System.setProperty("dexmaker.dexcache", getInstrumentation().getTargetContext().getCacheDir().getPath());
    }

//   @SmallTest
//   public void testRequest() throws IOException
//    {
//        Response.Builder builder = new Response.Builder().body(ResponseBody.create(MediaType.parse("application/madness"), "hello"))
//                .request(new Request.Builder().url("http://www.google.com").get().build())
//                .protocol(Protocol.HTTP_1_0)
//                .code(400);
//
//        Response response = builder.build();
//
//        when(mockcall.execute()).thenReturn(response);
//
//        //MobileTrackingRequest request = new MobileTrackingRequest("http://www.performancehorizon.com", "whocares");
//
//        String result = request.execute(this.mockclient);
//        assert(result.equals("hello"));
//    }
//
//    @SmallTest
//    public void testRequestFail() {
//        String result = "a result so simple it hardly needs a test!";
//
//        /*Response.Builder builder = new Response.Builder().body(ResponseBody.create(MediaType.parse("application/madness"), "hello"))
//                .request(new Request.Builder().url("http://www.google.com").get().build())
//                .protocol(Protocol.HTTP_1_0)
//                .code(400);
//
//        Response response = builder.build();*/
//
//        try {
//            when(mockcall.execute()).thenThrow(new MalformedURLException());
//
//            MobileTrackingRequest request = new MobileTrackingRequest("zre~§¶•ª•¶¶ºswerabfjkesgiu3ot378wy89", "whocares");
//
//            request.execute(this.mockclient);
//            fail("this should already have failed");
//        }
//        catch(Exception e)
//        {
//            assert(e instanceof MalformedURLException);
//        }
//    }
}

