package com.performancehorizon.measurementkit;

import junit.framework.Assert;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;


import java.math.BigDecimal;

/**
 * Created by owainbrown on 12/01/16.
 */
@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, manifest = Config.DEFAULT,sdk = 21)
public class TestSaleJSONBuilder {

    @Test //Test sale json from the default constructor
    public void testSaleFromConstructor() throws Exception {

        Sale sale = new Sale("product", new BigDecimal("0"));
        SaleJSONBuilder salebuilder = new SaleJSONBuilder(sale);

        JSONObject salejson = salebuilder.build();

        Assert.assertEquals(salejson.getString("category"), "product");
        Assert.assertEquals(salejson.getString("value"), "0");
        Assert.assertEquals(salejson.length(), 2);
    }

    @Test //Test sale json from the default constructor
    public void testSaleWithSKUAndQuantity() throws Exception {

        Sale sale = new Sale("product", new BigDecimal("0"), "sku", new Integer(1));
        SaleJSONBuilder salebuilder = new SaleJSONBuilder(sale);

        JSONObject salejson = salebuilder.build();

        Assert.assertEquals(salejson.getString("category"), "product");
        Assert.assertEquals(salejson.getString("value"), "0");
        Assert.assertEquals(salejson.getString("sku"), "sku");
        Assert.assertEquals(salejson.getInt("quantity"), 1);
        Assert.assertEquals(salejson.length(), 4);
    }

    @Test
    public void testSaleWithCommission() throws Exception {

        Sale sale = new Sale("product", new BigDecimal(0));
        sale.setCommission(new BigDecimal("0.1"));

        SaleJSONBuilder salebuilder = new SaleJSONBuilder(sale);

        JSONObject salejson = salebuilder.build();

        Assert.assertEquals(salejson.get("commission"), "0.1");
        Assert.assertEquals(salejson.length(), 3);
    }

    @Test
    public void testSaleWithOverride() throws Exception {

        Sale sale = new Sale("product", new BigDecimal(0));
        sale.setOverride(new BigDecimal("1.254"));

        SaleJSONBuilder salebuilder = new SaleJSONBuilder(sale);

        JSONObject salejson = salebuilder.build();

        Assert.assertEquals(salejson.get("override"), "1.254");
        Assert.assertEquals(salejson.length(), 3);
    }

    @Test
    public void testSaleWithVoucher() throws Exception {

        Sale sale = new Sale("product", new BigDecimal(0));
        sale.setVoucher("voucher");

        SaleJSONBuilder salebuilder = new SaleJSONBuilder(sale);

        JSONObject salejson = salebuilder.build();

        Assert.assertEquals(salejson.get("voucher"), "voucher");
        Assert.assertEquals(salejson.length(), 3);
    }

    @Test
    public void testSaleWithCountry() throws Exception {

        Sale sale = new Sale("product", new BigDecimal(0));
        sale.setCountry("USA");

        SaleJSONBuilder salebuilder = new SaleJSONBuilder(sale);

        JSONObject salejson = salebuilder.build();

        Assert.assertEquals(salejson.get("country"), "USA");
        Assert.assertEquals(salejson.length(), 3);
    }
}
