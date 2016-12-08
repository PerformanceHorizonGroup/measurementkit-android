package com.performancehorizon.measurementkit;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import bolts.Continuation;
import bolts.Task;


import com.google.android.gms.ads.identifier.AdvertisingIdClient;

import java.lang.ref.WeakReference;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import okhttp3.OkHttpClient;

/**
 * MeasurementService - interface to Performance Horizon's mobile tracking API.
 *@see com.performancehorizon.measurementkit.Event
 *@see com.performancehorizon.measurementkit.Sale
 */
public class MeasurementService implements TrackingRequestQueueDelegate, RegisterRequestQueueDelegate {

    protected static class ReferrerTrackerFactory {
        public ReferrerTracker getReferrerTracker() {
            return new ReferrerTracker();
        }
    }

    protected static class MeasurementStorageFactory
    {
        public MeasurementServiceStorage getMeasurementStorage(Context context) {
            return new MeasurementServiceStorage(context);
        }
    }

    protected static class ReachabilityFactory
    {
        public Reachability getReachability(ConnectivityManager manager, ReachabilityCallback callback) {
            return new Reachability(manager, callback);
        }
    }

    protected static class RegisterRequestFactory
    {
        public RegisterRequest getRegisterRequest(Context context, boolean trackAndroidAdvertisingIdentifier) {
            return new RegisterRequest(context, trackAndroidAdvertisingIdentifier);
        }
    }

    protected static class EventRequestFactory
    {
        public EventRequest getEventRequest(Event event, String trackingid) {
            return new EventRequest(event, trackingid);
        }

        public EventRequest getEventRequest(Event event) {
            return new EventRequest(event);
        }
    }

    protected static class IntentProcessorFactory
    {
        public WebClickIntentProccessor getWebIntentProcessor(Intent intent) {
            return new WebClickIntentProccessor(intent);
        }

        public AppClickIntentProcessor getAppIntentProcessor(Intent intent, String camrefKey) {
            return new AppClickIntentProcessor(intent, camrefKey);
        }

        public UniversalIntentProcessor getUniversalIntentProcessor(Intent intent, TrackingURLHelper helper)
        {
            return new UniversalIntentProcessor(intent, helper);
        }
    }

    protected static class UriBuilderFactory
    {
        public MeasurementServiceURIBuilder getTrackingUriBuilder(TrackingURLHelper trackinghelper) {
            return new MeasurementServiceURIBuilder(trackinghelper);
        }
    }

    protected static class IntentFactory
    {
        public Intent getIntent(Intent intent) {
            return (Intent)intent.clone();
        }

        public Intent getIntent(String action, Uri data) {
            return new Intent(action, data);
        }
    }

    protected static class RegistrationProcessorFactory
    {
        public RegistrationProcessor getRequestProcessor(String result) {
            return new RegistrationProcessor(result);
        }
    }

    /**
     * Status of the measurementservice;
     */
    public enum MeasurementServiceStatus {
        /**
         * Service is registering to confirm a mobile tracking id.
         */
        QUERYING,

        /**
         * Service has received a mobile tracking id, and is registering events.
         */
        ACTIVE,

        /**
         * Registration has confirmed that this device has no affliate click, so service is inactive.
         */
        INACTIVE,

        /**
         * Service has been constructed but not started.
         */
        AWAITING_INITIALISE,

        /**
         * Service has been halted externally
         */
        HALTED;
    }

    //singleton
    private static MeasurementService _sharedTrackingService;


    //service configuration
    @NonNull private  MeasurementServiceConfiguration config;
    @NonNull private  MeasurementServiceStatus status = MeasurementServiceStatus.AWAITING_INITIALISE;

    //callback
    @Nullable private MeasurementServiceCallback callback;

    //setup variables.
    @NonNull private String campaignID;
    @NonNull private String advertiserID;

    //dependencies.
    private EventRequestQueue eventQueue;
    private RegisterRequestQueue registerQueue;
    private Reachability reachability;

    private TrackingURLHelper urlHelper;

