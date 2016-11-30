package com.performancehorizon.measurementkit;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;

import java.math.BigDecimal;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 12/01/16.
 */

@RunWith(AndroidJUnit4.class)
public class TestEventRequestJSONBuilder {

    @Before
    public void init() {
        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());
    }

    @Test //test of basic event construction
    public void testCategoryOnlyEvent() throws Exception
    {
        JSONObject salejson = new JSONObject();
        SaleJSONBuilder mockbuilder = mock(SaleJSONBuilder.class);
        when(mockbuilder.build()).thenReturn(salejson);

        SaleJSONBuilderFactory mockbuilderfactory = mock(SaleJSONBuilderFactory.class);
        when(mockbuilderfactory.getSaleJSONBuilder(argThat(new SingleItemSale()))).thenReturn(mockbuilder);

        EventRequestJSONBuilder eventbuilder = new EventRequestJSONBuilder(mockbuilderfactory);

        eventbuilder.setEvent(new Event("category"))
                .setCampaignID("campaign_id")
                .setMobileTrackingID("mobile_tracking_id");

        JSONObject eventjson = eventbuilder.build();

        JSONArray salesjson = eventjson.getJSONArray("sales");

        Assert.assertEquals(1, salesjson.length());
        Assert.assertEquals(salesjson.get(0), salejson);

        Assert.assertEquals(eventjson.getString("mobiletracking_id"),
                "mobile_tracking_id");
        Assert.assertEquals(eventjson.getString("campaign_id"), "campaign_id");
        Assert.assertNotNull(eventjson.get("event_id"));
        Assert.assertNotNull(eventjson.get("date"));
        Assert.assertNull(eventjson.opt("meta"));
        Assert.assertNull(eventjson.opt("currency"));
    }

    @Test
    public void testEventWithSale() throws Exception
    {
        JSONObject salejson = new JSONObject();
        SaleJSONBuilder mockbuilder = mock(SaleJSONBuilder.class);
        when(mockbuilder.build()).thenReturn(salejson);

        SaleJSONBuilderFactory mockbuilderfactory = mock(SaleJSONBuilderFactory.class);
        when(mockbuilderfactory.getSaleJSONBuilder(argThat(new SomeBigSale()))).thenReturn(mockbuilder);

        EventRequestJSONBuilder eventbuilder = new EventRequestJSONBuilder(mockbuilderfactory);

        eventbuilder.setEvent(new Event(new Sale("categoryforabigsale", new BigDecimal("100000000000")), "USD"))
                .setCampaignID("campaign_id")
                .setMobileTrackingID("mobile_tracking_id");

        JSONObject eventjson = eventbuilder.build();

        JSONArray salesjson = eventjson.getJSONArray("sales");

        Assert.assertEquals(1, salesjson.length());
        Assert.assertEquals(salesjson.get(0), salejson);
        Assert.assertEquals(eventjson.get("currency"), "USD");
    }

    @Test //test the isValid method
    public void testIsValid()
    {
        EventRequestJSONBuilder missingevent = new EventRequestJSONBuilder();
        missingevent.setCampaignID("campaign_id")
                .setMobileTrackingID("mobile_tracking_id");

        Assert.assertFalse(missingevent.isValid());

        EventRequestJSONBuilder missingcampaignid = new EventRequestJSONBuilder();
        missingcampaignid.setEvent(new Event("category"))
                .setMobileTrackingID("mobile_tracking_id");

        Assert.assertFalse(missingcampaignid.isValid());

        EventRequestJSONBuilder missingtrackingid = new EventRequestJSONBuilder();
        missingtrackingid.setEvent(new Event("category"))
                .setCampaignID("campaign_id");
        Assert.assertFalse(missingtrackingid.isValid());

        EventRequestJSONBuilder allpresent = new EventRequestJSONBuilder();

        allpresent.setEvent(new Event("category"))
                .setCampaignID("campaign_id")
                .setMobileTrackingID("mobile_tracking_id");

        Assert.assertTrue(allpresent.isValid());
    }

    @Test //test that null is returned if eventrequest is invalid.
    public void testInvalidBuild()
    {
        EventRequestJSONBuilder invalid = new EventRequestJSONBuilder();
        invalid.setCampaignID("campaign_id")
                .setMobileTrackingID("mobile_tracking_id");

        Assert.assertFalse(invalid.isValid());
        Assert.assertNull(invalid.build());
    }

    @Test //test event with meta building
    public void testMetaBuild() throws Exception
    {
        EventRequestJSONBuilder eventbuilder = new EventRequestJSONBuilder();

        Event event = new Event("category");
        event.addMetaItem("meta", "meta-value");

        eventbuilder.setEvent(event)
                .setCampaignID("campaign_id")
                .setMobileTrackingID("mobile_tracking_id");

        JSONObject eventjson = eventbuilder.build();

        Assert.assertEquals(eventjson.getJSONObject("meta").getString("meta"), "meta-value");
    }

    @Test //test event with meta building
     public void testCustomerReferenceBuild() throws Exception
    {
        EventRequestJSONBuilder eventbuilder = new EventRequestJSONBuilder();

        Event event = new Event("category");
        event.setCustomerReference("customerreference");

        eventbuilder.setEvent(event)
                .setCampaignID("campaign_id")
                .setMobileTrackingID("mobile_tracking_id");

        JSONObject eventjson = eventbuilder.build();

        Assert.assertEquals(eventjson.getString("custref"), "customerreference");
    }

    @Test //test event with conversion reference
    public void testConversionReference() throws Exception
    {
        EventRequestJSONBuilder eventbuilder = new EventRequestJSONBuilder();

        Event event = new Event("category");
        event.setConversionReference("conversionreference");

        eventbuilder.setEvent(event)
                .setCampaignID("campaign_id")
                .setMobileTrackingID("mobile_tracking_id");

        JSONObject eventjson = eventbuilder.build();

        Assert.assertEquals(eventjson.getString("conversionref"), "conversionreference");
    }

    @Test
    public void testVoucher() throws Exception
    {
        EventRequestJSONBuilder eventbuilder = new EventRequestJSONBuilder();

        Event event = new Event("category");
        event.setVoucher("voucher");

        eventbuilder.setEvent(event)
                .setCampaignID("campaign_id")
                .setMobileTrackingID("mobile_tracking_id");

        JSONObject eventjson = eventbuilder.build();

        Assert.assertEquals(eventjson.getString("voucher"), "voucher");
    }

    @Test
    public void testCountry() throws Exception
    {
        EventRequestJSONBuilder eventbuilder = new EventRequestJSONBuilder();

        Event event = new Event("category");
        event.setCountry("country");

        eventbuilder.setEvent(event)
                .setCampaignID("campaign_id")
                .setMobileTrackingID("mobile_tracking_id");

        JSONObject eventjson = eventbuilder.build();

        Assert.assertEquals(eventjson.getString("country"), "country");
    }

    /*@Test
    public void testIntegrationsExample() throws Exception
    {
        EventRequestJSONBuilder eventbuilder = new EventRequestJSONBuilder();

        Sale examplesale = new Sale("FlightLH", new BigDecimal("993.65"));
        examplesale.setSKU("LHRLAS-LASLHR");
        examplesale.setMetaItem("netvalue", "-212.00");
        examplesale.setMetaItem("class", "World Traveller Plus:T-World Traveller Plus:T");
        examplesale.setMetaItem("depretbkd", "Thu 24 November 2016 15:50-Sun 27 November 2016 20:40-Sun Jan 24 2016 12:29:57 GMT+0000 (GMT)");
        examplesale.setMetaItem("psjs", "0");
        examplesale.setMetaItem("exchangerate", "1.0000");
        examplesale.setMetaItem("hotel", "0");
        examplesale.setMetaItem("car", "0");
        examplesale.setMetaItem("experience", "0");
        examplesale.setMetaItem("carrier", "");
        examplesale.setMetaItem("cookie", "Silver");
        examplesale.setMetaItem("u19", "993.65");
        examplesale.setMetaItem("passenger", "1");
        examplesale.setMetaItem("device", "MOBILE");
        examplesale.setMetaItem("origin","");
        examplesale.setMetaItem("dest","");

        Event event = new Event(examplesale, "GBP");
        event.setConversionReference("YIRY5U");
        event.setCustomerReference("48658795");
        event.setCountry("GB");
        event.setVoucher("null");

        eventbuilder.setEvent(event)
                .setCampaignID("CAMPAIGN_ID")
                .setMobileTrackingID("MOBILE_TRACKING_ID");

        JSONObject eventjson = eventbuilder.build();
    }*/

    //matchers
    private class SingleItemSale extends ArgumentMatcher<Sale> {

        @Override
        public boolean matches(Object argument) {
            Sale asale = (Sale) argument;

            boolean matches = asale.getCategory().equals("category") &&
                    asale.getValue().equals(new BigDecimal(0)) &&
                    asale.getCommission() == null && asale.getOverride() == null &&
                    asale.getCountry() == null && asale.getVoucher() == null &&
                    asale.getQuantity() == null && asale.getSKU() == null;

            return matches;

        }
    }

    private class SomeBigSale extends ArgumentMatcher<Sale> {

        @Override
        public boolean matches(Object argument) {

            Sale asale = (Sale) argument;

            boolean matches = asale.getCategory().equals("categoryforabigsale") &&
                    asale.getValue().equals(new BigDecimal("100000000000")) &&
                    asale.getCommission() == null && asale.getOverride() == null &&
                    asale.getCountry() == null && asale.getVoucher() == null &&
                    asale.getQuantity() == null && asale.getSKU() == null;

            return matches;
        }
    }
}
