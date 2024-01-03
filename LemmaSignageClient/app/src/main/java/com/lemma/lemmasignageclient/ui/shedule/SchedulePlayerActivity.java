package com.lemma.lemmasignageclient.ui.shedule;

import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;

import com.lemma.lemmasignageclient.common.AppConfig;
import com.lemma.lemmasignageclient.common.AppUtil;
import com.lemma.lemmasignageclient.common.LMTimer;
import com.lemma.lemmasignageclient.common.logger.Applogger;
import com.lemma.lemmasignageclient.databinding.SchedulePlayerActivityBinding;
import com.lemma.lemmasignageclient.sdkinstantiator.InstanceCreator;
import com.lemma.lemmasignageclient.ui.placeholder.PlaceholderView;
import com.lemma.lemmasignagesdk.api.LMAdRequestI;
import com.lemma.lemmasignagesdk.api.LemmaSDKI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerConfigI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerEventListener;
import com.lemma.lemmasignagesdk.api.SchedulePlayerI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerPreparationListener;

public class SchedulePlayerActivity extends Activity implements SchedulePlayerEventListener {

    private FrameLayout frameLayout;
    private SchedulePlayerI schedulePlayer;
    private SchedulePlayerActivityBinding binding;
    private PlaceholderView placeholderView;
    private LMAdRequestI mRequest;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        schedulePlayer.destroy();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = SchedulePlayerActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        try {
            initAndStartSchedulePlayback();
        }catch (Exception exception) {
            AppUtil.showMsg(this, "Failed with exception: "+exception.getLocalizedMessage());
        }
    }

    private void initAndStartSchedulePlayback() throws Exception {

        LemmaSDKI sdk = InstanceCreator.SDKInstance();
        sdk.init(this);

//        LMLogger.plant(new LMLogger.DebugTree());

        // 14503,178
        // PID 844 AID:18256
        // PID 844 AID:18257
        String pubId = AppConfig.instance.getPublisherId();
        String auId = AppConfig.instance.getAdunitId();

        LMAdRequestI request = InstanceCreator.adRequestInstance(pubId, auId);
        request.setAdServerBaseURL(AppUtil.scheduleAdsAPI());
        request.setMap(AppConfig.instance.getCustomParams());

        frameLayout = binding.adLinearContainer;

        Rect rect = AppConfig.instance.viewFrame();
        AppUtil.applyCoordinateToLayout(frameLayout, rect);

        SchedulePlayerConfigI config = InstanceCreator.schedulePlayerConfigInstance();
        config.setScheduleRefreshTimeInSeconds(AppConfig.instance.getScheduleRefreshTime());
        config.setFallbackVideoUri(AppUtil.getDefaultAdUri(this));

        schedulePlayer = InstanceCreator.schedulePlayerInstance(frameLayout, config);
        placeholderView = PlaceholderView.attachAndPlay(frameLayout);

        mRequest = request;
        startSchedulePreparation();
    }

    private void startSchedulePreparation() {


        //        ProgressDialog mDialog = AppUtil.showDialog(this,"Fetching the schedule ...");
        Applogger.i("Fetching the schedule ...");
        schedulePlayer.prepare(mRequest,new SchedulePlayerPreparationListener() {
            @Override
            public void onSuccess() {
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
//                        AppUtil.hideDialog(mDialog,0);
                        schedulePlayer.setSchedulePlayerEventListener(SchedulePlayerActivity.this);
                        schedulePlayer.play();
                    }
                });
            }

            @Override
            public void onFailure(Error error) {
                Applogger.e(error.getLocalizedMessage());
                retrySchedulePreparation();
//                AppUtil.hideDialog(mDialog,0);
//                AppUtil.showMsg(SchedulePlayerActivity.this, error.getLocalizedMessage());
            }
        });
    }

    private void retrySchedulePreparation() {
        /// Re-try after 5 mins
        LMTimer.TimerBuilder.aTimer()
                .withCallAfterSeconds(60 * 5)
                .withCallable(new LMTimer.Callable() {
                    @Override
                    public void call(boolean isOnMainThread) {
                        startSchedulePreparation();
                    }
                })
                .build().start();
    }

    @Override
    public void onStarted() {
        Applogger.i("Stopping placeholder media playback & starting schedule content");
    }

    @Override
    public void onCalibrating(long seconds) {
        Applogger.i("Starting playback in "+seconds+" seconds");

        LMTimer currentTimer = LMTimer.TimerBuilder.aTimer()
                .withCallAfterSeconds(seconds)
                .withCallable(new LMTimer.Callable() {
                    @Override
                    public void call(boolean isOnMainThread) {
                        placeholderView.stopAndClean();
                    }
                }).build();
        currentTimer.start();
//        ProgressDialog mDialog = AppUtil.showDialog(this,"Starting playback in "+seconds+" seconds");
//        AppUtil.hideDialog(mDialog, seconds);
    }

    @Override
    public void onScheduleCompletion() {

    }
}