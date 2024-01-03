package com.lemma.lemmasignagesdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.LinearLayout;

import com.lemma.lemmasignagesdk.AdManagerCallback.AD_EVENT;
import com.lemma.lemmasignagesdk.api.LMAdLoopStatI;
import com.lemma.lemmasignagesdk.api.LMAdRequestI;
import com.lemma.lemmasignagesdk.api.LMConfigI;
import com.lemma.lemmasignagesdk.api.LMVideoAdManagerI;
import com.lemma.lemmasignagesdk.common.LMError;
import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.common.LMUtils;
import com.lemma.lemmasignagesdk.common.VAdParam;
import com.lemma.lemmasignagesdk.live.manager.AdQueueManager;
import com.lemma.lemmasignagesdk.live.manager.AdURLBuilder;
import com.lemma.lemmasignagesdk.live.manager.AdvertisingIdClient;
import com.lemma.lemmasignagesdk.live.manager.LMAdLoopStat;
import com.lemma.lemmasignagesdk.live.manager.LMAppInfo;
import com.lemma.lemmasignagesdk.live.manager.LMDeviceInfo;
import com.lemma.lemmasignagesdk.live.manager.LMLocationManager;
import com.lemma.lemmasignagesdk.live.manager.NetworkStatusMonitor;
import com.lemma.lemmasignagesdk.live.manager.PersistenceStore;
import com.lemma.lemmasignagesdk.model.VideoViewInfo;
import com.lemma.lemmasignagesdk.vast.VastBuilder.AdGroup;
import com.lemma.lemmasignagesdk.vast.VastBuilder.AdI;
import com.lemma.lemmasignagesdk.vast.VastBuilder.LinearAd;
import com.lemma.lemmasignagesdk.vast.VastBuilder.MediaFile;
import com.lemma.lemmasignagesdk.vast.VastBuilder.NonLinearAd;
import com.lemma.lemmasignagesdk.vast.VastBuilder.Resource;
import com.lemma.lemmasignagesdk.vast.VastBuilder.Tracker;
import com.lemma.lemmasignagesdk.vast.VastBuilder.Vast;
import com.lemma.lemmasignagesdk.vast.tracker.TrackerDBHandler;
import com.lemma.lemmasignagesdk.vast.tracker.TrackerHandler;
import com.lemma.lemmasignagesdk.views.AdGroupPlayerView;

import java.io.File;
import java.util.ArrayList;

/**
 * This class is the main class exposed for video ad serving. Publisher app
 * needs to create an instance of this class & communicate for video ad serving
 * life-cycle. This class will render the Linear/Non-Linear ads and
 * will fire the tracking URLs as well.
 */
public class LMVideoAdManager implements AdQueueManager.AdQueueManagerListener, LMVideoAdManagerI {

    private static final String TAG = "LMVideoAdManager";
    private AdGroupPlayerView mAdGroupPlayerView;
    private Context mContext = null;
    private AdManagerCallback mAdManagerListener = null;
    private VAdParam mVAdParam = null;
    private AD_STATE mCurrentAdState = AD_STATE.IDLE;
    private AdQueueManager adQueueManager;
    private MANAGER_STATE managerState = MANAGER_STATE.IDLE;
    private TrackerHandler trackerHandler;

