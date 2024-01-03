package com.lemma.lemmasignageclient.common;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.provider.Settings;
import android.view.Display;
import android.view.WindowManager;

import com.lemma.lemmasignageclient.BuildConfig;
import com.lemma.lemmasignageclient.common.logger.Applogger;
import com.lemma.lemmasignageclient.sdkinstantiator.InstanceCreator;
import com.lemma.lemmasignagesdk.api.LemmaSDKI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;

public class AppConfig {

    public static final String MASTER = "Master";
    public static final String PREF_KEY_NAMESPACE = "namespace";
    public static final String KEY_PUBLISHER_ID = "publisher_id";
    public static final String KEY_GID = "group_id";
    public static final String KEY_CUSTOM_PARAMS_ID = "cutom_params__id";
    public static final String KEY_ADUNIT_ID = "adunit_id";
    public static final String KEY_SCHEDULE_REFRESH_TIME_IN_SECONDS = "schedule_refresh_time_in_seconds";
    public static final String APP_CONFIG = "AppConfig";
    public static final String KEY_START_X = "start_x";
    public static final String KEY_START_Y = "start_y";
    public static final String KEY_AVAILABLE_WIDTH = "available_width";
    public static final String KEY_AVAILABLE_HEIGHT = "available_height";
    public static final String KEY_DOMAIN = "domain";
    public static final String KEY_SCHEDULED_AD_API_PATH = "ScheduledAdAPIPath";
    public static final String KEY_SERVE_AD_API_PATH = "ServeAdAPIPath";
    public static final String KEY_LOCAL_DOMAIN = "LocalDomain";
    public static final String KEY_PROD_DOMAIN = "ProdDomain";
    public static final String KEY_ENV_TYPE = "ENV_TYPE";
    public static final String KEY_APP_CONFIG_UPDATE_INTERVAL_IN_SECS = "AppConfigUpdateIntervalInSecs";
    public static final String KEY_APP_CONFIG_UPDATE_API_URL = "AppConfigUpdateAPIUrl";
    public static AppConfig instance = new AppConfig();
    private Context context;
    private SharedPreferences prefs;

    private AppConfig() {
    }

    public static void setNamespace(Context context, NAMESPACE namespace) {
        SharedPreferences prefs = context.getSharedPreferences(MASTER, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PREF_KEY_NAMESPACE, namespace.toString());
        editor.apply();
    }

    public static String getNamespace(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(MASTER, MODE_PRIVATE);
        String namespace = prefs.getString(PREF_KEY_NAMESPACE, NAMESPACE.SYNC_SCHEDULE.toString());//"No name defined" is the default value.
        return namespace;
    }

    public static int[] getScreenSizeInlcudingTopBottomBar(Context context) {
        int [] screenDimensions = new int[2]; // width[0], height[1]
        int x, y, orientation = context.getResources().getConfiguration().orientation;
        WindowManager wm = ((WindowManager)
                context.getSystemService(Context.WINDOW_SERVICE));
        Display display = wm.getDefaultDisplay();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            Point screenSize = new Point();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                display.getRealSize(screenSize);
                x = screenSize.x;
                y = screenSize.y;
            } else {
                display.getSize(screenSize);
                x = screenSize.x;
                y = screenSize.y;
            }
        } else {
            x = display.getWidth();
            y = display.getHeight();
        }
        
        screenDimensions[0] = orientation == Configuration.ORIENTATION_PORTRAIT ? x : y; // width
        screenDimensions[1] = orientation == Configuration.ORIENTATION_PORTRAIT ? y : x; // height

        screenDimensions[0] = x;
//        orientation == Configuration.ORIENTATION_PORTRAIT ? x : y; // width
        screenDimensions[1] = y;
