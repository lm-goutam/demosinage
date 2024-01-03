package com.lemma.lemmasignagesdk.scedule.scheduleplayer;

import android.location.Location;
import android.net.Uri;
import android.util.DisplayMetrics;

import com.lemma.lemmasignagesdk.LMAdRequest;
import com.lemma.lemmasignagesdk.LemmaSDK;
import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.common.network.Request;
import com.lemma.lemmasignagesdk.live.manager.AdvertisingIdClient;
import com.lemma.lemmasignagesdk.live.manager.LMAppInfo;
import com.lemma.lemmasignagesdk.live.manager.LMDeviceInfo;
import com.lemma.lemmasignagesdk.live.manager.LMLocationManager;
import com.lemma.lemmasignagesdk.live.manager.NetworkStatusMonitor;

import java.util.HashMap;
import java.util.Map;

public class ScheduleRequestBuilder {

    public AdvertisingIdClient advertisingIdClient;
    public DisplayMetrics displayMetrics;
    public LMDeviceInfo deviceInfo;
    public LMAppInfo appInfo;
    public LMLocationManager locationManager;
    public NetworkStatusMonitor networkStatusMonitor;
    private String url;
    private Map<String, String> map;
    private LMAdRequest request;

    public ScheduleRequestBuilder setRequest(LMAdRequest request) {
        this.request = request;
        return this;
    }

    public ScheduleRequestBuilder setUrl(String url) {
        this.url = url;
        return this;
    }

    public ScheduleRequestBuilder setAdvertisingIdClientUrl(AdvertisingIdClient advertisingIdClient) {
        this.advertisingIdClient = advertisingIdClient;
        return this;
    }

    public ScheduleRequestBuilder setDisplayMetrics(DisplayMetrics displayMetrics) {
        this.displayMetrics = displayMetrics;
        return this;
    }

    public ScheduleRequestBuilder setDeviceInfo(LMDeviceInfo deviceInfo) {
        this.deviceInfo = deviceInfo;
        return this;
    }

    public ScheduleRequestBuilder setAppInfo(LMAppInfo appInfo) {
        this.appInfo = appInfo;
        return this;
    }

    public ScheduleRequestBuilder setLocationManager(LMLocationManager locationManager) {
        this.locationManager = locationManager;
        return this;
    }

    public ScheduleRequestBuilder setNetworkMonitor(NetworkStatusMonitor networkStatusMonitor) {
        this.networkStatusMonitor = networkStatusMonitor;
        return this;
    }

    public ScheduleRequestBuilder setCustomParams(Map<String, String> map) {
        this.map = map;
        return this;
    }

    public Request build() {

        String url = this.url;
        if (request.getAdServerBaseURL() != null) {
            try {
                Uri uri = Uri.parse(request.getAdServerBaseURL());
                url = uri.toString();
            } catch (Exception e) {
                LMLog.e(e.getMessage());
            }
        }
        Uri.Builder urlBuilder = Uri.parse(url).buildUpon();
        urlBuilder.appendQueryParameter("pid", request.getPublisherId());
        urlBuilder.appendQueryParameter("aid", request.getAdUnitId());
        urlBuilder.appendQueryParameter("at", "3");
        if (request.getMap() != null) {
            for (Map.Entry<String, String> entry : request.getMap().entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();

                if (key != null && value != null) {
                    urlBuilder.appendQueryParameter(key, value);
                }
            }
        }
        this.url = urlBuilder.build().toString();

        Uri.Builder builder = Uri.parse(url).buildUpon();
        Uri uri = Uri.parse(url);

        for (Map.Entry<String, String> entry : defaultMap().entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            String existingQueryValue = uri.getQueryParameter(key);

            // Do not override values from original URL
            if (!isValid(existingQueryValue)) {
                builder.appendQueryParameter(key, value);
            }
        }

        if (map != null) {
            for (Map.Entry<String, String> entry : map.entrySet()) {
                String key = entry.getKey();
                String value = entry.getValue();
                builder.appendQueryParameter(key, value);
            }
        }
        String updatedURL = builder.build().toString();

        HashMap<String, Object> map = new HashMap() {{
            put("adid", Integer.parseInt(request.getAdUnitId()));
            put("pid", Integer.parseInt(request.getPublisherId()));
            put("mode", 2);
        }};


        Request request = Request.RequestBuilder.aRequest()
                .withUrl(updatedURL)
                .withData(map)
                .build();
        return request;
    }


