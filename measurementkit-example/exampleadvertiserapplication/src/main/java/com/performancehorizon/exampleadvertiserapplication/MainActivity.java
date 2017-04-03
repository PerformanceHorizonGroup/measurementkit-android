package com.performancehorizon.exampleadvertiserapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.performancehorizon.measurementkit.Event;
import com.performancehorizon.measurementkit.MeasurementService;

import com.performancehorizon.measurementkit.MeasurementServiceConfiguration;
import com.performancehorizon.measurementkit.Sale;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ListView listview = (ListView) findViewById(R.id.listview);

        List<String> strings = new ArrayList<>();
        strings.add("A");
        strings.add("B");
        strings.add("C");
        strings.add("D");
        strings.add("E");

        listview.setAdapter(new ArrayAdapter<String>(this, R.layout.shop_item, strings) {

            class FakeItem {
                protected String name;
                protected String detail;
                protected int resourceId;
                protected Event event;

                FakeItem(String name, String detail, int resourceId, Event event) {
                    this.name = name;
                    this.detail = detail;
                    this.resourceId = resourceId;
                    this.event = event;
                }
            }


            @Override
            public View getView(final int position, View convertView, ViewGroup parent) {

                /*MeasurementService.sharedInstance().trackEvent(new Event.Builder()
                        .customerReference("366559")
                        .customerType("new")
                        .country("GB")
                        .sale(new Sale.Builder()
                                .category("registration")
                                .value(new BigDecimal(0.0))
                                .saleMetaItem("device", "mobile")
                                .build(), "EUR")
                        .build());

                MeasurementService.sharedInstance().trackEvent(new Event.Builder()
                        .conversionReference("RMCRL3")
                        .customerReference("171553")
                        .voucher("")
                        .customerType("existing")
                        .country("DE")
                        .sale(new Sale.Builder()
                                .category("repeat")
                                .sku("1")
                                .value(new BigDecimal(100))
                                .saleMetaItem("fee", "5")
                                .saleMetaItem("payment_fee", "null")
                                .saleMetaItem("transaction_purpose", "undefined")
                                .saleMetaItem("device", "mobile")
                                .saleMetaItem("payout_country", "KGZ")
                                .saleMetaItem("sending_country", "DEU")
                                .saleMetaItem("corridor", "DEU-KGZ")
                                .saleMetaItem("receving_currency", "EUR")
                                .saleMetaItem("transfer_total_amount", "40")
                                .saleMetaItem("payment_method", "DEBIT_CARD")
                                .saleMetaItem("delivery_method", "BANK_DEPOSIT")
                                .build(), "EUR")
                        .build());*/

                List<FakeItem> items = new ArrayList<>();
                items.add(new FakeItem("Cool shirt", "Extra large, really amazing shirt.", R.drawable.item_1,
                        new Event.Builder().sale(
                                new Sale.Builder()
                                        .category("Clothing - Men")
                                        .value(new BigDecimal(25.0))
                                        .sku("SKDDNKN")
                                        .saleMetaItem("comment", "this is a cool shirt, right?")
                                        .saleMetaItem("size", "XL")
                                        .saleMetaItem("brand", "Cool Guys")
                                .build(), "GBP")
                                .customerType("existing").build()));

                items.add(new FakeItem("Some men's shoes", "Brogues are cool", R.drawable.item_2,
                        new Event.Builder().sale(
                                new Sale.Builder()
                                        .category("Footwear - Men")
                                        .value(new BigDecimal(25.0))
                                        .sku("RMJTHUEJKE1334")
                                        .saleMetaItem("comment", "I own some of these ")
                                        .saleMetaItem("size", "12")
                                        .saleMetaItem("brand", "Shoes & Co")
                                        .build(), "GBP")
                                .customerType("existing").build()));

                items.add(new FakeItem("Funky dress", "A dress.  It's good I guess.", R.drawable.item_3,
                        new Event.Builder().sale(
                                new Sale.Builder()
                                        .category("Clothing - Women")
                                        .value(new BigDecimal(45.0))
                                        .sku("JKBGUKJDUI676767")
                                        .saleMetaItem("comment", "fancy, isn't it")
                                        .saleMetaItem("size", "8")
                                        .saleMetaItem("brand", "LA")
                                        .build(), "GBP")
                                .customerType("existing").build()));

                items.add(new FakeItem("Some more shoes", "Nice, aren't they?", R.drawable.item_4,
                        new Event.Builder().sale(
                                new Sale.Builder()
                                        .category("Footwar - Women")
                                        .value(new BigDecimal(12.34))
                                        .sku("JKBGUKJDUI676767")
                                        .saleMetaItem("comment", "but which shoes?")
                                        .saleMetaItem("size", "5")
                                        .saleMetaItem("brand", "Clarks")
                                        .build(), "GBP")
                                .customerType("existing").build()));

                items.add(new FakeItem("Trainers", "Nike trainers", R.drawable.item_5,
                        new Event.Builder().sale(
                                new Sale.Builder()
                                        .category("Footwar - Women")
                                        .value(new BigDecimal(12.34))
                                        .sku("JKBGUKJDUI676767")
                                        .saleMetaItem("comment", "go f")
                                        .saleMetaItem("size", "8")
                                        .saleMetaItem("brand", "Nike")
                                        .build(), "GBP")
                                .customerType("existing").build()));

                LayoutInflater inflater = LayoutInflater.from(parent.getContext());

                View listitem = (convertView != null) ? convertView: inflater.inflate(R.layout.listitem_fake, null);

                TextView title = (TextView) listitem.findViewById(R.id.item_title);
                title.setText(items.get(position).name);

                TextView detail = (TextView) listitem.findViewById(R.id.item_subtitle);
                detail.setText(items.get(position).detail);

                ImageView image = (ImageView) listitem.findViewById(R.id.item_image);
                image.setImageResource(items.get(position).resourceId);

                final Event event = items.get(position).event;

                FloatingActionButton fab = (FloatingActionButton) listitem.findViewById(R.id.item_add_button);
                fab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        MeasurementService.sharedInstance().trackEvent(event);
                    }
                });

                return listitem;
            }
        });

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                List<Sale> sales = new ArrayList();
                sales.add(new Sale.Builder()
                        .value(new BigDecimal("100"))
                        .category("repeat")
                        .saleMetaItem("fee", "5")
                        .saleMetaItem("payment_fee", "null")
                        .saleMetaItem("transaction_purpose", "undefined")
                        .saleMetaItem("device", "notweb")
                        .saleMetaItem("payout_country", "KGZ")
                        .saleMetaItem("sending_country", "DEU")
                        .saleMetaItem("corridor", "DEU-KGZ")
                        .saleMetaItem("receiving_currency", "EUR")
                        .saleMetaItem("transfer_total_amount", "105")
                        .saleMetaItem("payment_method", "SOFORT")
                        .saleMetaItem("delivery_method", "CASH_PICK_UP")
                        .build());

                Event event = new Event.Builder()
                        .conversionReference("RMCRL3")
                        .customerReference("171553")
                        .voucher("")
                        .customerType("existing")
                        .country("DE")
                        .sales(sales, "GBP").build();

                MeasurementService.sharedInstance().trackEvent(event);
            }
        });*/
    }

    @Override public void onResume() {
        super.onResume();

        MeasurementService.sharedInstance().clearTracking(this);

        MeasurementService.sharedInstance().initialise(this, this.getIntent(), "1100l12", "1011l45");
        //MeasurementService.sharedInstance(MeasurementServiceConfiguration.activeFingerprintConfig()).initialise(this, this.getIntent(), "1", "300659");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        return super.onOptionsItemSelected(item);
    }
}
