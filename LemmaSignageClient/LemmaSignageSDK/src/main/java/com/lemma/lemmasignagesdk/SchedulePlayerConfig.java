package com.lemma.lemmasignagesdk;

import android.net.Uri;

import com.lemma.lemmasignagesdk.api.SchedulePlayerConfigI;

public class SchedulePlayerConfig implements SchedulePlayerConfigI {
    private Uri fallbackVideoUri = null;
    private Boolean executeImpressionInWebContainer = false;
    private long scheduleRefreshTimeInSeconds = 15 * 60;
    private boolean playDefaultsOnScheduleCompletion = true;

    @Override
    public long getScheduleRefreshTimeInSeconds() {
        return scheduleRefreshTimeInSeconds;
    }

    @Override
    public void setScheduleRefreshTimeInSeconds(long scheduleRefreshTimeInSeconds) {
        this.scheduleRefreshTimeInSeconds = scheduleRefreshTimeInSeconds;
    }

    @Override
    public boolean isPlayDefaultsOnScheduleCompletion() {
        return playDefaultsOnScheduleCompletion;
    }

    @Override
    public void setPlayDefaultsOnScheduleCompletion(boolean playDefaultsOnScheduleCompletion) {
        this.playDefaultsOnScheduleCompletion = playDefaultsOnScheduleCompletion;
    }

    @Override
    public Uri getFallbackVideoUri() {
        return fallbackVideoUri;
    }

    @Override
    public void setFallbackVideoUri(Uri fallbackVideoUri) {
        this.fallbackVideoUri = fallbackVideoUri;
    }

    @Override
    public Boolean getExecuteImpressionInWebContainer() {
        return executeImpressionInWebContainer;
    }

    @Override
    public void setExecuteImpressionInWebContainer(Boolean executeImpressionInWebContainer) {
        this.executeImpressionInWebContainer = executeImpressionInWebContainer;
    }
}
