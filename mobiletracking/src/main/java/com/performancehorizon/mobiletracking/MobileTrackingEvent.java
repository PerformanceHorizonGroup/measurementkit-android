package com.performancehorizon.mobiletracking;

import android.support.annotation.Nullable;
import android.util.ArrayMap;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by owainbrown on 27/02/15.
 */
public class MobileTrackingEvent {
    private String eventCategory;
    private Map<String, Object> eventData;

    @Nullable
    private List<MobileTrackingSale> sales;
    @Nullable
    private String salesCurrency;

    private MobileTrackingEvent()
    {
        this.eventData = new HashMap<>();
    }

    public MobileTrackingEvent(String eventCategory) {
        this();
        this.eventCategory = eventCategory;
    }

    public MobileTrackingEvent(MobileTrackingSale sale, String currency) {
        this();
        this.addSale(sale, currency);
    }

    public MobileTrackingEvent(List<MobileTrackingSale> sales, String currency) {
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

            for (MobileTrackingSale sale : sales) {

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

    public void addSale(MobileTrackingSale sale, String currency) {

        List<MobileTrackingSale> items = new ArrayList<MobileTrackingSale>();
        items.add(sale);

        this.addSales(items, currency);
    }

    public void addSales(List<MobileTrackingSale> sales, String currency)
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