    private FingerprinterFactory fingerprinterfactory;

    @Nullable private WeakReference<Context> context;
    @Nullable private MeasurementServiceStorage storage;

    @Nullable private Uri referrer;
    @Nullable private Intent deepLinkIntent;
    private boolean isInstalled =  false;

    protected class TrackingConstants
    {
        protected final static String DEEPLINK_KEY = "deep_link";
        protected final static String DEEPLINK_ACTION_KEY = "deeplink_action";

        protected final static String TRACKING_INTENT_CAMREF = "com.performancehorizon.camref";
    }

    /**
     * returns a shared instance of the measurement service.
     * @return the shared instance of measurement service.
     */
    public static MeasurementService sharedInstance()
    {
        return MeasurementService.sharedInstance(new MeasurementServiceConfiguration());
    }

    /**
     * Returns a shared singleton instance of the measurement service with the given configuratedion @link{MeasurementServiceConfig}.
     *
     * The configuration will be ignored if a shared instance has already been generated.
     * @return the shared instance of measurement service.
     */
    public static MeasurementService sharedInstance(MeasurementServiceConfiguration config)
    {
        if (_sharedTrackingService == null) {
            _sharedTrackingService = new MeasurementService(config);
        }

        return _sharedTrackingService;
    }

    public MeasurementService() {
        this(new MeasurementServiceConfiguration());
    }

    public MeasurementService(MeasurementServiceConfiguration config) {

        this(config,
                new RegisterRequestQueue(new TrackingRequestQueue(new OkHttpClient()),
                new TrackingRequestFactory(),
                new TrackingURLHelper(config.getDebugModeActive())),
                new EventRequestQueue(
                        new TrackingRequestQueue(new OkHttpClient()),
                        new TrackingRequestFactory(),
                        new TrackingURLHelper(config.getDebugModeActive())),
                new FingerprinterFactory()
        );
    }

    public MeasurementService(MeasurementServiceConfiguration config,
                              RegisterRequestQueue registerQueue,
                              EventRequestQueue eventQueue,
                              FingerprinterFactory fingerprintFactory)
    {

        this.config = config;

        this.registerQueue = registerQueue;
        this.registerQueue.setDelegate(this);
        this.urlHelper = new TrackingURLHelper(config.getDebugModeActive());

        this.eventQueue = eventQueue;
        this.eventQueue.setDelegate(this);

        this.fingerprinterfactory = fingerprintFactory;

        this.status = MeasurementServiceStatus.AWAITING_INITIALISE;
    }


    private boolean registerQueueIsPaused(boolean networkActive) {
        return !(this.status == MeasurementServiceStatus.QUERYING &&
                networkActive);
    }

    private boolean eventQueueIsPaused(boolean networkActive) {

        return !(this.status == MeasurementServiceStatus.ACTIVE &&
                networkActive);
    }

    void fakeInstallBroadcast(Context currentcontext, String referrer)
    {
        Intent googleplayinstall = new Intent("com.android.vending.INSTALL_REFERRER");
        googleplayinstall.putExtra("referrer", referrer);

        LocalBroadcastManager.getInstance(currentcontext).sendBroadcast(googleplayinstall);
    }

    /**
     * Clears all the stored measurementkit data.  Service will restart afresh on next initialise.
     * @param context the current context.
     */
    public void clearTracking(Context context) {
        MeasurementServiceStorage.clearPreferences(context);
    }

    protected static void setTrackingInstance(MeasurementService service)
    {
        _sharedTrackingService = service;
    }

    private void register(RegisterRequestFactory registerRequestFactory) {
        this.register(registerRequestFactory, new HashMap<String, String>());
    }

