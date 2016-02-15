package com.performancehorizon.measurementkit;

/**
 * Created by owainbrown on 12/01/16.
 */
public class SaleJSONBuilderFactory {

    public SaleJSONBuilder getSaleJSONBuilder(Sale sale) {
        return new SaleJSONBuilder(sale);
    }
}
