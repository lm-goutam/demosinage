package com.lemma.lemmasignagesdk.scedule.scheduleplayer.group;

import android.content.Context;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.mediaplayer.HtmlPlayer;
import com.lemma.lemmasignagesdk.mediaplayer.ImagePlayer;
import com.lemma.lemmasignagesdk.mediaplayer.VideoPlayer;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.AdTrackerHandler;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.LMAdPlayerView;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.Utils;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAd;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAdGrp;
import com.lemma.lemmasignagesdk.vast.VastBuilder.AdI;

import java.util.ArrayList;
import java.util.Calendar;

public class ScheduleAdGroupPlayerView extends LinearLayout {

    private final ArrayList<LMAdPlayerView> players;
    private final ArrayList<LMAdPlayerView> preparationPlayersQueue = new ArrayList<>();
    private final ArrayList<ScheduleAdGrp> adQueue;
    private ScheduleAdGrp adGroup;
    private Integer completionCounter;
    private AdTrackerHandler trackerHandler;
    private ScheduleAdGroupPlayerView.AdGroupPlayerViewListener playerViewListener;
    private String trackerTemplate;
    private FallbackAdCallback fallbackAdCallback;

    public ScheduleAdGroupPlayerView(Context context) {
        super(context);
        players = new ArrayList<>();
        adQueue = new ArrayList<>();
    }

    public void setFallbackAdCallback(FallbackAdCallback fallbackAdCallback) {
        this.fallbackAdCallback = fallbackAdCallback;
    }

    public void setTrackerHandler(AdTrackerHandler trackerHandler) {
        this.trackerHandler = trackerHandler;
    }

    public void setTrackerTemplate(String trackerTemplate) {
        this.trackerTemplate = trackerTemplate;
    }

    public void setPlayerViewListener(ScheduleAdGroupPlayerView.AdGroupPlayerViewListener playerViewListener) {
        this.playerViewListener = playerViewListener;
    }

    public void stop() {
        for (LMAdPlayerView sdkPlayer : players) {
            sdkPlayer.stop();
        }
    }

    public void loadAd(ScheduleAdGrp adGrp) {
        prepareLayoutForAdGroup(adGrp);
        for (int i = 0; i < adGroup.getScheduleAds().size(); i++) {
            ScheduleAd ad = adGroup.getScheduleAds().get(i);
            if (!ad.isOfflineAvailable()) {
                if (this.fallbackAdCallback != null) {
                    ad = fallbackAdCallback.getFallbackAd(ad);
                }
            }
            LMAdPlayerView playerView = players.get(i);
            playerView.loadAd(ad);
        }
    }

    private void prepareLayoutForAdGroup(ScheduleAdGrp adGroup) {
        this.adGroup = adGroup;
        DynamicLayoutBuilder dynamicLayoutBuilder = new DynamicLayoutBuilder(this);
        dynamicLayoutBuilder.setCallback(new DynamicLayoutBuilder.DynamicLayoutBuilderCallback<ScheduleAd>() {
            @Override
            public ViewGroup viewForObject(ScheduleAd obj) {
                LMAdPlayerView playerView = viewForAd(obj);
                preparationPlayersQueue.add(playerView);
                players.add(playerView);
                return playerView;
            }
        });
        dynamicLayoutBuilder.build(adGroup);
    }

    LMAdPlayerView playerForAd(ScheduleAd ad) {
        Context context = getContext();
        if (ad.getScheduleAdType() == ScheduleAd.Type.IMAGE ||
                ad.getScheduleAdType() == ScheduleAd.Type.VAST_IMAGE) {
            ImagePlayer imagePlayer = new ImagePlayer(context);
            imagePlayer.setDuration(ad.getDuration());
            return new LMAdPlayerView(context, imagePlayer);
        } else if (ad.getScheduleAdType() == ScheduleAd.Type.VIDEO ||
                ad.getScheduleAdType() == ScheduleAd.Type.VAST_VIDEO) {
            VideoPlayer videoPlayer = new VideoPlayer(context);
            videoPlayer.setDuration(ad.getDuration());
            LMAdPlayerView lmAdPlayerView = new LMAdPlayerView(context, videoPlayer);
            if (Utils.isVastAd(ad.getScheduleAdType())) {
                lmAdPlayerView.setEnableQuartileTrackers(true);
            }
            return lmAdPlayerView;
        } else if (ad.getScheduleAdType() == ScheduleAd.Type.WEB ||
                ad.getScheduleAdType() == ScheduleAd.Type.VAST_WEB) {
            HtmlPlayer htmlPlayer = new HtmlPlayer(context);
            htmlPlayer.setDuration(ad.getDuration());
            return new LMAdPlayerView(context, htmlPlayer);
        }
        LMLog.i("Ad -> " + ad);
        return null;
    }