    /***
     * @param context
     * @param aRequest
     * @param adManagerListener
     * @param config
     * @throws IllegalArgumentException
     */
    public LMVideoAdManager(Context context,
                            LMAdRequestI aRequest,
                            AdManagerCallback adManagerListener,
                            LMConfigI aConfig) throws IllegalArgumentException {

        LMConfig config = (LMConfig) aConfig;
        LMAdRequest request = (LMAdRequest) aRequest;
        if (context == null || adManagerListener == null) {
            throw new IllegalArgumentException("Argument of LMVideoAdManager constructor can not be null. These are mandatory parameters.");
        }

        LemmaSDK.instance.init(context);

        if (request.getAdUnitId() == null || request.getPublisherId() == null) {
            String msg = "Publisher Id and Ad unit id should be provided.";
            LMLog.e(TAG, msg);
            throw new IllegalArgumentException(msg);
        }

        Vast defaultVast = null;
        // Create default config if not provided
        if (config == null) {
            config = new LMConfig();
            config.deleteCacheContinuously = false;
            config.playLastSavedLoop = false;
        }

        if (config.playLastSavedLoop) {
            Vast vast = savedVast(context);
            if (vast != null) {
                if (areAdsAvailableOnDisk(vast)) {
                    defaultVast = vast;
                }
            }
        }

        if (defaultVast == null) {
            defaultVast = config.vast;
        }

        if (defaultVast == null) {
            defaultVast = vastForInfo(config);
        }

        String rootDirectory = null;
        if (rootDirectory == null) {
            rootDirectory = LMUtils.getLemmaRootDir();
        }

        WebView webView = new WebView(context);
        trackerHandler = new TrackerHandler(new NetworkStatusMonitor(context), webView);
        trackerHandler.executeImpressionInWebContainer = config.getExecuteImpressionInWebContainer();

        try {
            TrackerDBHandler handler = new TrackerDBHandler(context);
            trackerHandler.trackerDBHandler = handler;
        } catch (Exception e) {
            LMLog.e(TAG, "Unable to create tracker db handler");
        }

        if (request.getTimeZone() != null) {
            trackerHandler.timeZone = request.getTimeZone();
        }

        AdURLBuilder adURLBuilder = new AdURLBuilder(request);
        adURLBuilder.advertisingIdClient = new AdvertisingIdClient(context);
        adURLBuilder.displayMetrics = context.getResources().getDisplayMetrics();
        adURLBuilder.appInfo = new LMAppInfo(context.getApplicationContext());
        adURLBuilder.networkStatusMonitor = new NetworkStatusMonitor(context);
        adURLBuilder.deviceInfo = new LMDeviceInfo(context);
        adURLBuilder.locationManager = new LMLocationManager(context);

        adQueueManager = new AdQueueManager(defaultVast, rootDirectory, adURLBuilder);
        adQueueManager.monitor = new NetworkStatusMonitor(context);
        adQueueManager.persistenceStore = new PersistenceStore(context);
        adQueueManager.listener = this;
        adQueueManager.deleteCacheContinuously = config.deleteCacheContinuously;
        adQueueManager.setPrefetchNextLoop(true);
        adQueueManager.deviceInfo = new LMDeviceInfo(context);
        adQueueManager.setRetryWithRTB(true);
        adQueueManager.executeImpressionInWebContainer = config.getExecuteImpressionInWebContainer();

        mContext = context;
        mAdManagerListener = adManagerListener;
    }

    /**
     * Constructor will initialize the passback handler & will download the ad
     * serving config properties from the CDN.
     *
     * @param context
     * @param adManagerListener
     * @throws IllegalArgumentException
     */
    public LMVideoAdManager(Context context,
                            LMAdRequestI request,
                            AdManagerCallback adManagerListener) throws IllegalArgumentException {


        this(context, request, adManagerListener, null);
    }

    public LMVideoAdManager() {

    }

    @Override
    public void setupInstance(Context context, LMAdRequestI aRequest, LMConfigI config, AdManagerCallback adManagerListener) {
        setup(context, aRequest, adManagerListener, config);
    }

