package com.lemma.lemmasignagesdk;

import com.lemma.lemmasignagesdk.api.LMAdRequestI;

import java.util.HashMap;
import java.util.TimeZone;


public class LMAdRequest implements LMAdRequestI {

    private final String publisherId;
    private final String adUnitId;
    private HashMap<String, String> map;
    private TimeZone timeZone;
    private String adServerBaseURL;

    public LMAdRequest(String publisherId, String adUnitId) {
        this.publisherId = publisherId;
        this.adUnitId = adUnitId;
    }

    @Override
    public HashMap<String, String> getMap() {
        return this.map;
    }

    @Override
    public void setMap(HashMap<String, String> map) {
        this.map = map;
    }

    @Override
    public LMAdRequestI getInnerIml() {
        return null;
    }

    public TimeZone getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(TimeZone timeZone) {
        this.timeZone = timeZone;
    }

    public String getAdServerBaseURL() {
        return adServerBaseURL;
    }

    public void setAdServerBaseURL(String adServerBaseURL) {
        this.adServerBaseURL = adServerBaseURL;
    }

    public String getPublisherId() {
        return publisherId;
    }

    public String getAdUnitId() {
        return adUnitId;
    }


}
