package com.lemma.lemmasignagesdk.scedule.scheduleplayer.itemprocessor;

import android.os.Handler;
import android.os.Looper;

import com.lemma.lemmasignagesdk.live.manager.AdLoader;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.MediaRepository;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.ScheduleAdItem;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.Utils;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAd;
import com.lemma.lemmasignagesdk.vast.VastBuilder.AdI;
import com.lemma.lemmasignagesdk.vast.VastBuilder.AdType;
import com.lemma.lemmasignagesdk.vast.VastBuilder.Vast;

public class VastProcessingStrategy implements ProcessingStrategy {

    private final MediaRepository mediaRepository = new MediaRepository();
    private ProcessingStrategy.Callback callback;
    private ScheduleAdItem item;

    @Override
    public void setCallback(ProcessingStrategy.Callback callback) {
        this.callback = callback;
    }

    @Override
    public void process(ScheduleAdItem item) {
        this.item = item;
        String url = item.getCreative();
        AdLoader loopAdLoader = new AdLoader(new AdLoader.AdLoaderListener() {
            @Override
            public void onSuccess(final Vast vast) {
                downloadMediaAndNotify(vast);
            }

            @Override
            public void onError(final Error err) {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onCompletion(err, null);
                    }
                });
            }
        });
        loopAdLoader.load(url);
    }

    private void downloadMediaAndNotify(Vast vast) {
        AdI ad = vast.ads.get(0);
        String url = ad.getAdRL();
        mediaRepository.get(url, new MediaRepository.Callback() {
            @Override
            public void onCompletion(Error error, String uriString) {

                if (error != null) {
                    VastProcessingStrategy.this.callback.onCompletion(error, null);
                } else {
                    notifySuccess(ad, uriString);
                }
            }
        });
    }

    private void notifySuccess(AdI ad, String resourceUriString) {
        ScheduleAd scheduleAd = ScheduleAd.newBuilder()
                .withAdItem(item)
                .withLocalUriString(resourceUriString)
                .build();

        if (ad.getType() == AdType.LINEAR) {
            if (Utils.isImageType(ad.mimeType())) {
                scheduleAd.setScheduleAdType(ScheduleAd.Type.VAST_IMAGE);
            } else {
                scheduleAd.setScheduleAdType(ScheduleAd.Type.VAST_VIDEO);
            }
        } else if (ad.getType() == AdType.NONLINEAR) {
            if (ad.isUrl()) {
                // Image
                scheduleAd.setScheduleAdType(ScheduleAd.Type.VAST_IMAGE);
            } else {
                scheduleAd.setScheduleAdType(ScheduleAd.Type.VAST_WEB);
            }
        }
        this.callback.onCompletion(null, scheduleAd);
    }
}