    private void setup(Context context,
                       LMAdRequestI aRequest,
                       AdManagerCallback adManagerListener,
                       LMConfigI aConfig) throws IllegalArgumentException {

        LMAdRequest request = (LMAdRequest) aRequest.getInnerIml();
        LMConfig config = null;
        if (aConfig != null) {
            config = (LMConfig) aConfig.getInnerIml();
        }

        if (context == null || adManagerListener == null) {
            throw new IllegalArgumentException("Argument of LMVideoAdManager constructor can not be null. These are mandatory parameters.");
        }

        LemmaSDK.instance.init(context);

        if (request.getAdUnitId() == null || request.getPublisherId() == null) {
            String msg = "Publisher Id and Ad unit id should be provided.";
            LMLog.e(TAG, msg);
            throw new IllegalArgumentException(msg);
        }

        Vast defaultVast = null;
        // Create default config if not provided
        if (config == null) {
            config = new LMConfig();
            config.deleteCacheContinuously = false;
            config.playLastSavedLoop = false;
        }

        if (config.playLastSavedLoop) {
            Vast vast = savedVast(context);
            if (vast != null) {
                if (areAdsAvailableOnDisk(vast)) {
                    defaultVast = vast;
                }
            }
        }

        if (defaultVast == null) {
            defaultVast = config.vast;
        }

        if (defaultVast == null) {
            defaultVast = vastForInfo(config);
        }

        String rootDirectory = null;
        if (rootDirectory == null) {
            rootDirectory = LMUtils.getLemmaRootDir();
        }

        WebView webView = new WebView(context.getApplicationContext());
        trackerHandler = new TrackerHandler(new NetworkStatusMonitor(context), webView);
        trackerHandler.executeImpressionInWebContainer = config.getExecuteImpressionInWebContainer();

        try {
            TrackerDBHandler handler = new TrackerDBHandler(context);
            trackerHandler.trackerDBHandler = handler;
        } catch (Exception e) {
            LMLog.e(TAG, "Unable to create tracker db handler");
        }

        if (request.getTimeZone() != null) {
            trackerHandler.timeZone = request.getTimeZone();
        }

        AdURLBuilder adURLBuilder = new AdURLBuilder(request);
        adURLBuilder.advertisingIdClient = new AdvertisingIdClient(context);
        adURLBuilder.displayMetrics = context.getResources().getDisplayMetrics();
        adURLBuilder.appInfo = new LMAppInfo(context.getApplicationContext());
        adURLBuilder.networkStatusMonitor = new NetworkStatusMonitor(context);
        adURLBuilder.deviceInfo = new LMDeviceInfo(context);
        adURLBuilder.locationManager = new LMLocationManager(context);

        adQueueManager = new AdQueueManager(defaultVast, rootDirectory, adURLBuilder);
        adQueueManager.monitor = new NetworkStatusMonitor(context);
        adQueueManager.persistenceStore = new PersistenceStore(context);
        adQueueManager.listener = this;
        adQueueManager.deleteCacheContinuously = config.deleteCacheContinuously;
        adQueueManager.setPrefetchNextLoop(true);
        adQueueManager.deviceInfo = new LMDeviceInfo(context);
        adQueueManager.setRetryWithRTB(true);
        adQueueManager.executeImpressionInWebContainer = config.getExecuteImpressionInWebContainer();

        mContext = context;
        mAdManagerListener = adManagerListener;
    }

    private Vast vastForInfo(LMConfig info) {

        try {

            ArrayList<AdI> linearAds = new ArrayList<>();

            if (info.uri != null) {

                LinearAd ad = new LinearAd();
                ad.id = "1";
                ad.sequence = 1;
                ad.setDurationString(info.duration);


                ArrayList<MediaFile> mediaFiles = new ArrayList<>();

                MediaFile mf = new MediaFile();
                mf.setUrl(info.uri.toString());

                mf.setWidth("320");
                mf.setHeight("480");
                mf.setType("video/mp4");

                mediaFiles.add(mf);

                ad.setMediaFiles(mediaFiles);
                linearAds.add(ad);

            } else if (info.imageUri != null) {
                NonLinearAd ad = new NonLinearAd();
                ad.id = "1";
                ad.sequence = 1;

                Resource r = new Resource();
                r.setType("StaticResource");
                r.setCreativeType("image/png");
                r.setValue(info.imageUri.toString());
                r.setWidth("320");
                r.setHeight("480");
                ArrayList<Resource> resources = new ArrayList<>();
                resources.add(r);
                ad.setResources(resources);

                linearAds.add(ad);
            }

            if (linearAds.size() > 0) {
                Vast vast = new Vast();
                vast.ads = linearAds;
                return vast;
            }
            return null;

        } catch (Exception i) {
            LMLog.i(TAG, i.getLocalizedMessage());
        }
        return null;
    }

    public LMAdLoopStatI getCurrentAdLoopStat() {
        return (adQueueManager != null) ? adQueueManager.getCurrentAdQueueStat() : new LMAdLoopStat();
    }

    public void pauseLoop() {
        managerState = MANAGER_STATE.PAUSED;
    }

    public void resumeLoop() {
        if (managerState == MANAGER_STATE.PLAYING) {
            return;
        }
        managerState = MANAGER_STATE.PLAYING;
        if (mAdManagerListener != null) {
            mAdManagerListener.onAdEvent(AD_EVENT.AD_RESUMED);
        }
        loadNextAd();
    }

    private Vast savedVast(Context context) {
        PersistenceStore persistenceStore = new PersistenceStore(context);
        Vast vast = persistenceStore.retrieve();
        if (vast != null && vast.ads.size() > 0) {
            LMLog.i(TAG, "Found persisted Vast -> " + vast);
            return vast;
        }
        return null;
    }

    private boolean areAdsAvailableOnDisk(Vast vast) {
        boolean areAdsAvailableOnDisk = true;
        for (AdI ad : vast.ads) {
            File file = new File(ad.getAdRL());
            if (ad.isUrl() && !file.exists()) {
                areAdsAvailableOnDisk = false;
                break;
            }
        }
        return areAdsAvailableOnDisk;
    }

