package com.lemma.lemmasignagesdk.scedule.scheduleplayer.itemprocessor;

import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.ScheduleAdItem;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.ScheduleAdItemGrp;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAd;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAdGrp;

import java.util.ArrayList;

public class ScheduleItemGrpProcessor {

    private ScheduleItemGrpProcessor.Callback callback;
    private final ArrayList<ScheduleItemProcessor> scheduleItemProcessors = new ArrayList<>();
    private Integer queueLenght;
    private final ArrayList<ScheduleAd> scheduleAds = new ArrayList<>();
    private ScheduleAdItemGrp scheduleAdItemGrp;
    private ScheduleAdGrp scheduleAdGrp;

    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    public ScheduleAdItemGrp getScheduleAdItemGrp() {
        return scheduleAdItemGrp;
    }

    public ScheduleAdGrp getScheduleAdGrp() {
        return scheduleAdGrp;
    }

    public void process(ScheduleAdItemGrp scheduleAdItemGrp) {
        this.scheduleAdItemGrp = scheduleAdItemGrp;
        ArrayList<ScheduleAdItem> scheduleAdItems = new ArrayList<>(scheduleAdItemGrp.getItems());
        queueLenght = scheduleAdItems.size();
        for (ScheduleAdItem item : scheduleAdItems) {
            ScheduleItemProcessor scheduleItemProcessor = ScheduleItemProcessor.builder()
                    .setScheduleAdItem(item).build();

            scheduleItemProcessors.add(scheduleItemProcessor);
            scheduleItemProcessor.process(item, new ScheduleItemProcessor.Callback() {
                @Override
                public void onCompletion(ScheduleItemProcessor processor, Error error) {
                    process(processor, error);
                }
            });
        }
    }

    private void process(ScheduleItemProcessor processor, Error error) {
        queueLenght--;

        if (error != null) {
            LMLog.e(error.getLocalizedMessage());
            //Create blank ad
        }

        if (queueLenght <= 0) {
            for (ScheduleItemProcessor scheduleItemProcessor : scheduleItemProcessors) {
                ScheduleAd scheduleAd = scheduleItemProcessor.getScheduleAd();
                scheduleAds.add(scheduleAd);
            }

            scheduleAdGrp = ScheduleAdGrp.builder()
                    .setScheduleAds(scheduleAds)
                    .build();
            callback.onCompletion(this, error);
        }

    }

    public interface Callback {
        void onCompletion(ScheduleItemGrpProcessor scheduleItemGrpProcessor, Error error);
    }
}
