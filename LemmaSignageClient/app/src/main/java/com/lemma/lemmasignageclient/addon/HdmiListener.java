package com.lemma.lemmasignageclient.addon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.lemma.lemmasignageclient.common.AppManager;

import org.greenrobot.eventbus.EventBus;

public class HdmiListener extends BroadcastReceiver {

    public static boolean state = true;
    private static String HDMIINTENT = "android.intent.action.HDMI_PLUGGED";

    public void onReceive(Context ctxt, Intent receivedIt) {
        String action = receivedIt.getAction();

        if (action.equals(HDMIINTENT)) {
            boolean state = receivedIt.getBooleanExtra("state", false);

            if (state) {
                Log.d("HDMIListener", "BroadcastReceiver.onReceive() : Connected HDMI-TV");
                Toast.makeText(ctxt, "HDMI  Connected >>", Toast.LENGTH_LONG).show();
                AppManager.getInstance().isDisplayOn = true;
            } else {
                Log.d("HDMIListener", "HDMI >>: Disconnected HDMI-TV");
                Toast.makeText(ctxt, "HDMI DisConnected>>", Toast.LENGTH_LONG).show();
                AppManager.getInstance().isDisplayOn = false;
            }

            EventBus.getDefault().post(state);

        }
    }
}
