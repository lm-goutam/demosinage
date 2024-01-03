package com.lemma.lemmasignageclient.appspector;

import com.appspector.sdk.Builder;
import com.appspector.sdk.monitors.log.LogMonitor;
import com.lemma.lemmasignageclient.common.AppConfig;

public class AppspectorConfig {

    public static final String KEY_MONS_FLAG = "monsFlag";

    public static String customDeviceName() {
        return AppConfig.instance.getDeviceId()+"_"+AppConfig.instance.getPublisherId()+"_"+AppConfig.instance.getAdunitId();
    }

    public static void setMonsFlag(String flagString) {
        AppConfig.instance.setValueForKey(KEY_MONS_FLAG,flagString);
    }

    public static void attachMonitors(Builder builder){
        String monsFlag = AppConfig.instance.getValueForKey(KEY_MONS_FLAG,"0");

        Integer monsFlagInt = Integer.parseInt(monsFlag);

        /*
                this.addMonitor(new EnvironmentMonitor(this.c));
        this.addMonitor(new PerformanceMonitor(this.c));
        this.addSharedPreferenceMonitor();
        this.addFileSystemMonitor();
        this.addMonitor(new LogMonitor());
        this.addMonitor(new HttpMonitor());
        this.addMonitor(new SQLiteMonitor());
        this.addMonitor(new ScreenshotMonitor());
        this.addCustomCommandsMonitor();
        this.a();
        this.addMonitor(new CustomEventsMonitor());

         */
        if ((monsFlagInt & 1) != 0) {
            builder.addMonitor(new LogMonitor());
        }
        if ((monsFlagInt & 2) != 0) {
            builder.addSharedPreferenceMonitor();
        }

        if ((monsFlagInt & 4) != 0) {
            builder.addPerformanceMonitor();
        }

        if ((monsFlagInt & 8) != 0) {
            builder.addScreenshotMonitor();
        }

        if ((monsFlagInt & 16) != 0) {
            builder.addHttpMonitor();
        }

        if ((monsFlagInt & 32) != 0) {
            builder.addEnvironmentMonitor();
        }

        if ((monsFlagInt & 64) != 0) {
            builder.addFileSystemMonitor();
        }
    }
}