    private void register(RegisterRequestFactory registerRequestFactory, final Map<String, String> additions) {

        final RegisterRequestFactory therequestfactory = registerRequestFactory;
        final boolean installed = this.isInstalled;

        Task.callInBackground(new Callable<RegisterRequest>() {
            @Override
            public RegisterRequest call() throws Exception {
                return therequestfactory.getRegisterRequest(MeasurementService.this.context.get(),
                        MeasurementService.this.config.getTrackAndroidAdvertisingIdentifier());
                }
            }).continueWith(new Continuation<RegisterRequest, Void>() {
            @Override
            public Void then(Task<RegisterRequest> task) throws Exception {
                RegisterRequest registerrequest = task.getResult();

                registerrequest.setCampaignID(MeasurementService.this.campaignID);
                registerrequest.setAdvertiserID(MeasurementService.this.advertiserID);

                Map<String, String> fingerprint = MeasurementService.this.fingerprinterfactory.getFingerprinter(MeasurementService.this.context.get()).generateFingerprint();
                fingerprint.putAll(additions);

                registerrequest.setFingerprint(fingerprint);

                if (MeasurementService.this.storage.getCamRef() != null) {
                    registerrequest.setCamref(MeasurementService.this.storage.getCamRef());
                }
                else if (MeasurementService.this.storage.getReferrer() != null) {
                    registerrequest.setReferrer(MeasurementService.this.storage.getReferrer());
                }

                if (installed) {
                    registerrequest.setInstalled();
                }

                MeasurementService.this.registerQueue.addRegisterRequest(registerrequest);

                return null;
            }
        });
        }

    /**
     *
     * Initialises the measurement service.  Requires activity {@link Context}, the {@link Intent} that launched the activity,
     * the advertisers advertiserID, and the campaignID you wish conversions to be attributed to.
     *
     * @param context the current context
     * @param intent intent that launched the current activity
     * @param advertiserID Performance Horizon advertiser identifer.  In the PH UI, see Settings, Advertiser
     * @param campaignID Performance Horizon campaign identifer.  In the PH UI, see Settings, Campaign
     */
    public void initialise(@NonNull Context context, Intent intent, @NonNull String advertiserID,
                           @NonNull String campaignID) {
        this.initialise(context, intent, advertiserID, campaignID,
                new MeasurementStorageFactory(),
                new ReachabilityFactory(),
                new IntentProcessorFactory(),
                new RegisterRequestFactory(),
                new ReferrerTrackerFactory()
                );
    }


    protected void initialise(Context context, Intent intent, @NonNull String advertiserID, @NonNull String campaignID,
                              @NonNull MeasurementStorageFactory storageFactory,
                              @NonNull ReachabilityFactory reachabilityFactory,
                              @NonNull IntentProcessorFactory processorFactory,
                              @NonNull final RegisterRequestFactory registerRequestFactory,
                              @NonNull ReferrerTrackerFactory trackerFactory)
    {
        this.context = new WeakReference<>(context);
        this.setAdvertiserID(advertiserID);
        this.setCampaignID(campaignID);

        this.storage = storageFactory.getMeasurementStorage(context);

        if (this.context.get() != null) {

            //load from shared preferences.
            this.storage.loadFromPreferences();

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            this.reachability = reachabilityFactory.getReachability(connectivityManager, new ReachabilityCallback() {
                @Override
                public void onNetworkActive() {
                    MeasurementService.this.eventQueue.setQueueIsPaused(MeasurementService.this.eventQueueIsPaused(true));
                    MeasurementService.this.registerQueue.setQueueIsPaused(MeasurementService.this.registerQueueIsPaused(true));
                }
            });
        }

        //now source data from the referrer
        ReferrerTracker tracker = trackerFactory.getReferrerTracker();
        if (context != null && tracker.getReferrer(context) != null) {
            //we're really just switching the referrer from one set of storage to another here.
            this.storage.putReferrerQuery(tracker.getReferrer(context));
            tracker.clearReferrer(context);
        }

        //and the opening intent
        this.deepLinkIntent = this.processDeepLink(intent, processorFactory);

        //set the initial status (also configures the queue states)
        this.setStatus(this.storage.status());

        //if a new query is needed, send off request.
        if (this.status == MeasurementServiceStatus.QUERYING) {

            if (this.config.useActiveFingerprinting()) {
                ActiveFingerprinter fingerprinter = new ActiveFingerprinter(this.context.get(), new ActiveFingerprinter.Callback() {
                    @Override
                    public void activeFingerprintComplete(ActiveFingerprinter fingerprinter, Map<String, String> fingerprint) {
                        register(registerRequestFactory, fingerprint);
                    }
                });

                fingerprinter.generateFingerprint();
            }
            else {
                this.register(registerRequestFactory);
            }
        }
    }

