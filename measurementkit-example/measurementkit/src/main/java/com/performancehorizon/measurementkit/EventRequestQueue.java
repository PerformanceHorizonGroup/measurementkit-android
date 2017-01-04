package com.performancehorizon.measurementkit;



import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by owainbrown on 25/01/16.
 */
public class EventRequestQueue {

    protected static class EventRequestJSONFactory {
        public EventRequestJSONBuilder getBuilder() {
            return new EventRequestJSONBuilder();
        }
    }

     private List<EventRequest> incompleteEventRequests;
     private TrackingRequestFactory factory;
     private TrackingRequestQueue requestQueue;
     private TrackingURLHelper urlHelper;
     private EventRequestJSONFactory jsonFactory;

     private String campaignID;


    protected EventRequestQueue( TrackingRequestQueue queue,
                              TrackingRequestFactory factory,
                              TrackingURLHelper urlHelper,
                              EventRequestJSONFactory jsonFactory) {
        this.requestQueue = queue;
        this.factory = factory;
        this.urlHelper = urlHelper;
        this.jsonFactory= jsonFactory;

        this.incompleteEventRequests = new ArrayList<>();
    }

    public EventRequestQueue( TrackingRequestQueue queue,
                              TrackingRequestFactory factory,
                              TrackingURLHelper urlHelper) {
        this(queue, factory, urlHelper, new EventRequestJSONFactory());
    }

    public void clearIncompleteRequests() {
        this.incompleteEventRequests.clear();
    }

    public void addEventRequest(EventRequest request) {
        if (this.campaignID != null && request.getTrackingID() != null) {

            EventRequestJSONBuilder jsonbuilder = this.jsonFactory.getBuilder();

            jsonbuilder.setCampaignID(this.campaignID)
                    .setEvent(request.getEvent())
                    .setMobileTrackingID(request.getTrackingID());

            JSONObject eventjson  = jsonbuilder.build();

            if (eventjson != null) {//if there's an error at this point, the event is invalid, and should be ignored.

                TrackingRequest eventrequest = factory.getRequest(this.urlHelper.urlStringForTracking() + "/event", eventjson);
                this.requestQueue.enqueueRequest(eventrequest);
            }
        }
        else
        {
            this.incompleteEventRequests.add(request);
        }
    }

    public void setCampaignID(String campaignID) {
        this.campaignID = campaignID;

        this.enqueueIncompletes();
    }

    public void setTrackingIDForIncompleteRequests( String trackingID) {
        for (EventRequest request : this.incompleteEventRequests) {
            request.setTrackingID(trackingID);
        }

        this.enqueueIncompletes();
    }

    public void enqueueIncompletes() {
        //clone the list, as we're going to be iterating over it while mutating.
        List<EventRequest> incompletes = new ArrayList<EventRequest>(this.incompleteEventRequests);

        for (EventRequest request : incompletes) {

            //remove from list while we consider whether it's ready for sending.
            this.incompleteEventRequests.remove(request);

            this.addEventRequest(request);
        }
    }

    public void setDelegate(TrackingRequestQueueDelegate delegate) {
        this.requestQueue.setDelegate(delegate);
    }

    public void setQueueIsPaused(boolean isPaused) {
        this.requestQueue.setQueueIsPaused(isPaused);
    }

    protected List<EventRequest> getIncompleteEventRequests() {
        return this.incompleteEventRequests;
    }

}
