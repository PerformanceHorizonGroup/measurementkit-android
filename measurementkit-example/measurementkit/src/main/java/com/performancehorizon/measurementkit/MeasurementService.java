package com.performancehorizon.measurementkit;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import com.squareup.okhttp.OkHttpClient;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.ref.WeakReference;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * MeasurementService - interface to Performance Horizon's mobile tracking API.
 *
 * @see {@link Event}
 * @see {@link Sale}
 */
public class MeasurementService implements TrackingRequestQueueDelegate {

    //singleton
    private static MeasurementService _sharedTrackingService;

    //callback
    private MeasurementServiceCallback callback;

    //setup variables.
    private String campaignID;
    private String clickRef;

    private String phgAdvertiserID;
    @Nullable String idfa;

    private Map<String, Object> activeFingerprint;

    //dependencies.
    private TrackingRequestQueue eventQueue;
    private TrackingRequestQueue clickRefQueue;
    private Reachability reachability;
    private WeakReference<Context> context;
    private TrackingRequestFactory factory;
    private TrackingURLHelper urlHelper;
    private FingerprinterFactory fingerprinterFactory;

    //fingerprint dependencies.
    private ActiveFingerprinter activeFingerprinter;
    private Fingerprinter fingerprinter;

    private boolean trackingActive = true;
    private boolean setupComplete = false;
    private String referrer;


    private boolean generateActiveFingerprint = false;

    private Intent filteredintent;

    protected class TrackingConstants
    {
        protected final static String TRACKING_PREF = "com.performancehorizon.phgmt";

        protected final static String TRACKING_PREF_ID = "com.performancehorizon.phgmt.id";
        protected final static String TRACKING_PREF_ACTIVE = "com.performancehorizon.com.phgmt.isactive";
        protected final static String TRACKING_PREF_SETUP_COMPLETE = "com.performancehorizon.com.phgmt.setupcomplete";
        protected final static String TRACKING_PREF_REFERRER = "com.performancehorizon.com.phgmt.referrrer";

        protected final static String TRACKING_LOG = "phgmt";

        protected final static String TRACKING_ID_KEY = "mobiletracking_id";
        protected final static String META_KEY = "meta";
        protected final static String DEEPLINK_KEY = "deep_link";
        protected final static String DEEPLINK_ACTION_KEY = "deeplink_action";
    }


    /**
     * returns a shared instance of the measurement service.
     * @return the shared instance of measurement service.
     */
    public static MeasurementService sharedInstance()
    {
        if (_sharedTrackingService == null) {
            _sharedTrackingService = new MeasurementService();
        }

        return _sharedTrackingService;
    }

    public void fakeInstallBroadcast(Context currentcontext, String referrer)
    {
        Intent googleplayinstall = new Intent("com.android.vending.INSTALL_REFERRER");
        googleplayinstall.putExtra("referrer", referrer);

        LocalBroadcastManager.getInstance(currentcontext).sendBroadcast(googleplayinstall);
    }

    public void clearTracking(Context currentcontext)
    {
        SharedPreferences preferences = currentcontext.getSharedPreferences(TrackingConstants.TRACKING_PREF, Context.MODE_PRIVATE);

        preferences.edit().clear().commit();
    }

    protected static void setTrackingInstance(MeasurementService service)
    {
        _sharedTrackingService = service;
    }

    public MeasurementService() {
        this(new TrackingRequestQueue(new OkHttpClient()),
                new TrackingRequestQueue(new OkHttpClient()), new TrackingRequestFactory(),
                new FingerprinterFactory()
        );
    }

    public MeasurementService(TrackingRequestQueue setupQueue,
                              TrackingRequestQueue eventQueue,
                              TrackingRequestFactory factory,
                              FingerprinterFactory fingerprintFactory) {
        this.clickRefQueue = setupQueue;
        this.clickRefQueue.setDelegate(this);

        this.eventQueue = eventQueue;
        this.eventQueue.setDelegate(this);

        this.factory = factory;
        this.fingerprinterFactory = fingerprintFactory;
        this.urlHelper = new TrackingURLHelper();
    }

    public MeasurementService(TrackingRequestQueue setupQueue,
                              TrackingRequestQueue eventQueue,
                              TrackingRequestFactory factory) {

        this(setupQueue, eventQueue, factory, new FingerprinterFactory());
    }

    protected Boolean isValid()
    {
        return (this.getClickRef() != null);
    }

    public void initialise(String advertiserID, String campaignID)
    {
        this.initialise(null, null, advertiserID, campaignID);
    }

    private Map<String, Object> getTrackingDetails()
    {
        HashMap<String, Object> tracking = new HashMap<String, Object>();

        tracking.put("advertiser_id", this.getAdvertiserID());
        tracking.put("campaign_id", this.getCampaignID());

        tracking.put("fingerprint", this.fingerprinter.generateFingerprint());

        if (this.getIdfa() != null) {
            tracking.put("idfa", this.getIdfa());
        }

        if (this.referrer != null) {
            tracking.put("referrer", this.referrer);
        }

        if (this.willGenerateActiveFingerprint()) {
            tracking.put("active_fingerprint", this.activeFingerprint);
        }

        return tracking;
    }

