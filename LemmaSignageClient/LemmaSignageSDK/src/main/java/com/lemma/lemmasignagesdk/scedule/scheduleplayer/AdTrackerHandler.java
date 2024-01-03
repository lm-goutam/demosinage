package com.lemma.lemmasignagesdk.scedule.scheduleplayer;

import android.content.Context;
import android.webkit.WebView;

import com.lemma.lemmasignagesdk.SchedulePlayerConfig;
import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.live.manager.NetworkStatusMonitor;
import com.lemma.lemmasignagesdk.vast.VastBuilder.AdI;
import com.lemma.lemmasignagesdk.vast.VastBuilder.Tracker;
import com.lemma.lemmasignagesdk.vast.tracker.TrackerDBHandler;
import com.lemma.lemmasignagesdk.vast.tracker.TrackerHandler;

import java.util.ArrayList;

public class AdTrackerHandler {

    private final TrackerHandler trackerHandler;

    public AdTrackerHandler(Context context, SchedulePlayerConfig config) {

        WebView webView = new WebView(context.getApplicationContext());
        trackerHandler = new TrackerHandler(new NetworkStatusMonitor(context), webView);
        trackerHandler.executeImpressionInWebContainer = config.getExecuteImpressionInWebContainer();

        try {
            TrackerDBHandler handler = new TrackerDBHandler(context);
            trackerHandler.trackerDBHandler = handler;
        } catch (Exception e) {
            LMLog.e("Unable to create tracker db handler");
        }
    }

    public void trackImpression(String impressionUrl, boolean isRTB) {
        LMLog.i("Tracking impression - " + impressionUrl);
        if (isRTB) {
            trackerHandler.sendRTBImpression(impressionUrl);
        } else {
            trackerHandler.sendImpression(impressionUrl);
        }
    }

    public void trackStringImpressions(ArrayList<String> impressionUrls, boolean isRTB) {
        for (String impressionUrl : impressionUrls) {
            trackImpression(impressionUrl, isRTB);
        }
    }

    public void trackImpressionsForAd(AdI ad, boolean isRTB) {
        for (Tracker tracker : ad.getAdTrackers()) {
            trackImpression(tracker.getUrl(), isRTB);
        }
    }

    private void trackImpressions(ArrayList<Tracker> eventTrackers, boolean isRTB) {
        for (Tracker tracker : eventTrackers) {
            trackImpression(tracker.getUrl(), isRTB);
        }
    }

    public void trackEventImpressions(AdI ad, String event, boolean isRTB) {
        ArrayList<Tracker> eventTrackers = ad.getEventTrackers(event);
        sendImpression(eventTrackers, isRTB);
    }

    private void sendImpression(ArrayList<Tracker> trackers, boolean isRTB) {
        trackImpressions(trackers, isRTB);
    }

    public void sendImpressionFromQueue() {
        trackerHandler.sendImpressionFromQueue();
    }

}
