![PHG Icon](http://performancehorizon.com/img/logo-on-white.svg)

# Measurement Kit Android SDK

## Overview

Measurement Kit facilitates install and event tracking from within your app, as part of Performance Horizon's performance marketing service. Simply add the SDK to your app, and you can begin to track a wide variety of actions with Perfomance Horizon's tracking API.

## Implementation

### Installation

If you're using android studio, mobile tracking can be added to your gradle build as follows.

To your project build.gradle, add a maven repository 

	buildscript {
    		repositories {
        		jcenter()

        		//maven repo for mobile tracking (will be added to jcenter in future)
        		maven {
            			url 'https://dl.bintray.com/owainbrown/maven/'
        		}
    		}
    	
    		dependencies {
        		classpath 'com.android.tools.build:gradle:1.1.0'
    		}
	}

To your app module, add the following.

	dependencies {
	    /*
	    *
	    * All other dependencies....
	    */
	    compile 'com.performancehorizon.android:measurementkit:0.2.3'
	}


### Implementing

####Initialisation
Import `com.performancehorizon.measurementkit.*` into your main Activity and initialise.

	import com.performancehorizon.measurementkit.MeasurementService;

	protected void onResume()
    	{
        	super.onResume();
        	
        	MeasurementService.sharedInstance().initialise(this.getApplicationContext(), this.getIntent(), "phg_advertiser_id", "phg_campaign_id");

    	}

You will receive your unique Performance Horizon Advertiser ID and Campaign ID when you are registered within the PHG platform. It is important to note that an Advertiser account can have multiple Campaigns (apps).

#####Google Play Install Referrer
The Google Play Store offers a method for ensuring optimal tracking accuracy via its Google Install Referrer. Clicks with a destination of the google play store will have a unique mobile tracking identifier is appended to the `referrer` parameter.

On install of the App, you can enable the pass back of this referrer value through the :

    <receiver android:name="com.performancehorizon.mobiletracking.ReferrerTracker" android:exported="true">
        <intent-filter>
             <action android:name="com.android.vending.INSTALL_REFERRER" />
        </intent-filter>
    </receiver>

The  SDK will collect the refererer token from the google play store in this case, and ensure accurate attribution.

####Tracking Events
You can use events to track a variety of actions within your app. Events are represented as conversions inside the affiliate interface.

#####Event
The most basic form of event has no value associated with it. (Perhaps an in-app action on which you're not looking to reward affiliates.)

The category parameter is used to set the product conversions.

    MobileTrackingEvent event = new MobileTrackingEvent("registration-initiated");
    MobileTrackingService.trackingInstance().trackEvent(event);

#####Sale
If an event has a value you'd like to track, sales can be associated with an event as follows.

The currency parameter is a ISO 4217 currency code. (eg, USD, GBP)

	Event event = new Event(new Sale("premium upgrade", new BigDecimal(34.5)), "GBP");
	
	MeasurementService.trackingInstance().trackEvent(event);
	
### Deep link configuration
	
####Deep links

In order to be opened via a deep link, a app would commonly register a splash activity with an Intent filter as follows.  (This example uses a custom scheme)

	<intent-filter>
       <data android:scheme="exactview" android:host="open" />
       <action android:name="android.intent.action.VIEW" />
       <category android:name="android.intent.category.DEFAULT" />
       <category android:name="android.intent.category.BROWSABLE" />
    </intent-filter>
