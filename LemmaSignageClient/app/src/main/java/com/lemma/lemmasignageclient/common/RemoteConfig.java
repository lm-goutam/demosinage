package com.lemma.lemmasignageclient.common;

import com.google.gson.Gson;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class RemoteConfig {
    @Override
    public String toString() {
        return "RemoteConfig{" +
                "aid='" + aid + '\'' +
                ", gid='" + gid + '\'' +
                ", pid='" + pid + '\'' +
                ", w=" + w +
                ", h=" + h +
                ", domain='" + domain + '\'' +
                ", isResetup=" + isResetup +
                ", isScreenEdit=" + isScreenEdit +
                ", isAdSync=" + isAdSync +
                ", isFullscreen=" + isFullscreen +
                ", isAutoUpdate=" + isAutoUpdate +
                ", environment=" + environment +
                ", defaultCreative='" + defaultCreative + '\'' +
                ", refreshInterval=" + refreshInterval +
                ", startTime='" + startTime + '\'' +
                ", endTime='" + endTime + '\'' +
                ", isWeather=" + isWeather +
                ", appUrl='" + appUrl + '\'' +
                ", scheduleApi='" + scheduleApi + '\'' +
                ", adServadApi='" + adServadApi + '\'' +
                ", notificationApi='" + notificationApi + '\'' +
                ", lemmaWeatherApi='" + lemmaWeatherApi + '\'' +
                ", thirdpartyWeatherApi='" + thirdpartyWeatherApi + '\'' +
                ", defaultDuration=" + defaultDuration +
                ", sitemapApi='" + sitemapApi + '\'' +
                ", restartTime='" + restartTime + '\'' +
                ", timezone='" + timezone + '\'' +
                ", localDomain='" + localDomain + '\'' +
                ", prodDomain='" + prodDomain + '\'' +
                ", isMute=" + isMute +
                '}';
    }

    public static RemoteConfig parse(String input) {
        Gson gson = new Gson();
        return gson.fromJson(input,RemoteConfig.class);
    }

    public Error validate(){
        if(AppUtil.isValid(getPid())){
            // TODO:: Add more validation
            return null;
        }
        return new Error("Invalid remote config");
    }

    @SerializedName("aid")
    @Expose
    private String aid;
    @SerializedName("gid")
    @Expose
    private String gid;
    @SerializedName("pid")
    @Expose
    private String pid;
    @SerializedName("w")
    @Expose
    private Integer w;
    @SerializedName("h")
    @Expose
    private Integer h;
    @SerializedName("domain")
    @Expose
    private String domain;
    @SerializedName("is_resetup")
    @Expose
    private Boolean isResetup;
    @SerializedName("is_screen_edit")
    @Expose
    private Boolean isScreenEdit;
    @SerializedName("is_adSync")
    @Expose
    private Boolean isAdSync;
    @SerializedName("is_fullscreen")
    @Expose
    private Boolean isFullscreen;
    @SerializedName("is_auto_update")
    @Expose
    private Boolean isAutoUpdate;
    @SerializedName("environment")
    @Expose
    private Integer environment;
    @SerializedName("default_creative")
    @Expose
    private String defaultCreative;
    @SerializedName("refresh_interval")
    @Expose
    private Integer refreshInterval;
    @SerializedName("start_time")
    @Expose
    private String startTime;
    @SerializedName("end_time")
    @Expose
    private String endTime;
    @SerializedName("is_weather")
    @Expose
    private Integer isWeather;
    @SerializedName("app_url")
    @Expose
    private String appUrl;
    @SerializedName("schedule_api")
    @Expose
    private String scheduleApi;
    @SerializedName("ad_servad_api")
    @Expose
    private String adServadApi;
    @SerializedName("notification_api")
    @Expose
    private String notificationApi;
    @SerializedName("lemma_Weather_api")
    @Expose
    private String lemmaWeatherApi;
    @SerializedName("thirdparty_Weather_api")
    @Expose
    private String thirdpartyWeatherApi;
    @SerializedName("default_duration")
    @Expose
    private Integer defaultDuration;
    @SerializedName("sitemap_api")
    @Expose
    private String sitemapApi;
    @SerializedName("restart_time")
    @Expose
    private String restartTime;
    @SerializedName("timezone")
    @Expose
    private String timezone;
    @SerializedName("local_domain")
    @Expose
    private String localDomain;
    @SerializedName("prod_domain")
    @Expose
    private String prodDomain;
    @SerializedName("is_mute")
    @Expose
    private Integer isMute;
    @SerializedName("chk_sum")
    @Expose
    private String checkSum;

    public String getCheckSum() {
        return checkSum;
    }

    public String getAid() {
        return aid;
    }

    public void setAid(String aid) {
        this.aid = aid;
    }

    public String getGid() {
        return gid;
    }

    public void setGid(String gid) {
        this.gid = gid;
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public Integer getW() {
        return w;
    }

    public void setW(Integer w) {
        this.w = w;
    }

    public Integer getH() {
        return h;
    }

    public void setH(Integer h) {
        this.h = h;
    }

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Boolean getIsResetup() {
        return isResetup;
    }

    public void setIsResetup(Boolean isResetup) {
        this.isResetup = isResetup;
    }

    public Boolean getIsScreenEdit() {
        return isScreenEdit;
    }

    public void setIsScreenEdit(Boolean isScreenEdit) {
        this.isScreenEdit = isScreenEdit;
    }

    public Boolean getIsAdSync() {
        return isAdSync;
    }

    public void setIsAdSync(Boolean isAdSync) {
        this.isAdSync = isAdSync;
    }

    public Boolean getIsFullscreen() {
        return isFullscreen;
    }

    public void setIsFullscreen(Boolean isFullscreen) {
        this.isFullscreen = isFullscreen;
    }

    public Boolean getIsAutoUpdate() {
        return isAutoUpdate;
    }

    public void setIsAutoUpdate(Boolean isAutoUpdate) {
        this.isAutoUpdate = isAutoUpdate;
    }

    public Integer getEnvironment() {
        return environment;
    }

    public void setEnvironment(Integer environment) {
        this.environment = environment;
    }

    public String getDefaultCreative() {
        return defaultCreative;
    }

    public void setDefaultCreative(String defaultCreative) {
        this.defaultCreative = defaultCreative;
    }

    public Integer getRefreshInterval() {
        return refreshInterval;
    }

    public void setRefreshInterval(Integer refreshInterval) {
        this.refreshInterval = refreshInterval;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Integer getIsWeather() {
        return isWeather;
    }

    public void setIsWeather(Integer isWeather) {
        this.isWeather = isWeather;
    }

    public String getAppUrl() {
        return appUrl;
    }

    public void setAppUrl(String appUrl) {
        this.appUrl = appUrl;
    }

    public String getScheduleApi() {
        return scheduleApi;
    }

    public void setScheduleApi(String scheduleApi) {
        this.scheduleApi = scheduleApi;
    }

    public String getAdServadApi() {
        return adServadApi;
    }

    public void setAdServadApi(String adServadApi) {
        this.adServadApi = adServadApi;
    }

    public String getNotificationApi() {
        return notificationApi;
    }

    public void setNotificationApi(String notificationApi) {
        this.notificationApi = notificationApi;
    }

    public String getLemmaWeatherApi() {
        return lemmaWeatherApi;
    }

    public void setLemmaWeatherApi(String lemmaWeatherApi) {
        this.lemmaWeatherApi = lemmaWeatherApi;
    }

    public String getThirdpartyWeatherApi() {
        return thirdpartyWeatherApi;
    }

    public void setThirdpartyWeatherApi(String thirdpartyWeatherApi) {
        this.thirdpartyWeatherApi = thirdpartyWeatherApi;
    }

    public Integer getDefaultDuration() {
        return defaultDuration;
    }

    public void setDefaultDuration(Integer defaultDuration) {
        this.defaultDuration = defaultDuration;
    }

    public String getSitemapApi() {
        return sitemapApi;
    }

    public void setSitemapApi(String sitemapApi) {
        this.sitemapApi = sitemapApi;
    }

    public String getRestartTime() {
        return restartTime;
    }

    public void setRestartTime(String restartTime) {
        this.restartTime = restartTime;
    }

    public String getTimezone() {
        return timezone;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public String getLocalDomain() {
        return localDomain;
    }

    public void setLocalDomain(String localDomain) {
        this.localDomain = localDomain;
    }

    public String getProdDomain() {
        return prodDomain;
    }

    public void setProdDomain(String prodDomain) {
        this.prodDomain = prodDomain;
    }

    public Integer getIsMute() {
        return isMute;
    }

    public void setIsMute(Integer isMute) {
        this.isMute = isMute;
    }
}
