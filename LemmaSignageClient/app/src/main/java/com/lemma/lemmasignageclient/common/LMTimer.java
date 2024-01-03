package com.lemma.lemmasignageclient.common;

import android.os.Handler;
import android.os.Looper;

import java.util.Timer;
import java.util.TimerTask;

public class LMTimer {

    private Callable callable;
    private Timer timerObj;
    private boolean dispatchOnMainThread;
    private long callAfterSeconds;
    private boolean repeat = false;

    public void cancel() {
        if (timerObj != null) {
            timerObj.cancel();
        }
    }

    public void start() {

        Timer timerObj = new Timer();
        TimerTask timerTaskObj = new TimerTask() {
            public void run() {

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        callable.call(true);
                    }
                });

            }
        };

        if (repeat){
            timerObj.schedule(timerTaskObj, 1000 * callAfterSeconds,1000 * callAfterSeconds);
        }else {
            timerObj.schedule(timerTaskObj, 1000 * callAfterSeconds);
        }

        this.timerObj = timerObj;
    }

    public interface Callable {
        void call(boolean isOnMainThread);
    }

    public static final class TimerBuilder {
        private Callable callable;
        private boolean dispatchOnMainThread;
        private long callAfterSeconds;
        private boolean repeat;

        private TimerBuilder() {
        }

        public static TimerBuilder aTimer() {
            return new TimerBuilder();
        }

        public TimerBuilder withCallable(Callable callable) {
            this.callable = callable;
            return this;
        }

        public TimerBuilder withDispatchOnMainThread(boolean dispatchOnMainThread) {
            this.dispatchOnMainThread = dispatchOnMainThread;
            return this;
        }

        public TimerBuilder withCallAfterSeconds(long callAfterSeconds) {
            this.callAfterSeconds = callAfterSeconds;
            return this;
        }

        public TimerBuilder withRepeat(boolean repeat) {
            this.repeat = repeat;
            return this;
        }

        public LMTimer build() {
            LMTimer timer = new LMTimer();
            timer.callable = this.callable;
            timer.dispatchOnMainThread = this.dispatchOnMainThread;
            timer.callAfterSeconds = this.callAfterSeconds;
            timer.repeat = this.repeat;
            return timer;
        }
    }
}
