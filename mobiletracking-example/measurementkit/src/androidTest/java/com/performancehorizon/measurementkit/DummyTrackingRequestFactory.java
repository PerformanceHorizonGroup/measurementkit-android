package com.performancehorizon.measurementkit;


import java.io.IOException;
import java.util.ArrayList;

import com.squareup.okhttp.OkHttpClient;

import org.mockito.Mockito;
import org.mockito.Matchers;

/**
 * Created by owainbrown on 13/03/15.
 */
public class DummyTrackingRequestFactory extends TrackingRequestFactory {

    private class RequestResult {

        private String result;
        private boolean isError = true;

        public RequestResult(String result) {
            this.isError = false;
            this.result = result;
        }

        public RequestResult() {};

        public String getResult() {
            return this.result;
        }

        public boolean isError() {
            return this.isError;
        }
    }

    private ArrayList<RequestResult> requestStack = new ArrayList<>();

    public void pushResult(String result)
    {
        this.requestStack.add(new RequestResult(result));
    }

    public void pushError()
    {
        this.requestStack.add(new RequestResult());
    }

    public TrackingRequest getRequest(String url, String postBody) {

        //get a result to mock with
        final RequestResult returnedresult;

        if (this.requestStack.size() > 0) {
            returnedresult = this.requestStack.get(0);
            this.requestStack.remove(0);
        } else {
            returnedresult = new RequestResult();
        }

        TrackingRequest dummyrequest = Mockito.mock(TrackingRequest.class);

        if (returnedresult.isError()) {
            try {
                Mockito.doThrow(new IOException()).when(dummyrequest).execute(Matchers.any(OkHttpClient.class));
            } catch (Exception exception) {
            }
        } else {
            try {
                Mockito.doReturn(returnedresult.getResult()).when(dummyrequest).execute(Matchers.any(OkHttpClient.class));
            } catch (Exception exception) {
            }
        }

        return dummyrequest;
    }
}
