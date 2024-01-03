package com.lemma.lemmasignagesdk.scedule.scheduleplayer.group;

import com.lemma.lemmasignagesdk.LMAdRequest;
import com.lemma.lemmasignagesdk.common.LMUtils;
import com.lemma.lemmasignagesdk.common.network.NetworkHandler;
import com.lemma.lemmasignagesdk.common.network.Request;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.Constants;
import com.lemma.lemmasignagesdk.scedule.scheduleplayer.ScheduleRequestBuilder;

public class APIResponseRepository {
    private final NetworkHandler handler = new NetworkHandler();
    private final boolean connected = true;

    public void getSchedule(LMAdRequest lmAdRequest,
                            CompletionCallback<String> completionCallback) {

        if (!connected) {
            //TODO: save live response in files & use it if network is unavailable
            String resp = LMUtils.getResourceFileContent("response1.json");
            completionCallback.onComplete(null, resp);
            return;
        }
        Request request = new ScheduleRequestBuilder()
                .setUrl(Constants.SCHEDULE_API)
                .setRequest(lmAdRequest)
                .build();
        handler.post(request, new NetworkHandler.CompletionCallback() {
            @Override
            public void onComplete(Error error, String response) {
                completionCallback.onComplete(error, response);
            }
        });
    }

    public interface CompletionCallback<T> {
        void onComplete(Error error, T obj);
    }
}
