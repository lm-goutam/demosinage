package com.lemma.lemmasignagesdk.api;

public interface LMAdLoopStatI {

    public boolean isLoopEmpty();
    public int getCurrentAdIndex();
    public int getCurrentAdLoopLength();
    public String getCurrentAdCreativeName();
    public float getCurrentAdDuration();
    public String toString();
}
