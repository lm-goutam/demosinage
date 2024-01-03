package com.lemma.lemmasignagesdk.api;

import android.content.Context;
import android.view.ViewGroup;

import com.lemma.lemmasignagesdk.AdManagerCallback;
import com.lemma.lemmasignagesdk.LMSharedVideoManagerPrefetchCallback;

public interface LMSharedVideoManagerI {


    public int getRetryCount();
    public void setRetryCount(int retryCount);
    public void setPrefetchNextLoop(boolean prefetchNextLoop);
    public void prepare(Context context, LMAdRequestI request, LMConfigI config);
    public void prefetch(LMSharedVideoManagerPrefetchCallback callback);
    public void renderAdInView(ViewGroup viewGroup, AdManagerCallback listener);
    public void destroySharedInstance();
    public LMAdLoopStatI getCurrentAdLoopStat();
    //LMAdLoopStat  Class object
   // public LMAdLoopStat getCurrentAdLoopStat();


}
