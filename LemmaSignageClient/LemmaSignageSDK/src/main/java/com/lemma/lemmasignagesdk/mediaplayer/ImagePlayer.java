package com.lemma.lemmasignagesdk.mediaplayer;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.lemma.lemmasignagesdk.common.LMTimer;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Timer;
import java.util.TimerTask;

public class ImagePlayer implements MediaPlayerI {

    private LMTimer timer;

    private MediaPlayerListenerI playerListener;
    private ImageView imageView;
    private FrameLayout containerLayout;
    private Uri uri;
    private Integer duration = 5;

    public ImagePlayer(Context context) {
        setupImageView(context);
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public void setPlayerListener(MediaPlayerListenerI playerListener) {
        this.playerListener = playerListener;
    }

    @Override
    public void loadMedia(Uri uri) {
        this.uri = uri;
        playerListener.onPrepared();
    }

    @Override
    public void loadMedia(String script) {
    }

    @Override
    public void startPlayback() {
        // Do nothing
        Picasso picasso = Picasso.with(imageView.getContext());
//        picasso.setIndicatorsEnabled(true);
        picasso.load(uri).into(imageView, new Callback() {
            @Override
            public void onSuccess() {
                playerListener.onStarted();
            }

            @Override
            public void onError() {

            }
        });
        scheduleTaskforDuration();
    }

    @Override
    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
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
    public ViewGroup playerView() {
        return containerLayout;
    }

    //Plain image rendering
    private void setupImageView(Context context) {
        containerLayout = new FrameLayout(context);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;

        imageView = new ImageView(context);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setBackgroundColor(Color.BLACK);

        containerLayout.addView(imageView, params);
    }
}
