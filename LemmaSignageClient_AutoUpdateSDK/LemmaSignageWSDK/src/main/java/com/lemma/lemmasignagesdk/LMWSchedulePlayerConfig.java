package com.lemma.lemmasignagesdk;

import android.net.Uri;

import com.lemma.lemmasignagesdk.api.SchedulePlayerConfigI;
import com.lemma.lemmasignagesdk.core.LMDexClassLoader;

public class LMWSchedulePlayerConfig implements SchedulePlayerConfigI {

    SchedulePlayerConfigI interfaceImpl;

    public SchedulePlayerConfigI getInnerIml(){
        return  interfaceImpl;
    }

    public LMWSchedulePlayerConfig(){
        interfaceImpl = LMDexClassLoader.getInstance().SchedulePlayerConfig();
    }

    @Override
    public long getScheduleRefreshTimeInSeconds() {
        return interfaceImpl.getScheduleRefreshTimeInSeconds();
    }

    @Override
    public void setScheduleRefreshTimeInSeconds(long scheduleRefreshTimeInSeconds) {
        interfaceImpl.setScheduleRefreshTimeInSeconds(scheduleRefreshTimeInSeconds);
    }

    @Override
    public boolean isPlayDefaultsOnScheduleCompletion() {
        return interfaceImpl.isPlayDefaultsOnScheduleCompletion();
    }

    @Override
    public void setPlayDefaultsOnScheduleCompletion(boolean playDefaultsOnScheduleCompletion) {
        interfaceImpl.setPlayDefaultsOnScheduleCompletion(playDefaultsOnScheduleCompletion);
    }

    @Override
    public Uri getFallbackVideoUri() {
        return interfaceImpl.getFallbackVideoUri();
    }

    @Override
    public void setFallbackVideoUri(Uri fallbackVideoUri) {
        interfaceImpl.setFallbackVideoUri(fallbackVideoUri);
    }

    @Override
    public Boolean getExecuteImpressionInWebContainer() {
        return interfaceImpl.getExecuteImpressionInWebContainer();
    }

    @Override
    public void setExecuteImpressionInWebContainer(Boolean executeImpressionInWebContainer) {
        interfaceImpl.setExecuteImpressionInWebContainer(executeImpressionInWebContainer);
    }
}
