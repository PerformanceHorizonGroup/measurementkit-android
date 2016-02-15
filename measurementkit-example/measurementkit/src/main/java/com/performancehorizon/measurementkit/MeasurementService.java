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
import java.net.URL;
import java.util.concurrent.Callable;


import okhttp3.OkHttpClient;

/**
 * MeasurementService - interface to Performance Horizon's mobile tracking API.
 *
 * @see {@link Event}
 * @see {@link Sale}
 */
public class MeasurementService implements TrackingRequestQueueDelegate, RegisterRequestQueueDelegate {

    static class MeasurementStorageFactory
    {
        public MeasurementServiceStorage getMeasurementStorage(Context context) {
            return new MeasurementServiceStorage(context);
        }
    }

    static class ReachabilityFactory
    {
        public Reachability getReachability(ConnectivityManager manager, ReachabilityCallback callback) {
            return new Reachability(manager, callback);
        }
    }

    static class RegisterRequestFactory
    {
        public RegisterRequest getRegisterRequest(Context context, boolean doNotTrackAAID) {
            return new RegisterRequest(context, doNotTrackAAID);
        }
    }

    static class EventRequestFactory
    {
        public EventRequest getEventRequest(Event event, String trackingid) {
            return new EventRequest(event, trackingid);
        }

        public EventRequest getEventRequest(Event event) {
            return new EventRequest(event);
        }
    }

    static class IntentProcessorFactory
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

    static class UriBuilderFactory
    {
        public MeasurementServiceURIBuilder getTrackingUriBuilder(TrackingURLHelper trackinghelper) {
            return new MeasurementServiceURIBuilder(trackinghelper);
        }
    }

    static class IntentFactory
    {
        public Intent getIntent(Intent intent) {
            return (Intent)intent.clone();
        }

        public Intent getIntent(String action, Uri data) {
            return new Intent(action, data);
        }
    }

    static class RegistrationProcessorFactory
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

