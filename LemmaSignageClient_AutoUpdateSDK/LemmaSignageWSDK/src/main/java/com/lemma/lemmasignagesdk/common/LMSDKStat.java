package com.lemma.lemmasignagesdk.common;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.lemma.lemmasignagesdk.common.logger.LMWLog;
import com.lemma.lemmasignagesdk.core.LMNetworkTask;

import org.json.JSONException;
import org.json.JSONObject;


public class LMSDKStat {

    public Integer getUpdateAfter() {
        return updateAfter;
    }

    Integer updateAfter;
    public boolean shouldFetchSDKBinary = true;
    public String coreSDKUrl;

    public String getCoreSDKBinaryHash() {
        return coreSDKBinaryHash;
    }

    public String coreSDKBinaryHash;
    public String coreSDKVersion;

    @Override
    public String toString() {
        return "coreSDKVersion: "+coreSDKVersion;
    }

    public boolean isValid() {
        return true;
    }

    public static interface SDKStatListener {
        public void onResult(LMSDKStat stat, Error error);
    }

    public boolean shouldUpdate(LMSDKStat stat){
        return  !(coreSDKBinaryHash.equalsIgnoreCase(stat.coreSDKBinaryHash));
    }

    public static LMSDKStat getSavedStat(Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        if(!preferences.contains("LMSDKStat")) {
            return null;
        }
        LMSDKStat stat = new LMSDKStat();
        try {
            JSONObject object = new JSONObject(preferences.getString("LMSDKStat",""));
            stat.updateAfter = object.getInt("updateAfter");
            stat.coreSDKUrl = object.getString("coreSDKUrl");
            stat.coreSDKBinaryHash = object.getString("coreSDKBinaryHash");
            stat.coreSDKVersion = object.getString("coreSDKVersion");
            stat.shouldFetchSDKBinary = false;
        } catch (JSONException e) {
            LMWLog.w("LMSDKStat",e.getMessage());
            return null;
        }
        return stat;
    }


    public static void savedStat(LMSDKStat stat,Context context) {
        JSONObject object = new JSONObject();
        try {
            object.put("updateAfter",stat.updateAfter);
            object.put("coreSDKUrl",stat.coreSDKUrl);
            object.put("coreSDKBinaryHash",stat.coreSDKBinaryHash);
            object.put("coreSDKVersion",stat.coreSDKVersion);
            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
            preferences .edit().putString("LMSDKStat", object.toString()).apply();
        } catch (JSONException e) {
            LMWLog.w("LMSDKStat",e.getMessage());
        }
    }

    public static void fetchStatFromNetwork(final Context context, final SDKStatListener listener){
        LMNetworkTask networkTask = new LMNetworkTask(context, new LMNetworkTask.TaskListener() {
            @Override
            public void onResult(String response, Error error) {
                if (response != null){
                    try {
                        LMSDKStat stat = new LMSDKStat();
                        JSONObject jsonObject = new JSONObject(response);
                        stat.updateAfter = jsonObject.getInt("update_after_in_mins");
                        // TODO: Fix
                        String sdkVersion = "";
//                        LemmaWSDK.getWVersion();


                        JSONObject sdkConfJsonObj = jsonObject.getJSONObject("core_sdk_conf");

                        JSONObject sdkVerJsonObj = sdkConfJsonObj.getJSONObject(sdkVersion);
                        stat.coreSDKUrl = sdkVerJsonObj.getString("url");
                        stat.coreSDKVersion = sdkVerJsonObj.getString("core_sdk_version");
                        stat.coreSDKBinaryHash = sdkVerJsonObj.getString("core_sdk_binary_md5_hash");
                        stat.shouldFetchSDKBinary = true;
                        listener.onResult(stat,null);
                        savedStat(stat,context);
                    } catch (JSONException e) {
                        LMWLog.w("LMSDKStat",e.getMessage());
                        listener.onResult(null,new Error(e));
                    }
                }
            }
        });
        networkTask.execute(configUrl);
    }

    static String configUrl = "https://lemmadigital.com/andriod_sdk/lmsdk.config";
}
