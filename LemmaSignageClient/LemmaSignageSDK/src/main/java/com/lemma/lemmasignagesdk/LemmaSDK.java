package com.lemma.lemmasignagesdk;

import android.content.Context;
import android.content.pm.ApplicationInfo;

import com.lemma.lemmasignagesdk.api.LMSDKInitializationCallback;
import com.lemma.lemmasignagesdk.api.LemmaSDKI;
import com.lemma.lemmasignagesdk.cache.ObjectBox;
import com.lemma.lemmasignagesdk.common.DateTimeProvider;
import com.lemma.lemmasignagesdk.common.LMLogger;
import com.lemma.lemmasignagesdk.common.LMUtils;
import com.liulishuo.filedownloader.FileDownloader;

public class LemmaSDK implements LemmaSDKI {

    public static LemmaSDK instance = new LemmaSDK();

    private boolean isConfigured;

    @Override
    public String getVersion() {
        return "2.2.10";
    }

    public String getApplicationName(Context context) {
        ApplicationInfo applicationInfo = context.getApplicationInfo();
        int stringId = applicationInfo.labelRes;
        return stringId == 0 ? applicationInfo.nonLocalizedLabel.toString() : context.getString(stringId);
    }

    private void init(Context context, String rootDirectory, Boolean useInternalStorage) {
        if (isConfigured) {
            return;
        }
        isConfigured = true;
        DateTimeProvider.setup(context);
        FileDownloader.setup(context);
        ObjectBox.init(context);
        LMLogger.plant(new LMLogger.DebugTree());
        LMUtils.setUpDirectories(context, rootDirectory, useInternalStorage);
    }

    @Override
    public void init(Context context, LMSDKInitializationCallback callback) {
        String rootDir = "/LemmaSDK/"; //"/"+getApplicationName(context)+"/LemmaSDK/";
        init(context, rootDir, false);
        if (callback != null) {
            callback.onCompletion(null);
        }
    }

    @Override
    public void init(Context context) {
        init(context, null);
    }


}