    protected class TrackingConstants
    {
        protected final static String TRACKING_LOG = "phgmt";
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
     * returns a shared singleton instance of the measurement service with the given configuratedion @link{MeasurementServiceConfig}.
     * @warning The configuration will be ignored if a shared instance has already been generated.
     * @return
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
                new RegisterRequestQueue(new TrackingRequestQueue(new OkHttpClient()), new TrackingRequestFactory(), new TrackingURLHelper(config.getDebugModeActive())),
                new EventRequestQueue(new TrackingRequestQueue(new OkHttpClient()), new TrackingRequestFactory(), new TrackingURLHelper(config.getDebugModeActive())),
                new FingerprinterFactory()
        );
    }

    public MeasurementService(MeasurementServiceConfiguration config,
                              RegisterRequestQueue registerQueue,
                              EventRequestQueue eventQueue,
                              FingerprinterFactory fingerprintFactory

    ) {

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

    public void fakeInstallBroadcast(Context currentcontext, String referrer)
    {
        Intent googleplayinstall = new Intent("com.android.vending.INSTALL_REFERRER");
        googleplayinstall.putExtra("referrer", referrer);

        LocalBroadcastManager.getInstance(currentcontext).sendBroadcast(googleplayinstall);
    }

    public void clearTracking(Context currentcontext) {
        MeasurementServiceStorage.clearPreferences(currentcontext);
    }

    protected static void setTrackingInstance(MeasurementService service)
    {
        _sharedTrackingService = service;
    }

    private void register(RegisterRequestFactory registerRequestFactory) {

        final RegisterRequestFactory therequestfactory = registerRequestFactory;

        Task.callInBackground(new Callable<RegisterRequest>() {
            @Override
            public RegisterRequest call() throws Exception {
                return therequestfactory.getRegisterRequest(MeasurementService.this.context.get(), MeasurementService.this.config.getDoNoTrackAAID());
                }
            }).continueWith(new Continuation<RegisterRequest, Void>() {
            @Override
            public Void then(Task<RegisterRequest> task) throws Exception {
                RegisterRequest registerrequest = task.getResult();

                registerrequest.setCampaignID(MeasurementService.this.campaignID);

                registerrequest.setAdvertiserID(MeasurementService.this.advertiserID);
                registerrequest.setFingerprint(MeasurementService.this.fingerprinterfactory.getFingerprinter(MeasurementService.this.context.get()).generateFingerprint());

                if (MeasurementService.this.storage.getCamRef() != null) {
                    registerrequest.setCamref(MeasurementService.this.storage.getCamRef());
                } else if (MeasurementService.this.storage.getReferrer() != null) {
                    registerrequest.setReferrer(MeasurementService.this.storage.getReferrer());
                }

                MeasurementService.this.registerQueue.addRegisterRequest(registerrequest);

                return null;
            }
        });
        }


    public void initialise(@NonNull String advertiserID, @NonNull String campaignID) {
        this.initialise(null, null, advertiserID, campaignID);
    }

    /**
     *
     * Initialises the measurement service.  Requires activity {@link Context}, the {@link Intent} that launched the activity,
     * the advertisers advertiserID, and the campaignID you wish conversions to be attributed to.
     *
     * @param context
     * @param intent
     * @param advertiserID
     * @param campaignID
     */
    public void initialise(Context context, Intent intent, @NonNull String advertiserID,
                           @NonNull String campaignID) {
        this.initialise(context, intent, advertiserID, campaignID,
                new MeasurementStorageFactory(),
                new ReachabilityFactory(),
                new IntentProcessorFactory(),
                new RegisterRequestFactory());
    }


    protected void initialise(Context context, Intent intent, @NonNull String advertiserID, @NonNull String campaignID,
                              @NonNull MeasurementStorageFactory storageFactory,
                              @NonNull ReachabilityFactory reachabilityFactory,
                              @NonNull IntentProcessorFactory processorFactory,
                              @NonNull RegisterRequestFactory registerRequestFactory)
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
        if (ReferrerTracker.getReferrer() != null) {
            this.storage.putReferrerQuery(ReferrerTracker.getReferrer());
        }

        //and the opening intent
        this.deepLinkIntent = this.processDeepLink(intent, processorFactory);

        //set the initial status (also configures the queue states)
        this.setStatus(this.storage.status());

        //if a new query is needed, send off request.
        if (this.status == MeasurementServiceStatus.QUERYING) {
            this.register(registerRequestFactory);
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
     * @param intent
     * @return
     */
    public static void addCamrefToIntent(Intent intent, String camref) {
        intent.putExtra(TrackingConstants.TRACKING_INTENT_CAMREF, camref);
    }

    public static Uri measurementServiceURI(String camref, Uri destinationuri, Uri deeplink) {
        return MeasurementService.measurementServiceURI(camref,null, destinationuri, deeplink, false);
    }

    public static Uri measurementServiceURI(String camref,@Nullable String advertisingid, Uri destinationuri, Uri deeplink) {
        return MeasurementService.measurementServiceURI(camref, advertisingid, destinationuri, deeplink, false);
    }

    public static Uri measurementServiceURI(String camref,@Nullable String advertisingid, Uri destinationuri, Uri deeplink, boolean isDebug) {
        return MeasurementService.measurementServiceURI(camref, advertisingid, destinationuri, deeplink, isDebug, new UriBuilderFactory());
    }

    public static Uri measurementServiceURI(@NonNull String camref,@Nullable String advertisingid,
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

        if (deeplink != null && AppStoreUriSpotter.isAppStoreURI(destinationuri)) {
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

    public static void openIntentWithAlternativeURI(Context context,Intent intent,String camref, Uri uri) {
        openIntentWithAlternativeURI(context, intent, camref, uri, new IntentFactory());
    }

    static void openIntentWithAlternativeURI(Context context,Intent intent,String camref, Uri uri, IntentFactory factory) {
        //gonna be using in the inner classes, so need to be final.
        final Context thecontext = context;
        final String thecamref= camref;
        final Intent theintent = intent;
        final Uri theuri = uri;

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
                    ServiceLog.debug("Retrieval of advertising ID failed with error:  " +  advertisingidfail.toString());

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

                    Uri trackeduri = MeasurementService.measurementServiceURI(thecamref, task.getResult(), theuri, deeplinkuri);
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
        ServiceLog.debug("Event request completed.");
    }

    @Override
    public void requestQueueErrorOnRequest(TrackingRequestQueue queue, TrackingRequest request, Exception error) {
        ServiceLog.debug("Event request failed with error: " + error.toString());
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
        ServiceLog.debug("Register queue failure. " + error.toString());
    }

    /**
     * Return the intent that
     * @return
     */
    @Nullable
    public Intent getDeepLinkIntent() {
        return deepLinkIntent;
    }

    @Nullable
    public Uri getReferrer() {
        return referrer;
    }

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

    @NonNull
    public MeasurementServiceConfiguration getConfiguration() {
        return this.config;
    }

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
}
