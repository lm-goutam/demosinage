package com.lemma.lemmasignageclient.ui.live.Bean;


public class APIUrl {

    //Note : Always point to production server, while testing point to staging
    static String PRODUCTION_SERVER_URL = "http://lemmadigital.com";
    static String STAGING_SERVER_URL = "http://sandbox.lemmatechnologies.com";
    static String SERVER_URL = PRODUCTION_SERVER_URL;

    static String LOGIN_API = SERVER_URL + "/lemma-sso/api/v1/login";
    static String SIGNUP_API = SERVER_URL + "/lemma-sso/api/v1/useradd";
    static String CREATE_AU_API = SERVER_URL + "/lemma/api/v1/adunitcreate";
    static String GEO_API = SERVER_URL + "/lemma/api/v1/geo";
    static String TARGETINGPARAM =  SERVER_URL + "/lemma/api/v1/targetingparams";

    // PDN specific constants
    static String PDN_PRODUCTION_SERVER_URL = "https://adpf.in.panasonic.com";
    static String PDN_ADTAG_API = PDN_PRODUCTION_SERVER_URL + "/api/v1/pdnadtag";

}
