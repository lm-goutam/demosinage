package com.lemma.lemmasignagesdk.scedule.scheduleplayer.common;

import com.lemma.lemmasignagesdk.scedule.scheduleplayer.ScheduleAdItem;
import com.lemma.lemmasignagesdk.vast.VastBuilder.AdI;

import java.util.ArrayList;
import java.util.Date;

public class ScheduleAd extends AdI implements ScheduleAdI {

    private final ScheduleAdItem adItem;
    private String localUriString = null;
    private boolean isFallbackAd = false;
    private Type scheduleAdType = Type.None;

    private ScheduleAd(Builder builder) {
        adItem = builder.adItem;
        localUriString = builder.localUriString;
        isFallbackAd = builder.isFallbackAd;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public ArrayList<String> getThirdpartyTrackers() {
        if (adItem != null) {
            return adItem.trackers;
        }
        return new ArrayList<>();
    }

    public boolean isFallbackAd() {
        return isFallbackAd;
    }

    @Override
    public String toString() {
        return "ScheduleAd{" +
                "adItem=" + adItem +
                ", localUriString='" + localUriString + '\'' +
                ", isFallbackAd=" + isFallbackAd +
                ", scheduleAdType=" + scheduleAdType +
                '}';
    }

    public Frame getFrame() {
        return new Frame(adItem.frame.left, adItem.frame.right, adItem.frame.top,
                adItem.frame.bottom);
    }

    public ScheduleAdItem getAdItem() {
        return adItem;
    }

    public Type getScheduleAdType() {
        return scheduleAdType;
    }

    public void setScheduleAdType(Type type) {
        this.scheduleAdType = type;
    }

    @Override
    public String getAdRL() {
        if (localUriString != null) {
            return localUriString;
        }
        return adItem.getCreative();
    }

    @Override
    public Integer getDuration() {
        return adItem.getDuration();
    }

    @Override
    public Date getScheduleTime() {
        return adItem.getStartTime();
    }

    @Override
    public boolean isRTB() {
        return (adItem.getCreativeType() == ScheduleAdItem.CREATIVE_TYPE.RealTime);
    }

    @Override
    public boolean isOfflineAvailable() {
        if (getScheduleAdType() == Type.WEB || getScheduleAdType() == Type.VAST_WEB) {
            return true;
        }
        return (localUriString != null);
    }

    public enum Type {
        None,
        IMAGE,
        VIDEO,
        WEB,
        VAST_IMAGE,
        VAST_WEB,
        VAST_VIDEO
    }

    public static final class Builder {
        private ScheduleAdItem adItem;
        private String localUriString = null;
        private boolean isFallbackAd;

        private Builder() {
        }

        public Builder withAdItem(ScheduleAdItem val) {
            adItem = val;
            return this;
        }

        public Builder withLocalUriString(String val) {
            localUriString = val;
            return this;
        }

        public Builder withIsFallbackAd(boolean val) {
            isFallbackAd = val;
            return this;
        }

        public ScheduleAd build() {
            return new ScheduleAd(this);
        }
    }
}
