package com.lemma.lemmasignageclient.ui.placeholder;

import android.content.Context;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.VideoView;

import androidx.annotation.NonNull;

import com.lemma.lemmasignageclient.common.AppUtil;

public class PlaceholderView extends FrameLayout {

    private VideoView videoView;

    public PlaceholderView(@NonNull Context context) {
        super(context);
        videoView = new VideoView(context);
        setBackgroundColor(Color.RED);
        RelativeLayout relativeLayout = new RelativeLayout(getContext());
        RelativeLayout.LayoutParams relativeParams = new RelativeLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        relativeParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        relativeLayout.addView(videoView, relativeParams);

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.gravity = Gravity.CENTER;
        addView(relativeLayout, params);
    }

    public void play(){
        videoView.setVideoURI(AppUtil.getDefaultAdUri(getContext()));
        videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {

                if (videoView != null) {
                    videoView.start();
                }
            }
        });
        videoView.start();
    }

    public void stopAndClean(){
        videoView.stopPlayback();
        videoView = null;
        ((ViewGroup) getParent()).removeView(this);
    }

    public static PlaceholderView attachAndPlay(ViewGroup parent) {
        PlaceholderView placeholderView = new PlaceholderView(parent.getContext());
        AppUtil.attachToParentAndMatch(placeholderView, parent);
        placeholderView.play();
        return placeholderView;
    }
}