    void trackEvent(Event event, EventRequestFactory factory)
    {
        //if you're inactive, ignore.  If you're active, send off.
        //all other states, queue without a confirmed mobile tracking id.
        switch(this.status) {
            case ACTIVE:

                //this is just a catch in case somehow
                this.eventQueue.addEventRequest((this.storage.getTrackingID() == null) ?
                        factory.getEventRequest(event) :
                        factory.getEventRequest(event, this.storage.getTrackingID()));
                break;
            case INACTIVE:
                //do nothing!
                break;
            default:
                this.eventQueue.addEventRequest(
                        factory.getEventRequest(event));
        }

        //prompt to check connectivity status and restart queues.
        if (this.reachability != null && this.reachability.isNetworkActive()) {
            this.eventQueue.setQueueIsPaused(this.eventQueueIsPaused(true));
            this.registerQueue.setQueueIsPaused(this.registerQueueIsPaused(true));
        }
    }

    /**
     * Track an event.  Events are registered as a conversion in the Performance horizon's affiliate tracking interface.
     * Events will be uploaded asynchronously in the background.
     * @param event the event to track
     */
    public void trackEvent(Event event)
    {
        this.trackEvent(event, new EventRequestFactory());
    }

    /**
     * Set the status of the measurement service.  Also sets the paused state of the event and register queues.
     * @param status the new status for the measurement service.
     */
    private void setStatus(MeasurementServiceStatus status) {
        this.status = status;

        boolean networkreachable = this.reachability != null ? this.getReachability().isNetworkActive() : false;

        this.eventQueue.setQueueIsPaused(this.eventQueueIsPaused(networkreachable));
        this.registerQueue.setQueueIsPaused(this.registerQueueIsPaused(networkreachable));
    }

    /**
     * Processes the given intent, detecting a deep link if present and using it to set up the state of the measurement service
     * (Specifically gets a tracking id or a query for a camref)
     * @param intent
     * @param factory
     * @return
     */
    private Intent processDeepLink(Intent intent, IntentProcessorFactory factory) {

        //first check for web link. (sli
        WebClickIntentProccessor webprocessor = factory.getWebIntentProcessor(intent);

        if (webprocessor.getMobileTrackingID() != null) {
            this.storage.putTrackingID(webprocessor.getMobileTrackingID());
            return webprocessor.getFilteredIntent();
        }

        AppClickIntentProcessor appprocessor = factory.getAppIntentProcessor(intent, TrackingConstants.TRACKING_INTENT_CAMREF);

        if (appprocessor.getCamref() != null) {
            this.storage.putCamrefQuery(appprocessor.getCamref());
            return appprocessor.getFilteredIntent();
        }

        UniversalIntentProcessor universalprocessor = factory.getUniversalIntentProcessor(intent, this.urlHelper);

        if (universalprocessor.getCamref() != null) {
            this.storage.putCamrefQuery(universalprocessor.getCamref());
            return universalprocessor.getFilteredIntent();
        }

        return null;
    }

    static Intent trackedIntent(Intent intent, String camref) {
        return MeasurementService.trackedIntent(intent, camref, null, new IntentFactory());
    }

    static Intent trackedIntent(Intent intent, String camref, String aaid) {
        return MeasurementService.trackedIntent(intent, camref, aaid, new IntentFactory());
    }