    private void retry() {

        final long period = 800;
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                loadNextAd();
            }
        }, period);
    }

    private void loadNextAd() {
        if (managerState == MANAGER_STATE.PAUSED) {
            // Wait until resumed again
            if (mAdManagerListener != null) {
                mAdManagerListener.onAdEvent(AD_EVENT.AD_PAUSED);
            }
            return;
        }
        if (adQueueManager == null) {
            return;
        }
        adQueueManager.nextAdGroup(new AdQueueManager.AdQueueManagerQueueListener() {

            @Override
            public void onQueueError(final Error error) {
                retry();
            }

            @Override
            public void onQueueReady(AdGroup adGroup) {
                if (adGroup == null) {
                    if (mAdManagerListener != null) {
                        mAdManagerListener.onAdEvent(AD_EVENT.AD_LOOP_COMPLETED);
                    }
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            loadNextAd();
                        }
                    });

                } else if (adGroup.ads.size() == 0) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            loadNextAd();
                        }
                    });
                } else {

                    if (managerState == MANAGER_STATE.PAUSED) {
                        // Wait until resumed again\
                        return;
                    }
                    ViewGroup videoAdUiContainer = mVAdParam.linearViewInfo.getVideoAdUiContainer();
                    AdGroupPlayerView oldAdGroupV = mAdGroupPlayerView;

                    mAdGroupPlayerView = new AdGroupPlayerView(mContext, adGroup);
                    mAdGroupPlayerView.setPlayerViewListener(new AdGroupPlayerView.AdGroupPlayerViewListener() {
                        @Override
                        public void onSuccessLayout() {

                        }

                        @Override
                        public void onFailureLayout(String reason) {

                        }


                        @Override
                        public void endPlayingAdGroup(AdGroup adGroup) {
                            if (adQueueManager.getCurrentAdQueueStat().isLoopEmpty()) {
                                if (mAdManagerListener != null) {
                                    LMLog.i(TAG, "AD_LOOP_COMPLETED");
                                    mAdManagerListener.onAdEvent(AdManagerCallback.AD_EVENT.AD_LOOP_COMPLETED);
                                }
                            }
                            loadNextAd();
                        }

                        @Override
                        public void endPlayingAd(AdGroup adGroup, AdI ad) {
                            trackEventImpressions(ad, "complete", adGroup.isRTB);
                            if (mAdManagerListener != null) {
                                mAdManagerListener.onAdEvent(AD_EVENT.AD_COMPLETED);
                            }

                        }

                        @Override
                        public void startedPlayingAd(AdGroup adGroup, AdI ad) {
                            trackImpressions(ad, adGroup.isRTB);
                            trackEventImpressions(ad, "start", adGroup.isRTB);
                            if (mAdManagerListener != null) {
                                mAdManagerListener.onAdEvent(AD_EVENT.AD_STARTED);
                            }
                        }

                        @Override
                        public void onFirstQuartileReached(AdGroup adGroup, AdI ad) {
                            trackEventImpressions(ad, "firstQuartile", adGroup.isRTB);
                            if (mAdManagerListener != null) {
                                mAdManagerListener.onAdEvent(AD_EVENT.AD_FIRST_QUARTILE);
                            }
                        }

                        @Override
                        public void onMidPointReached(AdGroup adGroup, AdI ad) {
                            trackEventImpressions(ad, "midpoint", adGroup.isRTB);
                            if (mAdManagerListener != null) {
                                mAdManagerListener.onAdEvent(AD_EVENT.AD_MID_POINT);
                            }
                        }

                        @Override
                        public void onThirdQuartileReached(AdGroup adGroup, AdI ad) {
                            trackEventImpressions(ad, "thirdQuartile", adGroup.isRTB);
                            if (mAdManagerListener != null) {
                                mAdManagerListener.onAdEvent(AD_EVENT.AD_THIRD_QUARTILE);
                            }
                        }
                    });

                    mAdGroupPlayerView.prepareLayoutForAdGroup(adGroup);

                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
                    videoAdUiContainer.addView(mAdGroupPlayerView, params);

                    mCurrentAdState = AD_STATE.LOADED;
                    if (mAdManagerListener != null) {
                        mAdManagerListener.onAdEvent(AD_EVENT.AD_LOADED);
                    }

                    if (oldAdGroupV != null) {
                        videoAdUiContainer.removeView(oldAdGroupV);
                        oldAdGroupV.reset();
                    }

                    mAdGroupPlayerView.prepareLayoutForAdGroup(adGroup);
                }
            }
        });

    }

    private void trackImpressions(AdI ad, boolean isRTB) {
        ArrayList<Tracker> impressionList = ad.getAdTrackers();
        sendImpression(impressionList, isRTB);
    }

    private void trackEventImpressions(AdI ad, String event, boolean isRTB) {
        ArrayList<Tracker> eventTrackers = ad.getEventTrackers(event);
        LMLog.d("LMVideoAdManager", "Scheduling tracker execution " + event + " for event : " + eventTrackers);
        sendImpression(eventTrackers, isRTB);
    }

    private void sendImpression(ArrayList<Tracker> trackers, boolean isRTB) {
        if (isRTB) {
            trackerHandler.sendRTBImpression(trackers);
        } else if (mAdManagerListener != null) {
            if (mAdManagerListener.shouldFireImpressions()) {
                trackerHandler.sendImpression(trackers);
            } else {
                LMLog.d("LMVideoAdManager", "App restricted impression tracking, is display off/not connected ?");
            }
        }
    }

    /**
     * Publisher calls this method once ad is loaded & informed via
     * adReceived(LMVideoAdManager adManager). Publisher creates the container & ads
     * them in VAdParam object and pass it to sdk via the argument of this method.
     *
     * @throws IllegalArgumentException
     */
    public void init(ViewGroup view) throws IllegalArgumentException {
        try {

            VAdParam param = new VAdParam();
            param.linearViewInfo = new VideoViewInfo();
            param.linearViewInfo.setPlayerAdapter(null);
            param.linearViewInfo.setVideoAdUiContainer(view);
            mVAdParam = param;
            loadNextAd();
        } catch (Exception e) {
            LMLog.e(TAG, "Exception occured in init(). exception = " + e);
            sendErrorTracking(LMError.VAST_UNDEFINED_ERROR);
            if (mAdManagerListener != null) {
                mAdManagerListener.onAdError(this, new Error("VAST_UNDEFINED_ERROR"));
                mAdManagerListener.onAdEvent(AD_EVENT.AD_LOOP_COMPLETED);
            }
        }
    }

    /**
     * Publisher should call this method to start/play the ad.
     */
    public void startAd() {
        //Render views
        mCurrentAdState = AD_STATE.STARTED;
        managerState = MANAGER_STATE.PLAYING;
        //Play ad
        startPlaying();
    }

    /**
     * It will destroy the current ad if any in execution or pending state.
     */
    public void destroy() {
        pauseLoop();
        adQueueManager.destroy();
        mAdManagerListener = null;

        ViewGroup vg = mVAdParam.linearViewInfo.getVideoAdUiContainer();
        if (vg != null) {
            vg.removeAllViews();
        }
        if (mAdGroupPlayerView != null) {
            mAdGroupPlayerView.reset();
        }
    }

    /**
     * Video ad serving passes thru the multiple states of ad life cycle. It
     * returns the current state of the ad.
     *
     * @return current ad state.
     */
    public AD_STATE getCurrentAdState() {
        return mCurrentAdState;
    }

    /**
     * It will actually start playing linear/non-linear creative.
     */
    private void startPlaying() {
        if (mAdGroupPlayerView != null) {
            mAdGroupPlayerView.play();
        } else {
            LMLog.e(TAG, "mAdGroupPlayerView  is null");
        }
    }

    /**
     * It will send the error tracking url for given error code. It will send
     * the tracking url for inline, all wrapper VAST as well.
     *
     * @param errorCode
     */
    private void sendErrorTracking(final int errorCode) {
        // TODO: Add support
    }

    @Override
    public void onLoopStart() {
        LMLog.i(TAG, "Sending queued impressions");
        trackerHandler.sendImpressionFromQueue();
    }

    public void setPlayerVisibility(int visibility) {
        if (mAdGroupPlayerView != null) {
            mAdGroupPlayerView.setVisibility(visibility);
        }
    }

    public enum AD_STATE {
        IDLE, INIT, REQUESTED, RECEIVED, LOADED, STARTED, PAUSED, RESUME,
        MUTE, UNMUTE, SKIP, FIRST_QUARTILE, MID_POINT, THIRD_QUARTILE,
        COMPLETED, ERROR, AD_LOOP_COMPLETED
    }

    public enum MANAGER_STATE {
        IDLE, PLAYING, PAUSED
    }

}
