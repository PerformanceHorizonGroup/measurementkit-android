package com.performancehorizon.measurementkit;

import junit.framework.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;



/**
 * Created by owainbrown on 06/01/16.
 */
public class TestEvent {

    @Test
    public void testConstructWithCategory()
    {
        Event event = new Event("event");

        String eventtag = event.getCategory();
        Assert.assertEquals(event.getCategory(), "event");

        Assert.assertNotNull(event.getInternalEventID());
        Assert.assertNotNull(event.getDate());
    }

    @Test
    public void testConstructWithSale()
    {
        Sale sale = new Sale("category", new BigDecimal(10.3));

        Event event = new Event(sale, "USD");

        Assert.assertEquals(event.getSales().size(), 1);
        Assert.assertEquals(event.getSales().get(0), sale);
        Assert.assertEquals(event.getSalesCurrency(), "USD");

        Assert.assertNotNull(event.getInternalEventID());
        Assert.assertNotNull(event.getDate());
    }

    @Test
    public void testConstructWithSales()
    {
        Sale asale = new Sale("category", new BigDecimal(10.3));
        Sale anothersale = new Sale("category", new BigDecimal(11));

        Sale[] sales = {asale,anothersale};

        List<Sale> listsales= Arrays.asList(sales);
        Event event = new Event(listsales, "USD");

        Assert.assertEquals(event.getSales().size(), 2);
        Assert.assertTrue(event.getSales().contains(asale));
        Assert.assertTrue(event.getSales().contains(anothersale));

        Assert.assertNotNull(event.getInternalEventID());
        Assert.assertNotNull(event.getDate());
    }

    @Test
    public void testAddMeta()
    {
        Event event = new Event("event");
        event.addMetaItem("meta", "100");

        Map<String, String> meta = event.getMeta();

        Assert.assertEquals(meta.size(), 1);
        Assert.assertEquals(meta.get("meta"), "100");

    }

    @Test
    public void testSetConversionReference() {
        Event event = new Event("event");

        event.setConversionReference("test");

        Assert.assertEquals(event.getConversionReference(), "test");
    }

    @Test
    public void testSetDate()
    {
        Date now = new Date();

        Event event = new Event("event");
        event.setDate(now);

        Assert.assertEquals(event.getDate(), now);
    }

    @Test
    public void testSetCustomerReference()
    {
        Event event = new Event("event");
        event.setCustomerReference("customerreference");

        Assert.assertEquals("customerreference", event.getCustomerReference());
    }

    /*public void testAddSale()
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
    }*/
}