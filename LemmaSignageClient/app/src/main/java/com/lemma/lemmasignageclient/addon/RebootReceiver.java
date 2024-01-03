package com.lemma.lemmasignageclient.addon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.lemma.lemmasignageclient.ui.MainActivity;


public class RebootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED)) {
            //To start new intent when phone starts up
            Intent i = new Intent(context, MainActivity.class);
            // To put activity on the top of the stack since activity is launched from context outside activity
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // EDITED
            i.addFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.startActivity(i);
        }
    }

}
