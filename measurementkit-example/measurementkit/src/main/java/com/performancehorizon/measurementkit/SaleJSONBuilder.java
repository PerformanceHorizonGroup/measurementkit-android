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

            //other properties. gah java boilerplate.
            if (sale.getSKU() != null) { salejson.put("sku", sale.getSKU().toString());};

            if (sale.getQuantity() != null) {salejson.put("quantity", sale.getQuantity().intValue());};

            if (sale.getCommission() != null) {salejson.put("commission", sale.getCommission().toPlainString());}

            if (sale.getOverride() != null) {salejson.put("override", sale.getOverride().toPlainString());}

            if (sale.getVoucher() != null) {salejson.put("voucher", sale.getVoucher()); }

            if (sale.getCountry() != null) {salejson.put("country", sale.getCountry());};

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