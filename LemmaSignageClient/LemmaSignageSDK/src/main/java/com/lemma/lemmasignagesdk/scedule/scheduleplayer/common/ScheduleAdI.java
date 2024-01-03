package com.lemma.lemmasignagesdk.scedule.scheduleplayer.common;

import java.util.Date;

public interface ScheduleAdI {
    Integer getDuration();

    Date getScheduleTime();

    boolean isRTB();

    boolean isOfflineAvailable();
}
