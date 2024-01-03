package com.lemma.lemmasignagesdk.scedule.scheduleplayer;

import android.content.Context;
import android.net.Uri;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.common.LMTimer;
import com.lemma.lemmasignagesdk.common.LMUtils;
import com.lemma.lemmasignagesdk.mediaplayer.MediaPlayerI;
import com.lemma.lemmasignagesdk.mediaplayer.MediaPlayerListenerI;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAd;
import com.lemma.lemmasignagesdk.vast.VastBuilder.AdI;

import java.util.Date;

public class LMAdPlayerView extends LinearLayout implements MediaPlayerListenerI {

    private final MediaPlayerI mediaPlayer;
    private AdI ad;
    private LMTimer currentTimer;
    private Date startTime;
    private boolean enableQuartileTrackers = false;
    private LMAdPlayerView.AdPlayerCallback mAdPlayerCallback;

    public LMAdPlayerView(Context context, MediaPlayerI mediaPlayer) {
        super(context);
        this.mediaPlayer = mediaPlayer;
        this.mediaPlayer.setPlayerListener(this);
    }

    public void setEnableQuartileTrackers(boolean enableQuartileTrackers) {
        this.enableQuartileTrackers = enableQuartileTrackers;
    }

    public void setAdPlayerCallback(LMAdPlayerView.AdPlayerCallback playerCallback) {
        mAdPlayerCallback = playerCallback;
    }

    public void loadAd(AdI ad) {
        this.startTime = LMUtils.getCurrentTime();

        this.ad = ad;
        String url = ad.getAdRL();
        if (Utils.isURL(url)) {
            Uri uri = Uri.parse(ad.getAdRL());
            this.mediaPlayer.loadMedia(uri);
        } else {
            this.mediaPlayer.loadMedia(url);
        }
    }

    public void play() {
        this.mediaPlayer.startPlayback();
    }

    public void stop() {
        if (this.mediaPlayer != null) {
            this.mediaPlayer.stop();
        }
        if (currentTimer != null) {
            currentTimer.cancel();
        }
    }

    public ViewGroup getView() {
        return this;
    }

    @Override
    public void onPrepared() {
        LMUtils.attachToParentAndMatch(this.mediaPlayer.playerView(), this);
        mAdPlayerCallback.onAdPlayerPrepared(this.ad);
        long diff = LMUtils.getCurrentTime().getTime() - startTime.getTime();
        LMLog.i("Prepared media after %s milli secs", diff);
    }

    @Override
    public void onStarted() {
        mAdPlayerCallback.onAdStarted(ad);
        installQuartileEvents();
    }

    private void installQuartileEvents() {
        if (enableQuartileTrackers) {
            installQuartileEventsTimer(3);
        }
    }

    private void installQuartileEventsTimer(int limit) {
        if (limit > 0) {
            ScheduleAd adItemWrapper = (ScheduleAd) ad;
            long duration = adItemWrapper.getDuration();
            currentTimer = LMTimer.TimerBuilder.aTimer()
                    .withCallAfterSeconds(duration / 4)
                    .withCallable(new LMTimer.Callable() {
                        @Override
                        public void call(boolean isOnMainThread) {
                            if (limit == 3) {
                                mAdPlayerCallback.onFirstQuartileReached(ad);
                            } else if (limit == 2) {
                                mAdPlayerCallback.onMidPointReached(ad);
                            } else if (limit == 1) {
                                mAdPlayerCallback.onThirdQuartileReached(ad);
                            }
                            installQuartileEventsTimer(limit - 1);
                        }
                    }).build();
            currentTimer.start();
        }
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onResume() {

    }

    @Override
    public void onMute() {

    }

    @Override
    public void onUnmute() {

    }

    @Override
    public void onSkip() {

    }

    @Override
    public void onStop() {
    }

    @Override
    public void onCompleted() {
        mAdPlayerCallback.onAdCompleted(ad);
    }

    @Override
    public void onError(Error error, boolean loadTime) {
        mAdPlayerCallback.onAdPlayError(error, loadTime);
    }

    public interface AdPlayerCallback {

        void onAdPlayerPrepared(AdI ad);

        void onAdStarted(AdI ad);

        void onAdPlayError(Error error, boolean loadTime);

        void onFirstQuartileReached(AdI ad);

        void onMidPointReached(AdI ad);

        void onThirdQuartileReached(AdI ad);

        void onAdCompleted(AdI ad);

    }
}
