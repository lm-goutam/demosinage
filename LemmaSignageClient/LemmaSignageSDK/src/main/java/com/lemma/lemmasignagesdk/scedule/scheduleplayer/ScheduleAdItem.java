package com.lemma.lemmasignagesdk.scedule.scheduleplayer;

import android.graphics.Rect;

import androidx.annotation.NonNull;

import com.lemma.lemmasignagesdk.common.LMLog;
import com.lemma.lemmasignagesdk.common.LMUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

public class ScheduleAdItem {

    private final HashMap<String, Object> rawMapForTracker = new HashMap();
    public String itemType;
    public Rect frame = new Rect(0, 0, 100, 100);
    @NonNull
    String creative;
    String lineItemId;
    String adType;
    CREATIVE_TYPE creativeType;
    Date startTime;
    Integer duration;
    private String temporaryGroupId = "";
    private String creativeId;
    public ArrayList<String> trackers;

    public ScheduleAdItem() {

    }

    static ArrayList<String> arrayForKey(JSONObject object, String key) {

        ArrayList<String> items = new ArrayList<>();
        try {
            JSONArray jsonArray = object.getJSONArray(key);
            for(int i = 0; i < jsonArray.length(); i++) {
                String tracker = jsonArray.getString(i);
                items.add(tracker);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return items;
    }

    @NonNull
    static ScheduleAdItem parse(JSONObject scheduleObj) {

        ScheduleAdItem scheduleItem = new ScheduleAdItem();
        try {
            scheduleItem.creative = scheduleObj.getString("Creative");
            scheduleItem.itemType = scheduleObj.getString("Type");
            scheduleItem.duration = scheduleObj.getInt("Duration");
            scheduleItem.creativeId = scheduleObj.getString("crid");
            scheduleItem.lineItemId = scheduleObj.getString("lid");
            scheduleItem.trackers = arrayForKey(scheduleObj,"trackers");
            String startTime = scheduleObj.getString("sdate");

            scheduleItem.rawMapForTracker.put("id", scheduleObj.getInt("id"));
            scheduleItem.rawMapForTracker.put("dur", scheduleObj.getInt("Duration"));
            scheduleItem.rawMapForTracker.put("lid", scheduleObj.getString("lid"));
            scheduleItem.rawMapForTracker.put("crid", scheduleObj.getString("crid"));
            scheduleItem.rawMapForTracker.put("cid", scheduleObj.getString("cid"));
            scheduleItem.rawMapForTracker.put("org_id", scheduleObj.getString("org_id"));
            scheduleItem.rawMapForTracker.put("ag_id", scheduleObj.getString("ag_id"));
            scheduleItem.rawMapForTracker.put("ad_type", scheduleObj.getString("ad_type"));
            scheduleItem.rawMapForTracker.put("cr_type", scheduleObj.getString("cr_type"));

            switch (scheduleObj.getInt("cr_type")) {
                case 1:
                    scheduleItem.creativeType = CREATIVE_TYPE.Publisher;
                    break;
                case 2:
                    scheduleItem.creativeType = CREATIVE_TYPE.Guaranteed;
                    break;
                case 3:
                    scheduleItem.creativeType = CREATIVE_TYPE.RealTime;
                    break;
                case 4:
                    scheduleItem.creativeType = CREATIVE_TYPE.AdNetwork;
                    break;
                case 5:
                    scheduleItem.creativeType = CREATIVE_TYPE.Passback;
                    break;
                default:
                    scheduleItem.creativeType = CREATIVE_TYPE.Publisher;
            }
            try {
                scheduleItem.startTime = LMUtils.getDateFromString(startTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        } catch (JSONException e) {
            LMLog.e(e.getLocalizedMessage());
        }
        return scheduleItem;
    }

    @NonNull
    public String getCreative() {
        return creative;
    }

    public CREATIVE_TYPE getCreativeType() {
        return creativeType;
    }

    public Integer getDuration() {
        return duration;
    }

    public Date getStartTime() {
        return startTime;
    }

    public String getTemporaryGroupId() {
        return temporaryGroupId;
    }

    public void setTemporaryGroupId(String temporaryGroupId) {
        this.temporaryGroupId = temporaryGroupId;
    }

//    static int cI = 0;
//    private static String urls(){
//
//        ArrayList<String> arrayList = new ArrayList();
//        arrayList.add("https://www.google.com");
//        arrayList.add("https://www.quora.com/");
//        arrayList.add("https://www.youtube.com/");
//        arrayList.add("<h1>Hello world</h1>");
//        String result = arrayList.get(cI%4);
//        cI++;
//        return result;
//    }

    public HashMap getRawMapForTracker() {
        return rawMapForTracker;
    }

    public String getCreativeId() {
        return creativeId;
    }

    public void setCreativeId(String creativeId) {
        this.creativeId = creativeId;
    }

    @Override
    public String toString() {
        return "ScheduleAdItem{" +
                "creative='" + creative + '\'' +
                ", adType='" + adType + '\'' +
                ", creativeType='" + creativeType + '\'' +
                ", itemType='" + itemType + '\'' +
                ", startTime=" + startTime +
                ", duration=" + duration +
                '}';
    }

    public enum CREATIVE_TYPE {
        Publisher,
        Guaranteed,
        RealTime,
        AdNetwork,
        Passback
    }
}
