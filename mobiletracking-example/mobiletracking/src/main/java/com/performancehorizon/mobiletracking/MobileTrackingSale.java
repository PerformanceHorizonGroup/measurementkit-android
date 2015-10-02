package com.performancehorizon.mobiletracking;

import java.math.BigDecimal;

/**
 * Created by owainbrown on 27/02/15.
 */
public class MobileTrackingSale {

    private String category;
    private BigDecimal value;
    private Integer quantity;
    private String sku;

    public MobileTrackingSale(String category, BigDecimal value) {
        this.category = category;
        this.value= value;
    }

    public MobileTrackingSale(String category, BigDecimal value, String sku, Integer quantity)
    {
        this(category, value);
        this.quantity= quantity;
        this.sku = sku;
    }

    protected String getCategory()
    {
        return this.category;
    }

    protected BigDecimal getValue()
    {
        return this.value;
    }

    public void setSKU(String sku)
    {
        this.sku = sku;
    }

    protected String getSKU() {
        return this.sku;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    protected Integer getQuantity() {
        return this.quantity;
    }



}
