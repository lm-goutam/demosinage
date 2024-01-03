package com.lemma.lemmasignagesdk.scedule.scheduleplayer.group;

import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.ScheduleAdItemGrp;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAdGrp;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.itemprocessor.ScheduleItemGrpProcessor;

import java.util.ArrayList;

public class AdGrpPrefetcher {

    ArrayList<ScheduleAdGrp> scheduleAdGrps = new ArrayList();
    ArrayList<ScheduleItemGrpProcessor> processors = new ArrayList();
    private Integer processingCount = 0;

    public void prefetch(ArrayList<ScheduleAdItemGrp> list,
                         final CompletionCallback completionCallback) {

        ArrayList<ScheduleAdItemGrp> itemGrps = new ArrayList(list);

        processingCount = itemGrps.size();
        for (ScheduleAdItemGrp itemGrp : list) {
            ScheduleItemGrpProcessor scheduleItemGrpProcessor = new ScheduleItemGrpProcessor();
            scheduleItemGrpProcessor.setCallback(new ScheduleItemGrpProcessor.Callback() {
                @Override
                public void onCompletion(ScheduleItemGrpProcessor scheduleAdItemGrp, Error error) {

                    if (error != null) {
                        LMLog.e("Failed to fetch ad %s", scheduleAdItemGrp);
                    }
                    processingCount--;

                    if (processingCount <= 0) {
                        notifySuccess(completionCallback);
                    }
                }
            });
            processors.add(scheduleItemGrpProcessor);
            scheduleItemGrpProcessor.process(itemGrp);
        }
    }

    private void notifySuccess(CompletionCallback completionCallback) {
        for (ScheduleItemGrpProcessor scheduleItemGrpProcessor : processors) {
            ScheduleAdGrp scheduleAdGrp = scheduleItemGrpProcessor.getScheduleAdGrp();
            scheduleAdGrps.add(scheduleAdGrp);
        }
        completionCallback.onComplete(null, scheduleAdGrps);
    }

    public interface CompletionCallback {
        void onComplete(Error error, ArrayList<ScheduleAdGrp> ads);
    }
}

