package com.performancehorizon.measurementkit;

import android.support.annotation.Nullable;

import com.performancehorizon.measurementkit.Sale;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * An Event in the measurement service (represented as a conversion in the affiliate tracking service)
 *
 * An example could be registration, or an in-app purchase.
 */
public class Event {
    private String eventCategory;
    private Map<String, Object> eventData;

    @Nullable
    private List<Sale> sales;
    @Nullable
    private String salesCurrency;

    private Event()
    {
        this.eventData = new HashMap<>();
    }

    /**
     * initialise event with category
     * @param eventCategory - category of the event (corresponds to the product of the created conversion)
     */
    public Event(String eventCategory) {
        this();
        this.eventCategory = eventCategory;
    }

    /**
     * initialise event with sale
     * @param sale - the sale attached to this event
     * @param currency - ISO 4217 currency code the sale takes place in.
     */
    public Event(Sale sale, String currency) {
        this();
        this.addSale(sale, currency);
    }


    /**
     * initialise event list of sales
     * @param sales - list of sales attached to this event.
     * @param currency - ISO 4217 currency code the sales takes place in.
     */
    public Event(List<Sale> sales, String currency) {
        this();
        this.addSales(sales, currency);
    }

    protected Map<String, Object> getEventData(){
        return this.eventData;
    }

    @Nullable
    protected Map<String, Object> getSalesData() {

        if (sales == null || salesCurrency == null) {
            return null;
        }
        else {
            Map<String, Object> items = new HashMap<>();
            List<Map<String, String>> thesales = new ArrayList<>();

            for (Sale sale : sales) {

                Map<String, String> item = new HashMap<>();

                item.put("category", sale.getCategory());
                item.put("value", sale.getValue().toString());

                if (sale.getQuantity() != null) {
                    item.put("quantity", sale.getQuantity().toString());
                }

                if (sale.getSKU() != null) {
                    item.put("sku", sale.getQuantity().toString());
                }

                thesales.add(item);
            }

            items.put("sales", thesales);
            items.put("currency", this.salesCurrency);

            return items;
        }
    }

    protected String getEventTag() {
        return this.eventCategory;
    }

    public void addEventInformation(String key, Object value) {
        this.eventData.put(key, value);
    }

    public void addSale(Sale sale, String currency) {

        List<Sale> items = new ArrayList<Sale>();
        items.add(sale);

        this.addSales(items, currency);
    }

    public void addSales(List<Sale> sales, String currency)
    {
        this.salesCurrency = currency;
        this.sales = sales;


        /*List<Map<String, String>> items = new ArrayList<Map<String, String>>();

        for (MobileTrackingSale sale : sales) {

            Map<String, String> item = new HashMap<String, String>();

            item.put("_phg_category", sale.getCategory());
            item.put("_phg_value", sale.getValue().toString());

            if (sale.getQuantity() != null) {
                item.put("_phg_quantity", sale.getQuantity().toString());
            }

            if (sale.getSKU() != null) {
                item.put("_phg_sale", sale.getQuantity().toString());
            }

            items.add(item);
        }

        this.addEventInformation("_phg_items", items);
        this.addEventInformation("_phg_currency", currency);*/
    }
}