    //convert an intent to one that is tracked by mobile tracking.
    static Intent trackedIntent(Intent intent, String camref, String aaid, IntentFactory factory)
    {
        Intent trackedintent = factory.getIntent(intent);
        MeasurementService.addCamrefToIntent(trackedintent, camref);

        if (intent.getData() != null) {
            final String scheme = intent.getData().getScheme();

            //for the present we'll approximate universal schemes to http and https.
            if (intent.getAction() == Intent.ACTION_VIEW && (scheme.equals("http") || scheme.equals("https"))) {
                trackedintent.setData(MeasurementService.measurementServiceURI(camref, aaid, intent.getData(), null));
            }
        }

        return trackedintent;
    }

    /**
     * Adds a campaign reference to an intent for affiliate tracking.
     * @param intent the intent to which a campaign reference will be added (as a string extra)
     * @param camref the camref attribution identifier, which represents the combination of campaign and publisher
     * @return
     */
    public static Intent addCamrefToIntent(Intent intent, String camref) {
        intent.putExtra(TrackingConstants.TRACKING_INTENT_CAMREF, camref);
        return intent;
    }

    public static Uri measurementServiceURI(String camref, Uri destinationuri, Uri deeplink) {
        return MeasurementService.measurementServiceURI(camref,null, destinationuri, deeplink, false);
    }

    /**
     * Composes a uri to Performance horizon's measurement kit API.
     *
     * When used in the context of an application, the destination field represents the alternative destination for an intent,
     * as it is presumed that the original intent opening has failed.  E.g.  Uri for app has failed, now opening the
     * tracked web equivalent.
     *
     * @param camref the campaign reference that represents the publisher-campaign combination.
     * @param advertisingid the android advertising id.
     * @param destinationuri the destination target for the uri.
     * @param deeplink the deeplink representing the original intent.
     * @return the uri for the measurement kit uri for opening
     *
     */
    public static Uri measurementServiceURI(String camref,@Nullable String advertisingid, Uri destinationuri, Uri deeplink) {
        return MeasurementService.measurementServiceURI(camref, advertisingid, destinationuri, deeplink, false);
    }

    static Uri measurementServiceURI(String camref,@Nullable String advertisingid, Uri destinationuri, Uri deeplink, boolean isDebug) {
        return MeasurementService.measurementServiceURI(camref, advertisingid, destinationuri, deeplink, isDebug, new UriBuilderFactory());
    }

    static Uri measurementServiceURI(@NonNull String camref,@Nullable String advertisingid,
                                            @NonNull Uri destinationuri, @Nullable Uri deeplink,
                                            boolean debuguri, UriBuilderFactory builderfactory) {

        TrackingURLHelper trackinghelper = new TrackingURLHelper(debuguri);
        trackinghelper.setDebug(debuguri);

        MeasurementServiceURIBuilder builder = builderfactory.getTrackingUriBuilder(trackinghelper);

        builder.setDestination(destinationuri);
        builder.setCamref(camref);

        if (advertisingid != null) {
            builder.putAlias("aaid", advertisingid);
        }

        if (deeplink != null) {
            builder.setDeeplink(deeplink);
            builder.setSkipDeepLink(true);
        }

        return builder.build();
    }

    private static boolean startActvity(Context context, Intent intent) {

        //convert exception error flow to boolean to aid the readability of the code.
        boolean activityloaded = true;

        try {
            context.startActivity(intent);
        }
        catch (ActivityNotFoundException activitynotfound) {
            activityloaded = false;
        }

        return activityloaded;
    }

    /**
     *
     * Opens the given intent, with associated PH measurement data.  If the intent opens fails, the given uri is used to construct an alternative Intent,
     * which is then opened (presuming a browser is available).
     *
     * This method can include calls on a background executor, so the activity launch will be asychronous.
     *
     * @param context the current contect, on which new activities will be started.
     * @param intent the intent for the application you wish to open.
     * @param camref camref representing the publisher's membership of the campaign.
     * @param uri alternative uri.  An intent will be constructed from this uri, with action: ACTION_VIEW.
    */
    public static void openIntentWithAlternativeURI(@NonNull Context context,Intent intent,String camref, Uri uri) {
        openIntentWithAlternativeURI(context, intent, camref, uri, new IntentFactory(), false);
    }