    protected boolean readyToRegister()
    {
        return (this.isTrackingActive() &&                      //active
                (this.clickRef == null) &&                      //no clickref.
                (!this.clickRefQueue.isRequestActive()) &&      //no active request.
                (this.getAdvertiserID() != null) &&
                (this.campaignID != null) && //must have campaign and advertiser ids.
                (!this.willGenerateActiveFingerprint() || this.activeFingerprint != null) //either don't wait for a fingerprint, or have one.
        );

    }

    protected void register()
    {
        Map<String, Object> tracking = this.getTrackingDetails();

        JSONObject jsonobject  = new JSONObject(tracking);

        TrackingRequest request = factory.getRequest(this.urlHelper.urlStringForTracking() + "/register", jsonobject);

        this.clickRefQueue.enqueueRequest(request);
    }

    private Map<String, Object> getEventTrackingDetails(Event event)
    {
        if (this.getClickRef() != null && this.getCampaignID() != null && this.getAdvertiserID() != null) {
            Map<String, Object> eventdetails = new HashMap<>();

            eventdetails.put("advertiser_id", this.getAdvertiserID());
            eventdetails.put("campaign_id", this.getCampaignID());
            eventdetails.put("mobiletracking_id", this.getClickRef());

            if (event.getEventTag() != null) {
                eventdetails.put("event_category", event.getEventTag());
            }

            eventdetails.put(TrackingConstants.META_KEY, event.getEventData());

            //optional sales.
            Map<String, Object> sales = event.getSalesData();

            eventdetails.putAll(sales);

            return eventdetails;
        }
        else {
            return null;
        }
    }

