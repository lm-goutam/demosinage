package com.lemma.lemmasignagesdk.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.lemma.lemmasignagesdk.common.logger.LMWLog;
import com.lemma.lemmasignagesdk.common.LMSDKStat;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DexUpdater {
    private ExecutorService executor = Executors.newSingleThreadExecutor();

    private Context context;
    private LMSDKStat stat;

    public DexUpdater(Context context){
        this.context = context;
    }

    private void scheduleAgain(Integer seconds, Runnable runnable) {
        Timer timerObj = new Timer();
        TimerTask timerTaskObj = new TimerTask() {
            public void run() {
                new Handler(Looper.getMainLooper()).post(runnable);
            }
        };
         timerObj.schedule(timerTaskObj, 1000 * seconds);
    }

    public void schedule() {

        LMSDKStat.fetchStatFromNetwork(context, new LMSDKStat.SDKStatListener() {
            @Override
            public void onResult(LMSDKStat stat, Error error) {

                if (error != null) {
                    // Try after 55 mins
                    scheduleAgain(55*60, new Runnable() {
                        @Override
                        public void run() {
                            schedule();
                        }
                    });
                    LMWLog.i("Failed to fetch update spec: "+error);
                    return;
                }

                DexUpdater.this.stat = stat;
                LMSDKStat savedState = LMSDKStat.getSavedStat(context);
                if (savedState == null) {
                    initiateUpdate();
                }else{
                    if (savedState.shouldUpdate(stat)) {
                        initiateUpdate();
                    }else {
                        scheduleAgain(stat.getUpdateAfter(), new Runnable() {
                            @Override
                            public void run() {
                                schedule();
                            }
                        });
                    }
                }
            }
        });
    }

    private String basePath() {
        String internalDataDirectory = context.getFilesDir().getAbsolutePath();
        return internalDataDirectory+"/sdk";
    }

    private void initiateUpdate(){
        String basePath = basePath();
        String sdkTempUpdatePath = basePath+"/update/lssdk_wip.dex";

        this.context = context.getApplicationContext();
        LMWLog.i("Downloading SDK update");
        DownloadTask downloadTask = new DownloadTask(context,new File(sdkTempUpdatePath),
                (error, file) -> {

            validateAndSave(error, file);
            scheduleAgain(stat.getUpdateAfter(), new Runnable() {
                @Override
                public void run() {
                    schedule();
                }
            });
        });
        downloadTask.urlString = stat.coreSDKUrl;
        downloadTask.execute("");
    }

    private void validateAndSave(Error error, File file) {
        executor.execute(new Runnable() {
            @Override
            public void run() {

                if (error != null) {
                    LMWLog.e("Failed to download update with error - "+error.getLocalizedMessage());
                }else {

                    boolean status = MD5.checkMD5(stat.coreSDKBinaryHash, file);
                    if (status) {
                        String sdkUpdatePath = basePath()+"/update/lssdk.dex";
                        file.renameTo(new File(sdkUpdatePath));
                        LMWLog.i("SDK Update downloaded at path - %s",sdkUpdatePath);
                        LMSDKStat.savedStat(stat ,context);
                    }else {
                        LMWLog.e("SDK Update checksum failed");
                    }

                }
            }
        });
    }

}
