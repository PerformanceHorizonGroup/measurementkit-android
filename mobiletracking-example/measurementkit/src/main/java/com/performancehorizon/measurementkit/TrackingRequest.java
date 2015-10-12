package com.performancehorizon.measurementkit;


import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

/**
 * Created by owainbrown on 02/03/15.
 */
public class TrackingRequest {

    private com.squareup.okhttp.Request request;

    private String url;
    private RequestBody postBody;

    public TrackingRequest(String url, RequestBody postBody) {
       this.setUrl(url);
       this.setPostBody(postBody);
    }

    public String execute(OkHttpClient client) throws IOException{

        com.squareup.okhttp.Request trackingrequest = new com.squareup.okhttp.Request.Builder()
                .url(this.getUrl())
                .post(getPostBody())
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
}
