package com.lemma.lemmasignagesdk;

import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.lemma.lemmasignagesdk.api.LMAdRequestI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerConfigI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerEventListener;
import com.lemma.lemmasignagesdk.api.SchedulePlayerI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerPreparationListener;
import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.common.LMTimer;
import com.lemma.lemmasignagesdk.common.LMUtils;
import com.lemma.lemmasignagesdk.live.manager.NetworkStatusMonitor;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.AdTrackerHandler;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.Schedule;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.Utils;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAd;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAdGrp;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.group.AdGrpScheduleManager;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.group.ScheduleAdGroupPlayerView;

import java.util.ArrayList;
import java.util.Calendar;

public class SchedulePlayer implements SchedulePlayerI {

    private final AdGrpScheduleManager scheduleManager;
    private final ViewGroup container;
    private final AdTrackerHandler trackerHandler;
    private final Integer preDownloadScheduleItemCount = 6;
    private final SchedulePlayerConfig schedulePlayerConfig;
    private Schedule schedule;
    private ArrayList<ScheduleAdGrp> ads = new ArrayList<>();
    private SchedulePlayerPreparationListener listener;
    private ScheduleAdGroupPlayerView currentSDKPlayer;
    private ScheduleAdGroupPlayerView oldSDKPlayer;
    private SchedulePlayerEventListener schedulePlayerEventListener;
    private boolean startNotified = false;
    private LMTimer timer;

    public SchedulePlayer(ViewGroup container) {
        this(container, new SchedulePlayerConfig());
    }

    public SchedulePlayer(ViewGroup container, SchedulePlayerConfigI config) {
        this.container = container;
        schedulePlayerConfig = (SchedulePlayerConfig) config;
        scheduleManager = new AdGrpScheduleManager();
        scheduleManager.setNetworkStatusMonitor(new NetworkStatusMonitor(container.getContext()));
        scheduleManager.setPreDownloadScheduleItemCount(preDownloadScheduleItemCount);
        trackerHandler = new AdTrackerHandler(container.getContext(), schedulePlayerConfig);
    }

    @Override
    public void setSchedulePlayerEventListener(SchedulePlayerEventListener schedulePlayerEventListener) {
        this.schedulePlayerEventListener = schedulePlayerEventListener;
    }

    @Override
    public void destroy() {

        if (timer != null) {
            timer.cancel();
        }

        // Apply cleanup
        if (currentSDKPlayer != null) {
            currentSDKPlayer.stop();
        }

        if (oldSDKPlayer != null) {
            oldSDKPlayer.stop();
        }
    }

    @Override
    public void refreshSchedule() {

        timer = LMTimer.TimerBuilder.aTimer()
                .withCallAfterSeconds(schedulePlayerConfig.getScheduleRefreshTimeInSeconds())
                .withCallable(new LMTimer.Callable() {
                    @Override
                    public void call(boolean isOnMainThread) {
                        LMLog.i("Refreshing the schedule");

                        scheduleManager.refreshSchedule(new AdGrpScheduleManager.CompletionCallback<Schedule>() {
                            @Override
                            public void onComplete(Error error, Schedule newSchedule) {
                                LMLog.i("Received refreshSchedule response: %s - %s", error, newSchedule);

                                if (newSchedule != null) {
                                    if (Utils.eligibleForReplacement(SchedulePlayer.this.schedule,
                                            newSchedule)) {
                                        SchedulePlayer.this.schedule = newSchedule;
                                        LMLog.i("Started using new schedule");
                                    } else {
                                        LMLog.i("Schedule replacement skipped as no content change");
                                    }
                                } else {
                                    if (error != null) {
                                        LMLog.i("Failed to refresh the schedule");
                                    } else {
                                        LMLog.i("Undefined error in refreshing the schedule");
                                    }
                                }
                            }
                        });
                    }
                })
                .withRepeat(true)
                .build();
        timer.start();

    }