    public void loadPreferences(SharedPreferences preferences)
    {
        this.setClickRef(preferences.getString(TrackingConstants.TRACKING_PREF_ID, null));
        this.trackingActive = preferences.getBoolean(TrackingConstants.TRACKING_PREF_ACTIVE, true);
        this.setupComplete = preferences.getBoolean(TrackingConstants.TRACKING_PREF_SETUP_COMPLETE, false);
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
    public void initialise(Context context, Intent intent, String advertiserID, String campaignID)
    {
        this.context = new WeakReference<>(context);
        this.setAdvertiserID(advertiserID);
        this.setCampaignID(campaignID);
        this.fingerprinter = new Fingerprinter(context);

        if (this.context != null && this.context.get() != null) {

            this.loadPreferences(context.getApplicationContext().getSharedPreferences(TrackingConstants.TRACKING_PREF, Context.MODE_PRIVATE));

            if (!this.setupComplete && ReferrerTracker.getReferrer() != null) {
                this.referrer = ReferrerTracker.getReferrer();
            }

            ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            this.reachability = new Reachability(connectivityManager, new ReachabilityCallback() {
                @Override
                public void onNetworkActive() {
                    MeasurementService.this.eventQueue.setQueueIsPaused(false);
                    MeasurementService.this.clickRefQueue.setQueueIsPaused(false);
                }
            });

            if (this.willGenerateActiveFingerprint()) {
                this.activeFingerprinter = fingerprinterFactory.getActiveFingerprinter(context, new ActiveFingerprinter.MobileTrackingActiveFingerprinterCallback() {
                    @Override
                    public void activeFingerprintComplete(ActiveFingerprinter fingerprinter, Map<String, Object> fingerprint) {

                        MeasurementService.this.activeFingerprint = fingerprint;

                        if (MeasurementService.this.readyToRegister()) {
                            MeasurementService.this.register();
                        }
                    }
                });

                this.activeFingerprinter.generateFingerprint();
            }
        }

        this.processDeepLink(intent);

        if (this.isTrackingActive() && !this.setupComplete && this.readyToRegister()) {
            this.register();
        }
    }


    /**
     * Track an event.  Events are registered as a conversion in the Performance horizon's affliate tracking interface.
     * Events will be uploaded asynchronously in the background.
     * @param event the event to track
     */
    public void trackEvent(Event event)
    {
        Map<String, Object> eventdetail = this.getEventTrackingDetails(event);

        if (eventdetail != null) {

            JSONObject jsonobject  = new JSONObject(eventdetail);

            TrackingRequest eventrequest = factory.getRequest(this.urlHelper.urlStringForTracking() + "/event", jsonobject);
            this.eventQueue.enqueueRequest(eventrequest);
        }

        //prompt to check connectivity status and restart queues.
        if (this.reachability != null && this.reachability.isNetworkActive()) {
            this.eventQueue.setQueueIsPaused(false);
            this.clickRefQueue.setQueueIsPaused(false);
        }
    }

    private Intent processDeepLink(Intent intent) {

        if (intent.getAction() == Intent.ACTION_VIEW) {

            Pattern trackingregex= Pattern.compile("/mobiletrackingid:(\\w*)");
            String path = intent.getData().getPath();
            Matcher regexfinder = trackingregex.matcher(path);

            if (regexfinder.find()) {
                String clickref = regexfinder.group(1);

                //setup preferences for future use.
                SharedPreferences prefs = this.context.get().getSharedPreferences(TrackingConstants.TRACKING_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor prefeditor = prefs.edit();

                prefeditor.putString(TrackingConstants.TRACKING_PREF_ID, (String) clickref);
                prefeditor.putBoolean(TrackingConstants.TRACKING_PREF_SETUP_COMPLETE, true);
                prefeditor.apply();

                this.clickRef = clickref;
                this.setupComplete = true;

                //filter intent to in case user wants to retrieve it without intent.

                Intent filteredintent =(Intent) intent.clone();
                String filteredpath = path.replaceFirst("/mobiletrackingid:\\w*", "");
                Uri filtereddata = intent.getData().buildUpon().path(filteredpath).build();

                return filteredintent;
            }
        }

        return null;
    }


    @Override
    public void requestQueueDidCompleteRequest(TrackingRequestQueue queue, TrackingRequest request, String result) {

        if (queue.equals(this.clickRefQueue) && this.context != null && this.context.get() != null) {
            try {
                JSONObject jsonresult = new JSONObject(result);

                //store clickref
                Object clickref = jsonresult.get(TrackingConstants.TRACKING_ID_KEY);

                SharedPreferences prefs = this.context.get().getSharedPreferences(TrackingConstants.TRACKING_PREF, Context.MODE_PRIVATE);
                SharedPreferences.Editor prefeditor = prefs.edit();

                if ((clickref instanceof String)) { //valid clickref
                    prefeditor.putString(TrackingConstants.TRACKING_PREF_ID, (String) clickref);

                    this.clickRef = (String) clickref;
                } else if ((clickref instanceof Boolean)) { //'inactive' response.
                    prefeditor.putBoolean(TrackingConstants.TRACKING_PREF_ACTIVE, ((Boolean) clickref).booleanValue());
                }

                //FIXME: serves no purpose at present.
                prefeditor.putBoolean(TrackingConstants.TRACKING_PREF_SETUP_COMPLETE, true);

                prefeditor.apply();

                //retrieve deeplink
                String deeplink = jsonresult.optString(TrackingConstants.DEEPLINK_KEY, null);

                try {
                    if (deeplink != null) {

                        if (this.callback != null) {
                            this.callback.MeasurementServiceDidRegisterDidRetrieveDeepLink(this, deeplink);
                        }
                        else {
                            String action = URLDecoder.decode(jsonresult.optString(TrackingConstants.DEEPLINK_ACTION_KEY, Intent.ACTION_VIEW), "UTF-8").trim();
                            Intent deeplinkintent = new Intent(action, Uri.parse(URLDecoder.decode(deeplink, "UTF-8")));
                            deeplinkintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                            this.context.get().startActivity(deeplinkintent);
                        }
                    }
                } catch (UnsupportedEncodingException exception) {
                    Log.d(TrackingConstants.TRACKING_LOG, "Unsupported encoding in deeplink url.");
                }
            } catch (JSONException exception) {
                Log.d(TrackingConstants.TRACKING_LOG, "Invalid JSON in click request.");
            }
        }
    }

    @Override
    public void requestQueueErrorOnRequest(TrackingRequestQueue queue, TrackingRequest request, Exception error) {
        Log.d(TrackingConstants.TRACKING_LOG, "Mobile Tracking Request has failed.");
    }

    protected void setCampaignID(String campaignID) {
        this.campaignID = campaignID.trim();
    }

    protected void setAdvertiserID(String advertiserID) {
        this.phgAdvertiserID = advertiserID.trim();
    }

    protected void setClickRef(String clickRef) {
        this.clickRef = clickRef;
    }

    protected String getAdvertiserID() {
        return phgAdvertiserID;
    }

    protected String getCampaignID() {
        return campaignID;
    }

    protected String getClickRef() {
        return clickRef;
    }


    protected Reachability getReachability() {return this.reachability;}

    protected boolean isTrackingActive() {
        return trackingActive;
    }


    public String getIdfa() {
        return idfa;
    }

    public void setIdfa(String idfa) {
        this.idfa = idfa;
    }

    public boolean willGenerateActiveFingerprint() {
        return generateActiveFingerprint;
    }

    public void setGenerateActiveFingerprint(boolean generateActiveFingerprint) {
        this.generateActiveFingerprint = generateActiveFingerprint;
    }

    public void setDebug(boolean debug)
    {
        this.urlHelper.setDebug(debug);
    }

    public void setCallback(MeasurementServiceCallback callback) { this.callback = callback;}

}
