package com.lemma.lemmasignagesdk.common;
import com.instacart.library.truetime.TrueTime;

import android.content.Context;
import android.os.AsyncTask;

import java.io.IOException;
import java.util.Date;

public class DateTimeProvider {
    Context appContext;
    public static void setup(Context appContext) {
        DateTimeProvider.instance.appContext = appContext;
        DateTimeProvider.instance.initTrueTime();
    }

    public static DateTimeProvider instance = new DateTimeProvider();

    private void initTrueTime() {
        new InitTrueTimeAsyncTask().execute();
    }

    // a little part of me died, having to use this
    private class InitTrueTimeAsyncTask extends AsyncTask<Void, Void, Void> {

        protected Void doInBackground(Void... params) {
            try {
                TrueTime.build()
                        //.withSharedPreferences(SampleActivity.this)
                        .withNtpHost("time.google.com")
                        .withLoggingEnabled(false)
                        .withSharedPreferencesCache(appContext)
                        .withConnectionTimeout(3_1428)
                        .initialize();
            } catch (IOException e) {
                LMLog.e( "Something went wrong when trying to initialize TrueTime", e);
            }
            return null;
        }
    }

    public Date getCurrentTime() {
        return TrueTime.now();
    }

    private DateTimeProvider() {

    }

}
