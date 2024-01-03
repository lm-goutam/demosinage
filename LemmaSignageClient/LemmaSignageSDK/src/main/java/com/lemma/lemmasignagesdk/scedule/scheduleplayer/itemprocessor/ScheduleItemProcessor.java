package com.lemma.lemmasignagesdk.scedule.scheduleplayer.itemprocessor;

import com.lemma.lemmasignagesdk.scedule.scheduleplayer.ScheduleAdItem;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAd;

import java.util.Objects;


public class ScheduleItemProcessor {


    private final ScheduleAdItem scheduleAdItem;
    private final ProcessingStrategy processingStrategy;
    private ScheduleAd scheduleAd;

    private ScheduleItemProcessor(Builder builder) {
        this.scheduleAdItem = Objects.requireNonNull(builder.scheduleAdItem, "scheduleAdItem");
        this.processingStrategy = Objects.requireNonNull(builder.processingStrategy, "processingStrategy");
    }

    public static Builder builder() {
        return new Builder();
    }

    public ScheduleAd getScheduleAd() {
        return scheduleAd;
    }

    public ScheduleAdItem getScheduleAdItem() {
        return scheduleAdItem;
    }

    public ProcessingStrategy getProcessingStrategy() {
        return processingStrategy;
    }

    public void process(ScheduleAdItem scheduleAdItem, Callback callback) {
        processingStrategy.setCallback(new ProcessingStrategy.Callback() {
            @Override
            public void onCompletion(Error error, ScheduleAd scheduleAd) {
                ScheduleItemProcessor.this.scheduleAd = scheduleAd;
                callback.onCompletion(ScheduleItemProcessor.this, error);
            }
        });
        processingStrategy.process(scheduleAdItem);
    }

    public interface Callback {
        void onCompletion(ScheduleItemProcessor processor, Error error);
    }

    public static class Builder {
        private ScheduleAdItem scheduleAdItem;
        private ProcessingStrategy processingStrategy;

        private Builder() {
        }

        public Builder setScheduleAdItem(ScheduleAdItem scheduleAdItem) {
            this.scheduleAdItem = scheduleAdItem;
            return this;
        }

        public Builder setProcessingStrategy(ProcessingStrategy processingStrategy) {
            this.processingStrategy = processingStrategy;
            return this;
        }

        public Builder of(ScheduleItemProcessor scheduleItemProcessor) {
            this.scheduleAdItem = scheduleItemProcessor.scheduleAdItem;
            this.processingStrategy = scheduleItemProcessor.processingStrategy;
            return this;
        }

        private boolean isVideo(ScheduleAdItem scheduleAdItem) {
            return scheduleAdItem.itemType.startsWith("video/");
        }

        private boolean isImage(ScheduleAdItem scheduleAdItem) {
            return scheduleAdItem.itemType.startsWith("image");
        }

        private boolean isVast(ScheduleAdItem scheduleAdItem) {
            return scheduleAdItem.itemType.startsWith("video");
        }

        private boolean isHtml(ScheduleAdItem scheduleAdItem) {
            return scheduleAdItem.itemType.startsWith("url") || scheduleAdItem.itemType.startsWith("script");
        }

        public ScheduleItemProcessor build() {
            if (isVideo(scheduleAdItem) || isImage(scheduleAdItem)) {
                setProcessingStrategy(new DirectResourceProcessingStrategy());
            } else if (isVast(scheduleAdItem)) {
                setProcessingStrategy(new VastProcessingStrategy());
            } else if (isHtml(scheduleAdItem)) {
                setProcessingStrategy(new ProcessingStrategy() {
                    Callback callback;

                    @Override
                    public void setCallback(Callback callback) {
                        this.callback = callback;
                    }

                    @Override
                    public void process(ScheduleAdItem item) {
                        ScheduleAd scheduleAd = ScheduleAd.newBuilder()
                                .withAdItem(item)
                                .build();
                        scheduleAd.setScheduleAdType(ScheduleAd.Type.WEB);
                        this.callback.onCompletion(null, scheduleAd);
                    }
                });
            }
            return new ScheduleItemProcessor(this);
        }
    }
}