    static void debugOpenIntentWithAlternativeURI(@NonNull Context context,Intent intent,String camref, Uri uri) {
        openIntentWithAlternativeURI(context, intent, camref, uri, new IntentFactory(), true);
    }

    static void openIntentWithAlternativeURI(Context context,Intent intent,String camref, Uri uri, IntentFactory factory, boolean isDebug) {
        //gonna be using in the inner classes, so need to be final.
        final Context thecontext = context;
        final String thecamref= camref;
        final Intent theintent = intent;
        final Uri theuri = uri;
        final boolean isdebug = isDebug;

        final IntentFactory intentfactory = factory;

        //retrieve the AAID on a background thread.
        Task.callInBackground(new Callable<String>() {
            @Override
            public String call() throws Exception {
                try {
                    if (Class.forName("com.google.android.gms.ads.identifier.AdvertisingIdClient") != null) {

                        AdvertisingIdClient.Info info = AdvertisingIdClient.getAdvertisingIdInfo(thecontext);

                        return info.getId();
                    }
                    else {
                        return null;
                    }
                }
                catch(Exception advertisingidfail) {
                    MeasurementServiceLog.d("Retrieval of advertising ID failed with error:  " +  advertisingidfail.toString()
                            + " while opening intent");

                    return null;
                }
            }
        }).continueWith(new Continuation<String, Void>() {
            @Override
            public Void then(Task<String> task) throws Exception {
                Intent trackedintent = MeasurementService.trackedIntent(theintent, thecamref, task.getResult(), intentfactory);

                //try and open the new uri
                boolean intentloaded = MeasurementService.startActvity(thecontext, trackedintent);

                //if it fails, try open the given alternative url.
                if (!intentloaded) {

                    Uri deeplinkuri = (theintent.getData() == null) ? null : theintent.getData();

                    Uri trackeduri = MeasurementService.measurementServiceURI(thecamref, task.getResult(), theuri, deeplinkuri, isdebug);
                    Intent alternativeintent = intentfactory.getIntent(Intent.ACTION_VIEW, trackeduri);
                    alternativeintent.addCategory(Intent.CATEGORY_BROWSABLE);

                    startActvity(thecontext, alternativeintent);
                }

                return null;
            }

        });
    }

    protected void setCampaignID(String campaignID) {
        String trimmedcampaignid = campaignID.trim();

        this.campaignID = trimmedcampaignid;
        this.eventQueue.setCampaignID(campaignID);
    }

    protected void setAdvertiserID(String advertiserID) {
        this.advertiserID = advertiserID.trim();
    }

    protected String getAdvertiserID() {
        return advertiserID;
    }

    protected String getCampaignID() {
        return campaignID;
    }

    protected Reachability getReachability() {return this.reachability;}

    public void setCallback(MeasurementServiceCallback callback) { this.callback = callback;}

    @Override
    public void requestQueueDidCompleteRequest(TrackingRequestQueue queue, TrackingRequest request, String result) {
    }

    @Override
    public void requestQueueErrorOnRequest(TrackingRequestQueue queue, TrackingRequest request, Exception error) {
        MeasurementServiceLog.d("MeasurementService - Event request failed with error: " + error.toString());
    }

    @Override
    public void registerRequestQueueDidComplete(RegisterRequestQueue queue, RegisterRequest request, String result)
    {
        this.registerRequestQueueDidComplete(queue, request, result, new RegistrationProcessorFactory());
    }

