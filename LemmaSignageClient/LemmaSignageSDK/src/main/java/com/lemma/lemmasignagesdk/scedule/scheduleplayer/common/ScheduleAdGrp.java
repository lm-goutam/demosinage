package com.lemma.lemmasignagesdk.scedule.scheduleplayer.common;

import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import javax.annotation.Nonnull;

public class ScheduleAdGrp {

    private final ArrayList<ScheduleAd> scheduleAds;

    private ScheduleAdGrp(Builder builder) {
        this.scheduleAds = Objects.requireNonNull(builder.scheduleAds, "scheduleAds");
    }

    public static Builder builder() {
        return new Builder();
    }

    public Date getScheduleTime() {
        ScheduleAd item = scheduleAds.get(0);
        return item.getScheduleTime();
    }

    @Nonnull
    public ArrayList<ScheduleAd> getScheduleAds() {
        return scheduleAds;
    }

    public static class Builder {
        private ArrayList<ScheduleAd> scheduleAds;

        private Builder() {
        }

        public Builder setScheduleAds(ArrayList<ScheduleAd> scheduleAds) {
            this.scheduleAds = scheduleAds;
            return this;
        }

        public Builder of(ScheduleAdGrp scheduleAdGrp) {
            this.scheduleAds = scheduleAdGrp.scheduleAds;
            return this;
        }

        public ScheduleAdGrp build() {
            return new ScheduleAdGrp(this);
        }
    }
}
