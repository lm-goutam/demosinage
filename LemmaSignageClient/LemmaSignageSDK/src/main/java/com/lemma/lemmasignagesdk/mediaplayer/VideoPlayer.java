package com.lemma.lemmasignagesdk.mediaplayer;

import android.content.Context;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;

import com.lemma.lemmasignagesdk.common.LMTimer;

import java.util.Timer;
import java.util.TimerTask;

public class VideoPlayer implements MediaPlayerI, LMVideoPlayerView.LMVideoPlayerListener {

    private LMTimer timer;

    private final LMVideoPlayerView lmVideoPlayerView;
    private Integer duration = 15;
    private MediaPlayerListenerI playerListener;

    public VideoPlayer(Context context) {
        this.lmVideoPlayerView = new LMVideoPlayerView(context);
        this.lmVideoPlayerView.setListener(this);
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
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

    public void setPlayerListener(MediaPlayerListenerI playerListener) {
        this.playerListener = playerListener;
    }

    @Override
    public void loadMedia(Uri uri) {
        this.lmVideoPlayerView.load(uri);
    }

    @Override
    public void loadMedia(String script) {
    }

    @Override
    public void startPlayback() {
        this.lmVideoPlayerView.play();
        scheduleTaskforDuration();
    }

    @Override
    public ViewGroup playerView() {
        return this.lmVideoPlayerView;
    }

    @Override
    public void onReady(LMVideoPlayerView player) {
        this.playerListener.onPrepared();
    }

    @Override
    public void onFailure(int errorCode, String errorMessage) {
        this.playerListener.onError(new Error(errorMessage), true);
    }

    @Override
    public void onBufferUpdate(int buffer) {

    }

    @Override
    public void onCompletion() {
    }

    @Override
    public void onStart() {
        playerListener.onStarted();
    }

    @Override
    public void onPause() {

    }

    @Override
    public void onProgressUpdate(int seekPosition) {

    }

    @Override
    public void stop() {
        if (timer != null) {
            timer.cancel();
        }

        if (lmVideoPlayerView != null) {
            lmVideoPlayerView.destroy();
        }
    }

}
