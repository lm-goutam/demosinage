package com.lemma.lemmasignageclient.common;
import android.content.Context;
import android.graphics.Rect;

import com.jakewharton.processphoenix.ProcessPhoenix;
import com.lemma.lemmasignageclient.common.logger.Applogger;
import com.lemma.lemmasignageclient.common.network.NetworkHandler;
import com.lemma.lemmasignageclient.common.network.Request;
import com.lemma.lemmasignageclient.ui.live.Bean.Publisher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AppManager {
    public static final String SITE_ID_KEY = "site_id";
    public static final String KEY_REMOTE_CONFIG_CHECK_SUM = "RemoteConfigCheckSum";
    private Context context;
    private NetworkHandler handler = new NetworkHandler();
    private static AppManager _instance;
    public Publisher publisher;
    private LMTimer timer;

    public boolean isDisplayOn = true;

    private AppManager() {
    }

    public static AppManager getInstance() {
        if (_instance == null) {
            _instance = new AppManager();
        }
        return _instance;
    }

    public void setup(Context context) {
        AppConfig.instance.setup(context);
        this.context = context;
        updateRemoteConfig();
    }

    public void updateRemoteConfig() {
//        parseConfig("{\"status\":200,\"data\":{\"aid\":16580,\"gid\":1366,\"pid\":178,\"w\":1080,\"h\":1920,\"domain\":\"sync.lemmatechnologies.com\",\"is_resetup\":false,\"is_screen_edit\":false,\"is_adSync\":true,\"is_fullscreen\":true,\"is_auto_update\":false,\"environment\":0,\"default_creative\":\"https://sync.lemmatechnologies.com/media/default.mp4\",\"refresh_interval\":3600,\"start_time\":\"\",\"end_time\":\"23:30\",\"is_weather\":0,\"app_url\":\"https://apps.lemmatechnologies.com/sdk/html5/?\",\"schedule_api\":\"/lemma/api/v1/getcrtvschedule\",\"ad_servad_api\":\"/lemma/servad?\",\"notification_api\":\"/lemma/api/v1/sch_upd_notify\",\"lemma_Weather_api\":\"https://apps.lemmatechnologies.com/js/weather.json\",\"thirdparty_Weather_api\":\"https://sync.lemmatechnologies.com/media/weather.json\",\"default_duration\":15,\"sitemap_api\":\"https://lemmadigital.com/lemma/api/v1/getsitemap\",\"restart_time\":\"01:40\",\"timezone\":\"+00:00\",\"local_domain\":\"sync.lemmatechnologies.com\",\"prod_domain\":\"lemmadigital.com\",\"is_mute\":0},\"error\":\"\"}");
        Integer  appConfigUpdateIntervalInSecs = AppConfig.instance.getAppConfigUpdateIntervalInSecs();

        Applogger.i("Scheduling remote config fetch after %s seconds",appConfigUpdateIntervalInSecs);
        timer = LMTimer.TimerBuilder.aTimer()
                .withCallAfterSeconds(appConfigUpdateIntervalInSecs)
                .withCallable(new LMTimer.Callable() {
                    @Override
                    public void call(boolean isOnMainThread) {
                        fetchRemoteConfig();
                    }
                })
                .withRepeat(true)
                .build();
        timer.start();
    }

    private void fetchRemoteConfig() {


        HashMap data = new HashMap() {{
            put(SITE_ID_KEY,AppConfig.instance.getDeviceId());
        }};

        Request request = Request.RequestBuilder.aRequest()
                .withUrl(AppConfig.instance.getAppConfigUpdateAPIUrl())
                .withData(data)
                .build();

        Applogger.i("Fetching remote config from %s for data %s",
                AppConfig.instance.getAppConfigUpdateAPIUrl(),
                data);

        handler.post(request, (error, response) -> {
            // Parse data into RemoteConfig
            // RemoteConfig -> AppConfig
            Applogger.i("Received config response- %s, err- %s",
                    response,
                    error);

            RemoteConfig remoteConfig = parseConfig(response);
            if (remoteConfig != null) {
                Error err = remoteConfig.validate();
                if (err == null){
                    Applogger.i("Applying the config %s",remoteConfig);
                    applyRemoteConfig(remoteConfig);
                }else{
                    Applogger.w("Not able to read remote config - %s",err);
                }
            }else {
                Applogger.w("Not able to read remote config");
            }
        });
    }

    private RemoteConfig parseConfig(String response){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(response);
            JSONObject dataObj =  jsonObject.getJSONObject("data");
            RemoteConfig remoteConfig = RemoteConfig.parse(dataObj.toString());
            return remoteConfig;
        } catch (JSONException e) {
            Applogger.e(e.getLocalizedMessage());
        }
        return null;
    }

    private void applyRemoteConfig(RemoteConfig remoteConfig){

        AppConfig.instance.setPublisherId(remoteConfig.getPid());
        AppConfig.instance.setAdunitId(remoteConfig.getAid());

        Rect frame = AppConfig.instance.viewFrame();
        Rect newFrame = new Rect(frame.left, frame.top, remoteConfig.getW(),remoteConfig.getH());
        AppConfig.instance.setViewFrame(newFrame);

        if (remoteConfig.getIsAdSync()){
            AppConfig.setNamespace(context, AppConfig.NAMESPACE.LIVE );
        }else {
            AppConfig.setNamespace(context, AppConfig.NAMESPACE.SYNC_SCHEDULE);
        }

        AppConfig.instance.setEnvironmentType(remoteConfig.getEnvironment());
        AppConfig.instance.setAppConfigUpdateIntervalInSecs(remoteConfig.getRefreshInterval());
        AppConfig.instance.setScheduledAdAPIPath(remoteConfig.getScheduleApi());
        AppConfig.instance.setServeAdAPIPath(remoteConfig.getAdServadApi());
        AppConfig.instance.setAppConfigUpdateAPIUrl(remoteConfig.getSitemapApi());
        AppConfig.instance.setLocalDomain(remoteConfig.getLocalDomain());
        AppConfig.instance.setProdDomain(remoteConfig.getProdDomain());

        String pastChkSum = AppConfig.instance.getValueForKey(KEY_REMOTE_CONFIG_CHECK_SUM,"");
        if (pastChkSum !=null && pastChkSum.length() !=0 && !(pastChkSum.equalsIgnoreCase(remoteConfig.getCheckSum()))) {
            AppConfig.instance.setValueForKey(KEY_REMOTE_CONFIG_CHECK_SUM,remoteConfig.getCheckSum());
            ProcessPhoenix.triggerRebirth(context);
        }else {
            AppConfig.instance.setValueForKey(KEY_REMOTE_CONFIG_CHECK_SUM,remoteConfig.getCheckSum());
        }
    }
}


