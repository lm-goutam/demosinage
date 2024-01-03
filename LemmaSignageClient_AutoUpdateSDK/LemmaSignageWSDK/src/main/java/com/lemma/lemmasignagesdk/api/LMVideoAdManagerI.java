package com.lemma.lemmasignagesdk.api;

import android.content.Context;
import android.view.ViewGroup;

import com.lemma.lemmasignagesdk.AdManagerCallback;

public interface LMVideoAdManagerI {

    public void setupInstance(Context context,
                      LMAdRequestI aRequest,
                      LMConfigI config,
                      AdManagerCallback adManagerListener);

    public void init(ViewGroup view) ;
    public LMAdLoopStatI getCurrentAdLoopStat();
    public void startAd();
    public void destroy();
}
