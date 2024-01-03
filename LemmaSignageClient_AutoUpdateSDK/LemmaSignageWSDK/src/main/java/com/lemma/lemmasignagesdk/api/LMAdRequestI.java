package com.lemma.lemmasignagesdk.api;

import java.util.HashMap;
import java.util.TimeZone;

public interface LMAdRequestI {

        public LMAdRequestI getInnerIml();

        public TimeZone getTimeZone();
        public void setTimeZone(TimeZone timeZone);
        public String getAdServerBaseURL();
        public void setAdServerBaseURL(String adServerBaseURL) ;

        public String getPublisherId();
        public String getAdUnitId();

        public HashMap<String, String> getMap();
        public void setMap(HashMap<String,String> map) ;

}
