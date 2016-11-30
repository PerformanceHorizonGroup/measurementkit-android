package com.performancehorizon.exampleadvertiserapplication;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.performancehorizon.measurementkit.Event;
import com.performancehorizon.measurementkit.MeasurementService;
import com.performancehorizon.measurementkit.MeasurementServiceConfiguration;
import com.performancehorizon.measurementkit.Sale;

import java.math.BigDecimal;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Event anevent = new Event(new Sale("A thing", new BigDecimal(12.50)), "GBP");
                MeasurementService.sharedInstance().trackEvent(anevent);
            }
        });
    }

    @Override public void onResume() {
        super.onResume();


        MeasurementService.sharedInstance(MeasurementServiceConfiguration.activeFingerprintConfig()).clearTracking(this);
        MeasurementService.sharedInstance(MeasurementServiceConfiguration.activeFingerprintConfig()).initialise(this, this.getIntent(), "1", "300659");
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

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