    @Override
    public void prepare(LMAdRequestI anAdRequest, SchedulePlayerPreparationListener listener) {
        LMLog.i("Schedule prepare : Device time - %s, True time - %s",LMUtils.getCurrentTime(),
                LMUtils.getCurrentTime());

        LMAdRequest lmAdRequest = (LMAdRequest) anAdRequest;
        this.listener = listener;
        scheduleManager.fetchSchedule(lmAdRequest, new AdGrpScheduleManager.CompletionCallback<Schedule>() {
            @Override
            public void onComplete(Error error, Schedule aSchedule) {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        if (error != null) {
                            listener.onFailure(error);
                            LMLog.e(error.getLocalizedMessage());
                        } else {
                            LMLog.i("Schedule of length %s is downloaded", aSchedule);
                            SchedulePlayer.this.schedule = aSchedule;
                            fetchAndPlay();
                        }
                    }
                });
            }
        });
    }

    private void fetchAndPlay() {
        scheduleManager.fetchNextAds(new AdGrpScheduleManager.CompletionCallback<ArrayList<ScheduleAdGrp>>() {
            @Override
            public void onComplete(Error error, ArrayList<ScheduleAdGrp> adList) {
                if (error != null) {
                    LMLog.e(error.getLocalizedMessage());
                } else {
                    LMLog.i("Prefetched %d items, ready for playback", preDownloadScheduleItemCount);
                    SchedulePlayer.this.ads = new ArrayList<>(adList);
                    SchedulePlayer.this.listener.onSuccess();
                }
            }
        });
    }

    private void attachPlayerAndPlay(ViewGroup v) {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        this.getContainer().addView(currentSDKPlayer, params);
        currentSDKPlayer.play();

        if (oldSDKPlayer != null) {
            ViewGroup playerView = oldSDKPlayer;
            oldSDKPlayer.stop();
            ((ViewGroup) playerView.getParent()).removeView(playerView);
        }
    }

    static interface Callback {
        public void onCompletion();
    }

    private void calibrateIfNeeded(long diffInSec, Callback callback) {
        LMTimer.TimerBuilder.aTimer()
                .withCallAfterSeconds(diffInSec)
                .withCallable(new LMTimer.Callable() {
                    @Override
                    public void call(boolean isOnMainThread) {
                        callback.onCompletion();
                    }
                })
                .build()
                .start();
    }

    @Override
    public void play() {
        refreshSchedule();
        if (ads.size() <= 0) {
            String msg = "No ads to play";
            listener.onFailure(new Error(msg));
            LMLog.e(msg);
        }

        ArrayList toRemoveList = new ArrayList();
        for (ScheduleAdGrp ad : ads) {
            ScheduleAdGrp wa = ad;
            long diffInSec = LMUtils.intervalFromCurrentTime(wa.getScheduleTime());
            if (diffInSec == -1) {
                toRemoveList.add(ad);
            } else {

                LMLog.i("Calibrating ad play by %d seconds - ", diffInSec);
                if (schedulePlayerEventListener != null) {
                    schedulePlayerEventListener.onCalibrating(diffInSec);
                }

                calibrateIfNeeded( diffInSec ,new Callback() {
                    @Override
                    public void onCompletion() {
                        playNextAd();
                    }
                });
                ads.removeAll(toRemoveList);
                break;
            }
        }
    }

    private void scheduleForNextAd() {
        long diffInSec = 10;
        if (ads.size() > 0) {
            ScheduleAdGrp ad = ads.get(0);
            diffInSec = LMUtils.intervalFromCurrentTime(ad.getScheduleTime());
        }else {
            LMLog.i("Next ad is not available, retrying after %d seconds", diffInSec);
        }
        calibrateIfNeeded(diffInSec, () -> playNextAd());
    }

    private void playNextAd() {

        if (ads.size() > 0) {
            ScheduleAdGrp ad = ads.get(0);
            ads.remove(ad);
            play(ad);
            scheduleForNextAd();
        } else {
            if (schedulePlayerConfig.isPlayDefaultsOnScheduleCompletion()) {
                LMLog.i("Playing default fallback ad");
                ScheduleAdGrp adGrp = Utils.freshFallbackAdGrp(schedulePlayerConfig.getFallbackVideoUri());
                play(adGrp);
                scheduleForNextAd();
            } else {
                LMLog.i("Probable end of schedule");
                schedulePlayerEventListener.onScheduleCompletion();
            }
        }

        replenishTheAdsQueue();
    }

    private void replenishTheAdsQueue() {

        if (ads.size() < (preDownloadScheduleItemCount / 2.0)) {
            LMLog.i("Ad queue about to empty so fetching next batch");

            scheduleManager.fetchNextAds(new AdGrpScheduleManager.CompletionCallback<ArrayList<ScheduleAdGrp>>() {
                @Override
                public void onComplete(Error error, ArrayList<ScheduleAdGrp> newAds) {

                    if (error != null) {
                        LMLog.e(error.getLocalizedMessage());
                    } else {
                        if (newAds.size() == 0) {
                            LMLog.i("No new schedule item available");
                        } else {
                            SchedulePlayer.this.ads.addAll(newAds);
                            LMLog.i("Added %d more ads to the queue,new size - %d",
                                    newAds.size(),
                                    SchedulePlayer.this.ads.size());
                        }
                    }
                }
            });

        }
    }

    private void play(ScheduleAdGrp adGrp) {

        LMLog.i("Schedule play: Device time - %s, True time - %s",LMUtils.getCurrentTime(),
                LMUtils.getCurrentTime());

        oldSDKPlayer = currentSDKPlayer;
        currentSDKPlayer = new ScheduleAdGroupPlayerView(container.getContext());

        currentSDKPlayer.setTrackerHandler(trackerHandler);
        currentSDKPlayer.setTrackerTemplate(schedule.tracker);
        currentSDKPlayer.setFallbackAdCallback(new ScheduleAdGroupPlayerView.FallbackAdCallback() {
            @Override
            public ScheduleAd getFallbackAd(ScheduleAd ad) {
                return Utils.fallbackForAd(ad, schedulePlayerConfig.getFallbackVideoUri());
            }
        });

        currentSDKPlayer.setPlayerViewListener(new ScheduleAdGroupPlayerView.AdGroupPlayerViewListener() {
            @Override
            public void onAdPlayerPrepared(ScheduleAdGrp ad) {
                LMLog.i("Playing the ad %s with, PlayTimes -> \n" + "Schedul: " + ad.getScheduleTime() + "\nCurrent: " + Calendar.getInstance().getTime(), ad);
                attachPlayerAndPlay(currentSDKPlayer);
            }

            @Override
            public void onAdStarted(ScheduleAdGrp ad) {
                if (!startNotified) {
                    startNotified = true;
                    schedulePlayerEventListener.onStarted();
                }
            }

            @Override
            public void onAdPlayError(Error error, ScheduleAdGrp ad) {
                LMLog.e("Player error - " + error.getLocalizedMessage());
            }

            @Override
            public void onAdCompleted(ScheduleAdGrp ad) {
//                loadAndPlayNextAd();
                LMLog.i("Executing complete quartile impressions");
            }
        });
        LMLog.i("Loading the ad " + adGrp.toString());
        currentSDKPlayer.loadAd(adGrp);
    }

    private ViewGroup getContainer() {
        return container;
    }

}
