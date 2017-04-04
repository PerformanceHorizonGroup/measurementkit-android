package com.performancehorizon.measurementkit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.HashMap;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.argThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.after;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Created by owainbrown on 04/02/16.
 */

@RunWith(AndroidJUnit4.class)
public class TestMeasurementService_Setup {

    private MeasurementService service;

    //mocks for constructor
    private RegisterRequestQueue registerQueue;
    private EventRequestQueue eventQueue;
    private FingerprinterFactory fingerprinterFactory;
    private MeasurementServiceConfiguration config;

    //mocks for initialisation
    private Context context;
    private Context applicationContext;
    private MeasurementService.MeasurementStorageFactory storageFactory;
    private MeasurementService.ReachabilityFactory reachabilityFactory;
    private MeasurementService.IntentProcessorFactory processorFactory;
    private MeasurementService.RegisterRequestFactory registerRequestFactory;
    private MeasurementService.ReferrerTrackerFactory trackerFactory;

    private MeasurementServiceStorage storage;
    private Reachability reachability;
    private RegisterRequest registerRequest;
    private Fingerprinter fingerprinter;
    private ReferrerTracker tracker;

    private WebClickIntentProccessor webProcessor;
    private UniversalIntentProcessor universalProcessor;
    private AppClickIntentProcessor appProcessor;

    private ReachabilityCallback reachabilitycallback;

    private HashMap<String, String> fingerprint =  new HashMap<>();

    //constansts for testins
    private final static String ADVERTISERID = "advertiser_id";
    private final static String CAMPAIGNID = "campaign_id";

