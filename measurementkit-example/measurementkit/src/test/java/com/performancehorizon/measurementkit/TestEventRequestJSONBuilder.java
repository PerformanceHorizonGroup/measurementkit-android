package com.performancehorizon.measurementkit;

import junit.framework.Assert;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import java.math.BigDecimal;

import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 12/01/16.
 */

@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT,sdk = 21)
public class TestEventRequestJSONBuilder {

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
