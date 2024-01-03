package com.lemma.lemmasignagesdk;

import android.net.Uri;

import com.lemma.lemmasignagesdk.api.LMConfigI;
import com.lemma.lemmasignagesdk.core.LMDexClassLoader;

public class LMWConfig implements LMConfigI {

    private    LMConfigI interfaceImpl;
    public  String getDuration() {
       return interfaceImpl.getDuration();
    }

    public  LMConfigI getInnerIml(){
        return  interfaceImpl;
    }

    public void setPlayLastSavedLoop(boolean playLastSavedLoop){
        interfaceImpl.setPlayLastSavedLoop(playLastSavedLoop);
    }

    public void setDeleteCacheContinuously(Boolean deleteCacheContinuously) {
        interfaceImpl.setDeleteCacheContinuously(deleteCacheContinuously);
    }

    public void setExecuteImpressionInWebContainer(Boolean executeImpressionInWebContainer){
        interfaceImpl.setExecuteImpressionInWebContainer(executeImpressionInWebContainer);
    }

    public Uri getVideoUri() {
        return interfaceImpl.getVideoUri();
    }

    public void setPlaceHolderVideo(Uri uri, long duration) {
        interfaceImpl.setPlaceHolderVideo(uri, duration);
    }

    public void setPlaceHolderImage(Uri uri, long duration) {
        interfaceImpl.setPlaceHolderImage(uri, duration);
    }

    public LMWConfig() {
        interfaceImpl = LMDexClassLoader.getInstance().LMConfigImpl();
    }

}
