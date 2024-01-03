package com.lemma.lemmasignagesdk;

import android.view.ViewGroup;

import com.lemma.lemmasignagesdk.api.LMAdRequestI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerConfigI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerEventListener;
import com.lemma.lemmasignagesdk.api.SchedulePlayerI;
import com.lemma.lemmasignagesdk.api.SchedulePlayerPreparationListener;
import com.lemma.lemmasignagesdk.core.LMDexClassLoader;

public class LMWSchedulePlayer implements SchedulePlayerI {

    SchedulePlayerI interfaceImpl;

    public LMWSchedulePlayer(ViewGroup container, SchedulePlayerConfigI config) {
        interfaceImpl = LMDexClassLoader.getInstance().SchedulePlayer(container, config);
    }

    @Override
    public void setSchedulePlayerEventListener(SchedulePlayerEventListener schedulePlayerEventListener) {
        interfaceImpl.setSchedulePlayerEventListener(schedulePlayerEventListener);
    }

    @Override
    public void destroy() {
        interfaceImpl.destroy();
    }

    @Override
    public void refreshSchedule() {
        interfaceImpl.refreshSchedule();
    }

    @Override
    public void prepare(LMAdRequestI lmAdRequest, SchedulePlayerPreparationListener listener) {
        interfaceImpl.prepare(lmAdRequest.getInnerIml(), listener);
    }

    @Override
    public void play() {
        interfaceImpl.play();
    }
}
