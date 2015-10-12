package com.performancehorizon.measurementkit;

import android.test.InstrumentationTestCase;

import com.performancehorizon.measurementkit.Event;
import com.performancehorizon.measurementkit.Sale;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by owainbrown on 24/03/15.
 */
public class Test_MobileTrackingEvent extends InstrumentationTestCase {

    private Event event;

    public void testEventTag()
    {
        this.event = new Event("event");

        String eventtag = this.event.getEventTag();
        assert(eventtag.equals("event"));
    }

    public void testAddInformation()
    {
        this.event = new Event("event");

        Integer size = new Integer(100);
        event.addEventInformation("size", size);

        Map<String, Object> result = this.event.getEventData();
        assert(result.size() == 1);
        assert(result.equals(size));
    }


    public void testAddSale()
    {
        this.event = new Event("event");

        Sale sale = new Sale("athing", new BigDecimal(100), "bob-6", 1);

        event.addSale(sale, "USD");

        Map<String, Object> result = this.event.getSalesData();
        Map<String, Object> salemap = ((List<Map<String, Object>>)result.get("sales")).get(0);

        //question of whether these "reverse implementation tests" are useful.  Leave them in for now for coverage.
        assert(result.get("currency").equals("USD"));
        assert(salemap.get("category").equals("athing"));
        assert(salemap.get("value").equals(new BigDecimal(100).toString()));
        assert(salemap.get("quantity").equals("1"));
        assert(salemap.get("sku").equals("bob-6"));
    }

    public void testAddSales()
    {
        this.event = new Event("event");

        Sale saleA = new Sale("athing", new BigDecimal(100), "bob-6", 1);
        Sale saleB = new Sale("athing", new BigDecimal(100), "bob-7", 1);

        ArrayList<Sale> sales = new ArrayList<>();
        sales.add(saleA);
        sales.add(saleB);

        event.addSales(sales, "USD");

        Map<String, Object> result = this.event.getSalesData();
        List<?> salelist = ((List<?>)result.get("sales"));

        //question of whether these "reverse implementation tests" are useful.  Leave them in for now for coverage.
        assert(result.get("currency").equals("USD"));
        assert(salelist.size() == 2);
    }

}
