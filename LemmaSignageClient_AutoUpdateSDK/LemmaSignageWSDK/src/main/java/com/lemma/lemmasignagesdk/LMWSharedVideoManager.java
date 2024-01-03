package com.lemma.lemmasignagesdk;

import android.content.Context;
import android.view.ViewGroup;

import com.lemma.lemmasignagesdk.api.LMAdLoopStatI;
import com.lemma.lemmasignagesdk.api.LMAdRequestI;
import com.lemma.lemmasignagesdk.api.LMConfigI;
import com.lemma.lemmasignagesdk.api.LMSharedVideoManagerI;
import com.lemma.lemmasignagesdk.core.LMDexClassLoader;


public class LMWSharedVideoManager implements LMSharedVideoManagerI {

    private static volatile LMWSharedVideoManager sSoleInstance = null;

    LMSharedVideoManagerI  interfaceImpl;

    public static LMWSharedVideoManager getInstance() {
        if (sSoleInstance == null){
            sSoleInstance = new LMWSharedVideoManager();
        }
        return sSoleInstance;
    }
    //private constructor.
    private LMWSharedVideoManager() {
        interfaceImpl = LMDexClassLoader.getInstance().LMSharedVideoManagerImpl();
    }

    public int getRetryCount() {
        return interfaceImpl.getRetryCount();
    }

    public void setRetryCount(int retryCount){
        interfaceImpl.setRetryCount(retryCount);
    }

    public void setPrefetchNextLoop(boolean prefetchNextLoop) {
        interfaceImpl.setPrefetchNextLoop(prefetchNextLoop);
    }

    @Override
    public void prepare(Context context, LMAdRequestI request, LMConfigI config) {
        interfaceImpl.prepare(context, request, config);
    }


    @Override
    public void prefetch(LMSharedVideoManagerPrefetchCallback callback) {
        interfaceImpl.prefetch(callback);
    }

    @Override
    public void renderAdInView(ViewGroup viewGroup, AdManagerCallback listener) {
        interfaceImpl.renderAdInView(viewGroup, listener);

    }

    @Override
    public LMAdLoopStatI getCurrentAdLoopStat() {
        return interfaceImpl.getCurrentAdLoopStat();
    }

    public void destroySharedInstance(){
        sSoleInstance = null;
        interfaceImpl.destroySharedInstance();
    }

}
