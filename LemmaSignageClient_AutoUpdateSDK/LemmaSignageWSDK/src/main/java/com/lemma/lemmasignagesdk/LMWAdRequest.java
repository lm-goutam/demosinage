package com.lemma.lemmasignagesdk;

import com.lemma.lemmasignagesdk.api.LMAdRequestI;
import com.lemma.lemmasignagesdk.core.LMDexClassLoader;

import java.util.HashMap;
import java.util.TimeZone;



public class LMWAdRequest implements LMAdRequestI {

    LMAdRequestI interfaceImpl;

    @Override
    public LMAdRequestI getInnerIml() {
        return interfaceImpl;
    }

    public LMWAdRequest(String publisherId, String adUnitId) {
        interfaceImpl = LMDexClassLoader.getInstance().RequestImpl(publisherId, adUnitId);
    }

    public TimeZone getTimeZone() {
       return interfaceImpl.getTimeZone();
    }

    public void setTimeZone(TimeZone timeZone) {
        interfaceImpl.setTimeZone(timeZone);
    }

    public String getAdServerBaseURL() {
        return interfaceImpl.getAdServerBaseURL();
    }

    public void setAdServerBaseURL(String adServerBaseURL) {
        interfaceImpl.setAdServerBaseURL(adServerBaseURL);
    }

    public String getPublisherId() {
        return interfaceImpl.getPublisherId();
    }

    public String getAdUnitId() {
        return interfaceImpl.getAdUnitId();
    }

    @Override
    public HashMap<String, String> getMap() {
        return interfaceImpl.getMap();
    }

    @Override
    public void setMap(HashMap<String, String> map) {
        interfaceImpl.setMap(map);
    }
}
