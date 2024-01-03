package com.lemma.lemmasignagesdk.api;

public interface SchedulePlayerEventListener {
    void onStarted();

    void onCalibrating(long seconds);

    void onScheduleCompletion();
}
