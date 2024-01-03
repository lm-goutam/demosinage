package com.lemma.lemmasignageclient.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.lemma.lemmasignageclient.common.AppConfig;
import com.lemma.lemmasignageclient.common.AppUtil;
import com.lemma.lemmasignageclient.sdkinstantiator.InstanceCreator;
import com.lemma.lemmasignageclient.ui.live.Activity.HomeScreen;
import com.lemma.lemmasignageclient.ui.live.Activity.LoginActivity;
import com.lemma.lemmasignageclient.ui.shedule.SchedulePlayerActivity;
import com.lemma.lemmasignageclient.ui.test.TestActivity;
import com.lemma.lemmasignagesdk.api.LMSDKInitializationCallback;
import com.lemma.lemmasignagesdk.api.LemmaSDKI;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 101;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        checkPermissionsAdnRoute();
    }

    private void checkPermissionsAdnRoute() {
        if (false) {
            AppUtil.launchActivity(this, TestActivity.class);
            return;
        }
        if (!AppUtil.checkForAllPermissions(this)) {
            AppUtil.requestPermissions(this, AppUtil.permissionList(this));
        }else {
            routeToAppropriateActivity();
        }
    }

    private void route() {
        if (AppConfig.instance.isSyncScheduleNamespace()) {
            if (!AppConfig.instance.alreadySetup()) {
                AppUtil.launchActivity(this, SettingsActivity.class);
            }else {
                AppUtil.launchActivity(this, SchedulePlayerActivity.class);
            }
            finishAfterTransition();
        }else {
            if (!AppConfig.instance.alreadySetup()) {
                // Launch login
                AppUtil.launchActivity(this, LoginActivity.class);
            }else {
                // launch live player
                AppUtil.launchActivity(this, HomeScreen.class);
            }
            finishAfterTransition();
        }
    }
    private void routeToAppropriateActivity() {
        LemmaSDKI sdk = InstanceCreator.SDKInstance();
        sdk.init(this, new LMSDKInitializationCallback() {
            @Override
            public void onCompletion(Error error) {
                route();
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(0, 0);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {
                checkPermissionsAdnRoute();
            }
        }
    }

}