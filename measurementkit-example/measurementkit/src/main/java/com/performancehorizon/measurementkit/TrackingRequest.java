package com.performancehorizon.measurementkit;

import java.io.IOException;
import java.util.Map;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by owainbrown on 02/03/15.
 */
public class TrackingRequest {

    private Request request;

    private String url;
    private RequestBody postBody;

    //so you can refer back to the original params used to construct.
    //(quick solution, this class is meant to encapsulate transport rather than a whole request chain)
    private Map<String, Object> requestParameters;

    public TrackingRequest(String url, RequestBody postBody) {
       this.setUrl(url);
       this.setPostBody(postBody);
    }

    public String execute(OkHttpClientWrapper client) throws IOException{

        okhttp3.Request trackingrequest = new okhttp3.Request.Builder()
                .url(this.getUrl())
                .post(this.getPostBody())
                .build();

        Response response =  client.newCall(trackingrequest).execute();

        return response.body().string();
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public RequestBody getPostBody() {
        return postBody;
    }

    public void setPostBody(RequestBody postBody) {
        this.postBody = postBody;
    }

    public void setRequestParameters(Map<String, Object> params)
    {
        this.requestParameters = params;
    }

    public Map<String, Object> getRequestParameters()
    {
        return this.requestParameters;
    }
}
