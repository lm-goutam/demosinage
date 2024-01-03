package com.lemma.lemmasignageclient.ui.live.AdLoader;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.widget.FrameLayout;

import com.lemma.lemmasignageclient.common.AppConfig;
import com.lemma.lemmasignageclient.common.AppManager;
import com.lemma.lemmasignageclient.common.AppUtil;
import com.lemma.lemmasignageclient.sdkinstantiator.InstanceCreator;
import com.lemma.lemmasignagesdk.AdManagerCallback;
import com.lemma.lemmasignagesdk.api.LMAdRequestI;
import com.lemma.lemmasignagesdk.api.LMConfigI;
import com.lemma.lemmasignagesdk.api.LMVideoAdManagerI;

import org.xmlpull.v1.XmlPullParserException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;


public class AdLoader {

    private LMVideoAdManagerI mVAdManager = null;
    private Context mContext = null;
    private FrameLayout mLinerAdContainer;

    public AdLoader(FrameLayout linearAdContainer) {

        String pubId = AppConfig.instance.getPublisherId();
        String adUnitId = AppConfig.instance.getAdunitId();

        mLinerAdContainer = linearAdContainer;
        mContext = linearAdContainer.getContext();

        LMConfigI config = InstanceCreator.configInstance();

        String mp4Path = AppUtil.getDefaultAdUri(mContext).toString();

        config.setPlaceHolderVideo(Uri.fromFile(new File(mp4Path)),15);
        config.setPlayLastSavedLoop(true);
        config.setExecuteImpressionInWebContainer(true);

        LMAdRequestI adRequest = InstanceCreator.adRequestInstance(pubId, adUnitId);
        adRequest.setAdServerBaseURL(AppUtil.serveAdsAPI());

        // added rtbParam map fro external file
        HashMap map = AppConfig.instance.getCustomParams();
        adRequest.setMap(map);
        mVAdManager = InstanceCreator.videoAdmanagerInstance(mContext, adRequest,new AdManagerCallback() {

            @Override
            public boolean shouldFireImpressions() {
                return AppManager.getInstance().isDisplayOn;
            }

            @Override
            public void onAdError(LMVideoAdManagerI manager, Error error) {
//                LMLogger.e(error.getLocalizedMessage());
            }

            @Override
            public void onAdEvent(AD_EVENT event) {
//                LMLogger.i(event.name());
//                LMLogger.i("" + mVAdManager.getCurrentAdLoopStat());

                switch (event) {
                    case AD_LOADED:
                        mVAdManager.startAd();
                        break;
                    case AD_LOOP_COMPLETED:
//                        LMLogger.i("AD_LOOP_COMPLETED");
                        break;
                    default:
                        break;
                }
            }
        },config);
    }

    private void render() {
        Handler mainHandler = new Handler(Looper.getMainLooper());
        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {
                mVAdManager.init(mLinerAdContainer);
            } // This is your code
        };
        mainHandler.post(myRunnable);
    }

    public void start() throws InterruptedException, FileNotFoundException, XmlPullParserException, IOException {
        render();
    }

    public void destroy(){
        if(mVAdManager != null){
            mVAdManager.destroy();
        }
    }
}
