![PHG Icon](http://performancehorizon.com/img/logo-on-white.svg)

# Mobile Tracking Android SDK

## Overview

The PHG mobile tracking SDK facilitates install and event tracking from within your app. Simply download the SDK, add into your app, and you can begin to track a wide variety of actions with PHG tracking API.

### Implementation

#### Installation

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
	    compile 'com.performancehorizon.android:mobiletracking:0.1.0'
	}


#### Implementing
Import `com.performancehorizon.mobiletracking` into your main Activity and initialise.

	import com.performancehorizon.mobiletracking.MobileTrackingService;

	protected void onResume()
    	{
        	super.onResume();
        
        	MobileTrackingService.trackingInstance().initialise(this.getApplicationContext(), this.getIntent(), "phg_advertiser_id", "phg_campaign_id");

    	}

You will receive your unique PHG Advertiser ID and Campaign ID when you are registered within the PHG platform. It is important to note that an Advertiser account can have multiple Campaigns (apps).

###Tracking Events
You can use events to track a variety of actions within your app. Events are represented as conversions inside the affiliate interface.

####Event
The most basic form of event has no value associated with it. (Perhaps an in-app action on which you're not looking to reward affiliates.)

The category parameter is used to set the product conversions.

    MobileTrackingEvent event = new MobileTrackingEvent("registration-initiated");
    MobileTrackingService.trackingInstance().trackEvent(event);

####Event
If an event has a value you'd like to track, sales can be associated with an event as follows.

The currency parameter is a ISO 4217 currency code. (eg, USD, GBP)

    MobileTrackingEvent event = new MobileTrackingEvent(new );

    MobileTrackingService.trackingInstance().trackEvent(event);

Sales

If an event has a value you'd like to track, sales can be associated with an event as follows.

The currency parameter is a ISO 4217 currency code. (eg, USD, GBP)

	MobileTrackingEvent event = new MobileTrackingEvent(new MobileTrackingSale("premium upgrade", new BigDecimal(34.5)), "GBP");
	
	MobileTrackingService.trackingInstance().trackEvent(event);
	
	

###Google Play Install Referrer
The Google Play Store offers a method for ensuring optimal tracking accuracy via its Google Install Referrer. Clicks with a destination of the google play store will have a unique mobile tracking identifier is appended to the `referrer` parameter.

On install of the App, you can enable the pass back of this referrer value (`com.android.vending.INSTALL_REFERRER`) through a broadcast reciever :

    <receiver android:name="com.performancehorizon.mobiletracking.ReferrerTracker" android:exported="true">
        <intent-filter>
             <action android:name="com.android.vending.INSTALL_REFERRER" />
        </intent-filter>
    </receiver>

The PHG SDK will collect the refererer token from the google play store in this case, and ensure accurate attribution.
