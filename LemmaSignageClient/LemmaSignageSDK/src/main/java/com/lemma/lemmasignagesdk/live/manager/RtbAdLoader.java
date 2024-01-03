package com.lemma.lemmasignagesdk.live.manager;

import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.vast.VastBuilder.Vast;

public class RtbAdLoader {

    private static final String TAG = "RtbAdLoader";
    private final RtbAdLoaderListener listener;
    private final DownloadManager downloadManager;
    LMDeviceInfo deviceInfo;
    Boolean executeImpressionInWebContainer = false;
    private AdLoader adLoader;

    public RtbAdLoader(DownloadManager downloadManager, RtbAdLoaderListener listener) {
        this.listener = listener;
        this.downloadManager = downloadManager;
    }

    public void loadAds(final String url, final int count) {

        LMLog.i(TAG, "Pending ad count - " + count);
        if (count <= 0) {
            LMLog.i(TAG, " All requested RTB counts are downloaded");
            reset();
            return;
        }
        this.adLoader = new AdLoader(new AdLoader.AdLoaderListener() {
            @Override
            public void onSuccess(Vast vast) {
                if (listener != null) {
                    vast.isRTB = true;
                    listener.onAdReceived(vast);
                }
                loadAds(url, count - 1);
            }

            @Override
            public void onError(Error err) {
                LMLog.i(TAG, err.getLocalizedMessage());
                loadAds(url, count - 1);
            }
        });
        adLoader.setExecuteImpressionInWebContainer(executeImpressionInWebContainer);
        adLoader.deviceInfo = deviceInfo;
        adLoader.downloadManager = this.downloadManager;
        adLoader.load(url);
    }

    public void reset() {
        this.adLoader = null;
    }

    public interface RtbAdLoaderListener {
        void onAdReceived(Vast vast);
    }
}
