package com.lemma.lemmasignagesdk.api;

public interface LMAdLoopStatI {

    boolean isLoopEmpty();

    int getCurrentAdIndex();

    int getCurrentAdLoopLength();

    String getCurrentAdCreativeName();

    float getCurrentAdDuration();

    String toString();
}
