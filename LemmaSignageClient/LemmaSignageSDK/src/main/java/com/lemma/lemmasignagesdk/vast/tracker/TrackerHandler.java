package com.lemma.lemmasignagesdk.vast.tracker;

import android.os.Handler;
import android.os.Looper;
import android.webkit.ConsoleMessage;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.common.LMUtils;
import com.lemma.lemmasignagesdk.live.manager.NetworkStatusMonitor;
import com.lemma.lemmasignagesdk.vast.VastBuilder.Tracker;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class TrackerHandler {

    private static final String TAG = "TrackerHandler";
    final SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
    private final NetworkStatusMonitor monitor;
    private final String trackerJSHandler = "<html><head><script>function fireURL(url) {" +
            "        new Image().src = url;" +
            "console.log('Tracking impression url - '+url);" +
            "}</script></head><body></body></html>";
    public TrackerDBHandler trackerDBHandler;
    public TimeZone timeZone;
    public Boolean executeImpressionInWebContainer = false;
    OkHttpClient client = null;
    android.text.format.DateFormat df = new android.text.format.DateFormat();
    private WebView trackerWebContainer = null;

    public TrackerHandler(NetworkStatusMonitor monitor, WebView trackerWebContainer) {
        this.monitor = monitor;

        client = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        this.trackerWebContainer = trackerWebContainer;
        this.trackerWebContainer.setWebViewClient(new WebViewClient() {
            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                LMLog.i(TAG, "Error - " + errorCode + description);
                super.onReceivedError(view, errorCode, description, failingUrl);
            }

          /*  @Override
            public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                handler.proceed();
            }*/
        });
        this.trackerWebContainer.getSettings().setJavaScriptEnabled(true);

        this.trackerWebContainer.setWebChromeClient(new WebChromeClient() {

            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                LMLog.i(TAG, consoleMessage.message());
                return true;
            }
        });
        this.trackerWebContainer.loadData(trackerJSHandler, "text/html", "UTF-8");
    }

    public void sendImpression(String url) {
        executeTracker(url);
    }

    private void executeTracker(String url) {

        if (url == null) {
            return;
        }

        if (!URLUtil.isValidUrl(url)) {
            LMLog.w(TAG, "Invalid impression URL " + url);
            return;
        }

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                LMLog.d(TAG, "Tracking request failed with err: " + e.getLocalizedMessage() + "for [" + call.request().toString() + "]");

                // Add url again for retry in persistent queue
                final String url = call.request().url().toString();
                if (url != null && url.length() > 0 && trackerDBHandler != null) {

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            trackerDBHandler.add(url);
                        }
                    });
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                LMLog.d(TAG, "Tracking request completed for [" + call.request().toString() + "]");
            }
        });
    }

    public void sendRTBImpression(String url) {
        if (url != null && URLUtil.isValidUrl(url)) {
            String jsURL = String.format("javascript:fireURL('%s')", url);
            this.trackerWebContainer.loadUrl(jsURL);
        } else {
            LMLog.w(TAG, "Invalid impression URL " + url);
        }
    }

    public void sendRTBImpression(final List<Tracker> impressionList) {
        if (executeImpressionInWebContainer && this.trackerWebContainer != null) {
            for (Tracker impression : impressionList) {
                String url = impression.getUrl();
                sendRTBImpression(url);
            }
        } else {
            sendImpression(impressionList);
        }
    }

    public void sendImpression(final List<Tracker> impressionList) {

        if (impressionList == null || impressionList.isEmpty()) {
            return;
        }

        if (monitor != null && !monitor.isNetworkConnected()) {
            // Save for future execution
            for (Tracker impression : impressionList) {
                try {
                    String url = impression.getUrl();

                    if (url != null && url.length() > 0 && trackerDBHandler != null) {

                        TimeZone timeZone = this.timeZone;
                        if (timeZone == null) {
                            timeZone = TimeZone.getTimeZone("UTC");
                            //  timeZone = TimeZone.getDefault();
                        }
                        String tz = timeZone.getID();
                        sdf.setTimeZone(timeZone);
                        String ts = sdf.format(new Date());
                        // String ts = String.valueOf(df.format("yyyyMMddHHmmss", new java.util.Date()));
                        String updatedUrl = LMUtils.replaceUriParameter(url, "ts", ts);
                        LMLog.i(TAG, "Saving tracker " + url + " in persistent queue with updated time stamp URL " + impression.getUrl() + " bcoz network is not available");
                        LMLog.i(TAG, updatedUrl);
                        String offline_tracker = updatedUrl + "&offline=1&tz=" + tz;
                        LMLog.i(TAG, offline_tracker);
                        trackerDBHandler.add(offline_tracker);
                    }
                } catch (Exception e) {
                    LMLog.e(TAG, "Failed to save tracker " + impression.getUrl());
                }
            }
        } else {
            for (Tracker impression : impressionList) {
                executeTracker(impression.getUrl());
            }
        }
    }

    private void sendImpressionList(final List<String> impressionList) {

        if (impressionList == null || impressionList.isEmpty()) {
            LMLog.d(TAG, "ImpressionList is invalid");
            return;
        }

        for (String url : impressionList) {
            executeTracker(url);
        }
    }

    public void sendImpressionFromQueue() {

        if (monitor.isNetworkConnected()) {
            ArrayList<String> trackers = trackerDBHandler.popAll();
            LMLog.i(TAG, "Tracking Impression backlog count " + trackers.size());
            sendImpressionList(trackers);
        }
    }
}
