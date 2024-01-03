
package com.lemma.lemmasignageclient.ui.live.Activity;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.FrameLayout;

import com.lemma.lemmasignageclient.addon.UncaughtExceptionHandler;
import com.lemma.lemmasignageclient.common.AppConfig;
import com.lemma.lemmasignageclient.common.AppUtil;
import com.lemma.lemmasignageclient.common.logger.Applogger;
import com.lemma.lemmasignageclient.databinding.ActivityHomeBinding;
import com.lemma.lemmasignageclient.sdkinstantiator.InstanceCreator;
import com.lemma.lemmasignageclient.ui.live.AdLoader.AdLoader;
import com.lemma.lemmasignagesdk.api.LemmaSDKI;


public class HomeScreen extends Activity {

    ActivityHomeBinding binding;

    private AdLoader adLoader = null;
    private String adid = null;

    private void installGlobalExceptionHandler() {
        Thread.UncaughtExceptionHandler exceptionHandler = Thread.getDefaultUncaughtExceptionHandler();
        UncaughtExceptionHandler uncaughtExceptionHandler = new UncaughtExceptionHandler(this);
        uncaughtExceptionHandler.exceptionHandler = exceptionHandler;
        Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        installGlobalExceptionHandler();

        LemmaSDKI sdk = InstanceCreator.SDKInstance();
        sdk.init(this);

//        LMLogger.plant(new LMLogger.DebugTree());

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);

        FrameLayout frameLayout = binding.adLinearContainer;
        Rect rect = AppConfig.instance.viewFrame();
        AppUtil.applyCoordinateToLayout(frameLayout, rect);
        this.adid = AppConfig.instance.getAdunitId();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(adLoader != null){
            return;
        }

        final HomeScreen activity = this;
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    adLoader = new AdLoader(binding.adLinearContainer);
                    adLoader.start();
                } catch (Exception e) {
                    Applogger.e(e.toString());
                }
            }
        }, 400);
    }

    @Override
    protected void onDestroy() {
        if (adLoader != null) {
            adLoader.destroy();
            adLoader = null;
            Applogger.d("HomeScreen-Destroy","HomeScreen-Destroy");
        }
        super.onDestroy();
    }
}