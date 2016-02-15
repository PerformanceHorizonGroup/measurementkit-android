package com.performancehorizon.measurementkit;

import android.support.annotation.Nullable;

import com.performancehorizon.measurementkit.Sale;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;


/**
 * An Event in the measurement service (represented as a conversion in the affiliate tracking service)
 *
 * An example could be registration, or an in-app purchase.
 */
public class Event {
    private UUID internalEventID;
    private Map<String, String> meta;
    private Date date;

    private @Nullable String conversionReference;
    private @Nullable String customerReference;
    private @Nullable String category;
    private @Nullable List<Sale> sales;
    private @Nullable String salesCurrency;

    private Event()
    {
        this.meta = new HashMap<>();
        this.internalEventID = UUID.randomUUID();
        this.date = new Date();
    }

    /**
     * initialise event with category
     * @param category - category of the event (corresponds to the product of the created conversion)
     */
    public Event(String category) {
        this();
        this.category = category;
    }

    /**
     * initialise event with sale
     * @param sale - the sale attached to this event
     * @param currency - ISO 4217 currency code the sale takes place in.
     */
    public Event(Sale sale, String currency) {
        this();
        this.setSale(sale, currency);
    }


    /**
     * initialise event list of sales
     * @param sales - list of sales attached to this event.
     * @param currency - ISO 4217 currency code the sales takes place in.
     */
    public Event(List<Sale> sales, String currency) {
        this();
        this.setSales(sales, currency);
    }

    //Get meta data for given event.
    protected Map<String, String> getMeta(){
        return this.meta;
    }

    public @Nullable String getCategory() {
        return this.category;
    }

    public void addMetaItem(String key, String value) {
        this.meta.put(key, value);
    }

    protected List<Sale> getSales() {
        return this.sales;
    }

    private void setSale(Sale sale, String currency) {

        List<Sale> items = new ArrayList<Sale>();
        items.add(sale);

        this.setSales(items, currency);
    }

    private void setSales(List<Sale> sales, String currency)
    {
        this.salesCurrency = currency;
        this.sales = sales;
    }

    protected @Nullable String getSalesCurrency() {
        return this.salesCurrency;
    }

    protected String getInternalEventID() {
        return this.internalEventID.toString();
    }


    /**
     * get the conversion reference for the event.  An example could be the order id corresponding to the sales.
     */
    public @Nullable String getConversionReference() {
        return conversionReference;
    }

    /**
     * set the conversion reference for the event.  An example could be the order id corresponding to the sales.
     * @param conversionReference - advertiser conversion reference for this event
     */
    public void setConversionReference(String conversionReference) {
        this.conversionReference = conversionReference;
    }

    /**
     * get the customer reference for the event.  An example could be a tranformation of the username used
     * by the user to log in.
     */
    public @Nullable String getCustomerReference() {
        return this.customerReference;
    }

    /**
     * set the customer reference for the event.  An example could be a tranformation of the username used
     * by the user to log in.
     * @warning please avoid entering personally identifiable information in this field.  A way to avoid this could be to
     * hash the usernames entered.
     */
    public void setCustomerReference(String customerReference) {
        this.customerReference = customerReference;
    }

    /**
     * sets the date the event occurred.
     * @param date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    protected Date getDate() {
        return this.date;
    }
}