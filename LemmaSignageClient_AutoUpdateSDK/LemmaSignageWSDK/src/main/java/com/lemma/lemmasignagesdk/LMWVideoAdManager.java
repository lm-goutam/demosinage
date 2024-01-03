package com.lemma.lemmasignagesdk;

import android.content.Context;
import android.view.ViewGroup;

import com.lemma.lemmasignagesdk.api.LMAdLoopStatI;
import com.lemma.lemmasignagesdk.api.LMAdRequestI;
import com.lemma.lemmasignagesdk.api.LMConfigI;
import com.lemma.lemmasignagesdk.api.LMVideoAdManagerI;
import com.lemma.lemmasignagesdk.core.LMDexClassLoader;

/**
 * This class is the main class exposed for video ad serving. Publisher app
 * needs to create an instance of this class & communicate for video ad serving
 * life-cycle. This class will render the Linear/Non-Linear ads and
 * will fire the tracking URLs as well.
 */
public class LMWVideoAdManager implements LMVideoAdManagerI {

    LMVideoAdManagerI interfaceImpl;

    public LMWVideoAdManager(Context context,
                             LMWAdRequest request,
                             AdManagerCallback adManagerListener
    ) throws IllegalArgumentException {
        interfaceImpl = LMDexClassLoader.getInstance().VideoAdManagerImpl(context,request,null,adManagerListener);
    }

    public LMWVideoAdManager(Context context,
                             LMAdRequestI request,
                             LMConfigI config,
                             AdManagerCallback adManagerListener

    ) throws IllegalArgumentException {
        interfaceImpl = LMDexClassLoader.getInstance().VideoAdManagerImpl(context,request,config,adManagerListener);
    }

    @Override
    public void setupInstance(Context context, LMAdRequestI aRequest, LMConfigI config, AdManagerCallback adManagerListener) {

    }

    public void init(ViewGroup view) throws IllegalArgumentException {
        interfaceImpl.init(view);
    }

    public LMAdLoopStatI getCurrentAdLoopStat() {
        return interfaceImpl.getCurrentAdLoopStat();
    }

    public void startAd() {
        interfaceImpl.startAd();
    }

    public void destroy(){
        interfaceImpl.destroy();
    }

}