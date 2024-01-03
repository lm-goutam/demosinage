package com.lemma.lemmasignagesdk.api;

import java.util.HashMap;
import java.util.TimeZone;

public interface LMAdRequestI {
    HashMap<String, String> getMap();

    void setMap(HashMap<String, String> map);

    LMAdRequestI getInnerIml();

    TimeZone getTimeZone();

    void setTimeZone(TimeZone timeZone);

    String getAdServerBaseURL();

    void setAdServerBaseURL(String adServerBaseURL);

    String getPublisherId();

    String getAdUnitId();
}
