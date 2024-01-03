package com.lemma.lemmasignageclient.ui.test;

import android.app.Activity;

public class TestActivity extends Activity {

//    private FrameLayout frameLayout;
//    private SchedulePlayer schedulePlayer;
//    private SchedulePlayerActivityBinding binding;
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        schedulePlayer.destroy();
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        binding = SchedulePlayerActivityBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
//        try {
//            initAndStartSchedulePlayback();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    private void initAndStartSchedulePlayback() throws Exception {
//
//        AdGrpScheduleManager manager = new AdGrpScheduleManager();
//
//        String pubId = AppConfig.instance.getPublisherId();
//        String auId = AppConfig.instance.getAdunitId();
//        LMAdRequest request = new LMAdRequest(pubId,auId);
//        request.setMap(AppConfig.instance.getCustomParams());
//
//        manager.fetchSchedule(request, new AdGrpScheduleManager.CompletionCallback<Schedule>() {
//            @Override
//            public void onComplete(Error error, Schedule obj) {
//                renderLayout( obj);
//            }
//        });
//    }
//
//    private void renderLayout(Schedule schedule) {
//
//        ArrayList<ScheduleAdItemGrp> itemGrps = schedule.getAdGroups(1,1);
//
//        ScheduleAdItemWrapperGrp scheduleAdItemWrapper = ScheduleAdItemWrapperGrp.ScheduleAdItemWrapperGrpBuilder.aScheduleAdItemWrapperGrp()
//                .withItemGrp(itemGrps.get(0)).build();
//        scheduleAdItemWrapper.fetch(new ScheduleAdItemWrapperGrp.ScheduleAdItemWrapperGrpFetchCallback() {
//            @Override
//            public void onCompletion(Error error, ScheduleAdItemWrapperGrp scheduleAdItemWrapperGrp) {
//                renderAdGrpLayout(scheduleAdItemWrapperGrp);
//            }
//        });
//    }
//
//    private void renderAdGrpLayout(ScheduleAdGrp adItemWrapperGrp) {
//        frameLayout = binding.adLinearContainer;
//        ScheduleAdGroupPlayerView view = new ScheduleAdGroupPlayerView(this);
//        view.loadAd(adItemWrapperGrp);
//        LMUtils.attachToParentAndMatch(view, frameLayout);
//    }
}
