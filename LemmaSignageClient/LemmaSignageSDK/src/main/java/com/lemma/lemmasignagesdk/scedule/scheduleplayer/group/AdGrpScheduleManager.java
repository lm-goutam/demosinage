package com.lemma.lemmasignagesdk.scedule.scheduleplayer.group;

import com.lemma.lemmasignagesdk.LMAdRequest;
import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.common.LMUtils;
import com.lemma.lemmasignagesdk.live.manager.NetworkStatusMonitor;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.Schedule;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.ScheduleAdItemGrp;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.common.ScheduleAdGrp;

import java.util.ArrayList;
import java.util.Date;

public class AdGrpScheduleManager {

    private final APIResponseRepository responseRepository = new APIResponseRepository();
    private Integer preDownloadScheduleItemCount = 5;
    private Integer currentIndex = -1;
    private Schedule schedule;
    private LMAdRequest lmAdRequest;
    private NetworkStatusMonitor networkStatusMonitor;

    public void setNetworkStatusMonitor(NetworkStatusMonitor networkStatusMonitor) {
        this.networkStatusMonitor = networkStatusMonitor;
    }

    public void setPreDownloadScheduleItemCount(Integer preDownloadScheduleItemCount) {
        this.preDownloadScheduleItemCount = preDownloadScheduleItemCount;
    }

    public void destroy() {
        //TODO: Apply cleanup
    }

    public void refreshSchedule(AdGrpScheduleManager.CompletionCallback<Schedule> completionCallback) {
        if (!validatePreconditions(completionCallback)) {
            return;
        }

        fetchSchedule(new AdGrpScheduleManager.CompletionCallback<String>() {
            @Override
            public void onComplete(Error error, String response) {

                if (error != null) {
                    LMLog.e(error.getLocalizedMessage());
                } else {
                    process(response, completionCallback);
                }
            }
        });
    }

    private void fetchSchedule(AdGrpScheduleManager.CompletionCallback<String> completionCallback) {
        responseRepository.getSchedule(lmAdRequest, new APIResponseRepository.CompletionCallback<String>() {
            @Override
            public void onComplete(Error error, String response) {
                completionCallback.onComplete(error, response);
            }
        });

//        Request request = new ScheduleRequestBuilder()
//                .setUrl(Constants.SCHEDULE_API)
//                .setRequest(lmAdRequest)
//                .build();
//        handler.post(request, new NetworkHandler.CompletionCallback() {
//            @Override
//            public void onComplete(Error error, String response) {
//                completionCallback.onComplete(error,response);
//            }
//        });
    }

    private boolean validatePreconditions(AdGrpScheduleManager.CompletionCallback<Schedule> completionCallback) {
        if (networkStatusMonitor != null &&
                !networkStatusMonitor.isNetworkConnected()) {
            completionCallback.onComplete(new Error("No internet"), null);
            return false;
        }
        return true;
    }

    public void fetchSchedule(LMAdRequest lmAdRequest,
                              AdGrpScheduleManager.CompletionCallback<Schedule> completionCallback) {
        if (!validatePreconditions(completionCallback)) {
            return;
        }

        this.lmAdRequest = lmAdRequest;
        fetchSchedule(new AdGrpScheduleManager.CompletionCallback<String>() {
            @Override
            public void onComplete(Error error, String response) {

                if (error != null) {
                    LMLog.e(error.getLocalizedMessage());
                } else {
                    process(response, completionCallback);
                }
            }
        });
    }

    private void process(String response,
                         AdGrpScheduleManager.CompletionCallback<Schedule> completionCallback) {
        Schedule schedule = Schedule.parse(response);
        Error error = schedule.validate();
        if (error != null) {
            completionCallback.onComplete(error, null);
        } else if (schedule != null) {
            this.schedule = schedule;
            completionCallback.onComplete(null, schedule);
        } else {
            completionCallback.onComplete(new Error("No schedule found"),
                    null);
        }
    }

    private Integer getCalibratedGrpIndex() {
        Date currentTime = LMUtils.getCurrentTime();
        for (int i = 0; i < schedule.getScheduleAdItemGrps().size(); i++) {
            Date scheduleDate = schedule.getScheduleAdItemGrps().get(i).getStartTime();
            if (scheduleDate.after(currentTime)) {
                Integer index = i - 1;
                if (index < 0) {
                    index = 0;
                }
                return index;
            }
        }
        return -1;
    }

    public void fetchNextAds(AdGrpScheduleManager.CompletionCallback<ArrayList<ScheduleAdGrp>> completionCallback) {

        if (this.currentIndex == -1) {
            this.currentIndex = getCalibratedGrpIndex();
            LMLog.i("Selected Start Index %d", this.currentIndex);

            if (this.currentIndex == -1) {
                completionCallback.onComplete(new Error("Unable to calibrate the schedule"), null);
                return;
            }
        }
        final ArrayList<ScheduleAdItemGrp> ads = this.schedule.getAdGroups(this.currentIndex, preDownloadScheduleItemCount);
        AdGrpPrefetcher adPrefetcher = new AdGrpPrefetcher();
//        adPrefetcher.prefetch(ads, new AdGrpPrefetcher.CompletionCallback() {
//            @Override
//            public void onComplete(Error error, ArrayList<ScheduleAdItemWrapperGrp> ads) {
//                completionCallback.onComplete(error, ads);
//            }
//        });
        adPrefetcher.prefetch(ads, new AdGrpPrefetcher.CompletionCallback() {
            @Override
            public void onComplete(Error error, ArrayList<ScheduleAdGrp> ads) {
                completionCallback.onComplete(error, ads);
            }
        });
        this.currentIndex = currentIndex + preDownloadScheduleItemCount;
    }

    public interface CompletionCallback<T> {
        void onComplete(Error error, T obj);
    }
}