    private Map<String, String> defaultMap() {
        Map<String, String> map = new HashMap<String, String>();
        if (advertisingIdClient != null) {
            AdvertisingIdClient.AdInfo adInfo = advertisingIdClient.refreshAdvertisingInfo();
            if (adInfo != null && adInfo.getId() != null && adInfo.getId().length() > 0) {
                map.put("ifa", adInfo.getId());
            }
        }
        if (displayMetrics != null && (displayMetrics.widthPixels > 0 && displayMetrics.heightPixels > 0)) {
            map.put("vw", String.valueOf(displayMetrics.widthPixels));
            map.put("vh", String.valueOf(displayMetrics.heightPixels));
            map.put("bw", String.valueOf(displayMetrics.widthPixels));
            map.put("bh", String.valueOf(displayMetrics.heightPixels));
        }

        if (deviceInfo != null) {
            // Device parameters
            String ua = deviceInfo.getUserAgent();
            if (isValid(ua)) {
                map.put("ua", ua);
            }

            String deviceMake = deviceInfo.getMake();
            if (isValid(deviceMake)) {
                map.put("dmake", deviceMake);
            }

            String deviceModel = deviceInfo.getModel();
            if (isValid(deviceModel)) {
                map.put("dmodel", deviceModel);
            }

            String os = deviceInfo.getOsName();
            if (isValid(os)) {
                map.put("os", os);
            }

            String osv = deviceInfo.getOsVersion();
            if (isValid(osv)) {
                map.put("osv", osv);
            }

            String androidId = deviceInfo.getAndroidId();
            if (isValid(androidId)) {
                map.put("sid", androidId);
            }

            String carrier = deviceInfo.getCarrierName();
            if (isValid(carrier)) {
                map.put("carrier", carrier);
            }

        }

        map.put("js", "1");

        if (locationManager != null) {
            Location location = locationManager.getLocation();
            if (location != null) {

                String lat = String.valueOf(location.getLatitude());
                if (isValid(lat)) {
                    map.put("dlat", lat);
                }

                String lon = String.valueOf(location.getLongitude());
                if (isValid(lon)) {
                    map.put("dlon", lon);
                }
            }
        }


        if (networkStatusMonitor != null) {

            // Network info
            NetworkStatusMonitor.NETWORK_TYPE networkType = networkStatusMonitor.getCurrentNetworkType();
            if (networkType == NetworkStatusMonitor.NETWORK_TYPE.WIFI) {
                map.put("conntype", "2");
            } else if (networkType == NetworkStatusMonitor.NETWORK_TYPE.CELLULAR) {
                map.put("conntype", "3");
            }
        }


        if (appInfo != null) {
            // App information
            String appId = appInfo.getPackageName();
            if (isValid(appId)) {
                map.put("apid", appId);
            }

            String appName = appInfo.getAppName();
            if (isValid(appName)) {
                map.put("apnm", appName);
            }


            String appVersion = appInfo.getAppVersion();
            if (isValid(appVersion)) {
                map.put("apver", appVersion);
            }

            String appBundle = appInfo.getPackageName();
            if (isValid(appBundle)) {
                map.put("apbndl", appBundle);
            }
        }

        map.put("apurl", "https://play.google.com/store/apps/details?id=com.lemma.digital");
        map.put("sdkver", LemmaSDK.instance.getVersion());
        map.put("rst", "2");

        return map;
    }

    private boolean isValid(String str) {
        return (str != null && str.length() > 0);
    }

}
