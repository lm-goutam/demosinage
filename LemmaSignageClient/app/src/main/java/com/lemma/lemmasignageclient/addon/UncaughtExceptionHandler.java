package com.lemma.lemmasignageclient.addon;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import com.lemma.lemmasignageclient.ui.MainActivity;

public class UncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {

    public Thread.UncaughtExceptionHandler exceptionHandler;
    private Activity activity;

    public UncaughtExceptionHandler(Activity activity) {
        this.activity = activity;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable throwable) {
        Intent intent = new Intent(activity, MainActivity.class);
        intent.putExtra("LAUNCHED_FROM_EXC_HANDLER", true);
        PendingIntent pendingIntent = PendingIntent.getActivity(activity.getBaseContext(), 0, intent, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager mgr = (AlarmManager) activity.getBaseContext().getSystemService(Context.ALARM_SERVICE);
        mgr.set(AlarmManager.RTC, System.currentTimeMillis() + 100, pendingIntent);
        activity.finish();
        System.exit(2);
    }
}