//        orientation == Configuration.ORIENTATION_PORTRAIT ? y : x; // height

        return screenDimensions;
    }

    public int getScreenWidth() {
        return getScreenSizeInlcudingTopBottomBar(context)[0];
    }

    public int getScreenHeight() {
        return getScreenSizeInlcudingTopBottomBar(context)[1];
    }

    public void setup(Context context) {
        this.context = context;
        String name = APP_CONFIG + "-" + getNamespace(context);
        prefs = context.getSharedPreferences(name, MODE_PRIVATE);
    }


    public boolean alreadySetup() {
        return getPublisherId() != null && getAdunitId() != null;
    }

    public String getPublisherId() {
        String pubId = prefs.getString(KEY_PUBLISHER_ID, null);//"No name defined" is the default value.
        return pubId;
    }

    public void setPublisherId(String pubId) {
        if (!AppUtil.isValid(pubId)) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PUBLISHER_ID, pubId);
        editor.apply();
    }

    public String getAdunitId() {
        String auId = prefs.getString(KEY_ADUNIT_ID, null);//"No name defined" is the default value.
        return auId;
    }

    public void setAdunitId(String auId) {
        if (!AppUtil.isValid(auId)) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_ADUNIT_ID, auId);
        editor.apply();
    }

    public long getScheduleRefreshTime() {
        long scheduleRefreshTimeInSeconds = prefs.getLong(KEY_SCHEDULE_REFRESH_TIME_IN_SECONDS, (60 * 5));//"No name defined" is the default value.
        return scheduleRefreshTimeInSeconds;
    }

    public void setScheduleRefreshTime(long scheduleRefreshTimeInSeconds) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putLong(KEY_SCHEDULE_REFRESH_TIME_IN_SECONDS, scheduleRefreshTimeInSeconds);
        editor.apply();
    }

    public String getDeviceId() {
        String androidId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return androidId;
    }

    public String getResolution() {
        return getScreenWidth() + "x" + getScreenHeight();
    }

    public String getDeviceName() {
        return android.os.Build.MANUFACTURER + " " + android.os.Build.MODEL;
    }

    public String getAppSDKVersion() {
        LemmaSDKI sdk = InstanceCreator.SDKInstance();
        return BuildConfig.VERSION_NAME + "-" + sdk.getVersion();
    }

    public Rect viewFrame() {
        int x = prefs.getInt(KEY_START_X, 0);
        int y = prefs.getInt(KEY_START_Y, 0);
        int width = prefs.getInt(KEY_AVAILABLE_WIDTH, getScreenWidth());
        int height = prefs.getInt(KEY_AVAILABLE_HEIGHT, getScreenHeight());
        return new Rect(x, y, x + width, x + height);
    }

    public void setViewFrame(Rect rect) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_START_X, rect.left);
        editor.putInt(KEY_START_Y, rect.top);
        editor.putInt(KEY_AVAILABLE_WIDTH, rect.right - rect.left);
        editor.putInt(KEY_AVAILABLE_HEIGHT, rect.bottom - rect.top);
        editor.apply();
    }

    public boolean isSyncScheduleNamespace() {
        return AppConfig.getNamespace(context).equalsIgnoreCase(NAMESPACE.SYNC_SCHEDULE.toString());
    }

    public void setCustomParams(HashMap<String, String> map) {
        if (Objects.isNull(map)) {
            return;
        }
        JSONObject jsonObject = new JSONObject(map);
        String jsonString = jsonObject.toString();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_CUSTOM_PARAMS_ID, jsonString);
        editor.apply();
    }

    public HashMap<String, String>  getCustomParams() {
        HashMap<String, String> customParamsMap = new HashMap<>();
        String jsonString = prefs.getString(KEY_CUSTOM_PARAMS_ID, null);
        try {
            customParamsMap = AppUtil.stringToMap(jsonString);
        } catch (JSONException e) {
            Applogger.i("failed to read custom params");
        }
        return customParamsMap;
    }

    public enum NAMESPACE {
        SYNC_SCHEDULE("SYNC_SCHEDULE"),
        LIVE("LIVE");

        private final String text;

        /**
         * @param text
         */
        NAMESPACE(final String text) {
            this.text = text;
        }

        @Override
        public String toString() {
            return text;
        }
    }



    public String getGid() {
        String pubId = prefs.getString(KEY_GID, null);//"No name defined" is the default value.
        return pubId;
    }

    public void setGid(String gid) {
        if (!AppUtil.isValid(gid)) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_GID, gid);
        editor.apply();
    }

    public String getDomain() {
        if (getEnvironmentType() == 1) {
            return getProdDomain();
        }else {
            return getLocalDomain();
        }
    }

    public String getAPIPath() {
        String pubId = prefs.getString(KEY_DOMAIN, "lemmadigital.com");//"No name defined" is the default value.
        return pubId;
    }

    public String getServeAdAPIPath() {
        String pubId = prefs.getString(KEY_SERVE_AD_API_PATH, "/lemma/servad?");//"No name defined" is the default value.
        return pubId;
    }

    public void setServeAdAPIPath(String serveAdAPIPath) {
        if (!AppUtil.isValid(serveAdAPIPath)) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SERVE_AD_API_PATH, serveAdAPIPath);
        editor.apply();
    }

    public String getScheduledAdAPIPath() {
        String pubId = prefs.getString(KEY_SCHEDULED_AD_API_PATH, "/lemma/api/v1/getcrtvschedule");//"No name defined" is the default value.
        return pubId;
    }

    public void setScheduledAdAPIPath(String scheduledAdAPIPath) {
        if (!AppUtil.isValid(scheduledAdAPIPath)) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_SCHEDULED_AD_API_PATH, scheduledAdAPIPath);
        editor.apply();
    }

    public String getLocalDomain() {
        String pubId = prefs.getString(KEY_LOCAL_DOMAIN, "sync.lemmatechnologies.com");
        return pubId;
    }

    public void setLocalDomain(String localDomain) {
        if (!AppUtil.isValid(localDomain)) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_LOCAL_DOMAIN, localDomain);
        editor.apply();
    }

    public String getProdDomain() {
        String pubId = prefs.getString(KEY_PROD_DOMAIN, "lemmadigital.com");
        return pubId;
    }

    public void setProdDomain(String prodDomain) {
        if (!AppUtil.isValid(prodDomain)) {
            return;
        }
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_PROD_DOMAIN, prodDomain);
        editor.apply();
    }

    public Integer getEnvironmentType() {
         return prefs.getInt(KEY_ENV_TYPE, 1);
    }

    // 1 - live , 0 - local
    public void setEnvironmentType(Integer envType) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_ENV_TYPE, envType);
        editor.apply();
    }

    public Integer getAppConfigUpdateIntervalInSecs() {
        //TODO: change me to greater value
        return prefs.getInt(KEY_APP_CONFIG_UPDATE_INTERVAL_IN_SECS, (60*60));
    }

    public void setAppConfigUpdateIntervalInSecs(Integer envType) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putInt(KEY_APP_CONFIG_UPDATE_INTERVAL_IN_SECS, envType);
        editor.apply();
    }

    public String getAppConfigUpdateAPIUrl() {
        return prefs.getString(KEY_APP_CONFIG_UPDATE_API_URL, "https://lemmadigital.com/lemma/api/v1/getsitemap");
    }

    public void setAppConfigUpdateAPIUrl(String apiUrl) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_APP_CONFIG_UPDATE_API_URL, apiUrl);
        editor.apply();
    }

    public String getValueForKey(String key, String defaultValue) {
        return prefs.getString(key, defaultValue);
    }

    public void setValueForKey(String key, String val) {
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(key, val);
        editor.apply();
    }
}
