package com.lemma.lemmasignagesdk.api;

import android.content.Context;
import android.view.ViewGroup;

import com.lemma.lemmasignagesdk.AdManagerCallback;

public interface LMVideoAdManagerI {

    void setupInstance(Context context,
                       LMAdRequestI aRequest,
                       LMConfigI config,
                       AdManagerCallback adManagerListener);

    LMAdLoopStatI getCurrentAdLoopStat();

    void init(ViewGroup view);

    void startAd();

    void destroy();

}
