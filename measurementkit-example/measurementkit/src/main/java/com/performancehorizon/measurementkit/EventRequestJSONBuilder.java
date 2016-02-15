package com.performancehorizon.measurementkit;

import android.support.annotation.Nullable;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by owainbrown on 11/01/16.
 */
public class EventRequestJSONBuilder {

    private Event event;
    private String mobileTrackingID;
    private String campaignID;

    public SaleJSONBuilderFactory saleBuilderFactory;

    public EventRequestJSONBuilder()
    {
        this(new SaleJSONBuilderFactory());
    }
    
    public EventRequestJSONBuilder(SaleJSONBuilderFactory factory) {
        this.saleBuilderFactory = factory;
    }

    public EventRequestJSONBuilder setEvent(Event event) {
        this.event = event;
        return this;
    }

    public EventRequestJSONBuilder setMobileTrackingID(String mobileTrackingID) {
        this.mobileTrackingID = mobileTrackingID;
        return this;
    }

    public EventRequestJSONBuilder setCampaignID(String campaignID) {
        this.campaignID = campaignID;
        return this;
    }

    protected boolean isValid() {
        return (this.event != null) &&
                this.campaignID != null &&
                this.mobileTrackingID != null;
    }

    public @Nullable JSONObject build() {
        if (this.isValid()) try {
            JSONObject eventrequest = new JSONObject();

            eventrequest.put("mobiletracking_id", this.mobileTrackingID);
            eventrequest.put("campaign_id", this.campaignID);
            eventrequest.put("event_id", this.event.getInternalEventID());
            eventrequest.put("date", event.getDate().getTime() / 1000);

            //start with building the sales json.
            List<Sale> sales = event.getSales();

            //normalize no-sale items into single-sale items.
            if (sales == null) {
                sales = new ArrayList<>();
                sales.add(new Sale(event.getCategory(), new BigDecimal(0)));
            }

            //build array of sales.
            JSONArray salesjson = new JSONArray();
            for (Sale sale: sales) {
                   SaleJSONBuilder salejson = this.saleBuilderFactory.getSaleJSONBuilder(sale);
                   salesjson.put(salejson.build());
            }

            eventrequest.put("sales", salesjson);

            //write meta to a jsonobject
            if(event.getMeta().size() > 0) {
                JSONObject metajson = new JSONObject();

                for (Map.Entry<String, String> item : event.getMeta().entrySet()) {
                    metajson.put(item.getKey(), item.getValue());
                }

                eventrequest.put("meta", metajson);
            }

            //conversion reference, customer reference, sales currency
            if (event.getConversionReference() != null) {
                eventrequest.put("conversionref", event.getConversionReference());
            }

            if (event.getCustomerReference() != null) {
                eventrequest.put("custref", event.getCustomerReference());
            }

            if (event.getSalesCurrency() != null) {
                eventrequest.put("currency", event.getSalesCurrency());
            }

            return eventrequest;

        } catch (JSONException jsonexception) {
        }

        //default return for exceptions or invalid event.
        return null;
    }
}