    @Before
    public void initialise()
    {
        System.setProperty("dexmaker.dexcache", InstrumentationRegistry.getTargetContext().getCacheDir().getPath());

        //so many things to mock
        registerQueue = mock(RegisterRequestQueue.class);
        eventQueue = mock(EventRequestQueue.class);
        fingerprinterFactory = mock(FingerprinterFactory.class);
        config = mock(MeasurementServiceConfiguration.class);

        context = mock(Context.class);
        storageFactory = mock(MeasurementService.MeasurementStorageFactory.class);
        reachabilityFactory = mock(MeasurementService.ReachabilityFactory.class);
        processorFactory = mock(MeasurementService.IntentProcessorFactory.class);
        registerRequestFactory = mock(MeasurementService.RegisterRequestFactory.class);
        trackerFactory = mock(MeasurementService.ReferrerTrackerFactory.class);

        storage = mock(MeasurementServiceStorage.class);
        reachability = mock(Reachability.class);
        registerRequest = mock(RegisterRequest.class);
        fingerprinter = mock(Fingerprinter.class);

        webProcessor = mock(WebClickIntentProccessor.class);
        universalProcessor = mock(UniversalIntentProcessor.class);
        appProcessor = mock(AppClickIntentProcessor.class);

        reachabilitycallback = mock(ReachabilityCallback.class);

        //grab the reachability callback so we can trigger it.
        when(reachabilityFactory.getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class)))
                .thenAnswer(new Answer<Reachability>() {
                    @Override
                    public Reachability answer(InvocationOnMock invocation) throws Throwable {

                        reachabilitycallback = invocation.getArgumentAt(1, ReachabilityCallback.class);
                        return reachability;
                    }
                });

        when(storageFactory.getMeasurementStorage(any(Context.class))).thenReturn(storage);
        when(processorFactory.getAppIntentProcessor(any(Intent.class), anyString())).thenReturn(appProcessor);
        when(processorFactory.getUniversalIntentProcessor(any(Intent.class), any(TrackingURLHelper.class)))
                .thenReturn(universalProcessor);
        when(processorFactory.getWebIntentProcessor(any(Intent.class))).thenReturn(webProcessor);
        when(registerRequestFactory.getRegisterRequest(any(Context.class), anyBoolean())).thenReturn(registerRequest);
        when(fingerprinterFactory.getFingerprinter(any(Context.class))).thenReturn(fingerprinter);

        service = spy(new MeasurementService(config, registerQueue, eventQueue, fingerprinterFactory));

        //default- return nothing from the processors
        when(webProcessor.getMobileTrackingID()).thenReturn(null);
        when(universalProcessor.getCamref()).thenReturn(null);
        when(appProcessor.getCamref()).thenReturn(null);

        //default- return nothing from storage
        when(storage.getCamRef()).thenReturn(null);
        when(storage.getIsTrackingHalted()).thenReturn(false);
        when(storage.getIsTrackingInactive()).thenReturn(false);
        when(storage.getReferrer()).thenReturn(null);
        when(storage.getTrackingID()).thenReturn(null);

        //fingerprinter hands out a fingerprint
        when(fingerprinter.generateFingerprint()).thenReturn(fingerprint);

        this.tracker = mock(ReferrerTracker.class);
        when(trackerFactory.getReferrerTracker()).thenReturn(this.tracker);
        when(tracker.getReferrer(any(Context.class))).thenReturn(null);

        this.applicationContext = mock(Context.class);
        when(context.getApplicationContext()).thenReturn(this.applicationContext);
    }

    @Test
    public void testSharedInstance()
    {
        MeasurementService sharedinstance = MeasurementService.sharedInstance(config);
        MeasurementService sharedinstanceagain = MeasurementService.sharedInstance();

        Assert.assertEquals(sharedinstance.getConfiguration(), config);
        Assert.assertEquals(sharedinstance, sharedinstanceagain);
    }

    @Test
    public void testTrackEventBeforeInitialise()
    {
        Event event = mock(Event.class);

        service.trackEvent(event);

        verify(registerQueue, times(0)).setQueueIsPaused(anyBoolean());
        verify(eventQueue, times(0)).setQueueIsPaused(anyBoolean());

        verify(this.eventQueue, times(0)).addEventRequest(argThat(new EventRequestNoTrackingID()));
    }

    @Test
    public void testStatusBeforeInitialise()
    {
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.AWAITING_INITIALISE);
    }

    @Test
    public void testReachabilityCallback()
    {

        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return active in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.ACTIVE);
        when(storage.getTrackingID()).thenReturn("tracking_id");

        service.initialise(context, new Intent(Intent.ACTION_MAIN), ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        //Bad practise, I know, but fiddling with additional injection just to test a couple of lines....
        reset(registerQueue, eventQueue);

        reachabilitycallback.onNetworkActive();

        //should set the event queue to active, register queue to inactive
        verify(registerQueue).setQueueIsPaused(false);
        verify(eventQueue).setQueueIsPaused(false);
    }


    @Test //NB - this tests the non-registration parts of initialise( with no context, and no query)
    public void testInitialiseSetupNoContext() throws Exception
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);

        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.QUERYING);

        //setup for fingerprint
        service.initialise(null, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        Assert.assertEquals(service.getAdvertiserID(), ADVERTISERID);
        Assert.assertEquals(service.getCampaignID(), CAMPAIGNID);

        verify(storageFactory).getMeasurementStorage(null);
        verify(context, times(0)).getSystemService(Context.CONNECTIVITY_SERVICE);

        //service
        verify(storage).status();

        Assert.assertNull(service.getDeepLinkIntent());
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.QUERYING);

        //verify the register calls were made asynchronously.
        verify(registerRequest, timeout(1000)).setCampaignID(CAMPAIGNID);
        verify(registerRequest, timeout(1000)).setAdvertiserID(ADVERTISERID);
        verify(registerRequest, timeout(1000)).setFingerprint(fingerprint);

        verify(registerQueue, timeout(1000)).addRegisterRequest(registerRequest);
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testInitialiseSetupWithContextNoStored()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return querying in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(reachabilityFactory).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));

        //Should query, no passed setup data.
        Assert.assertNull(service.getDeepLinkIntent());
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.QUERYING);

        //verify the register calls were made asynchronously.
        verify(registerRequest, timeout(1000)).setCampaignID(CAMPAIGNID);
        verify(registerRequest, timeout(1000)).setAdvertiserID(ADVERTISERID);
        verify(registerRequest, timeout(1000)).setFingerprint(fingerprint);

        verify(registerQueue, timeout(1000)).addRegisterRequest(registerRequest);
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testInitialiseSetupWithContextWithStoredInactive()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return querying in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.INACTIVE);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(reachabilityFactory).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));

        //Should query, no passed setup data.
        Assert.assertNull(service.getDeepLinkIntent());
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.INACTIVE);

        //verify the register calls were not made.
        verify(registerQueue, times(0)).addRegisterRequest(registerRequest);
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testInitialiseSetupWithContextWithStoredHalted()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return querying in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.HALTED);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(reachabilityFactory).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));

        //Should query, no passed setup data.
        Assert.assertNull(service.getDeepLinkIntent());
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.HALTED);

        //verify the register calls were not made.
        verify(registerQueue, times(0)).addRegisterRequest(registerRequest);
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testInitialiseSetupWithContextWithStoredActive()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return active in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.ACTIVE);
        when(storage.getTrackingID()).thenReturn("tracking_id");

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(reachabilityFactory).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));

        //Should query, no passed setup data.
        Assert.assertNull(service.getDeepLinkIntent());
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.ACTIVE);
        Assert.assertEquals(service.getTrackingID(), "tracking_id");

        //verify the register calls were not made.
        verify(registerQueue, times(0)).addRegisterRequest(registerRequest);
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testInitialiseSetupWithContextWithStoredActiveAndReferrer()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);
        when(this.tracker.getReferrer(any(Context.class))).thenReturn("referrer");
        when(storage.getReferrer()).thenReturn("referrer");

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return active in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.ACTIVE);
        when(storage.getTrackingID()).thenReturn("tracking_id");

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(reachabilityFactory).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));

        //Should query, no passed setup data.
        Assert.assertNull(service.getDeepLinkIntent());
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.ACTIVE);
        Assert.assertEquals(service.getTrackingID(), "tracking_id");

        //verify the register calls were not made.
        verify(registerQueue, times(0)).addRegisterRequest(registerRequest);
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testInitialiseSetupWithContextWithReferrer()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);
        when(this.tracker.getReferrer(any(Context.class))).thenReturn("referrer");
        when(storage.getReferrer()).thenReturn("referrer");

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return active in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.QUERYING);
        when(storage.getTrackingID()).thenReturn("tracking_id");

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(reachabilityFactory).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));
        verify(storage).putReferrerQuery("referrer");

        //Should query, no passed setup data.
        Assert.assertNull(service.getDeepLinkIntent());
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.QUERYING);

        //verify the register calls were made asynchronously.
        verify(registerRequest, timeout(1000)).setCampaignID(CAMPAIGNID);
        verify(registerRequest, timeout(1000)).setAdvertiserID(ADVERTISERID);
        verify(registerRequest, timeout(1000)).setFingerprint(fingerprint);
        verify(registerRequest, timeout(1000)).setReferrer("referrer");

        verify(registerQueue, timeout(1000)).addRegisterRequest(registerRequest);
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testInitialiseSetupWithContextWithStoredReferrer()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);
        when(storage.getReferrer()).thenReturn("referrer");

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return active in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(reachabilityFactory).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));

        //Should query, no passed setup data.
        Assert.assertNull(service.getDeepLinkIntent());
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.QUERYING);

        //verify the register calls were made asynchronously.
        verify(registerRequest, timeout(1000)).setCampaignID(CAMPAIGNID);
        verify(registerRequest, timeout(1000)).setAdvertiserID(ADVERTISERID);
        verify(registerRequest, timeout(1000)).setFingerprint(fingerprint);
        verify(registerRequest, timeout(1000)).setReferrer("referrer");

        verify(registerQueue, timeout(1000)).addRegisterRequest(registerRequest);
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testInitialiseSetupWithContextWithStoredCamref()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);
        when(storage.getCamRef()).thenReturn("camref");

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return active in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(reachabilityFactory).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));

        //Should query, no passed setup data.
        Assert.assertNull(service.getDeepLinkIntent());
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.QUERYING);

        //verify the register calls were made asynchronously.
        verify(registerRequest, timeout(1000)).setCampaignID(CAMPAIGNID);
        verify(registerRequest, timeout(1000)).setAdvertiserID(ADVERTISERID);
        verify(registerRequest, timeout(1000)).setFingerprint(fingerprint);
        verify(registerRequest, timeout(1000)).setCamref("camref");
        verify(registerRequest, never()).setReferrer(anyString());

        verify(registerQueue, timeout(1000)).addRegisterRequest(registerRequest);
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testInitialiseSetupWithContextWithStoredCamrefAndReferrer()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);
        when(storage.getCamRef()).thenReturn("camref");
        when(storage.getReferrer()).thenReturn("referrer");

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return active in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(reachabilityFactory).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));

        //Should query, no passed setup data.
        Assert.assertNull(service.getDeepLinkIntent());
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.QUERYING);

        //verify the register calls were made asynchronously.
        verify(registerRequest, timeout(1000)).setCampaignID(CAMPAIGNID);
        verify(registerRequest, timeout(1000)).setAdvertiserID(ADVERTISERID);
        verify(registerRequest, timeout(1000)).setFingerprint(fingerprint);
        verify(registerRequest, timeout(1000)).setCamref("camref");
        verify(registerRequest, never()).setReferrer(anyString());

        verify(registerQueue, timeout(1000)).addRegisterRequest(registerRequest);
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testRepeatedInitialise()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);
        when(storage.getCamRef()).thenReturn("camref");
        when(storage.getReferrer()).thenReturn("referrer");

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return active in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(applicationContext, times(1)).registerReceiver(Matchers.any(BroadcastReceiver.class), Matchers.any(IntentFilter.class));
        verify(reachabilityFactory, times(1)).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testRepeatedRegisteredAreIgnored()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);
        when(storage.getCamRef()).thenReturn("camref");
        when(storage.getReferrer()).thenReturn("referrer");

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return active in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(applicationContext, times(1)).registerReceiver(Matchers.any(BroadcastReceiver.class), Matchers.any(IntentFilter.class));
        verify(reachabilityFactory, times(1)).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testRepeatedRegistersNoResubmit() throws Exception
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);
        when(storage.getCamRef()).thenReturn("camref");
        when(storage.getReferrer()).thenReturn("referrer");

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return active in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.QUERYING);

        RegisterRequest request = new RegisterRequest(null);
        request.setCampaignID("campaignID");
        request.setAdvertiserID("advertiserID");
        request.setReferrer("referrer");
        request.setCamref("camref");

        when(registerRequestFactory.getRegisterRequest(any(Context.class), anyBoolean())).thenReturn(request);
        when(registerQueue.containsRequest(request)).thenReturn(true);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        // if we already have a matching request in the queue don't both to add a new one.
        // and potentially double up on installs
        verify(registerQueue, after(500).times(0)).addRegisterRequest(any(RegisterRequest.class));
    }


    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testInitialiseSetupWithContextWithUniveralIntent()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);
        Intent filteredintent = new Intent(Intent.ACTION_VIEW);
        when(universalProcessor.getCamref()).thenReturn("camref");
        when(universalProcessor.getFilteredIntent()).thenReturn(filteredintent);

        when(storage.getCamRef()).thenReturn("camref");

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return active in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(reachabilityFactory).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));

        //Should query, no passed setup data.
        Assert.assertEquals(service.getDeepLinkIntent(), filteredintent);
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.QUERYING);

        //verify the register calls were made asynchronously.
        verify(registerRequest, timeout(1000)).setCampaignID(CAMPAIGNID);
        verify(registerRequest, timeout(1000)).setAdvertiserID(ADVERTISERID);
        verify(registerRequest, timeout(1000)).setFingerprint(fingerprint);
        verify(registerRequest, timeout(1000)).setCamref("camref");

        verify(registerQueue, timeout(1000)).addRegisterRequest(registerRequest);
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testInitialiseSetupWithContextWithAppIntent()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);
        Intent filteredintent = new Intent(Intent.ACTION_VIEW);
        when(appProcessor.getCamref()).thenReturn("camref");
        when(appProcessor.getFilteredIntent()).thenReturn(filteredintent);

        when(storage.getCamRef()).thenReturn("camref");

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return active in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.QUERYING);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(reachabilityFactory).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));

        //Should query, no passed setup data.
        Assert.assertEquals(service.getDeepLinkIntent(), filteredintent);
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.QUERYING);

        //verify the register calls were made asynchronously.
        verify(registerRequest, timeout(1000)).setCampaignID(CAMPAIGNID);
        verify(registerRequest, timeout(1000)).setAdvertiserID(ADVERTISERID);
        verify(registerRequest, timeout(1000)).setFingerprint(fingerprint);
        verify(registerRequest, timeout(1000)).setCamref("camref");

        verify(registerQueue, timeout(1000)).addRegisterRequest(registerRequest);
    }

    @Test //NB - this tests the non-registration parts of initialise( with context, but no query)
    public void testInitialiseSetupWithContextWithWebIntent()
    {
        Intent boringlink = new Intent(Intent.ACTION_MAIN);
        Intent filteredintent = new Intent(Intent.ACTION_VIEW);
        when(webProcessor.getMobileTrackingID()).thenReturn("tracking_id");
        when(webProcessor.getFilteredIntent()).thenReturn(filteredintent);

        when(storage.getCamRef()).thenReturn("camref");

        //setup for reachability.
        when(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(mock(ConnectivityManager.class));
        //storage should return active in this case.
        when(storage.status()).thenReturn(MeasurementService.MeasurementServiceStatus.ACTIVE);

        service.initialise(context, boringlink, ADVERTISERID, CAMPAIGNID,
                storageFactory, reachabilityFactory, processorFactory, registerRequestFactory, trackerFactory);

        verify(reachabilityFactory).getReachability(any(ConnectivityManager.class), any(ReachabilityCallback.class));

        verify(this.storage).putTrackingID("tracking_id");

        //Should query, no passed setup data.
        Assert.assertEquals(service.getDeepLinkIntent(), filteredintent);
        Assert.assertEquals(service.getStatus(), MeasurementService.MeasurementServiceStatus.ACTIVE);

        //verify the register calls were not made.
        verify(registerQueue, times(0)).addRegisterRequest(registerRequest);
    }

    private class EventRequestNoTrackingID extends ArgumentMatcher<EventRequest>{

        @Override
        public boolean matches(Object argument) {
            EventRequest request = (EventRequest) argument;

            return request.getTrackingID() == null;
        }
    }
}
