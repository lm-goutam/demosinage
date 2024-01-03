package com.lemma.lemmasignageclient.sdkinstantiator;

import android.content.Context;
import android.view.ViewGroup;

import com.lemma.lemmasignagesdk.AdManagerCallback;
import com.lemma.lemmasignagesdk.LMWAdRequest;
import com.lemma.lemmasignagesdk.LMWConfig;
import com.lemma.lemmasignagesdk.LMWSchedulePlayer;
import com.lemma.lemmasignagesdk.LMWSchedulePlayerConfig;
import com.lemma.lemmasignagesdk.LMWVideoAdManager;
import com.lemma.lemmasignagesdk.LemmaWSDK;
import com.lemma.lemmasignagesdk.api.LMAdRequestI;
import com.lemma.lemmasignagesdk.api.LMConfigI;
import com.lemma.lemmasignagesdk.api.LMVideoAdManagerI;
import com.lemma.lemmasignagesdk.api.LemmaSDKI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerConfigI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerI;

public class InstanceCreator {

    public static LemmaSDKI SDKInstance() {
        return LemmaWSDK.instance;
    }

    public static LMConfigI configInstance() {
        return new LMWConfig();
    }

    public static LMVideoAdManagerI videoAdmanagerInstance(Context context,
                                                           LMAdRequestI aRequest,
                                                           AdManagerCallback adManagerListener,
                                                           LMConfigI config) {
        return new LMWVideoAdManager(context, aRequest, config, adManagerListener);
    }

    public static SchedulePlayerI schedulePlayerInstance(ViewGroup container, SchedulePlayerConfigI config) {
        return new LMWSchedulePlayer(container, config);
    }

    public static SchedulePlayerConfigI schedulePlayerConfigInstance() {
        return new LMWSchedulePlayerConfig();
    }

    public static LMAdRequestI adRequestInstance(String pubid, String au) {
        return new LMWAdRequest(pubid, au);
    }



}

