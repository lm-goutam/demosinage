package com.lemma.lemmasignagesdk.api;

import android.net.Uri;

public interface SchedulePlayerConfigI {
    long getScheduleRefreshTimeInSeconds();

    void setScheduleRefreshTimeInSeconds(long scheduleRefreshTimeInSeconds);

    boolean isPlayDefaultsOnScheduleCompletion();

    void setPlayDefaultsOnScheduleCompletion(boolean playDefaultsOnScheduleCompletion);

    Uri getFallbackVideoUri();

    void setFallbackVideoUri(Uri fallbackVideoUri);

    Boolean getExecuteImpressionInWebContainer();

    void setExecuteImpressionInWebContainer(Boolean executeImpressionInWebContainer);
}