    LMAdPlayerView viewForAd(ScheduleAd ad) {

        LMAdPlayerView adPlayer = playerForAd(ad);
        adPlayer.setAdPlayerCallback(new LMAdPlayerView.AdPlayerCallback() {
            @Override
            public void onAdPlayerPrepared(AdI ad) {
                ScheduleAd wa = (ScheduleAd) ad;
                LMLog.i("Playing the ad %s with, PlayTimes -> \n" + "Schedul: " + wa.getScheduleTime() + "\nCurrent: " + Calendar.getInstance().getTime(), ad);
                preparationPlayersQueue.remove(adPlayer);
                if (preparationPlayersQueue.size() == 0) {
                    playerViewListener.onAdPlayerPrepared(adGroup);
                }
            }

            @Override
            public void onAdStarted(AdI ad) {
                playerViewListener.onAdStarted(adGroup);

                ScheduleAd wa = (ScheduleAd) ad;
                if (!wa.isFallbackAd()) {
                    String updatedUrl = Utils.updatedScheduleTracker(trackerTemplate,
                            wa.getAdItem());
                    trackerHandler.trackImpression(updatedUrl, wa.isRTB());
                    trackerHandler.trackStringImpressions(wa.getThirdpartyTrackers() ,wa.isRTB());
                }

                if (Utils.isVastAd(wa.getScheduleAdType())) {
                    trackerHandler.trackImpressionsForAd(ad, wa.isRTB());
                    LMLog.i("Executing start quartile impressions");
                    trackerHandler.trackEventImpressions(ad, "start", wa.isRTB());
                }
            }

            @Override
            public void onAdCompleted(AdI ad) {

                ScheduleAd wa = (ScheduleAd) ad;
                LMLog.i("Executing complete quartile impressions");
                trackerHandler.trackEventImpressions(ad, "complete", wa.isRTB());
                trackerHandler.sendImpressionFromQueue();

                completionCounter--;
                if (completionCounter <= 0) {
                    playerViewListener.onAdCompleted(adGroup);
                }

            }

            @Override
            public void onAdPlayError(Error error, boolean loadTime) {
                LMLog.e("Player error - " + error.getLocalizedMessage());
            }

            @Override
            public void onFirstQuartileReached(AdI ad) {
                ScheduleAd wa = (ScheduleAd) ad;
                LMLog.i("Executing first quartile impressions");
                trackerHandler.trackEventImpressions(ad, "start", wa.isRTB());
            }

            @Override
            public void onMidPointReached(AdI ad) {
                LMLog.i("Executing mid quartile impressions");
                ScheduleAd wa = (ScheduleAd) ad;
                trackerHandler.trackEventImpressions(ad, "midpoint", wa.isRTB());
            }

            @Override
            public void onThirdQuartileReached(AdI ad) {
                LMLog.i("Executing third quartile impressions");
                ScheduleAd wa = (ScheduleAd) ad;
                trackerHandler.trackEventImpressions(ad, "thirdQuartile", wa.isRTB());
            }
        });

        LMLog.i("Loading the ad " + ad);
        return adPlayer;
    }

    public void play() {
        for (LMAdPlayerView sdkPlayer : players) {
            sdkPlayer.play();
        }
        completionCounter = players.size();
    }

    public interface FallbackAdCallback {
        ScheduleAd getFallbackAd(ScheduleAd ad);
    }

    public interface AdGroupPlayerViewListener {

        void onAdPlayerPrepared(ScheduleAdGrp ad);

        void onAdStarted(ScheduleAdGrp ad);

        void onAdPlayError(Error error, ScheduleAdGrp ad);

        void onAdCompleted(ScheduleAdGrp ad);

    }

}
