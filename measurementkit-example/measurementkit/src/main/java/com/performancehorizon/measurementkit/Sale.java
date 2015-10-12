package com.performancehorizon.measurementkit;

import android.support.annotation.Nullable;

import java.math.BigDecimal;

/**
 * Sale item.  Attached to {@link Event}
 */
public class Sale {

    private String category;
    private BigDecimal value;
    private Integer quantity;
    private String sku;

    /**
     *
     * intialise a sale with a category and a value
     * @param category category of the sale (corresponds to product in PH affilate tracking service)
     * @param value decimal value of the sale
     */
    public Sale(String category, BigDecimal value) {
        this.category = category;
        this.value= value;
    }

    /**
     *
     * @param category  category of the sale (corresponds to product in PH affilate tracking service)
     * @param value decimal value of the sale
     * @param sku SKU of the product sold
     * @param quantity quanity of products sold
     */
    public Sale(String category, BigDecimal value, String sku, Integer quantity)
    {
        this(category, value);
        this.quantity= quantity;
        this.sku = sku;
    }

    /**
     * get the category of the sale
     * @return category of the sale (corresponds to product in PH affilate tracking service)
     */
    protected String getCategory()
    {
        return this.category;
    }

    /**
     * get the Decimal value of the sale
     * @return value of the sale
     */
    protected BigDecimal getValue()
    {
        return this.value;
    }

    /**
     * sets the SKU of the sale
     * @param sku SKU of the product
     */
    public void setSKU(String sku)
    {
        this.sku = sku;
    }

    /**
     * get the SKU of the sale
     * @return SKU of the product sold (may be null)
     */
    protected @Nullable String getSKU() {
        return this.sku;
    }

    /**
     * set the quantity of products sold
     * @param quantity
     */
    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    /**
     * get the quantity of products sold
     * @return quantity sold (may be null)
     */
    protected Integer getQuantity() {
        return this.quantity;
    }



}
