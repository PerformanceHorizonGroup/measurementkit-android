package com.performancehorizon.measurementkit;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by owainbrown on 12/01/16.
 */
public class SaleJSONBuilder {

    private Sale sale;

    public SaleJSONBuilder(Sale sale) {
        this.sale = sale;
    }

    public JSONObject build() {
        try {
            JSONObject salejson = new JSONObject();

            //defaults
            salejson.put("category", sale.getCategory());
            salejson.put("value", sale.getValue().toPlainString());

            //optional properties
            salejson.putOpt("sku", sale.getSKU());
            salejson.putOpt("voucher", sale.getVoucher());
            salejson.put("country", sale.getCountry());

            //optional non-string properties.
            if (sale.getQuantity() != null) {salejson.put("quantity", sale.getQuantity().intValue());};
            if (sale.getCommission() != null) {salejson.put("commission", sale.getCommission().toPlainString());}
            if (sale.getOverride() != null) {salejson.put("override", sale.getOverride().toPlainString());}


            if (sale.getMetaItems().size() > 0) {
                JSONObject meta = new JSONObject();

                for (Map.Entry<String, String> entry: sale.getMetaItems().entrySet()) {
                    meta.put(entry.getKey(), entry.getValue());
                }

                salejson.put("meta", meta);
            }

            return salejson;
        }
        catch (Exception jsonexception) {
            return null;
        }
    }
}