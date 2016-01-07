package com.performancehorizon.measurementkit;

import junit.framework.Assert;
import org.junit.Test;

import java.math.BigDecimal;

/**
 * Created by owainbrown on 07/01/16.
 */
public class TestSale {


    @Test
    public void testConstructWithCategoryAndValue() {
        Sale sale = new Sale("category", new BigDecimal(5));

        Assert.assertEquals(sale.getCategory(), "category");
        Assert.assertEquals(sale.getValue(), new BigDecimal(5));
    }

    @Test
    public void testConstructWithCategoryValueSKUAndQuantity() {

        Sale sale = new Sale("product", new BigDecimal(6), "sku", new Integer(7));

        Assert.assertEquals(sale.getCategory(), "product");
        Assert.assertEquals(sale.getValue(), new BigDecimal(6));
        Assert.assertEquals(sale.getQuantity(), new Integer(7));
        Assert.assertEquals(sale.getSKU(), "sku");
    }

    @Test
    public void testSetSKU() {

        Sale sale = new Sale("category", new BigDecimal(5));
        sale.setSKU("sku");

        Assert.assertEquals(sale.getSKU(), "sku");
    }

    @Test
    public void testSetQuantity() {

        Sale sale = new Sale("category", new BigDecimal(5));
        sale.setQuantity(new Integer(5));

        Assert.assertEquals(sale.getQuantity(), new Integer(5));
    }

    @Test
    public void testSetCommission() {

        Sale sale = new Sale("category", new BigDecimal(5));
        sale.setCommission(new BigDecimal(23.5));

        Assert.assertEquals(sale.getCommission(), new BigDecimal(23.5));
    }

    @Test
    public void testSetOverride() {

        Sale sale = new Sale("category", new BigDecimal(5));
        sale.setOverride(new BigDecimal(12.5));

        Assert.assertEquals(sale.getOverride(), new BigDecimal(12.5));
    }

    @Test
    public void testSetVoucher() {

        Sale sale = new Sale("category", new BigDecimal(5));
        sale.setVoucher("voucher");

        Assert.assertEquals(sale.getVoucher(), "voucher");
    }

    @Test
    public void testSetCountry() {

        Sale sale = new Sale("category", new BigDecimal(5));
        sale.setCountry("UK");

        Assert.assertEquals(sale.getCountry(), "UK");
    }

}
