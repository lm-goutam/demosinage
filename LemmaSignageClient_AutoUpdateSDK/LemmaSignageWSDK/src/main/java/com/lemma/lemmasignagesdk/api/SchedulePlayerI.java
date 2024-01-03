package com.lemma.lemmasignagesdk.api;

public interface SchedulePlayerI {
    public void setSchedulePlayerEventListener(SchedulePlayerEventListener schedulePlayerEventListener);

    public void destroy();

    public void refreshSchedule();

    public void prepare(LMAdRequestI lmAdRequest, SchedulePlayerPreparationListener listener);

    public void play();
}
