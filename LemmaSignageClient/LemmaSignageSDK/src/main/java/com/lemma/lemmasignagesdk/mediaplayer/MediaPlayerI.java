package com.lemma.lemmasignagesdk.mediaplayer;

import android.net.Uri;
import android.view.ViewGroup;

public interface MediaPlayerI {

    void setPlayerListener(MediaPlayerListenerI playerListener);

    void loadMedia(Uri uri);

    void loadMedia(String script);

    void startPlayback();

    void stop();

    ViewGroup playerView();
}
