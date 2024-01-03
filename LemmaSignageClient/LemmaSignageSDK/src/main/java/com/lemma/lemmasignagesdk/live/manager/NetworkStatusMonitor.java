package com.lemma.lemmasignagesdk.live.manager;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkStatusMonitor {

    Context context;

    public NetworkStatusMonitor(Context context) {
        this.context = context;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public NETWORK_TYPE getCurrentNetworkType() {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm != null) {
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (null != activeNetwork) {

                switch (activeNetwork.getType()) {

                    case ConnectivityManager.TYPE_WIFI:
                        return NETWORK_TYPE.WIFI;
                    case ConnectivityManager.TYPE_MOBILE:
                        return NETWORK_TYPE.CELLULAR;
                    default:
                        return NETWORK_TYPE.UNKNOWN;
                }
            }
        }
        return NETWORK_TYPE.UNKNOWN;
    }

    public enum NETWORK_TYPE {
        CELLULAR("cellular"), WIFI("wifi"), UNKNOWN(null);

        private final String value;

        NETWORK_TYPE(String val) {
            this.value = val;
        }

        public String getValue() {
            return value;
        }
    }
}
