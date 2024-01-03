package com.lemma.lemmasignagesdk.api;

import android.net.Uri;

public interface SchedulePlayerConfigI {
    public SchedulePlayerConfigI getInnerIml();

    public long getScheduleRefreshTimeInSeconds();

    public void setScheduleRefreshTimeInSeconds(long scheduleRefreshTimeInSeconds);

    public boolean isPlayDefaultsOnScheduleCompletion();

    public  void setPlayDefaultsOnScheduleCompletion(boolean playDefaultsOnScheduleCompletion);

    public Uri getFallbackVideoUri();

    public void setFallbackVideoUri(Uri fallbackVideoUri);

    public Boolean getExecuteImpressionInWebContainer();

    public void setExecuteImpressionInWebContainer(Boolean executeImpressionInWebContainer);
}
