package com.lemma.lemmasignagesdk.scedule.scheduleplayer.itemprocessor;

import com.lemma.lemmasignagesdk.scedule.scheduleplayer.ScheduleAdItem;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAd;

public interface ProcessingStrategy {

    void setCallback(Callback callback);

    void process(ScheduleAdItem item);

    interface Callback {
        void onCompletion(Error error, ScheduleAd scheduleAd);
    }

}
