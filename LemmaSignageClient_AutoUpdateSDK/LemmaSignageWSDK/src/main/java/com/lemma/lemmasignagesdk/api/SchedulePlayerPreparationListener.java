package com.lemma.lemmasignagesdk.api;

public interface SchedulePlayerPreparationListener {
    void onSuccess();

    void onFailure(Error error);
}
