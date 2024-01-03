package com.lemma.lemmasignagesdk;

import com.lemma.lemmasignagesdk.api.LMVideoAdManagerI;



abstract public class AdManagerCallback {

    /**
     * This method is called when SDK gets error while fetching Vast ad from Ad server
     *
     * @param adManager Instance of LMVideoAdManager related to Ad request
     * @param error     Occurred Error details
     */
    public abstract void onAdError(LMVideoAdManagerI adManager, Error error);

    /**
     * @param adManager
     * @deprecated use onAdEvent instead
     */
    @Deprecated
    public void onAdLoopComplete(LMVideoAdManagerI adManager) {

    }


    /**
     * @method onAdEvent()
     * @discussion This method is called for each event from AdEventType
     */
    public abstract void onAdEvent(final AD_EVENT event);

    public boolean shouldFireImpressions() {
        return true;
    }

    public enum AD_EVENT {
        AD_LOADED, AD_STARTED, AD_PAUSED, AD_RESUMED,
        AD_FIRST_QUARTILE, AD_MID_POINT, AD_THIRD_QUARTILE,
        AD_LOOP_COMPLETED, AD_COMPLETED
    }
}

