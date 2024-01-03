package com.lemma.lemmasignagesdk.api;

public interface SchedulePlayerI {
    void setSchedulePlayerEventListener(SchedulePlayerEventListener schedulePlayerEventListener);

    void destroy();

    void refreshSchedule();

    void prepare(LMAdRequestI lmAdRequest, SchedulePlayerPreparationListener listener);

    void play();
}
