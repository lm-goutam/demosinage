package com.lemma.lemmasignagesdk.scedule.scheduleplayer.itemprocessor;

import com.lemma.lemmasignagesdk.scedule.scheduleplayer.MediaRepository;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.ScheduleAdItem;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAd;

public class DirectResourceProcessingStrategy implements ProcessingStrategy {

    private final MediaRepository mediaRepository = new MediaRepository();
    private ProcessingStrategy.Callback callback;
    private ScheduleAdItem item;

    @Override
    public void setCallback(Callback callback) {
        this.callback = callback;
    }

    @Override
    public void process(ScheduleAdItem item) {
        this.item = item;
        String url = item.getCreative();

        mediaRepository.get(url, new MediaRepository.Callback() {
            @Override
            public void onCompletion(Error error, String uriString) {

                if (error != null) {
                    DirectResourceProcessingStrategy.this.callback.onCompletion(error, null);
                } else {
                    notifySuccess(uriString);
                }
            }
        });
    }

    private void notifySuccess(String resourceUriString) {
        ScheduleAd scheduleAd = ScheduleAd.newBuilder()
                .withAdItem(item)
                .withLocalUriString(resourceUriString)
                .build();
        if (this.item.itemType.startsWith("image")) {
            scheduleAd.setScheduleAdType(ScheduleAd.Type.IMAGE);
        } else if (this.item.itemType.startsWith("video/")) {
            scheduleAd.setScheduleAdType(ScheduleAd.Type.VIDEO);
        }
        this.callback.onCompletion(null, scheduleAd);
    }
}
