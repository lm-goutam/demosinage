package com.lemma.lemmasignageclient.common;

import android.app.Application;

import com.appspector.sdk.AppSpector;
import com.appspector.sdk.Builder;
import com.lemma.lemmasignageclient.appspector.AppspectorConfig;
import com.lemma.lemmasignageclient.appspector.RestartAppCommand;

public class LSCApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppManager.getInstance().setup(this);

//        Builder builder = AppSpector
//                .build(this);
//
//        AppspectorConfig.attachMonitors(builder);
//        //TODO: get new paid account & replace the key
//        builder.addCustomCommandsMonitor().run("android_YjIyZDQ3NTUtZGUwNS00NjFhLWJmNmMtYTNkNDYzYThiY2Rl");
//
//        AppSpector.shared().setMetadataValue(AppSpector.METADATA_KEY_DEVICE_NAME, AppspectorConfig.customDeviceName());
//        AppSpector.shared().commands().register(RestartAppCommand.class, new RestartAppCommand.RestartAppCommandExecutor(getApplicationContext()));
    }


}
