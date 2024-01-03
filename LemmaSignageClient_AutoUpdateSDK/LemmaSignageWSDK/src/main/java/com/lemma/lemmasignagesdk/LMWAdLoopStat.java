package com.lemma.lemmasignagesdk;

import com.lemma.lemmasignagesdk.api.LMAdLoopStatI;

public class LMWAdLoopStat implements LMAdLoopStatI {

    LMAdLoopStatI interfaceImpl;

    public boolean isLoopEmpty() { return interfaceImpl.isLoopEmpty(); }
    public int getCurrentAdIndex() {
        return interfaceImpl.getCurrentAdIndex();
    }
    public int getCurrentAdLoopLength() {
        return interfaceImpl.getCurrentAdLoopLength();
    }
    public String getCurrentAdCreativeName(){return interfaceImpl.getCurrentAdCreativeName();}
    public float getCurrentAdDuration(){return interfaceImpl.getCurrentAdDuration();}
    public String toString(){return  interfaceImpl.toString();}
}
