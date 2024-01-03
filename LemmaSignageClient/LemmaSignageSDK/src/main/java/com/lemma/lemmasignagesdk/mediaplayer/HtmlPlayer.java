package com.lemma.lemmasignagesdk.mediaplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.webkit.ConsoleMessage;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.common.LMTimer;
import com.lemma.lemmasignagesdk.views.LMWebView;

import java.util.Timer;
import java.util.TimerTask;

public class HtmlPlayer implements MediaPlayerI {

    private LMTimer timer;
    private final LMWebView lmWebView;
    private MediaPlayerListenerI playerListener;
    private Integer duration = 5;
    private boolean isFinishedAlready = false;

    public HtmlPlayer(Context context) {

        lmWebView = new LMWebView(context) {
            @Override
            protected void launchLandingPage() {

            }
        };

        lmWebView.getSettings().setJavaScriptEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            lmWebView.getSettings().setMediaPlaybackRequiresUserGesture(false);
        }
        lmWebView.setSoundEffectsEnabled(true);
        lmWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
                LMLog.i(consoleMessage.message());
                return super.onConsoleMessage(consoleMessage);
            }

        });
        lmWebView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (!isFinishedAlready) {
                    isFinishedAlready = true;
                    playerListener.onPrepared();
                }

            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                playerListener.onError(new Error("description"), false);
            }
        });
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    @Override
    public void setPlayerListener(MediaPlayerListenerI playerListener) {
        this.playerListener = playerListener;
    }

    @Override
    public void loadMedia(Uri uri) {
        lmWebView.loadUrl(uri.toString());
    }

    public void loadMedia(String script) {
        lmWebView.loadDataWithBaseURL("http://lemmadigital.com/", script, "text/html", "UTF-8", null);
    }

    @Override
    public void startPlayback() {
        scheduleTaskforDuration();
    }

    private void scheduleTaskforDuration() {
        timer = LMTimer.TimerBuilder.aTimer()
                .withCallAfterSeconds(duration)
                .withCallable(new LMTimer.Callable() {
                    @Override
                    public void call(boolean isOnMainThread) {
                        playerListener.onCompleted();
                    }
                }).build();
        timer.start();
    }

    @Override
    public void stop() {
        if (lmWebView != null) {
            lmWebView.stopLoading();
            lmWebView.destroy();
        }
    }

    @Override
    public ViewGroup playerView() {
        return lmWebView;
    }
}
