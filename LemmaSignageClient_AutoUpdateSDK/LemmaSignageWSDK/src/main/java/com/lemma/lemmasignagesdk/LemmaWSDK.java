package com.lemma.lemmasignagesdk;

import android.content.Context;

import com.lemma.lemmasignagesdk.api.LMSDKInitializationCallback;
import com.lemma.lemmasignagesdk.api.LemmaSDKI;
import com.lemma.lemmasignagesdk.common.logger.LMWLog;
import com.lemma.lemmasignagesdk.common.logger.LMWLogger;
import com.lemma.lemmasignagesdk.core.DexLoadingTask;
import com.lemma.lemmasignagesdk.core.DexUpdater;
import com.lemma.lemmasignagesdk.core.LMDexClassLoader;
import com.lemma.lemmasignagewsdk.R;

import java.io.File;
import java.io.InputStream;

public class LemmaWSDK implements LemmaSDKI {
    private boolean isConfigured = false;
    private DexUpdater dexUpdater;

    public static LemmaWSDK instance = new LemmaWSDK();
    LemmaSDKI interfaceImpl;

    public static interface SDKInitListener {
        public void onInit();
    }

    public String getVersion() {
        return getWVersion()+"-"+getCoreVersion();
    }

    public String getWVersion() {
        return "1.0.0";
    }

    public String getCoreVersion() {
        return interfaceImpl.getVersion();
    }

    @Override
    public void init(Context context, LMSDKInitializationCallback callback) {
        if (isConfigured) {
            callback.onCompletion(null);
            return;
        }
        isConfigured = true;
        LMWLogger.plant(new LMWLogger.DebugTree());
        LMWLog.i("Loading runtime SDK");

        LMDexClassLoader.getInstance().load(context, dexLoadingTask(context),new LMDexClassLoader.CompletionBlock() {
            @Override
            public void onLoad(Error error) {
                interfaceImpl = LMDexClassLoader.getInstance().LMLemmaSDKImpl();
                interfaceImpl.init(context);
                LMWLog.i("Runtime SDK loaded - v"+getVersion());
                callback.onCompletion(null);

                // Schedule dex update at defined interval
                dexUpdater = new DexUpdater(context);
                dexUpdater.schedule();
            }
        });
    }

    private DexLoadingTask dexLoadingTask(Context context) {

        String internalDataDirectory = context.getFilesDir().getAbsolutePath();
        String basePath = internalDataDirectory+"/sdk";

        String updatePath = basePath+"/update/lssdk.dex";
        String sdkPath = basePath+"/lssdk.dex";

        InputStream is =
                context.getResources().openRawResource(R.raw.lssdk);

        return new DexLoadingTask(is,new File(updatePath), new File(sdkPath));
    }


    @Override
    public void init(Context context) {

    }

    private LemmaWSDK() {
    }
}
