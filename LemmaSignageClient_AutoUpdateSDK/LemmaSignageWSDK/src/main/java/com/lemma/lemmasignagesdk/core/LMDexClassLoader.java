package com.lemma.lemmasignagesdk.core;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.view.ViewGroup;

import com.lemma.lemmasignagesdk.AdManagerCallback;
import com.lemma.lemmasignagesdk.common.logger.LMWLog;
import com.lemma.lemmasignagesdk.api.LMAdRequestI;
import com.lemma.lemmasignagesdk.api.LMConfigI;
import com.lemma.lemmasignagesdk.api.LMSharedVideoManagerI;
import com.lemma.lemmasignagesdk.api.LMVideoAdManagerI;
import com.lemma.lemmasignagesdk.api.LemmaSDKI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerConfigI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerI;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import dalvik.system.DexClassLoader;

public class LMDexClassLoader {

    public static final String COM_LEMMA_LEMMASIGNAGESDK_LEMMA_SDK = "com.lemma.lemmasignagesdk.LemmaSDK";
    public static final String COM_LEMMA_LEMMASIGNAGESDK_COMMON_LMWLOG = "com.lemma.lemmasignagesdk.common.LMWLog";
    public static final String COM_LEMMA_LEMMASIGNAGESDK_LMVIDEO_AD_MANAGER = "com.lemma.lemmasignagesdk.LMVideoAdManager";
    public static final String COM_LEMMA_LEMMASIGNAGESDK_LMCONFIG = "com.lemma.lemmasignagesdk.LMConfig";
    public static final String COM_LEMMA_LEMMASIGNAGESDK_LMAD_REQUEST = "com.lemma.lemmasignagesdk.LMAdRequest";
    public static final String COM_LEMMA_LEMMASIGNAGESDK_SCHEDULE_PLAYER = "com.lemma.lemmasignagesdk.SchedulePlayer";
    public static final String COM_LEMMA_LEMMASIGNAGESDK_SCHEDULE_PLAYER_CONFIG = "com.lemma.lemmasignagesdk.SchedulePlayerConfig";
    public static final String COM_LEMMA_LEMMASIGNAGESDK_LMSHARED_VIDEO_MANAGER = "com.lemma.lemmasignagesdk.LMSharedVideoManager";
    private static LMDexClassLoader singleInstance = null;
    DexClassLoader classLoader;
    private DexLoadingTask dexLoadingTask;

    // static method to create instance of Singleton class
    public static LMDexClassLoader getInstance() {
        if (singleInstance == null) {
            singleInstance = new LMDexClassLoader();
        }
        return singleInstance;
    }

    public void load(final Context context, DexLoadingTask dexLoadingTask, final CompletionBlock completionBlock) {

        this.dexLoadingTask = dexLoadingTask;
        dexLoadingTask.execute(new DexLoadingTask.CompletionCallback() {
            @Override
            public void onCompletion(Error error, File file) {

                if (error != null) {
                    completionBlock.onLoad(error);
                } else {
                    setupClassLoader(context, file);
                    completionBlock.onLoad(null);
                }
            }
        });
    }

    private void setupClassLoader(final Context context, final File dexFile) {
        ApplicationInfo info = context.getApplicationInfo();
        String dexPath = info.sourceDir;
        String dexOutputDir = info.dataDir;
        String libPath = info.nativeLibraryDir;
        classLoader = new DexClassLoader(dexFile.getAbsolutePath(), dexOutputDir, libPath, context.getClass().getClassLoader());
    }

    public DexClassLoader getClassLoader() {
        return classLoader;
    }

    public Class sdkClass() {
        String cls = COM_LEMMA_LEMMASIGNAGESDK_LEMMA_SDK;
        try {
            Class myClass = classLoader.loadClass(cls);
            return myClass;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public Class logClass() {
        String cls = COM_LEMMA_LEMMASIGNAGESDK_COMMON_LMWLOG;
        try {
            Class myClass = classLoader.loadClass(cls);
            return myClass;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean coreSDKReady() {
        return (classLoader != null);
    }

    public LMSharedVideoManagerI LMSharedVideoManagerImpl() {
        String cls = COM_LEMMA_LEMMASIGNAGESDK_LMSHARED_VIDEO_MANAGER;
        try {
            Class myClass = classLoader.loadClass(cls);
            Method method = myClass.getMethod("getInstance");
            Object o = method.invoke(null);
            return (LMSharedVideoManagerI) o;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SchedulePlayerConfigI SchedulePlayerConfig() {
        String cls = COM_LEMMA_LEMMASIGNAGESDK_SCHEDULE_PLAYER_CONFIG;
        try {
            Class myClass = classLoader.loadClass(cls);
            Constructor constructor = myClass.getConstructor();
            return (SchedulePlayerConfigI) constructor.newInstance();
        } catch (Exception e) {
            LMWLog.i(e.getLocalizedMessage());
        }
        return null;
    }

    public SchedulePlayerI SchedulePlayer(ViewGroup container, SchedulePlayerConfigI config) {
        String cls = COM_LEMMA_LEMMASIGNAGESDK_SCHEDULE_PLAYER;
        try {
            Class myClass = classLoader.loadClass(cls);
            Constructor<?>[] constructors = myClass.getConstructors();

            Constructor constructor = constructors[1];
            return (SchedulePlayerI) constructor.newInstance(container, config.getInnerIml());
        } catch (Exception e) {
            LMWLog.i(e.getLocalizedMessage());
        }
        return null;
    }

    public LMAdRequestI RequestImpl(String publisherId, String adUnitId) {
        String cls = COM_LEMMA_LEMMASIGNAGESDK_LMAD_REQUEST;
        try {
            Class myClass = classLoader.loadClass(cls);
            Constructor constructor = myClass.getConstructor(String.class, String.class);
            return (LMAdRequestI) constructor.newInstance(publisherId, adUnitId);
        } catch (Exception e) {
            LMWLog.i(e.getLocalizedMessage());
        }
        return null;
    }

    public LMConfigI LMConfigImpl() {
        String cls = COM_LEMMA_LEMMASIGNAGESDK_LMCONFIG;
        try {
            Class myClass = classLoader.loadClass(cls);
            Constructor constructor = myClass.getConstructor();
            return (LMConfigI) constructor.newInstance();
        } catch (Exception e) {
            LMWLog.i(e.getLocalizedMessage());
        }
        return null;
    }

    public LemmaSDKI LMLemmaSDKImpl() {
        String cls = COM_LEMMA_LEMMASIGNAGESDK_LEMMA_SDK;
        try {
            Class myClass = classLoader.loadClass(cls);
            Constructor constructor = myClass.getConstructor();
            return (LemmaSDKI) constructor.newInstance();
        } catch (Exception e) {
            LMWLog.i(e.getLocalizedMessage());
        }
        return null;
    }

    public LMVideoAdManagerI VideoAdManagerImpl(Context context,
                                                LMAdRequestI request,
                                                LMConfigI config,
                                                AdManagerCallback adManagerListener) {
        String cls = COM_LEMMA_LEMMASIGNAGESDK_LMVIDEO_AD_MANAGER;
        try {
            Class myClass = classLoader.loadClass(cls);
            Constructor constructor = myClass.getConstructor();
            LMVideoAdManagerI object = (LMVideoAdManagerI) constructor.newInstance();
            LMAdRequestI req = request;
            object.setupInstance(context, req, config, adManagerListener);
            return object;
        } catch (Exception e) {
            LMWLog.i(e.getLocalizedMessage());
        }
        return null;
    }

    public interface CompletionBlock {
        void onLoad(Error error);
    }
}
