package com.lemma.lemmasignageclient.sdkinstantiator;

import android.content.Context;
import android.view.ViewGroup;

import com.lemma.lemmasignagesdk.AdManagerCallback;
import com.lemma.lemmasignagesdk.LMAdRequest;
import com.lemma.lemmasignagesdk.LMConfig;
import com.lemma.lemmasignagesdk.LMVideoAdManager;
import com.lemma.lemmasignagesdk.LemmaSDK;
import com.lemma.lemmasignagesdk.SchedulePlayer;
import com.lemma.lemmasignagesdk.SchedulePlayerConfig;
import com.lemma.lemmasignagesdk.api.LMAdRequestI;
import com.lemma.lemmasignagesdk.api.LMConfigI;
import com.lemma.lemmasignagesdk.api.LMVideoAdManagerI;
import com.lemma.lemmasignagesdk.api.LemmaSDKI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerConfigI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerI;

public class InstanceCreator {

    public static LemmaSDKI SDKInstance() {
        return LemmaSDK.instance;
    }

    public static LMConfigI configInstance() {
        return new LMConfig();
    }

    public static LMVideoAdManagerI videoAdmanagerInstance(Context context,
                                                           LMAdRequestI aRequest,
                                                           AdManagerCallback adManagerListener,
                                                           LMConfigI config) {
        return new LMVideoAdManager(context, aRequest,  adManagerListener,config);
    }

    public static SchedulePlayerI schedulePlayerInstance(ViewGroup container, SchedulePlayerConfigI config) {
        return new SchedulePlayer(container, config);
    }

    public static SchedulePlayerConfigI schedulePlayerConfigInstance() {
        return new SchedulePlayerConfig();
    }

    public static LMAdRequestI adRequestInstance(String pubid, String au) {
        return new LMAdRequest(pubid, au);
    }



}

