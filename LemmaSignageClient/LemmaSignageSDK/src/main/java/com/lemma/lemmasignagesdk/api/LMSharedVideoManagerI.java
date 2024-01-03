package com.lemma.lemmasignagesdk.api;

import android.content.Context;
import android.view.ViewGroup;

import com.lemma.lemmasignagesdk.AdManagerCallback;
import com.lemma.lemmasignagesdk.LMSharedVideoManagerPrefetchCallback;

public interface LMSharedVideoManagerI {


    int getRetryCount();

    void setRetryCount(int retryCount);

    void setPrefetchNextLoop(boolean prefetchNextLoop);

    void prepare(Context context, LMAdRequestI request,
                 LMConfigI config);

    void prefetch(LMSharedVideoManagerPrefetchCallback callback);


    void renderAdInView(ViewGroup viewGroup, AdManagerCallback listener);


    void destroySharedInstance();

    LMAdLoopStatI getCurrentAdLoopStat();

}