    void registerRequestQueueDidComplete(RegisterRequestQueue queue, RegisterRequest request, String result,
                                         RegistrationProcessorFactory registerFactory) {

        if (this.status == MeasurementServiceStatus.QUERYING) {

            RegistrationProcessor registrationprocessor = registerFactory.getRequestProcessor(result);

            //clear camref
            if (request.getCamref() != null && request.getCamref().equals(this.storage.getCamRef())) {
                this.storage.clearCamref();
            }

            //clear referrer
            if (request.getReferrer() != null && request.getReferrer().equals(this.storage.getReferrer())) {
                this.storage.clearReferrer();
            }

            //if the registration has failed
            if (registrationprocessor.hasRegistrationFailed()) {
                //if there's already a tracking id, then just return to using it.
                if (this.storage.getTrackingID() != null) {
                    this.setStatus(MeasurementServiceStatus.ACTIVE);
                    this.eventQueue.setTrackingIDForIncompleteRequests(this.storage.getTrackingID());
                }
                else {
                    this.storage.putTrackingInactive();
                    this.setStatus(MeasurementServiceStatus.INACTIVE);
                    this.eventQueue.clearIncompleteRequests();
                }
            }
            else {
                this.setStatus(MeasurementServiceStatus.ACTIVE);
                this.storage.putTrackingID(registrationprocessor.getTrackingID());

                //call the callback for registration complete
                if (this.callback != null) {
                    this.callback.MeasurementServiceDidCompleteRegistration(this, registrationprocessor.getTrackingID());
                }

                //set the referrer
                if (registrationprocessor.getReferrer() != null) {
                    this.referrer = registrationprocessor.getReferrer();
                }

                //get the deep link
                if (registrationprocessor.getDeeplink() != null) {
                    this.deepLinkIntent =  new Intent(Intent.ACTION_VIEW, registrationprocessor.getDeeplink());

                    //callback & referrer.
                    if (this.callback != null) {
                        this.callback.MeasurementServiceWillOpenDeepLink(this, this.deepLinkIntent.getData());
                    }
                }
            }
        }
    }

    @Override
    public void registerRequestQueueDidError(RegisterRequestQueue queue, RegisterRequest request, Exception error) {
        MeasurementServiceLog.d("MeasurementService - Register queue failure." + error.toString());
    }

    /**
     * Return an intent that represents the attributed deep link. E.g. a uri that represents the content shown in the
     * advertisement that prompted the launch/install.
     *
     * Please note that at present the original action is not recorded.  A default action of ACTION_VIEW is added.
     * @return the intent that represents the deep link.
     */
    @Nullable
    public Intent getDeepLinkIntent() {
        return deepLinkIntent;
    }

    /**
     * Get the referrer uri.  This is the link that displayed the original advertisement.
     *
     * Please note that this uri is only captured in a web-app scenario at present.
     * @return the referrer uri
     */
    @Nullable
    public Uri getReferrer() {
        return referrer;
    }

    /**
     * Get the Performance Horizon mobile tracking id.  This is generated by the measurement kit API, and represents
     * the registration of this device against a provided ID.  This could be a cookie ID from the web, an AAID from the an app,
     * or a registration token passed through the referrer field in google play.
     * @return the tracking identifier for the last registration on this device
     */
    @Nullable
    public String getTrackingID() {
        //guard for pre-init.

        if (this.storage == null) {
            return null;
        }
        else {
            return this.storage.getTrackingID();
        }
    }

    /**
     * Get the configuration used by the measurement service.
     * @return the current configuration in use.
     */
    @NonNull
    public MeasurementServiceConfiguration getConfiguration() {
        return this.config;
    }

    /**
     * Get the current status of the measurement service.
     * @return the current status
     *
     * @see MeasurementService.MeasurementServiceStatus
     */
    @NonNull
    public MeasurementServiceStatus getStatus() {
        return this.status;
    }

    //This method is used for testing, to avoid having to initialization.
    void putStatus(MeasurementServiceStatus status) {
        this.status = status;
    }

    void putReachability(Reachability reachability) {
        this.reachability = reachability;
    }

    void putMeasurementStorage(MeasurementServiceStorage storage) {
        this.storage = storage;
    }

    void putEventQueue(EventRequestQueue eventQueue) {
        this.eventQueue = eventQueue;
    }

    /**
     * as the metric the measurement service uses to identify installs is an estimate, you
     * can also determine an install locally, and installs will be
     */
    public void setIsInstalled(boolean isInstalled) {
        this.isInstalled = true;
    }
}
