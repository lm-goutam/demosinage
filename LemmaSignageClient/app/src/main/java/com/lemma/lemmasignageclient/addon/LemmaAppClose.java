package com.lemma.lemmasignageclient.addon;

import static java.lang.System.exit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class LemmaAppClose extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        exit(0);
    }
}