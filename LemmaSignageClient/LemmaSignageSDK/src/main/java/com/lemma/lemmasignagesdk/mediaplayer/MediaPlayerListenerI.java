package com.lemma.lemmasignagesdk.mediaplayer;

public interface MediaPlayerListenerI {

    void onPrepared();

    void onStarted();

    void onPause();

    void onResume();

    void onMute();

    void onUnmute();

    void onSkip();

    void onStop();

    void onCompleted();

    void onError(Error error, boolean loadTime);

}
